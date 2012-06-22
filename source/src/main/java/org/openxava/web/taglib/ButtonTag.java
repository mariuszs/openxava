package org.openxava.web.taglib;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.apache.commons.logging.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.web.*;
import org.openxava.web.style.*;

/**
 * @author Javier Paniza
 */

public class ButtonTag extends TagSupport implements IActionTag{ 
	
	private String argv; 
	 
	private static Log log = LogFactory.getLog(ButtonTag.class);
	
	private String action;
	
	public int doStartTag() throws JspException {
		try {				
			if (Is.emptyString(getAction())) {  
				return SKIP_BODY;
			}
			
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();			
			MetaAction metaAction = MetaControllers.getMetaAction(getAction());
			String application = request.getParameter("application");
			String module = request.getParameter("module");
			pageContext.getOut().print("<input name='");
			pageContext.getOut().print(Ids.decorate(application, module, "action." + getAction())); 
			pageContext.getOut().println("' type='hidden'/>");			
			pageContext.getOut().print("<input type='button' "); 
			if (Is.emptyString(getArgv())) { 
				pageContext.getOut().print("id='"); 
				pageContext.getOut().print(Ids.decorate(application, module, getAction()));
				pageContext.getOut().print("'");
			}				
			pageContext.getOut().print(" title='"); 
			pageContext.getOut().print(metaAction.getKeystroke() + " - " + metaAction.getDescription(request));
			pageContext.getOut().print("'");
			pageContext.getOut().print(" class='");
			Style style = (Style) request.getAttribute("style");
			pageContext.getOut().print(style.getButton());
			pageContext.getOut().print("'\tonclick='openxava.executeAction(");
			pageContext.getOut().print('"');				
			pageContext.getOut().print(application);
			pageContext.getOut().print('"');
			pageContext.getOut().print(", ");
			pageContext.getOut().print('"');				
			pageContext.getOut().print(module);
			pageContext.getOut().print('"');
			pageContext.getOut().print(", ");			
			pageContext.getOut().print('"');				
			pageContext.getOut().print(metaAction.getConfirmMessage(request));
			pageContext.getOut().print('"');
			pageContext.getOut().print(", ");
			pageContext.getOut().print(metaAction.isTakesLong());
			pageContext.getOut().print(", \"");
			pageContext.getOut().print(getAction());
			pageContext.getOut().print('"');
			if (!Is.emptyString(getArgv())) { 
				pageContext.getOut().print(", \"");
				pageContext.getOut().print(getArgv());
				pageContext.getOut().print('"');
			}
			pageContext.getOut().print(")' value='");
			pageContext.getOut().print(metaAction.getLabel(request));
			pageContext.getOut().println("'/>");
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("button_tag_error"));				
		}
		return SKIP_BODY;
	}
	
	public String getArgv() { 
		return argv; 
	} 
		 
	public void setArgv(String string) { 
		argv = string; 
	} 
		
	public String getAction() {
		return action;
	}

	public void setAction(String string) {
		action = string;
	}

}