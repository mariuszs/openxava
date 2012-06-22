package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class FamilyWithInheritanceControllerTest extends ModuleTestBase {

	private String [] detailActions = {
		"Navigation.previous",
		"Navigation.first",
		"Navigation.next",
		"Family.new",
		"CRUD.save",
		"CRUD.delete",
		"CRUD.search",
		"CRUD.refresh",
		"HideShowCRUDActions.hideDelete",
		"HideShowCRUDActions.showDelete",
		"HideShowCRUDActions.hideSaveDelete",
		"HideShowCRUDActions.showSaveDelete",
		"AddRemoveCRUDActions.addDelete",
		"AddRemoveCRUDActions.removeDelete",
		"AddRemoveCRUDActions.addSaveDelete",
		"AddRemoveCRUDActions.removeSaveDelete",		
		"Mode.list",
		"Mode.split"
	};
	
	
	private String [] listActions = {
		"Print.generatePdf",
		"Print.generateExcel",
		"Family.new",
		"CRUD.deleteSelected",
		"CRUD.deleteRow",
		"Mode.detailAndFirst",
		"Mode.split",
		"List.filter",
		"List.customize",
		"List.orderBy",
		"List.viewDetail",
		"List.hideRows",
		"List.sumColumn" 
	};
	
	private String [] actionsWithoutDelete = {
		"Navigation.previous",
		"Navigation.first",
		"Navigation.next",
		"Family.new",
		"CRUD.save",
		"CRUD.search",
		"CRUD.refresh",
		"HideShowCRUDActions.hideDelete",
		"HideShowCRUDActions.showDelete",
		"HideShowCRUDActions.hideSaveDelete",
		"HideShowCRUDActions.showSaveDelete",
		"AddRemoveCRUDActions.addDelete",
		"AddRemoveCRUDActions.removeDelete",
		"AddRemoveCRUDActions.addSaveDelete",
		"AddRemoveCRUDActions.removeSaveDelete",		
		"Mode.list",
		"Mode.split"
	};
	
	private String [] actionsWithoutSaveDelete = {
		"Navigation.previous",
		"Navigation.first",
		"Navigation.next",
		"Family.new",
		"CRUD.search",
		"CRUD.refresh",
		"HideShowCRUDActions.hideDelete",
		"HideShowCRUDActions.showDelete",
		"HideShowCRUDActions.hideSaveDelete",
		"HideShowCRUDActions.showSaveDelete",
		"AddRemoveCRUDActions.addDelete",
		"AddRemoveCRUDActions.removeDelete",
		"AddRemoveCRUDActions.addSaveDelete",
		"AddRemoveCRUDActions.removeSaveDelete",		
		"Mode.list",					
		"Mode.split"
	};
	
	public FamilyWithInheritanceControllerTest(String testName) {
		super(testName, "FamilyWithInheritanceController");		
	}
	
	public void testOverwriteAction() throws Exception {
		assertActions(listActions);
		execute("Family.new");
		assertActions(detailActions);		
		assertValue("number", "99");
		assertValue("description", "NOVA FAMILIA");
	}
	
	public void testHideShowActions() throws Exception {
		assertActions(listActions);
		execute("Family.new");
		assertActions(detailActions);		
		execute("HideShowCRUDActions.hideDelete");
		assertActions(actionsWithoutDelete);
		execute("HideShowCRUDActions.showDelete");
		assertActions(detailActions);	
		execute("HideShowCRUDActions.hideSaveDelete");
		assertActions(actionsWithoutSaveDelete);
		execute("HideShowCRUDActions.showSaveDelete");
		assertActions(detailActions);
	}
	
	public void testAddRemoveActions() throws Exception { 
		assertActions(listActions);
		execute("Family.new");
		assertActions(detailActions);		
		execute("AddRemoveCRUDActions.removeDelete");
		assertActions(actionsWithoutDelete);
		execute("AddRemoveCRUDActions.addDelete");
		assertActions(detailActions);	
		execute("AddRemoveCRUDActions.removeSaveDelete");
		assertActions(actionsWithoutSaveDelete);
		execute("AddRemoveCRUDActions.addSaveDelete");
		assertActions(detailActions);
	}
	
					
}
