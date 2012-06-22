package org.openxava.actions;

import javax.inject.*;

import org.openxava.controller.*;
import org.openxava.tab.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class GoAddColumnsAction extends BaseAction implements INavigationAction , IChangeModeAction, IModuleContextAction {
	
	private String collection;
	@Inject
	private Tab customizingTab;
	private ModuleContext context; 
	
	public void execute() throws Exception {
		String objectName =  Is.emptyString(collection)?"xava_tab":Tab.COLLECTION_PREFIX + collection;
		setCustomizingTab((Tab) context.get(getRequest(), objectName));		
	}

	public String[] getNextControllers() throws Exception {
		return new String [] { getNextController() };
	}

	public String getCustomView() throws Exception {
		return "xava/addColumns"; 		
	}
	
	public String getNextController() {
		return "AddColumns";
	}
	
	public String getNextMode() {
		return DETAIL;		
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public Tab getCustomizingTab() {
		return customizingTab;
	}

	public void setCustomizingTab(Tab customizingTab) {
		this.customizingTab = customizingTab;
	}

	public void setContext(ModuleContext context) {
		this.context = context;		
	}
	
}
