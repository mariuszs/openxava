package org.openxava.actions;

import org.openxava.tab.*;
import org.openxava.util.*;

/**
 * Base class for actions that works with Tabs. <p>
 * 
 * It's not needed to inject the <code>xava_tab</code> objects to these actions.
 * These actions obtain the appropriate tab object depend on the current collection
 * or if not a collection from main list mode.<br>
 * 
 * @author Javier Paniza
 */
abstract public class TabBaseAction extends ViewBaseAction {
		
	private Tab tab;	
	private int row = -1;
	
	private String collection;
	private String viewObject;  
	
	/**
	 * Returns the indexes of the selected rows. <p>
	 * 
	 * If row property has value return an array with it as unique value.
	 * This happens when the action has been clicked from the row, 
	 * 
	 * @return
	 */
	protected int [] getSelected() {
		return row<0?getTab().getSelected():new int [] { row };		
	}

	protected Tab getTab() throws XavaException {
		if (tab == null ) {			
			String tabObject = Is.emptyString(getCollection())?"xava_tab":Tab.COLLECTION_PREFIX + Strings.change(getCollection(), ".", "_"); 
			tab = (Tab) getContext().get(getRequest(), tabObject);
			if (tab.getCollectionView() != null) {				
				tab.getCollectionView().refreshCollections(); 				
			}
		}
		return tab;
	}

	public String getCollection() {
		if (collection == null && viewObject != null) {
			this.collection = viewObject.substring("xava_view_".length());									
			while (this.collection.startsWith("section")) {
				this.collection = this.collection.substring(this.collection.indexOf('_') + 1);				
			}
			String objectName = Tab.COLLECTION_PREFIX + Strings.change(collection, ".", "_");
			while (!Is.emptyString(this.collection) && !getContext().exists(getRequest(), objectName)) {
				this.collection = this.collection.substring(this.collection.indexOf('_') + 1);
				objectName = Tab.COLLECTION_PREFIX + Strings.change(collection, ".", "_");
			}
		}
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;		
	}

	/**
	 * Extract from the viewObject the name of the collection. <p>
	 * 
	 * Useful for using Tab actions for collections. <br> 
	 */
	public void setViewObject(String viewObject) {
		this.viewObject = viewObject; 
	}

	/**
	 * This property has value when the action has been clicked from the row. <p>
	 * 
	 * If not its value is -1.
	 */
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
	
}
