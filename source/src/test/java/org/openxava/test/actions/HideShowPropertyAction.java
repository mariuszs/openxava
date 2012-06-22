package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */

public class HideShowPropertyAction extends ViewBaseAction {

	private boolean hide;
	private String property;

	public void execute() throws Exception {
		getView().setHidden(property, hide);		
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean b) {
		hide = b;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String string) {
		property = string;
	}

}
