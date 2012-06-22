package org.openxava.web.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.sf.jasperreports.engine.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class JasperReportServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(JasperReportServlet.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		try {
			String model = request.getParameter("model");
			String language = request.getParameter("language");
			String tab = request.getParameter("tab");
			String properties = request.getParameter("properties");
			String totalProperties = request.getParameter("totalProperties"); 			
			
			ServletContext application = request.getSession().getServletContext();		
									
			System.setProperty("jasper.reports.compile.class.path",					 
					application.getRealPath("/WEB-INF/lib/jasperreports.jar") +
					System.getProperty("path.separator") + 
					application.getRealPath("/WEB-INF/classes/")
					);											
			JasperCompileManager.compileReportToStream(getReportStream(request, response, model, language, tab, properties, totalProperties), response.getOutputStream());
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ServletException(XavaResources.getString("jasper_error"));
		}		
	}
	
	private InputStream getReportStream(HttpServletRequest request, HttpServletResponse response, String model, String language, String tab, String properties, String totalProperties) throws IOException, ServletException {
		StringBuffer suri = new StringBuffer();
		suri.append("/xava/jasperReport");		
		suri.append(".jsp?model=");
		suri.append(model);
		suri.append("&language=");
		suri.append(language);
		suri.append("&tab=");
		suri.append(tab);
		suri.append("&properties=");
		suri.append(properties);
		suri.append("&totalProperties=");
		suri.append(totalProperties);
		return Servlets.getURIAsStream(request, response, suri.toString(), XSystem.getEncoding());
	}
		
}
