package org.openxava.actions;

import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * 
 * @author Javier Paniza
 */

abstract public class CollectionElementViewBaseAction extends ViewBaseAction { 
	

	
	private View collectionElementView;		
	private String viewObject;
	private boolean closeDialogDisallowed = false;
	private boolean dialogOpened = false; 

	abstract public void execute() throws Exception;
	
	public View getView() { 
		if (viewObject != null && !dialogOpened) return super.getView();		
		return getCollectionElementView().getRoot();		
	}
	
	protected boolean mustRefreshCollection() { 
		return true;
	}
		
	protected void showDialog(View viewToShowInDialog) throws Exception {
		super.showDialog(viewToShowInDialog);
		dialogOpened = true;
		collectionElementView = null; 
	}
		
	/** @since 4m5 */
	protected View getParentView() throws XavaException {
		return getCollectionElementView().getParent();
	}
	
	protected View getCollectionElementView() throws XavaException {
		if (collectionElementView == null) {
			if (viewObject == null || dialogOpened) collectionElementView = super.getView(); // In a dialog
			else {
				collectionElementView = (View) getContext().get(getRequest(), viewObject);
			}
			if (mustRefreshCollection()) collectionElementView.refreshCollections(); 			
		}
		return collectionElementView;
	}
		
	protected boolean isEntityReferencesCollection() throws XavaException {
		return getCollectionElementView().getMetaModel() instanceof MetaEntity;		
	}
	
	public String getViewObject() {
		return viewObject;
	}

	public void setViewObject(String viewObject) {
		this.viewObject = viewObject;
	}

	@Override
	protected void closeDialog() {  
		if (isCloseDialogDisallowed()) {
			getCollectionElementView().reset();
		} else {
			super.closeDialog();
			dialogOpened = false;
			collectionElementView = null; 
		}
	}	
	
	public void setCloseDialogDisallowed(boolean closeDialogDisallowed) {
		this.closeDialogDisallowed = closeDialogDisallowed;
	}

	public boolean isCloseDialogDisallowed() {
		return closeDialogDisallowed;
	}
	
}
