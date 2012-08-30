package org.openxava.actions;

import org.apache.commons.logging.*;
import org.openxava.hibernate.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class GenerateReportAction extends TabBaseAction implements IForwardAction {

	private static Log log = LogFactory.getLog(GenerateReportAction.class);
	private String type;	

	public void execute() throws Exception {
		if (!("pdf".equals(getType()) || "csv".equals(getType()))) {
			throw new XavaException("report_type_not_supported", getType(), "pdf, csv");
		}
		getRequest().getSession().setAttribute("xava_reportTab", getTab());		
		getRequest().getSession().setAttribute("xava_selectedRowsReportTab", getTab().getSelected());
		String hibernateDefaultSchema = getHibernateDefaultSchema();
		if (!Is.emptyString(hibernateDefaultSchema)) {
			getRequest().getSession().setAttribute("xava_hibernateDefaultSchemaTab", hibernateDefaultSchema);
		}
		if (!Is.emptyString(XPersistence.getDefaultSchema())) {
			getRequest().getSession().setAttribute("xava_jpaDefaultSchemaTab", XPersistence.getDefaultSchema());
		}
		getRequest().getSession().setAttribute("xava_user", Users.getCurrent());
	}
	
	private String getHibernateDefaultSchema() {  
		try {
			return XHibernate.getDefaultSchema();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("hibernate_default_schema_warning", "__UNKNOWN__")); 
			return "__UNKNOWN__"; // Not null in order to avoid security holes
		}
	}
		
	public boolean inNewWindow() {
		return true;				
	}

	public String getForwardURI() {		
		return "/xava/list." + getType() + 
			"?application=" + getRequest().getParameter("application") +
			"&module=" + getRequest().getParameter("module") +
			"&time=" + System.currentTimeMillis();
	}

	public String getType() {
		return type;
	}

	public void setType(String string) {
		type = string;
	}

}
