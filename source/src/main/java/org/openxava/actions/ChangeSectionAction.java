package org.openxava.actions;

import javax.servlet.http.*;



import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * @author Javier Paniza
 */

public class ChangeSectionAction extends BaseAction implements IModuleContextAction /* tmp , IRequestAction */ {
	
	private int activeSection;
	private String viewObject;
	private ModuleContext context;
	// tmp transient private HttpServletRequest request; // tmp 
	
	
	public void execute() throws Exception {
		getView().setActiveSection(getActiveSection());
	}

	public int getActiveSection() {
		return activeSection;
	}

	public void setActiveSection(int i) {
		activeSection = i;
	}
	
	public void setContext(ModuleContext context) {
		this.context = context;
	}
	
	/* tmp
	public void setRequest(HttpServletRequest request) {
		super.setRequest(request);
		this.request = request;
	}
	*/
	
	private View getView() throws XavaException {
		return (View) context.get(getRequest(), viewObject==null?"xava_view":viewObject);
	}

	public String getViewObject() {
		return viewObject;
	}

	public void setViewObject(String viewObject) {
		this.viewObject = viewObject;
	}
	
}
