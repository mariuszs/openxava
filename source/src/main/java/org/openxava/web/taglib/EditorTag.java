package org.openxava.web.taglib;

import javax.servlet.*;
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
 * @author Javier Paniza
 */

public class EditorTag extends TagSupport {
	
	private static Log log = LogFactory.getLog(EditorTag.class);
	
	private String property;		
	private boolean editable; 
	private boolean explicitEditable = false; 
	private boolean throwPropertyChanged; 
	private boolean explicitThrowPropertyChanged; 
	
	
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

			ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
									
			String viewObject = request.getParameter("viewObject");				
			viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
			View view = (View) context.get(request, viewObject);

			MetaProperty metaProperty = view.getMetaProperty(property); 

			String propertyPrefix = request.getParameter("propertyPrefix");
			propertyPrefix = propertyPrefix == null?"":propertyPrefix; 
			String application = request.getParameter("application");
			String module = request.getParameter("module");
			String propertyKey = Ids.decorate(application, module, propertyPrefix + property); 
			String valueKey = propertyKey + ".value";
			request.setAttribute(propertyKey, metaProperty);
			Object value = view.getValue(property);
			request.setAttribute(valueKey, value);
									
			Messages errors = (Messages) request.getAttribute("errors"); 													
			boolean throwsChanged=explicitThrowPropertyChanged?this.throwPropertyChanged:view.throwsPropertyChanged(property);
			
			String scriptFocus =  
				" onblur=\"openxava.onBlur(" +
				"'" + application + "'," +
				"'" + module + "'," +
				"'" + propertyKey + "'" +
				")\"" +
				" onfocus=\"openxava.onFocus(" +
				"'" + application + "'," +
				"'" + module + "'," +
				"'" + propertyKey + "'" +
				")\"";  
			
			String script = throwsChanged? 
				" onchange=\"openxava.throwPropertyChanged(" +
				"'" + application + "'," +
				"'" + module + "'," +
				"'" + propertyKey + "'" +
				")\""  
			:
			"";

			script = script + scriptFocus;

			boolean editable = explicitEditable?this.editable:view.isEditable(property);  
			
			String editorURL = org.openxava.web.WebEditors.getUrl(metaProperty, view.getViewName());
			char nexus = editorURL.indexOf('?') < 0?'?':'&';
			String maxSize = "";
			int displaySize = view.getDisplaySizeForProperty(property);
			if (displaySize > -1) {
				maxSize = "maxSize=" + displaySize + "&";
			}
			editorURL = editorURL + nexus + maxSize + "script="+script+"&editable="+editable+"&propertyKey="+propertyKey;			
			
			if (org.openxava.web.WebEditors.mustToFormat(metaProperty, view.getViewName())) {
				Object fvalue = org.openxava.web.WebEditors.formatToStringOrArray(request, metaProperty, value, errors, view.getViewName(), false);
				request.setAttribute(propertyKey + ".fvalue", fvalue); 
			}
						
			String editableKey = propertyKey + "_EDITABLE_";  
			pageContext.getOut().print("<input type='hidden' name='");
			pageContext.getOut().print(editableKey);
			pageContext.getOut().print("' value='");
			pageContext.getOut().print(editable);
			pageContext.getOut().println("'/>");
			if (org.openxava.web.WebEditors.hasMultipleValuesFormatter(metaProperty, view.getViewName())) {
				pageContext.getOut().print("<input type='hidden' name='");
				pageContext.getOut().print(Ids.decorate(application, module, "xava_multiple"));
				pageContext.getOut().print("' value='");
				pageContext.getOut().print(propertyKey);
				pageContext.getOut().println("'/>");				
			}			
			try {
				// If the JSP that uses this tag is in a subfolder
				pageContext.include("../xava/" + editorURL);								
			}
			catch (ServletException ex) {
				Throwable cause = ex.getRootCause() == null?ex:ex.getRootCause(); 
				log.error(cause.getMessage(), cause);
				pageContext.include("../xava/editors/notAvailableEditor.jsp");
			}
			catch (Exception ex) {	
				// If the JSP that uses this tag is in root folder
				try {
					pageContext.include("xava/" + editorURL);
				}
				catch (ServletException ex2) { 	
					log.error(ex2.getRootCause().getMessage(), ex2.getRootCause());
					pageContext.include("xava/editors/notAvailableEditor.jsp");
				}
				catch (Exception ex2) {
					log.error(ex2.getMessage(), ex2);
					pageContext.include("xava/editors/notAvailableEditor.jsp");					
				}		
			}		
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("editor_tag_error", property));
		}	
		return SKIP_BODY;
	}
	

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;		
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		this.explicitEditable = true;
	}

	public boolean isThrowPropertyChanged() {
		return throwPropertyChanged;
	}

	public void setThrowPropertyChanged(boolean throwPropertyChanged) {
		this.throwPropertyChanged = throwPropertyChanged;
		this.explicitThrowPropertyChanged = true;
	}

	
}