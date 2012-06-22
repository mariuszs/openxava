package org.openxava.test.tests;

import org.openxava.model.meta.*;
import org.openxava.test.model.*;

/**
 * @author Javier Paniza
 */

public class CarrierTest extends CarrierTestBase {
	
	public CarrierTest(String testName) {
		super(testName, "Carrier");		
	}	
		
	public void testRowActions() throws Exception {
		execute("List.orderBy", "property=number"); 		
		assertListRowCount(5);
		execute("CRUD.deleteRow", "row=2");
		assertListRowCount(4);
		execute("Mode.detailAndFirst");
		
		assertCollectionRowCount("fellowCarriers", 2);
		assertValueInCollection("fellowCarriers", 0, "name", "DOS");
		assertValueInCollection("fellowCarriers", 1, "name", "CUATRO");
		assertCollectionRowCount("fellowCarriersCalculated", 2);
		assertValueInCollection("fellowCarriersCalculated", 0, "name", "DOS");
		assertValueInCollection("fellowCarriersCalculated", 1, "name", "CUATRO");
				
		execute("Carrier.translateName", "row=0,viewObject=xava_view_fellowCarriers");
		assertValueInCollection("fellowCarriers", 0, "name", "TWO");
		assertValueInCollection("fellowCarriers", 1, "name", "CUATRO");
		
		execute("Carrier.translateName", "row=1,viewObject=xava_view_fellowCarriersCalculated");
		assertValueInCollection("fellowCarriersCalculated", 0, "name", "TWO");
		assertValueInCollection("fellowCarriersCalculated", 1, "name", "FOUR");	
	}
	
	public void testCustomizeCollection() throws Exception {
		// Original status		
		assertListColumnCount(3);
		assertLabelInList(0, "Calculated");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Name");
		execute("Mode.detailAndFirst");
				
		assertCollectionColumnCount("fellowCarriers", 4);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");		
		assertLabelInCollection("fellowCarriers", 2, "Remarks");
		assertLabelInCollection("fellowCarriers", 3, "Calculated");
		
		// Customize the collection
		execute("List.customize", "collection=fellowCarriers");
		execute("List.moveColumnToRight", "columnIndex=2,collection=fellowCarriers");
		assertNoErrors();
		
		assertCollectionColumnCount("fellowCarriers", 4);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Remarks");		
		
		// The main list not modified
		execute("Mode.list");
		assertListColumnCount(3);
		assertLabelInList(0, "Calculated");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Name");

		// The collection continues modified
		execute("Mode.detailAndFirst");
		assertCollectionColumnCount("fellowCarriers", 4);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Remarks");		
		
		// Add columns
		execute("List.addColumns", "collection=fellowCarriers");
		assertCollectionRowCount("xavaPropertiesList", 6);
		execute("AddColumns.sort");				
		assertValueInCollection("xavaPropertiesList",  0, 0, "drivingLicence.description");
		assertValueInCollection("xavaPropertiesList",  0, 1, "Description of Driving licence");
		assertValueInCollection("xavaPropertiesList",  1, 0, "drivingLicence.level");
		assertValueInCollection("xavaPropertiesList",  1, 1, "Level of Driving licence");
		assertValueInCollection("xavaPropertiesList",  2, 0, "drivingLicence.type");
		assertValueInCollection("xavaPropertiesList",  3, 0, "warehouse.name");
		assertValueInCollection("xavaPropertiesList",  4, 0, "warehouse.number");
		assertValueInCollection("xavaPropertiesList",  5, 0, "warehouse.zoneNumber");
		checkRow("selectedProperties", "warehouse.name");		
 		execute("AddColumns.addColumns");

		assertCollectionColumnCount("fellowCarriers", 5);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Remarks");				
		assertLabelInCollection("fellowCarriers", 4, "Name of Warehouse");
 		
		// Other customizations
		execute("List.moveColumnToLeft", "columnIndex=4,collection=fellowCarriers");
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Name of Warehouse");
		assertLabelInCollection("fellowCarriers", 4, "Remarks");
				
		execute("List.removeColumn", "columnIndex=4,collection=fellowCarriers");
		assertCollectionColumnCount("fellowCarriers", 4);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Calculated");
		assertLabelInCollection("fellowCarriers", 3, "Name of Warehouse");
		
		// Restoring		
		execute("List.addColumns", "collection=fellowCarriers");
		execute("AddColumns.restoreDefault");
		assertCollectionColumnCount("fellowCarriers", 4);
		assertLabelInCollection("fellowCarriers", 0, "Number");
		assertLabelInCollection("fellowCarriers", 1, "Name");
		assertLabelInCollection("fellowCarriers", 2, "Remarks");
		assertLabelInCollection("fellowCarriers", 3, "Calculated");
		
		// Cancel in AddColumns returns to detail (not list mode)
		execute("List.addColumns", "collection=fellowCarriers");
		execute("AddColumns.cancel");
		assertValue("name", "UNO"); // In detail mode		
	}
		
	public void testHideShowRows() throws Exception {		
		assertListRowCount(5);
		assertAction("List.hideRows");
		assertNoAction("List.showRows");
		
		execute("List.hideRows");		
		assertListRowCount(0);
		assertNoAction("List.hideRows");
		assertAction("List.showRows");
		
		execute("List.filter");
		assertListRowCount(5);
		assertAction("List.hideRows");
		assertNoAction("List.showRows");

		resetModule();
		assertListRowCount(0);
		assertNoAction("List.hideRows");
		assertAction("List.showRows");

		execute("List.showRows");
		assertListRowCount(5);
		assertAction("List.hideRows");
		assertNoAction("List.showRows");
		
		resetModule();
		assertListRowCount(5);
		assertAction("List.hideRows");
		assertNoAction("List.showRows");	
		
		execute("List.hideRows");
		assertListRowCount(0);
		assertNoAction("List.hideRows");
		assertAction("List.showRows");
		
		customizeList();
		assertListRowCount(0);
		assertNoAction("List.hideRows");
		assertAction("List.showRows");

		execute("List.showRows");
		assertListRowCount(5);
		assertAction("List.hideRows");
		assertNoAction("List.showRows");
	}
	
	private void customizeList() throws Exception { 
		execute("List.customize");
		execute("List.addColumns");
		checkRow("selectedProperties", "drivingLicence.type");
		execute("AddColumns.addColumns");
		
		execute("List.addColumns");
		execute("AddColumns.restoreDefault");		
	}

	public void testJDBCAction() throws Exception {
		assertListRowCount(5); 		
		execute("Carrier.deleteAll");
		assertNoErrors();
		assertListRowCount(0);
	}
	
	public void testResetSelectedOnReturnToList() throws Exception {
		checkRow(3);
		assertRowChecked(3);
		execute("CRUD.new");
		execute("Mode.list");
		assertRowUnchecked(3);
	}
	
	public void testActionOfCalculatedPropertyAlwaysPresent_referenceKeyEditableWhenInGroup() throws Exception {
		execute("CRUD.new");		
		assertAction("Carrier.translateName");
		assertExists("calculated");
		assertNoEditable("calculated");

		assertEditable("warehouse.zoneNumber");
		assertEditable("warehouse.number");
		assertNoEditable("warehouse.name");
	}
	
	public void testFilterIgnoringCase() throws Exception {
		assertListRowCount(5);
		String [] condition = { "", "cinco" };
		setConditionValues(condition);		
		execute("List.filter");		
		assertListRowCount(1);
		assertValueInList(0, "number", "5");
		assertValueInList(0, "name", "Cinco");
	}
	
	public void testPropertyDependsDescriptionsListReference_multipleKeyWithSpaces_descriptionsListLabels_modifyDialog() throws Exception {
		execute("CRUD.new");
		assertLabel("drivingLicence", "Driving licence"); 
		assertValue("remarks","");
		DrivingLicence licence = new DrivingLicence();
		licence.setType("C ");			
		licence.setLevel(2); 
		String key = MetaModel.getForPOJO(licence).toString(licence);
		setValue("drivingLicence.KEY", key);		
		assertNoErrors();
		assertValue("drivingLicence.KEY", key);
		assertValue("remarks", "He can drive trucks");
		
		assertNoDialog();
		execute("Reference.modify", "model=DrivingLicence,keyProperty=drivingLicence__KEY__"); 
		assertNoErrors();
		assertDialog();
		assertValue("description", "CAMIONES GRANDES");
	}
	
	
	public void testOwnControllerForCreatingAndModifyingFromReference() throws Exception {
		execute("Mode.detailAndFirst");		
		// Modifying		
		execute("Reference.modify", "model=Warehouse,keyProperty=warehouse.number");		
		assertNoErrors();		
		assertDialog();
		assertAction("Modification.update");
		assertAction("Modification.cancel");
		assertValue("Warehouse", "name", "MODIFIED WAREHOUSE");		
		execute("Modification.cancel");
		assertNoDialog();
		
		// Creating
		execute("Reference.createNew", "model=Warehouse,keyProperty=warehouse.number");
		assertDialog(); 		
		assertNoErrors();
		assertAction("NewCreation.saveNew");
		assertAction("NewCreation.cancel");
		assertValue("Warehouse", "name", "NEW WAREHOUSE");
		assertNoAction("Mode.list"); 	// Inside a dialog mode actions are disable
		
		execute("NewCreation.cancel");		
		execute("WarehouseReference.createNewNoDialog");
		
		assertNoDialog(); 		
		assertNoErrors();
		assertAction("NewCreation.saveNew");
		assertAction("NewCreation.cancel");
		assertValue("Warehouse", "name", "NEW WAREHOUSE");
		assertNoAction("Mode.list"); 	// When navigate to another view actions are disable		
	}
	
	public void testDeleteUsingBeforeReferenceSearch_dialogLabel() throws Exception {
		assertListNotEmpty();
		execute("Mode.detailAndFirst");
		execute("Reference.search", "keyProperty=xava.Carrier.warehouse.number");
		assertDialog();
		assertDialogTitle("Choose a new value for Warehouse");
		execute("ReferenceSearch.cancel");
		assertNoDialog();
		execute("CRUD.delete");		
		assertNoErrors();
		assertMessage("Carrier deleted successfully");		
	}
	
	public void testGoListModeWithoutRecords() throws Exception {
		execute("Mode.detailAndFirst");
		assertNoErrors();		
		
		deleteCarriers();
		
		execute("Mode.list");				
		execute("Mode.detailAndFirst");
		assertError("Impossible go to detail mode, there are no elements in list");		
	}

	
	public void testDeleteWithoutSelected() throws Exception {
		assertCarriersCount(5);
		execute("List.orderBy", "property=number");
		execute("List.viewDetail", "row=2");		
		assertValue("number", "3");
		assertValue("name", "TRES");
		execute("CRUD.delete");
		assertMessage("Carrier deleted successfully");
		assertNoEditable("number");
		assertEditable("name");				
		assertValue("number", "4");
		assertValue("name", "CUATRO");
		assertCarriersCount(4);
		execute("Navigation.previous");
		assertValue("number", "2");
		assertValue("name", "DOS");
		assertNoErrors();		
		execute("Navigation.previous");
		assertValue("number", "1");
		assertValue("name", "UNO");
		assertNoErrors();		
		execute("CRUD.delete");
		assertMessage("Carrier deleted successfully");
		assertValue("number", "2");
		assertValue("name", "DOS");
		execute("Navigation.next");
		assertValue("number", "4");
		assertValue("name", "CUATRO");
		assertNoErrors();
		execute("Navigation.next");
		assertValue("number", "5");
		assertValue("name", "Cinco");
		assertNoErrors();				
		execute("CRUD.delete");
		assertMessage("Carrier deleted successfully");
		assertValue("number", "4");
		assertValue("name", "CUATRO");
		execute("CRUD.delete");		
		assertMessage("Carrier deleted successfully");
		assertValue("number", "2");
		assertValue("name", "DOS");
		assertCarriersCount(1);
		execute("CRUD.delete");		
		assertMessage("Carrier deleted successfully");
		assertNoErrors(); // If removal is done, any additional error message may be confused		
		assertValue("number", "");
		assertValue("name", "");
		// The last ramain without edit
		assertNoEditable("number");
		assertNoEditable("name");						
		assertCarriersCount(0);
		execute("CRUD.new");
		assertEditable("number");
		assertEditable("name");
	}
	
	
	public void testDeleteWithSelected() throws Exception {
		assertCarriersCount(5);
		checkRow(1); // 2, DOS
		checkRow(2); // 3, TRES
		checkRow(4); // 5, CINCO		
		execute("Mode.detailAndFirst");		
		assertValue("number", "2");
		assertValue("name", "DOS");
		execute("Navigation.next");
		assertValue("number", "3");
		assertValue("name", "TRES");
		assertNoErrors();		
		execute("CRUD.delete");		
		assertMessage("Carrier deleted successfully");
		assertCarriersCount(4);
		assertValue("number", "5");
		assertValue("name", "Cinco");
		assertNoErrors();
		execute("CRUD.delete");		
		assertMessage("Carrier deleted successfully");
		assertCarriersCount(3);
		assertValue("number", "2");
		assertValue("name", "DOS");
		execute("CRUD.delete");		
		assertMessage("Carrier deleted successfully");
		assertValue("number", "");
		assertValue("name", "");
		assertCarriersCount(2);
	}
		
	public void testFilterWithCalculatedValues() throws Exception {
		setConditionValues(new String [] { "3" });
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, "number", "3");
		assertValueInList(0, "name", "TRES");
		setConditionValues(new String [] { "4", "CUA" }); // With 2 arguments
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, "number", "4");
		assertValueInList(0, "name", "CUATRO");		
	}
	
	public void testCollectionWithCondition() throws Exception {
		execute("CRUD.new");
		setValue("number", "1");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("name", "UNO");
		assertCollectionRowCount("fellowCarriers", 3);
		assertValueInCollection("fellowCarriers", 0, "number", "2");
		assertValueInCollection("fellowCarriers", 1, "number", "3");
		assertValueInCollection("fellowCarriers", 2, "number", "4"); 
		setConditionValues("fellowCarriers", new String [] { "3"});
		execute("List.filter", "collection=fellowCarriers");
		assertCollectionRowCount("fellowCarriers", 1);
		assertValueInCollection("fellowCarriers", 0, "number", "3");		
	}
	
	public void testCalculatedCollection() throws Exception {
		execute("CRUD.new");
		setValue("number", "1");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("name", "UNO");
		assertCollectionRowCount("fellowCarriersCalculated", 3);
		assertValueInCollection("fellowCarriersCalculated", 0, "number", "2");
		assertValueInCollection("fellowCarriersCalculated", 0, "name", "DOS");
		assertValueInCollection("fellowCarriersCalculated", 1, "number", "3");
		assertValueInCollection("fellowCarriersCalculated", 1, "name", "TRES");
		assertValueInCollection("fellowCarriersCalculated", 2, "number", "4");
		assertValueInCollection("fellowCarriersCalculated", 2, "name", "CUATRO");
		
		checkRowCollection("fellowCarriersCalculated", 1);
		checkRowCollection("fellowCarriersCalculated", 2);
		execute("Carrier.translateName", "viewObject=xava_view_fellowCarriersCalculated");
		assertNoErrors();
		assertValueInCollection("fellowCarriersCalculated", 0, "name", "DOS");
		assertValueInCollection("fellowCarriersCalculated", 1, "name", "THREE"); 
		assertValueInCollection("fellowCarriersCalculated", 2, "name", "FOUR");		
	}
	
	
	public void testListActionInCollection() throws Exception {		
		execute("CRUD.new");		
		setValue("number", "1");		
		execute("CRUD.refresh");
		assertNoErrors();		

		assertValueInCollection("fellowCarriers", 0, "name", "DOS");
		assertValueInCollection("fellowCarriers", 1, "name", "TRES");
		assertValueInCollection("fellowCarriers", 2, "name", "CUATRO");
		
		execute("Carrier.translateName", "viewObject=xava_view_fellowCarriers");
		assertNoErrors();
		assertValueInCollection("fellowCarriers", 0, "name", "DOS");
		assertValueInCollection("fellowCarriers", 1, "name", "TRES");
		assertValueInCollection("fellowCarriers", 2, "name", "CUATRO");
				
		checkRowCollection("fellowCarriers", 1);
		checkRowCollection("fellowCarriers", 2);
		execute("Carrier.translateName", "viewObject=xava_view_fellowCarriers");
		assertNoErrors();
		assertValueInCollection("fellowCarriers", 0, "name", "DOS");
		assertValueInCollection("fellowCarriers", 1, "name", "THREE");
		assertValueInCollection("fellowCarriers", 2, "name", "FOUR");
		
		// Testing add/remove list actions programatically
		assertAction("Carrier.allToEnglish");
		assertNoAction("Carrier.todosAEspanol");		
		execute("Carrier.allToEnglish", "viewObject=xava_view_fellowCarriers");
		assertNoAction("Carrier.allToEnglish");
		assertAction("Carrier.todosAEspanol");
		
		// After ordering
		assertValueInCollection("fellowCarriers", 0, "name", "TWO");
		execute("List.orderBy", "property=number,collection=fellowCarriers"); // Ascending
		execute("List.orderBy", "property=number,collection=fellowCarriers"); // Descending
		assertValueInCollection("fellowCarriers", 0, "name", "FOUR");
		checkRowCollection("fellowCarriers", 0);
		execute("Carrier.translateName", "viewObject=xava_view_fellowCarriers");
		assertValueInCollection("fellowCarriers", 0, "name", "CUATRO");		
	}
	
	private void assertCarriersCount(int c) throws Exception {
		int carrierCount = Carrier.findAll().size(); 
		assertEquals("Carriers count",c,carrierCount);
	}
	
	
}
