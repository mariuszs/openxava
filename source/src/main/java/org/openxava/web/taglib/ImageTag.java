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

public class ImageTag extends TagSupport implements IActionTag {
	
	private static Log log = LogFactory.getLog(ImageTag.class);
	
	private String action;
	private String argv;
	private String cssClass;
	private String cssStyle;
	
	
	public int doStartTag() throws JspException {
		try {	
			if (Is.emptyString(getAction())) { 
				return SKIP_BODY;
			}
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			MetaAction metaAction = MetaControllers.getMetaAction(getAction());
			String application = request.getParameter("application");
			String module = request.getParameter("module");
			Style style = (Style) request.getAttribute("style");
			pageContext.getOut().print("<input name='");
			pageContext.getOut().print(Ids.decorate(application, module, "action." + getAction())); 
			pageContext.getOut().println("' type='hidden'/>");			
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
			pageContext.getOut().print(application);
			pageContext.getOut().print("', '");
			pageContext.getOut().print(module);
			pageContext.getOut().print("', ");									
			pageContext.getOut().print("'");
			if (!Is.empty(getArgv())) pageContext.getOut().print(metaAction.getConfirmMessage(request, getArgv()));	
			else pageContext.getOut().print(metaAction.getConfirmMessage(request));
			pageContext.getOut().print("'");
			pageContext.getOut().print(", ");			
			pageContext.getOut().print(metaAction.isTakesLong());
			pageContext.getOut().print(", '");
			pageContext.getOut().print(getAction());
			if (!Is.emptyString(getArgv())) {
				pageContext.getOut().print("', '");
				pageContext.getOut().print(getArgv());				
			}
			pageContext.getOut().print("')\">");
			pageContext.getOut().print("<img src='");
			pageContext.getOut().print(request.getContextPath() + "/" + style.getImagesFolder() + "/"+ metaAction.getImage());
			pageContext.getOut().println("'");
			pageContext.getOut().print("\talt='");
			pageContext.getOut().print(metaAction.getKeystroke() + " - " +  metaAction.getDescription(request));
			pageContext.getOut().println("'"); 
			pageContext.getOut().print("\tborder='0' align='absmiddle'/></a>"); 
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("image_tag_error"));				
		}
		return SKIP_BODY;
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

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}

	public String getCssStyle() {
		return cssStyle;
	}

}