package org.openxava.test.tests;

import java.rmi.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;


/**
 * 
 * @author Javier Paniza
 */

public class SellerTest extends ModuleTestBase {
		
	private Customer customer2;
	private Customer customer1;

	public SellerTest(String testName) {
		super(testName, "Seller");		
	}
	
	public void testNotRemoveRowWhenAddingCollectionElements_addingWithNoElements() throws Exception { 
		execute("Mode.detailAndFirst");
		execute("Collection.add", "viewObject=xava_view_customers");
		
		// NotRemoveRowWhenAddingCollectionElements
		assertAction("AddToCollection.add");
		assertNoAction("Collection.removeSelected");
		
		// AddingWithNoElements
		execute("AddToCollection.add");
		assertDialog();
		assertAction("AddToCollection.add");
		assertError("Please, choose an element before pressing 'Add'");
	}
	
	public void testRowStyleInCollections() throws Exception {		
		execute("Mode.detailAndFirst");
		assertValue("number", "1");
		assertValue("name", "MANUEL CHAVARRI");
				
		int c = getCollectionRowCount("customers");
		boolean found = false;		
		for (int i=0; i<c; i++) {
			String type = getValueInCollection("customers", i, "type");			
			if ("Steady".equals(type)) {				
				assertRowStyleInCollection("customers", i, "row-highlight");				
				found = true;
			}
			else {
				assertNoRowStyleInCollection("customers", i);				
			}						
		}
		if (!found) {
			fail("It is required at least one Customer of 'Steady' type for run this test");
		}
	}

	
	public void testListFeaturesInCollection() throws Exception {
		// The correct elements
		execute("List.viewDetail", "row=1");
		assertValue("number", "2");
		assertValue("name", "JUANVI LLAVADOR");
		assertCollectionRowCount("customers", 1);		
		execute("Navigation.previous");
		assertValue("number", "1");
		assertValue("name", "MANUEL CHAVARRI");
		assertCollectionRowCount("customers", 2);
		
		// The properties in list
		assertLabelInCollection("customers", 0, "Number");
		assertLabelInCollection("customers", 1, "Name");
		assertLabelInCollection("customers", 2, "Remarks");
		assertLabelInCollection("customers", 3, "Relation with seller");
		assertLabelInCollection("customers", 4, "Seller level");
		
		// The values in collection
		assertValueInCollection("customers", 0, "number", "1");
		assertValueInCollection("customers", 0, "name", "Javi");
		assertValueInCollection("customers", 0, "remarks", "");
		assertValueInCollection("customers", 0, "relationWithSeller", "BUENA");
		assertValueInCollection("customers", 0, "seller.level.description", "MANAGER");
		
		assertValueInCollection("customers", 1, "number", "2");
		assertValueInCollection("customers", 1, "name", "Juanillo");
		assertValueInCollection("customers", 1, "remarks", "");
		assertValueInCollection("customers", 1, "relationWithSeller", "");
		assertValueInCollection("customers", 1, "seller.level.description", "MANAGER");
		
		// Order by column
		execute("List.orderBy", "property=number,collection=customers");
		assertValueInCollection("customers", 0, "number", "1");
		assertValueInCollection("customers", 1, "number", "2");
		execute("List.orderBy", "property=number,collection=customers");
		assertValueInCollection("customers", 0, "number", "2");
		assertValueInCollection("customers", 1, "number", "1");
		
		// Hide rows is not available for collection since 2.2.5
		/*
		assertCollectionRowCount("customers", 2);
		execute("List.hideRows", "collection=customers");
		assertCollectionRowCount("customers", 0);
		*/
		
		// Filter  
		String [] condition = { "1" }; 
		setConditionValues("customers", condition);		
		execute("List.filter", "collection=customers");		
		assertCollectionRowCount("customers", 1);		
		assertValueInCollection("customers", 0, "number", "1");
		assertValueInCollection("customers", 0, "name", "Javi");
		
		// Hide/Show rows are not available for collection since 2.2.5
		/*
		execute("List.hideRows", "collection=customers");
		assertCollectionRowCount("customers", 0);
		execute("List.showRows", "collection=customers");
		assertCollectionRowCount("customers", 1);
		*/
	}
	
	public void testMembersOfReferenceToEntityNotEditable() throws Exception {
		execute("Mode.detailAndFirst");
		execute("Collection.view", "row=0,viewObject=xava_view_customers"); 
		assertNoEditable("number"); 
		assertNoEditable("name");
	}
	
	public void testOverwriteCollectionControllers_defaultListActionsForCollections_tabActionsForCollections() throws Exception { 
		execute("CRUD.new");
		setValue("number", "1");
		execute("CRUD.refresh");
		assertValue("name", "MANUEL CHAVARRI");
		execute("Collection.view", "row=0,viewObject=xava_view_customers");
		execute("Collection.hideDetail");
		assertMessage("Detail is hidden");
		
		execute("Print.generatePdf", "viewObject=xava_view_customers");
		assertContentTypeForPopup("application/pdf");
		
		execute("Print.generateExcel", "viewObject=xava_view_customers");				
		assertContentTypeForPopup("text/x-csv");				
	}

	
	
	public void testCustomEditorWithMultipleValuesFormatter_arraysInList() throws Exception {

		// Arrays in list
		assertValueInList(0, 0, "1");
		assertValueInList(0, 1, "MANUEL CHAVARRI");
		assertValueInList(0, 2, "1/3"); // This is a String []
		
		// Multiple values formatters
		String [] emptyRegions = {};
		String [] regions = { "1", "3" };
		String [] oneRegion = { "2" };
		
		execute("CRUD.new");
		assertValues("regions", emptyRegions);
		setValue("number", "66");
		setValue("name", "SELLER JUNIT 66");
		setValue("level.id", "A");
		setValues("regions", regions);
		assertValues("regions", regions);
		
		execute("CRUD.save");
		assertNoErrors();
		assertValues("regions", emptyRegions);		
		
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValues("regions", regions);
		
		setValues("regions", oneRegion);
		execute("CRUD.save");
		assertNoErrors();
		assertValues("regions", emptyRegions);

		setValue("number", "66");
		execute("CRUD.refresh");
		assertValues("regions", oneRegion);
		
		execute("CRUD.delete");
		assertMessage("Seller deleted successfully");
	}
	
	public void testCollectionOfEntityReferencesElementsNotEditables() throws Exception {
		execute("Mode.detailAndFirst");
		execute("Collection.view", "row=0,viewObject=xava_view_customers");
		assertNoEditable("number");
		assertNoEditable("name");
		assertNoAction("Collection.new"); // of deliveryPlaces
	}
	
	public void testCustomizeListSupportsRecursiveReferences() throws Exception {
		execute("List.customize");
		execute("List.addColumns");
		assertAction("AddColumns.addColumns");
	}
	
	public void testOnChangeListDescriptionReferenceWithStringSingleKey() throws Exception {
		execute("CRUD.new");
		setValue("level.id", "A");
		assertNoErrors();
		setValue("level.id", "");
		assertNoErrors();
	}
	
	public void testEntityReferenceCollections() throws Exception { 		
		createCustomers(); 
		createSeller66();
		createSeller67();
		verifySeller66();
		deleteCustomers(); 
		deleteSeller("66");
		deleteSeller("67");					
	}
	
	/* Since v2.2 this does not apply. See at testEntityReferenceCollections to
	 * see the current entity collection behaviour
	public void testSearchElementInReferencesCollectionUsingList() throws Exception {
		execute("CRUD.new");
		execute("Collection.new", "viewObject=xava_view_customers");
		execute("Reference.search", "keyProperty=xava.Seller.customers.number"); 
		String name = getValueInList(1, 0);
		execute("ReferenceSearch.choose", "row=1");
		assertValue("customers.name", name);
	}
	*/

	/* Since v2.2 this does not apply. See at testEntityReferenceCollections to
	 * see the current entity collection behaviour
	public void testCreateElementInReferencesCollectionUsingList() throws Exception {
		execute("CRUD.new");
		execute("Collection.new", "viewObject=xava_view_customers");
		execute("Reference.createNew", "model=Customer,keyProperty=xava.Seller.customers.number");
		assertAction("NewCreation.saveNew");
		assertAction("NewCreation.cancel");
	}
	*/
	
	private void createSeller66() throws Exception {
		execute("CRUD.new");
		setValue("number", "66");
		setValue("name", "SELLER JUNIT 66");
		setValue("level.id", "A");

		assertNoDialog();
		execute("Collection.add", "viewObject=xava_view_customers");
		assertDialog();
		assertDialogTitle("Add elements to 'Customers of Seller'");
		assertValueInList(5, 0, getCustomer1().getName());
		assertValueInList(6, 0, getCustomer2().getName());
		checkRow(5);
		checkRow(6);
		execute("AddToCollection.add");
		assertNoDialog();
		assertNoErrors();
		assertMessage("2 element(s) added to Customers of Seller");
		assertCollectionRowCount("customers",2);
						
		refreshCustomers();				
		
		assertValueInCollection("customers", 0, 0, getCustomerNumber1());
		assertValueInCollectionIgnoringCase("customers", 0, 1, getCustomer1().getName());
		assertValueInCollection("customers", 0, 2, getCustomer1().getRemarks());
		assertValueInCollection("customers", 0, 3, getCustomer1().getRelationWithSeller());
		assertValueInCollection("customers", 0, 4, getCustomer1().getSeller().getLevel().getDescription());
		
		assertValueInCollection("customers", 1, 0, getCustomerNumber2());
		assertValueInCollectionIgnoringCase("customers", 1, 1, getCustomer2().getName());
		assertValueInCollection("customers", 1, 2, getCustomer2().getRemarks());
		assertValueInCollection("customers", 1, 3, getCustomer2().getRelationWithSeller());
		assertValueInCollection("customers", 1, 4, getCustomer2().getSeller().getLevel().getDescription());
	}
	
	private void refreshCustomers() {
		customer1 = refresh(customer1);
		customer2 = refresh(customer2);
	}
	
	private Customer refresh(Customer object) {
		if (object == null) return null;
		if (!XPersistence.getManager().contains(object)) {
			object = XPersistence.getManager().merge(object);
		}
		XPersistence.getManager().refresh(object);
		return object;
	}

	private void createSeller67() throws Exception {
		execute("CRUD.new");
		setValue("number", "67");
		setValue("name", "SELLER JUNIT 67");
		setValue("level.id", "B");

		assertCollectionRowCount("customers",0);
		execute("Collection.add", "viewObject=xava_view_customers");		
		assertValueInList(6, 0, getCustomer2().getName());
		execute("AddToCollection.add", "row=6");		
		assertMessage("1 element(s) added to Customers of Seller");		
		assertCollectionRowCount("customers",1);
		
		XPersistence.getManager().refresh(getCustomer2());
		
		assertValueInCollection("customers", 0, 0, getCustomerNumber2());
		assertValueInCollectionIgnoringCase("customers", 0, 1, getCustomer2().getName());
		assertValueInCollection("customers", 0, 2, getCustomer2().getRemarks());
		assertValueInCollection("customers", 0, 3, getCustomer2().getRelationWithSeller());
		assertValueInCollection("customers", 0, 4, getCustomer2().getSeller().getLevel().getDescription());
	}
	
	private void verifySeller66() throws Exception {
		execute("CRUD.new");
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();
		assertCollectionRowCount("customers", 1);
		assertValueInCollection("customers", 0, 0, getCustomerNumber1());
		assertValueInCollectionIgnoringCase("customers", 0, 1, getCustomer1().getName());
		assertValueInCollection("customers", 0, 2, getCustomer1().getRemarks());
		assertValueInCollection("customers", 0, 3, getCustomer1().getRelationWithSeller());
		assertValueInCollection("customers", 0, 4, getCustomer1().getSeller().getLevel().getDescription());
		
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_customers"); 
		assertMessage("Association between Customer and Seller has been removed, but Customer is still in database");
		assertNoErrors();
		assertCollectionRowCount("customers", 0);		
	}
		
	private void deleteSeller(String number) throws Exception {
		execute("CRUD.new");
		setValue("number", number);
		execute("CRUD.refresh");
		assertNoErrors();

		execute("CRUD.delete");											
		assertNoErrors();
		assertMessage("Seller deleted successfully");
		assertExists("number"); // A bug did that the screen remained in blank after delete		
	}
	
	private void deleteCustomers() throws RemoteException, Exception {
		XPersistence.getManager().remove(getCustomer1());
		XPersistence.getManager().remove(getCustomer2());
		XPersistence.commit();
	}

	
	
	private Customer getCustomer1() throws Exception {
		if (customer1 == null) {
			createCustomers();
		}
		return customer1;
	}
	
	private Customer getCustomer2() throws Exception {
		if (customer2 == null) {
			createCustomers();
		}
		return customer2;
	}
	
		
	private void createCustomers() throws Exception {
		customer1 = new Customer();
		customer1.setNumber(66);
		customer1.setName("Customer Junit 66");
		// customer1.setType(1); // For XML components
		customer1.setType(Customer.Type.NORMAL); // For annotated POJOs
		customer1.setAddress(createAddress());
		customer1.setRemarks("REMARKS JUNIT 66");
		customer1.setRelationWithSeller("RELATION JUNIT 66");
		XPersistence.getManager().persist(customer1);		

		customer2 = new Customer();
		customer2.setNumber(67);
		customer2.setName("Customer Junit 67");
		// customer2.setType(1); // For XML components
		customer2.setType(Customer.Type.NORMAL); // For annotated POJOs
		customer2.setAddress(createAddress());
		customer2.setRemarks("REMARKS JUNIT 67");
		customer2.setRelationWithSeller("RELATION JUNIT 67");
		XPersistence.getManager().persist(customer2);
	
		XPersistence.commit();
	}
	
	private Address createAddress() { 
		Address address = new Address();
		address.setCity("EL PUIG");
		address.setStreet("MI CALLE");
		address.setZipCode(46540);
		State state = new State();
		state.setId("CA");
		address.setState(state);
		return address;
	}

	private String getCustomerNumber1() throws Exception {
		return String.valueOf(getCustomer1().getNumber());
	}
	
	private String getCustomerNumber2() throws Exception {
		return String.valueOf(getCustomer2().getNumber());
	}
				
}
