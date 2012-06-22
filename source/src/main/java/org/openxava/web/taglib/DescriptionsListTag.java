package org.openxava.web.taglib;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;


/**
 * 
 * @author Javier Paniza
 */

public class DescriptionsListTag extends TagSupport {
	
	private static Log log = LogFactory.getLog(DescriptionsListTag.class);
	private String reference;
	
	public int doStartTag() throws JspException {
		try {			
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");			
									
			String viewObject = request.getParameter("viewObject");
			viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
			View view = (View) context.get(request, viewObject);

			MetaReference metaReference = view.getMetaReference(reference);
			String prefix = request.getParameter("propertyPrefix");
			prefix = prefix == null?"":prefix;
			String application = request.getParameter("application");
			String module = request.getParameter("module");
			String referenceKey = Ids.decorate(application, module, prefix + reference); 
			request.setAttribute(referenceKey, metaReference);
			String editorURL = "reference.jsp?referenceKey=" + referenceKey + "&onlyEditor=true&frame=false&composite=false&descriptionsList=true"; 
			try {
				// If the JSP that uses this tag is in a subfolder
				pageContext.include("../xava/" + editorURL);
			}
			catch (Exception ex) {
				// If the JSP that uses this tag is in root folder
				pageContext.include("xava/" + editorURL);
			}						
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("descriptionsList_tag_error", reference));
		}	
		return SKIP_BODY;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String property) {
		this.reference = property;		
	}
	
}