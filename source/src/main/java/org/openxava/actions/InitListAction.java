package org.openxava.actions;

import javax.inject.*;

import org.openxava.tab.*;

/**
 * @author Javier Paniza
 */
public class InitListAction extends TabBaseAction {
	
	@Inject
	private Tab mainTab;

	public void execute() throws Exception {
		setMainTab(getTab());
	}

	public Tab getMainTab() {
		return mainTab;
	}
	public void setMainTab(Tab mainTab) {
		this.mainTab = mainTab;
	}
	
}
