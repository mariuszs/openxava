package org.openxava.actions;

import java.rmi.*;
import java.util.*;

import javax.ejb.*;

import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.validators.*;
import org.openxava.view.*;

/**
 * To save a collection element. <p>
 * 
 * The case of collections of entities with @AsEmbedded (or with as-aggregate="true")
 * is treated by {@link AddElementsToCollectionAction}. <p>
 * 
 * @author Javier Paniza
 */

public class SaveElementInCollectionAction extends CollectionElementViewBaseAction {
	
	private boolean containerSaved = false;
	
	public void execute() throws Exception {		
		Map containerKey = saveIfNotExists(getCollectionElementView().getParent());
		if (XavaPreferences.getInstance().isMapFacadeAutoCommit()) {
			getView().setKeyEditable(false); // To mark as saved
		}
		if (isEntityReferencesCollection()) saveEntity(containerKey);
		else saveAggregate(containerKey);		
		getView().setKeyEditable(false); // To mark as saved
		getCollectionElementView().setCollectionEditingRow(-1);
		getCollectionElementView().clear();
		resetDescriptionsCache();
		if (containerSaved) getView().getRoot().refresh(); 
		else getView().recalculateProperties();
		closeDialog(); 		
	}
	
	
	protected Map getValuesToSave() throws Exception {
		return getCollectionElementView().getValues();		
	}
	
	private void validateMaximum() throws ValidationException, XavaException {
		MetaCollection metaCollection = getMetaCollection();
		int maximum = metaCollection.getMaximum(); 
		if (maximum > 0) {
			if (getCollectionElementView().getCollectionSize() >= maximum) {
				Messages errors = new Messages();
				errors.add("maximum_elements", new Integer(maximum), metaCollection.getName(), metaCollection.getMetaModel().getName());
				throw new ValidationException(errors);
			}
		}		
	}

	private void saveEntity(Map containerKey) throws Exception {
		if (getCollectionElementView().isEditable()) {
			// Entity reference used as aggregate
			Map parentKey = new HashMap();
			MetaCollection metaCollection = getMetaCollection();
			parentKey.put(metaCollection.getMetaReference().getRole(), containerKey);
			Map values = getValuesToSave();			
			values.putAll(parentKey);
			try {
				validateMaximum();
				MapFacade.create(getCollectionElementView().getModelName(), values);
				addMessage("entity_created_and_associated", getCollectionElementView().getModelName(), getCollectionElementView().getParent().getModelName()); 				
			}
			catch (DuplicateKeyException ex) {
				MapFacade.setValues(getCollectionElementView().getModelName(), getCollectionElementView().getKeyValues(), values);
				addMessage("entity_modified", getCollectionElementView().getModelName());				
			}	
		}
		else {
			// Entity reference used in the standard way
			associateEntity(getCollectionElementView().getKeyValues());
			addMessage("entity_associated" , getCollectionElementView().getModelName(), getCollectionElementView().getParent().getModelName());
		}
	}

	protected void associateEntity(Map keyValues) throws ValidationException, XavaException, ObjectNotFoundException, FinderException, RemoteException {		
		validateMaximum();
		MapFacade.addCollectionElement(
				getCollectionElementView().getParent().getMetaModel().getName(),
				getCollectionElementView().getParent().getKeyValues(),
				getCollectionElementView().getMemberName(),  
				keyValues);		
	}

	private MetaCollection getMetaCollection() throws ElementNotFoundException, XavaException {
		return getCollectionElementView().getParent().getMetaModel().getMetaCollection(getCollectionElementView().getMemberName());
	}

	private void saveAggregate(Map containerKey) throws Exception{
		if (getCollectionElementView().getKeyValuesWithValue().isEmpty()) {
			createAggregate(containerKey);			
		}
		else {
			try {				
				MapFacade.setValues(getCollectionElementView().getModelName(), getCollectionElementView().getKeyValues(), getValuesToSave());
				addMessage("aggregate_modified", getCollectionElementView().getModelName());
			}
			catch (ObjectNotFoundException ex) {
				// In case not hidden primary key in aggregate
				createAggregate(containerKey);								
			}
		}								
	}
	
	private void createAggregate(Map containerKey) throws Exception {
		validateMaximum();
		int row = 0;
		if (!getMetaCollection().getMetaModel().isAnnotatedEJB3()) {
			if (!getCollectionElementView().isCollectionCalculated()) {
				getCollectionElementView().getCollectionTab().reset();
			}
			row = getCollectionElementView().getCollectionSize();
		}
		MapFacade.createAggregate(
			getCollectionElementView().getModelName(),						
			containerKey, row+1, // +1 for start in 1, because 0 is equals to no value					
			getValuesToSave() ); 
		addMessage("aggregate_created", getCollectionElementView().getModelName());
	}
	
	private boolean isAutoCommit() {
		return XavaPreferences.getInstance().isMapFacadeAutoCommit() || XavaPreferences.getInstance().isMapFacadeAsEJB();
	}

	/**
	 * @return The saved object 
	 */
	protected Map saveIfNotExists(View view) throws Exception {
		if (getView() == view) {
			if (view.isKeyEditable()) {				
				Map key = MapFacade.createNotValidatingCollections(getModelName(), view.getValues());
				addMessage("entity_created", getModelName());
				view.addValues(key);
				containerSaved=true;				
				return key;								
			}			
			else {										
				return view.getKeyValues();									
			}
		}			
		else {
			if (view.getKeyValuesWithValue().isEmpty()) {				
				Map parentKey = saveIfNotExists(view.getParent());
				Map key = MapFacade.createAggregateReturningKey( 
					view.getModelName(),
					parentKey, 0,					
					view.getValues() );
				addMessage("aggregate_created", view.getModelName());
				view.addValues(key);
				return key;										
			}
			else {			
				return view.getKeyValues();
			}
		}
	}

	public String getNextAction() throws Exception { 		
		return getErrors().contains()?null:getCollectionElementView().getNewCollectionElementAction();
	}
		
}
