package org.openxava.actions;

/**
 * 
 * @author Javier Paniza
 */

public class FilterAction extends TabBaseAction {
	
	public void execute() throws Exception {
		getTab().setRowsHidden(false);
		getTab().goPage(1); 
	}

}
