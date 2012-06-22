package org.openxava.actions;

/**
 * The default action to execute for search a reference when the
 * user types the value. <p>
 * 
 * @author Javier Paniza
 */
public class OnChangeSearchAction extends OnChangePropertyBaseAction {
	
	public void execute() throws Exception {			
		getView().findObject(getChangedMetaProperty());		
	}

}
