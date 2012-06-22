package org.openxava.actions;


/**
 * To save a collection element. <p>
 * 
 * The case of collections of entities with @AsEmbedded (or with as-aggregate="true")
 * is treated by {@link AddElementsToCollectionAction}. <p>
 * 
 * @author Javier Paniza
 */

public class SaveAndStayElementInCollectionAction extends CollectionElementViewBaseAction implements IChainAction, IChainActionWithArgv {
	
	public void execute() throws Exception {
	}

	public String getNextAction() throws Exception {
		return (getCollectionElementView().getSaveCollectionElementAction());
	}

	public String getNextActionArgv() throws Exception {
		return "closeDialogDisallowed=true";
	}
		
}
