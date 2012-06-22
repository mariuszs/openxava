package org.openxava.actions;

import java.util.Arrays;
import javax.inject.*;
import org.openxava.tab.Tab;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class AddColumnsAction extends BaseAction implements INavigationAction, IChangeModeAction {
	
	@Inject @Named("xava_customizingTab")
	private Tab tab;
	
	
	public void execute() throws Exception {
		if (!XavaPreferences.getInstance().isCustomizeList()) return; 
		String [] values = getRequest().getParameterValues("selectedProperties");
		getTab().addProperties(Arrays.asList(values));		
	}

	public Tab getTab() {
		return tab;
	}
	public void setTab(Tab tab) {
		this.tab = tab;
	}

	public String[] getNextControllers() throws Exception {
		return PREVIOUS_CONTROLLERS;
	}

	public String getCustomView() throws Exception {
		return PREVIOUS_VIEW;
	}

	public String getNextMode() {
		return PREVIOUS_MODE;
	}
}
