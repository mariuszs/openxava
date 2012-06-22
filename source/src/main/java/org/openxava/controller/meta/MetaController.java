package org.openxava.controller.meta;


import java.util.*;



import org.openxava.util.*;
import org.openxava.util.meta.*;


public class MetaController extends MetaElement {

	private String className; // Only for spanish/swing version
	private Collection metaActions = new ArrayList();
	private Collection parentsNames = new ArrayList();
	private Collection parents = new ArrayList();
	private Map mapMetaActions = new HashMap();
	
		
	/**
	 * Only for spanish/swing version
	 */
	public String getClassName() {
		return Is.emptyString(className)?"puntocom.xava.xcontrolador.tipicos.ControladorVacio":className;
	}
	/**
	 * Only for spanish/swing version
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	public void addMetaAction(MetaAction action) {
		metaActions.add(action);
		action.setMetaController(this);
		mapMetaActions.put(action.getName(), action);
	}
	
	public void addParentName(String parentName) {
		if (parentsNames == null) parentsNames = new ArrayList();
		parentsNames.add(parentName);  	
		parents = null;	
	}
	
	public MetaAction getMetaAction(String name) throws ElementNotFoundException {
		MetaAction a = (MetaAction) mapMetaActions.get(name);
		if (a == null) throw new ElementNotFoundException("action_not_found", name, getName());		
		return a; 
	}
	
	/**
	 * @return Not null, of type <tt>MetaAction</tt> and read only.
	 */
	
	public Collection getMetaActions() {
		return Collections.unmodifiableCollection(metaActions);
	}
	
	public boolean containsMetaAction(String actionName) {
		return metaActions.contains(new MetaAction(actionName));
	}	

	// Search by name, so ignoring the controller name
	private int indexOf(List<MetaAction> actions, MetaAction metaAction) { 
		for (int i=0; i<actions.size(); i++) {
			if (actions.get(i).getName().equals(metaAction.getName())) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * The MetaActions of this controller and all its parents. <p>
	 */
	public Collection getAllMetaActions() throws XavaException { 
		return getAllMetaActions(false);
	}
	
	/**
	 * The not hidden MetaActions of this controller and all its parents. <p>
	 */
	public Collection getAllNotHiddenMetaActions() throws XavaException {  
		return getAllMetaActions(true);
	}
	
	private Collection getAllMetaActions(boolean excludeHidden) throws XavaException {  
		List result = new ArrayList();
		// Adding parents
		Iterator itParents = getParents().iterator();
		while (itParents.hasNext()) {
			MetaController parent = (MetaController) itParents.next();
			result.addAll(parent.getAllMetaActions(excludeHidden));
		}
				
		// and now ours 
		Iterator it = metaActions.iterator();
		while (it.hasNext()) {
			MetaAction metaAction = (MetaAction) it.next();
			if (excludeHidden && metaAction.isHidden()) continue;
			int pos = indexOf(result, metaAction); 
			if (pos < 0) result.add(metaAction);
			else {
				result.remove(pos);
				result.add(pos, metaAction);
			} 			
		}		
		return result;
	}
	
		
	
	public String getId() {
		return getName();
	}
	
	/**
	 * Fathers actions are included. <p> 
	 */
	public Collection getMetaActionsOnInit() throws XavaException {
		Collection result = new ArrayList();
		
		// Adding parents
		Iterator itParents = getParents().iterator();
		while (itParents.hasNext()) {
			MetaController parent = (MetaController) itParents.next();
			result.addAll(parent.getMetaActionsOnInit());
		}

		// Ours
		Iterator it = metaActions.iterator();		
		while (it.hasNext()) {
			MetaAction metaAction = (MetaAction) it.next();			
			if (metaAction.isOnInit()) {
				result.add(metaAction);
			}
		}
		return result;
	}
	public boolean hasParents() {
		return parentsNames != null;		 	
	}		
	
	/**
	 * @return of type MetaController
	 */
	public Collection getParents() throws XavaException {
		if (!hasParents()) return Collections.EMPTY_LIST;
		if (parents == null) {
			parents = new ArrayList();
			Iterator it = parentsNames.iterator();
			while (it.hasNext()) {
				String name = (String) it.next();
				parents.add(MetaControllers.getMetaController(name));
			}
		}
		return parents;
	}
	
	public Collection getMetaActionsOnEachRequest() throws XavaException {
		Collection result = new ArrayList();

		Iterator itParents = getParents().iterator();
		while (itParents.hasNext()) {
			MetaController parent = (MetaController) itParents.next();
			result.addAll(parent.getMetaActionsOnEachRequest());
		}

		Iterator it = metaActions.iterator();		
		while (it.hasNext()) {
			MetaAction metaAction = (MetaAction) it.next();			
			if (metaAction.isOnEachRequest()) {
				result.add(metaAction);
			}
		}
		return result;
	}
	public Collection getMetaActionsAfterEachRequest() throws XavaException { 
		Collection result = new ArrayList();

		Iterator itParents = getParents().iterator();
		while (itParents.hasNext()) {
			MetaController parent = (MetaController) itParents.next();
			result.addAll(parent.getMetaActionsAfterEachRequest());
		}

		Iterator it = metaActions.iterator();		
		while (it.hasNext()) {
			MetaAction metaAction = (MetaAction) it.next();			
			if (metaAction.isAfterEachRequest()) {
				result.add(metaAction);
			}
		}
		return result;
	}
	
	public Collection getMetaActionsBeforeEachRequest() throws XavaException { 
		Collection result = new ArrayList();

		Iterator itParents = getParents().iterator();
		while (itParents.hasNext()) {
			MetaController parent = (MetaController) itParents.next();
			result.addAll(parent.getMetaActionsBeforeEachRequest());
		}

		Iterator it = metaActions.iterator();		
		while (it.hasNext()) {
			MetaAction metaAction = (MetaAction) it.next();			
			if (metaAction.isBeforeEachRequest()) {
				result.add(metaAction);
			}
		}
		return result;
	}	
	
}


