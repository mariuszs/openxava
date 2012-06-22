package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class HideShowGroupAction extends ViewBaseAction {
	
	private boolean hide;
	private boolean show;
	private String group;

	public void execute() throws Exception {
		if (hide && show) {
			throw new IllegalStateException("It can not set the 2 options: 'hide' and 'show'");
		}
		if (!hide && !show) {
			throw new IllegalStateException("It must ot set some option: 'hide' o 'show'");			
		}
		if (Is.emptyString(group)) {
			throw new IllegalStateException("It si required to specify the 'group'");
		}
		getView().setHidden(group, hide);
	}

	public boolean isShow() {
		return show;
	}
	public void setShow(boolean b) {
		show = b;
	}

	public boolean isHide() {
		return hide;
	}	
	public void setHide(boolean b) {
		hide = b;
	}
	
	public String getGroup() {
		return group;
	}
	public void setGroup(String string) {
		group = string;
	}

}
