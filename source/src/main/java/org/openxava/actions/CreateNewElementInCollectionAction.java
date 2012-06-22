package org.openxava.actions;

import java.util.Iterator;

import org.openxava.util.XavaPreferences;


/**
 * Create a new element in collection. <p>
 * 
 * Since 4m5 creates two buttons for the save action, 
 * ones that closes the dialog and the other that stays.
 * 
 * @author Javier Paniza
 * 
 */

public class CreateNewElementInCollectionAction extends CollectionElementViewBaseAction {
	
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {				
		if (getCollectionElementView().isRepresentsAggregate()) {		
			getCollectionElementView().reset();						
		}
		getCollectionElementView().setCollectionDetailVisible(true); 
		getCollectionElementView().setCollectionEditingRow(-1);
		showDialog(getCollectionElementView());				
		if (getCollectionElementView().isCollectionEditable() || 
			getCollectionElementView().isCollectionMembersEditables()) 
		{ 
			// The Collection.saveAndStay will function as trapper for the save action,
			// and will prevent the dialog to close while clearing the form and filling with default values.
			addActions(getCollectionElementView().getSaveCollectionElementAction());
			
			if(XavaPreferences.getInstance().isSaveAndStayForCollections()){
				addActions("Collection.saveAndStay");
			}
		} 		
		Iterator itDetailActions = getCollectionElementView().getActionsNamesDetail().iterator();		
		while (itDetailActions.hasNext()) {			
			addActions(itDetailActions.next().toString());			
		}
		addActions(getCollectionElementView().getHideCollectionElementAction());
	}
	
}
