package org.openxava.actions;


/**
 * @author Javier Paniza
 */

public class GoPageAction extends TabBaseAction {
	
	private int page;
	
	public void execute() throws Exception {
		getTab().goPage(page);
		getTab().setNotResetNextTime(true);		
	}

	public int getPage() {
		return page;
	}

	public void setPage(int i) {
		page = i;
	}

}
