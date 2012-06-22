package org.openxava.web.taglib;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class MessageTag extends TagSupport {
	
	private static Log log = LogFactory.getLog(MessageTag.class);
	
	private String key;
	private Object param;
	private int intParam = Integer.MIN_VALUE; // because java 1.4 haven't autoboxing
	
	

	public int doStartTag() throws JspException {
		try {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			String string = null;
			if (getIntParam() > Integer.MIN_VALUE) {
				string = XavaResources.getString(request, getKey(), new Integer(getIntParam())); 
			}
			else if (!Is.empty(getParam())) {
				string = XavaResources.getString(request, getKey(), getParam()); 
			}
			else {
				string = XavaResources.getString(request, getKey()); 
			}			
			pageContext.getOut().print(string);
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new JspException(XavaResources.getString("message_tag_error", getKey()));				
		}
		return SKIP_BODY;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String string) {
		key = string;
	}

	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
	}

	public int getIntParam() {
		return intParam;
	}

	public void setIntParam(int intParam) {
		this.intParam = intParam;
	}

}