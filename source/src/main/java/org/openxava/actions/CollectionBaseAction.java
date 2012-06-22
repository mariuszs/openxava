package org.openxava.actions;

import java.rmi.*;
import java.util.*;

import javax.ejb.*;

import org.apache.commons.logging.*;
import org.openxava.model.*;
import org.openxava.util.*;

/**
 * Base class for creating actions to be used as list actions.<p>
 * 
 * That is in &lt;list-action/&gt; of &lt;collection-view/&gt; or
 * in @ListAction annotation.<br>
 * 
 * @author Javier Paniza
 */

abstract public class CollectionBaseAction extends CollectionElementViewBaseAction {

	private static Log log = LogFactory.getLog(CollectionBaseAction.class);

	private List mapValues = null;
	private List mapsSelectedValues;
	private List objects;
	private List selectedObjects;
	private int row = -1;  
	

	/**
	 * A list of all collection element when each element is a map 
	 * with the values of the collection element.<p>
	 * 
	 * The values only include the displayed data in the row.<br>
	 * @return  Of type <tt>Map</tt>. Never null.
	 */
	protected List getMapValues() throws XavaException {
		if (mapValues == null) {
			mapValues = getCollectionElementView().getCollectionValues();
		}
		return mapValues;
	}
	
	/**
	 * A list of selected collection element when each element is a map 
	 * with the values of the collection element.<p>
	 * 
	 * If <code>row</code> property has value it returns the resulting list
	 * will contain the value of that row only.<br>
	 * 
	 * The values only include the displayed data in the row.<br>
	 * 
	 * @return  Of type <tt>Map</tt>. Never null.
	 */
	protected List getMapsSelectedValues() throws XavaException { 
		if (mapsSelectedValues == null) {
			if (row >= 0) {
				Map key;
				try {
					if (getCollectionElementView().isCollectionCalculated()) {
						key = (Map) getCollectionElementView().getCollectionValues().get(row);
					}
					else {
						key = (Map) getCollectionElementView().getCollectionTab().getTableModel().getObjectAt(getRow());
					}				
				} 
				catch (Exception ex) {
					log.error(XavaResources.getString("get_row_object_error", row), ex);
					throw new XavaException("get_row_object_error", row); 
				}
				mapsSelectedValues = Collections.singletonList(key);
			}			
			else {
				mapsSelectedValues = getCollectionElementView().getCollectionSelectedValues();
			}
		}
		return mapsSelectedValues;
	}
	
	
	/**
	 * A list of all objects (POJOs or EntityBeans) in the collection.<p>
	 * 
	 * Generally the objects are POJOs, although if you use EJBPersistenceProvider
	 * the they will be EntityBeans (of EJB2).<br> 
	 *  
	 * @return  Never null.
	 */	
	protected List getObjects() throws RemoteException, FinderException, XavaException {
		if (objects == null) {
			objects = getCollectionElementView().getCollectionObjects(); 
		}
		return objects;
	}
	
	/**
	 * A list of selected objects (POJOs or EntityBeans) in the collection.<p>
	 * 
	 * Generally the objects are POJOs, although if you use EJBPersistenceProvider
	 * the they will be EntityBeans (of EJB2).<br>
	 * 
	 * If <code>row</code> property has value it returns the resulting list
	 * will contain the object of that row only.<br>
	 *  
	 * @return  Never null.
	 */	
	protected List getSelectedObjects() throws RemoteException, FinderException, XavaException {
		if (selectedObjects == null) {
			if (row >= 0) {
				Map key;
				try {
					if (getCollectionElementView().isCollectionCalculated()) {
						key = (Map) getCollectionElementView().getCollectionValues().get(row);
					}
					else {
						key = (Map) getCollectionElementView().getCollectionTab().getTableModel().getObjectAt(row);
					}
									 
				} 
				catch (Exception ex) {
					log.error(XavaResources.getString("get_row_object_error", row), ex);
					throw new XavaException("get_row_object_error", row); 
				}
				Object entity = MapFacade.findEntity(getCollectionElementView().getModelName(), key);
				selectedObjects = Collections.singletonList(entity);
			}			
			else {
				selectedObjects = getCollectionElementView().getCollectionSelectedObjects();							
			}
		}
		return selectedObjects;		
	}

	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * This property has value when the action has been clicked from the row. <p>
	 * 
	 * If not its value is -1.
	 */	
	public int getRow() {
		return row;
	}
	
}
