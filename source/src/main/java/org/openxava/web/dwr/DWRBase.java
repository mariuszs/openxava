package org.openxava.web.dwr;

import javax.servlet.http.*;

import org.openxava.controller.*;

/**
 * Base class for creating DWR remote classes. <p>
 * 
 * @author Javier Paniza
 */
class DWRBase {
	
	protected static ModuleContext getContext(HttpServletRequest request) {
		return (ModuleContext) request.getSession().getAttribute("context");
	}

	protected static void checkSecurity(HttpServletRequest request, String application, String module) {		
		ModuleContext context = getContext(request);
		if (context == null) { // This user has not executed any module yet
			throw new SecurityException("6859"); 
		}
		if (!context.exists(application, module, "manager")) { // This user has not execute this module yet 
			throw new SecurityException("9876");  
		}
	}	

}
