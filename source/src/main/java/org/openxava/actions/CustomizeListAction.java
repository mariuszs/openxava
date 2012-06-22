package org.openxava.actions;

/**
 * @author Javier Paniza
 */
public class CustomizeListAction extends TabBaseAction {
	
	public void execute() throws Exception {
		getTab().setCustomize(!getTab().isCustomize());
	}

}
