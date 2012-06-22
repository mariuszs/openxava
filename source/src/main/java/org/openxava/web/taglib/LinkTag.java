package org.openxava.web.taglib;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.apache.commons.logging.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.web.*;


/**
 * @author Javier Paniza
 */

public class LinkTag extends TagSupport implements IActionTag {

	private static Log log = LogFactory.getLog(LinkTag.class);
	
	private String action;
	private String argv;
	private String cssClass;
	private String cssStyle;
	private boolean hasBody;
		
	public int doStartTag() throws JspException {		
		try {
			if (Is.emptyString(getAction())) {  
				return EVAL_BODY_INCLUDE; 
			}
			hasBody=false;
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			MetaAction metaAction = MetaControllers.getMetaAction(getAction());
			
			String application = request.getParameter("application");
			String module = request.getParameter("module");
						 		
			pageContext.getOut().print("<input name='");
			pageContext.getOut().print(Ids.decorate(application, module, "action." + getAction())); 
			pageContext.getOut().println("' type='hidden'/>\n");
			
			pageContext.getOut().print("<a ");
			if (Is.emptyString(getArgv())) { 
				pageContext.getOut().print("id='");
				pageContext.getOut().print(Ids.decorate(application, module, getAction())); 
				pageContext.getOut().println("'");
			}
			if (!Is.emptyString(getCssClass())) {
				pageContext.getOut().print(" class='");
				pageContext.getOut().print(getCssClass());
				pageContext.getOut().print("'");	
			}
			if (!Is.emptyString(getCssStyle())) {
				pageContext.getOut().print(" style='");
				pageContext.getOut().print(getCssStyle());
				pageContext.getOut().print("'");	
			}
			pageContext.getOut().print(" title='");
			pageContext.getOut().print(metaAction.getKeystroke() + " - " +  metaAction.getDescription(request));
			pageContext.getOut().print("'");			
			pageContext.getOut().print(" href=\"javascript:openxava.executeAction(");
			pageContext.getOut().print("'");				
			pageContext.getOut().print(request.getParameter("application"));
			pageContext.getOut().print("', '");
			pageContext.getOut().print(request.getParameter("module"));
			pageContext.getOut().print("', ");						
			pageContext.getOut().print("'");
			pageContext.getOut().print(metaAction.getConfirmMessage(request));
			pageContext.getOut().print("'");
			pageContext.getOut().print(", ");			
			pageContext.getOut().print(metaAction.isTakesLong());
			pageContext.getOut().print(", '");
			pageContext.getOut().print(getAction());
			pageContext.getOut().print("'");
			if (!Is.emptyString(getArgv())) {
				pageContext.getOut().print(", '");
				pageContext.getOut().print(getArgv());
				pageContext.getOut().print("'");
			}
			pageContext.getOut().print(")\">");

		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("link_tag_error", getAction()));
		}
		return EVAL_BODY_INCLUDE;
	}

	public int doAfterBody() throws JspException {					
		hasBody = true;
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		try {
			if (!hasBody && !Is.emptyString(getAction())) {
				pageContext.getOut().print(
					MetaControllers.getMetaAction(getAction()).getLabel(
						pageContext.getRequest()));								
			}
			if (!Is.emptyString(getAction())) {
				pageContext.getOut().print("</a>");
			}
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("link_tag_error", getAction()));
		}
		return super.doEndTag();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String string) {
		action = string;
	}

	public String getArgv() {
		return argv;
	}

	public void setArgv(String string) {
		argv = string;		
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}
	
	public String getCssStyle() {
		return cssStyle;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}
	

}