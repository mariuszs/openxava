package org.openxava.actions;

import javax.inject.*;

import org.openxava.tab.*;

/**
 * 
 * @author Javier Paniza
 */

public class GoAddColumnsPageAction extends BaseAction {
	
	@Inject @Named("customizingTab") 
	private Tab tab;
	private int increment;
	private int page;
	
	public void execute() throws Exception {
		if (page > 0) tab.goAddColumnsPage(page);
		else {
			tab.goAddColumnsPage(tab.getAddColumnsPage() + increment);			
		}
	}
	
	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}
	
	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

}
