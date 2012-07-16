package org.openxava.actions;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.servlet.*;

import org.openxava.jpa.XPersistence;
import org.openxava.util.*;
import net.sf.jasperreports.engine.*;

/**
 * To generate your custom Jasper Report. <p>
 * 
 * You only need to overwrite the abstract methods.<br>
 * 
 * @author Javier Paniza
 * @author Daniel García Salas  
 */

abstract public class JasperReportBaseAction extends ViewBaseAction implements IForwardAction, IModelAction { 
	
	public static String PDF = "pdf";
	public static String EXCEL = "excel"; 
	public static String RTF = "rtf";
	public static String ODT = "odt"; 
    public static String CSV = "csv";
    public static String TXT = "txt";
		
	private String modelName;
	private String format = PDF;
	
	/**
	 * Data to print. <p>
	 * 
	 * If return null then a JDBC connection is sent to JasperReport,
	 * this is for the case of a SQL inside JasperReport design.
	 */
	abstract protected JRDataSource getDataSource() throws Exception;
	
	/**
	 * The name of the XML with the JasperReports design. <p>
	 * 
	 * If it is a relative path (as <code>reports/myreport.jrxml</code> has 
	 * to be in classpath. If it is a absolute path (as 
	 * <code>/home/java/reports/myreport.xml</code> or 
	 * <code>C:\\JAVA\\REPORTS\MYREPORT.JRXML</code> then it look at the 
	 * file system.
	 */
	abstract protected String getJRXML() throws Exception;
	
	/**
	 * Parameters to send to report.
	 */
	abstract protected Map getParameters() throws Exception;
	
	/**
	 * Output report format, it can be 'pdf' or 'excel'. <p>
	 */
	public String getFormat() throws Exception {
		return format;
	}

	/**
	 * Output report format, it can be 'pdf', 'excel' or 'rtf'. <p>
	 */	
	public void setFormat(String format) throws Exception {
		if (!EXCEL.equalsIgnoreCase(format) && 
			!PDF.equalsIgnoreCase(format) &&
			!RTF.equalsIgnoreCase(format) &&
			!TXT.equalsIgnoreCase(format) &&
			!CSV.equalsIgnoreCase(format) &&
			!ODT.equalsIgnoreCase(format)) { 
			throw new XavaException("invalid_report_format", "'excel', 'pdf', 'rtf', 'odt', 'txt', 'csv'");
		} 
		this.format = format;		
	}

	public void execute() throws Exception {
		ServletContext application = getRequest().getSession().getServletContext();
		System.setProperty("jasper.reports.compile.class.path",					 
			application.getRealPath("/WEB-INF/lib/jasperreports.jar") +
			System.getProperty("path.separator") + 
			application.getRealPath("/WEB-INF/classes/")
		);
				
		InputStream xmlDesign = null;		
		String jrxml = getJRXML();
		if (isAbsolutePath(jrxml)) {
			xmlDesign = new FileInputStream(jrxml);
		}
		else {
			xmlDesign = JasperReportBaseAction.class.getResourceAsStream("/" + jrxml);
		} 
		if (xmlDesign == null) throw new XavaException("design_not_found"); 
		JasperReport report = JasperCompileManager.compileReport(xmlDesign);
		Map parameters = getParameters(); // getParameters() before getDatasource()
		JRDataSource ds = getDataSource();
		JasperPrint jprint = null;
		if (ds == null) {
			Connection con = null;
			try {
				con = DataSourceConnectionProvider.getByComponent(modelName).getConnection();
				// If the schema is changed through URL or XPersistence.setDefaultSchema, the connection
				// contains the original catalog (schema) instead of the new one, thus rendering the
				// wrong data on the report. This is a fix for such behavior.
				if (!Is.emptyString(XPersistence.getDefaultSchema())) {
					con.setCatalog(XPersistence.getDefaultSchema());
				}
				jprint = JasperFillManager.fillReport(report, parameters, con);
			} finally {
				con.close();
			}
		}
		else {
			jprint = JasperFillManager.fillReport(report, parameters, ds);
		}		
		getRequest().getSession().setAttribute("xava.report.jprint", jprint);
		getRequest().getSession().setAttribute("xava.report.format", getFormat());
	}
	
	private boolean isAbsolutePath(String design) { 
		return design.startsWith("/") || 
			(
				design.length() > 2 &&
				design.charAt(1) == ':' && 
				Character.isLetter(design.charAt(0))
			);
	}


	public String getForwardURI() {		
		return "/xava/report.pdf?time="+System.currentTimeMillis();
	}

	public boolean inNewWindow() {
		return true;
	}
				
	public void setModel(String modelName) { //  to obtain a JDCB connection, if required
		this.modelName = modelName;
	}
	
}