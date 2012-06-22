package org.openxava.actions;

/**
 * @author Javier Paniza
 */

public class GoNextPageAction extends TabBaseAction {
	
	public void execute() throws Exception {
		getTab().pageForward();
		getTab().setNotResetNextTime(true);		
	}

}
