package org.openxava.web.servlets;

import java.io.*;
import java.math.*;
import java.sql.*;
import java.text.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.swing.event.*;
import javax.swing.table.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;

import org.apache.commons.logging.*;
import org.openxava.hibernate.*;
import org.openxava.jpa.*;
import org.openxava.model.meta.*;
import org.openxava.tab.*;
import org.openxava.tab.impl.*;
import org.openxava.util.*;
import org.openxava.web.*;

/**
 * To generate automatically reports from list mode. <p>
 * 
 * Uses JasperReports.
 * 
 * @author Javier Paniza
 */

public class GenerateReportServlet extends HttpServlet {
	
	private static Log log = LogFactory.getLog(GenerateReportServlet.class);
	
	public static class TableModelDecorator implements TableModel {
							 
		private TableModel original;		
		private List metaProperties;
		private boolean withValidValues = false;
		private Locale locale;
		private boolean labelAsHeader = false;
		
		public TableModelDecorator(TableModel original, List metaProperties, Locale locale, boolean labelAsHeader) throws Exception {
			this.original = original;
			this.metaProperties = metaProperties;
			this.locale = locale;
			this.withValidValues = calculateWithValidValues();
			this.labelAsHeader = labelAsHeader;
		}

		private boolean calculateWithValidValues() {
			Iterator it = metaProperties.iterator();
			while (it.hasNext()) {
				MetaProperty m = (MetaProperty) it.next();
				if (m.hasValidValues()) return true;
			}
			return false;
		}
		
		private MetaProperty getMetaProperty(int i) {
			return (MetaProperty) metaProperties.get(i);
		}

		public int getRowCount() {			
			return original.getRowCount();
		}

		public int getColumnCount() {			
			return original.getColumnCount();
		}

		public String getColumnName(int c) {			
			return labelAsHeader?getMetaProperty(c).getLabel(locale):Strings.change(getMetaProperty(c).getQualifiedName(), ".", "_");
		}

		public Class getColumnClass(int c) {						
			return original.getColumnClass(c);
		}

		public boolean isCellEditable(int row, int column) {			
			return original.isCellEditable(row, column);
		}

		public Object getValueAt(int row, int column) {
			Object r = original.getValueAt(row, column);

			if (r instanceof Boolean) {
				if (((Boolean) r).booleanValue()) return XavaResources.getString(locale, "yes");
				return XavaResources.getString(locale, "no");
			}
			if (withValidValues) {
				MetaProperty p = getMetaProperty(column);
				if (p.hasValidValues()) {					
					return p.getValidValueLabel(locale, original.getValueAt(row, column));
				}
			}
			
			if (r instanceof java.util.Date) {				
				MetaProperty p = getMetaProperty(column); // In order to use the type declared by the developer 
					// and not the one returned by JDBC or the JPA engine				
				if (java.sql.Time.class.isAssignableFrom(p.getType())) {
					return DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(r);
				}
				if (java.sql.Timestamp.class.isAssignableFrom(p.getType())) {
					DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					return dateFormat.format( r );
				}
				return DateFormat.getDateInstance(DateFormat.SHORT, locale).format(r);
			}

			if (r instanceof BigDecimal) {
				return formatBigDecimal(r, locale); 
			}
			
			return r;
		}

		public void setValueAt(Object value, int row, int column) {
			original.setValueAt(value, row, column);			
		}

		public void addTableModelListener(TableModelListener l) {
			original.addTableModelListener(l);			
		}

		public void removeTableModelListener(TableModelListener l) {
			original.removeTableModelListener(l);			
		}
	}
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		try {				
			Locales.setCurrent(request); 
			if (Users.getCurrent() == null) { // for a bug in websphere portal 5.1 with Domino LDAP
				Users.setCurrent((String)request.getSession().getAttribute("xava.user"));
			}						
			request.getParameter("application"); // for a bug in websphere 5.1 
			request.getParameter("module"); // for a bug in websphere 5.1
			Tab tab = (Tab) request.getSession().getAttribute("xava_reportTab");
			request.getSession().removeAttribute("xava_reportTab"); 
			int [] selectedRows = (int []) request.getSession().getAttribute("xava_selectedRowsReportTab"); 
			request.getSession().removeAttribute("xava_selectedRowsReportTab");
			setDefaultSchema(request);
			String user = (String) request.getSession().getAttribute("xava_user");
			request.getSession().removeAttribute("xava_user");
			Users.setCurrent(user);
			String uri = request.getRequestURI();				
			if (uri.endsWith(".pdf")) {
				InputStream is;
				JRDataSource ds;
				Map parameters = new HashMap();
				synchronized (tab) {
					tab.setRequest(request);
					parameters.put("Title", tab.getTitle());				
					parameters.put("Organization", getOrganization());
					for (String totalProperty: tab.getTotalPropertiesNames()) { 								
						parameters.put(totalProperty + "__TOTAL__", getTotal(request, tab, totalProperty));
					}
					is  = getReport(request, response, tab);
					ds = getDataSource(tab, selectedRows, request);
				}				
				JasperPrint jprint = JasperFillManager.fillReport(is, parameters, ds);					
				response.setContentType("application/pdf");				
				JasperExportManager.exportReportToPdfStream(jprint, response.getOutputStream());
				
			}
			else if (uri.endsWith(".csv")) {	
				String csvEncoding = XavaPreferences.getInstance().getCSVEncoding(); 
				if (!Is.emptyString(csvEncoding)) {
					response.setCharacterEncoding(csvEncoding);
				}
				response.setContentType("text/x-csv");
				synchronized (tab) {
					tab.setRequest(request);
					response.getWriter().print(TableModels.toCSV(getTableModel(tab, selectedRows, request, true)));
				}
			}
			else {
				throw new ServletException(XavaResources.getString("report_type_not_supported", "", ".pdf .csv"));
			}			
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ServletException(XavaResources.getString("report_error"));
		}		
	}
	
	private Object getTotal(HttpServletRequest request, Tab tab, String totalProperty) {
		Object total = tab.getTotal(totalProperty);
		return WebEditors.format(request, tab.getMetaProperty(totalProperty), total, new Messages(), null, true);
	}

	private void setDefaultSchema(HttpServletRequest request) {
		String hibernateDefaultSchemaTab = (String) request.getSession().getAttribute("xava_hibernateDefaultSchemaTab");
		if (hibernateDefaultSchemaTab != null) {
			request.getSession().removeAttribute("xava_hibernateDefaultSchemaTab");
			XHibernate.setDefaultSchema(hibernateDefaultSchemaTab);
			
		}
		String jpaDefaultSchemaTab = (String) request.getSession().getAttribute("xava_jpaDefaultSchemaTab");
		if (jpaDefaultSchemaTab != null) {
			request.getSession().removeAttribute("xava_jpaDefaultSchemaTab");
			XPersistence.setDefaultSchema(jpaDefaultSchemaTab);			
		}
	}

	protected String getOrganization() throws MissingResourceException, XavaException {
		return ReportParametersProviderFactory.getInstance().getOrganization();
	}
	
	private InputStream getReport(HttpServletRequest request, HttpServletResponse response, Tab tab) throws ServletException, IOException {		
		StringBuffer suri = new StringBuffer();
		suri.append("/xava/jasperReport");
		suri.append("?model=");
		suri.append(tab.getModelName());
		suri.append("&language=");		
		suri.append(Locales.getCurrent().getLanguage());
		suri.append("&tab=");
		suri.append(tab.getTabName());
		suri.append("&properties=");
		suri.append(tab.getPropertiesNamesAsString());		
		suri.append("&totalProperties=");
		suri.append(tab.getTotalPropertiesNamesAsString());						
		response.setCharacterEncoding(XSystem.getEncoding()); 				
		return Servlets.getURIAsStream(request, response, suri.toString());
	}
	
	private JRDataSource getDataSource(Tab tab, int [] selectedRows, ServletRequest request) throws Exception {
		return new JRTableModelDataSource(getTableModel(tab, selectedRows, request, false));		
	}		  	
	
	private TableModel getTableModel(Tab tab, int [] selectedRows, ServletRequest request, boolean labelAsHeader) throws Exception {
		TableModel data = null;
		if (selectedRows != null && selectedRows.length > 0) {
			data = new SelectedRowsXTableModel(tab.getTableModel(), selectedRows);
		}
		else {
			data = tab.getAllDataTableModel();
		}
		return new TableModelDecorator(data, tab.getMetaProperties(), Locales.getCurrent(), labelAsHeader);
	}
	
	private static Object formatBigDecimal(Object number, Locale locale) { 
		NumberFormat nf = NumberFormat.getNumberInstance(locale);
		nf.setMinimumFractionDigits(2);
		return nf.format(number);
	}
	
}
