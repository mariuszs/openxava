package org.openxava.test.tests;

import org.openxava.jpa.*;
import org.openxava.test.model.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * @author Javier Paniza
 */

public class CustomerWithSectionTest extends CustomerTest {

	private String [] listActions = {
		"Print.generatePdf",
		"Print.generateExcel",
		"CRUD.new",
		"CRUD.deleteSelected",
		"CRUD.deleteRow", 
		"Mode.detailAndFirst",
		"Mode.split",
		"List.filter",
		"List.orderBy",
		"List.viewDetail",
		"List.customize",
		"List.hideRows",
		"Customer.hideSellerInList",
		"Customer.showSellerInList"
	};

	private String [] listCustomizeActions = {
		"Print.generatePdf",
		"Print.generateExcel",
		"CRUD.new",
		"CRUD.deleteSelected",
		"CRUD.deleteRow",
		"Mode.detailAndFirst",
		"Mode.split",
		"List.filter",
		"List.orderBy",
		"List.viewDetail",
		"List.customize",
		"List.addColumns",
		"List.moveColumnToLeft",
		"List.moveColumnToRight",
		"List.removeColumn",
		"List.hideRows",
		"Customer.hideSellerInList",
		"Customer.showSellerInList"
	};
	

	public CustomerWithSectionTest(String testName) {
		super(testName, "CustomerWithSection", true);		
	}
	
	public void testDialogsInNestedCollections() throws Exception { 
		assertDialogsInNestedCollections(false);
		assertDialogsInNestedCollections(true);
	}
	
	public void assertDialogsInNestedCollections(boolean closeDialog) throws Exception { 
		execute("CRUD.new");
		assertNoDialog();		
		assertAction("CRUD.new");
		assertNoAction("Collection.hideDetail");
		assertExists("deliveryPlaces");
		assertNotExists("receptionists");
		
		execute("Collection.new", "viewObject=xava_view_section0_deliveryPlaces");
		assertDialog();		
		assertNoAction("CRUD.new");
		assertAction("Collection.save");
		assertAction("Collection.hideDetail");
		assertNotExists("deliveryPlaces");
		assertExists("receptionists");
		
		execute("Collection.new", "viewObject=xava_view_receptionists");
		assertDialog();		
		assertActions(new String [] { "Collection.save", "Collection.saveAndStay", "Collection.hideDetail" });
		assertNotExists("deliveryPlaces");
		assertNotExists("receptionists");
		
		if (closeDialog) closeDialog();
		else execute("Collection.hideDetail");
		assertDialog();		
		assertNoAction("CRUD.new");
		assertAction("Collection.save");
		assertAction("Collection.hideDetail");
		assertAction("Collection.new");
		assertNotExists("deliveryPlaces");
		assertExists("receptionists");
	
		if (closeDialog) closeDialog();
		else execute("Collection.hideDetail");
		assertNoDialog();		
		assertAction("CRUD.new");
		assertNoAction("Collection.hideDetail");
		assertAction("Collection.new");
		assertExists("deliveryPlaces");
		assertNotExists("receptionists");		
	}
	
	public void testForwardToAbsoluteURL() throws Exception { 
		execute("CRUD.new");
		assertValue("website", "");
		execute("WebURL.go", "property=website,viewObject=xava_view_section0");
		assertError("Empty URL, so you cannot go to it");
		setValue("website", "http://www.example.com/");
		execute("WebURL.go", "property=website,viewObject=xava_view_section0");		
		assertTrue(getHtml().indexOf("we maintain a number of domains such as EXAMPLE.COM") >= 0); 		
	}
	
	public void testForwardToJavaScript() throws Exception {  
		execute("CRUD.new");
		HtmlElement console = getHtmlPage().getElementById("xava_console");
		assertTrue(!console.asText().contains("[CustomerWithSection.testForwardToJavaScript()] javascript: works"));
		setValue("website", "javascript:openxava.log('[CustomerWithSection.testForwardToJavaScript()] javascript: works');"); 
		execute("WebURL.go", "property=website,viewObject=xava_view_section0");		
		assertTrue(console.asText().contains("[CustomerWithSection.testForwardToJavaScript()] javascript: works"));
	}
	
	// To fix a concrete bug
	public void testNavigateToSearchReferenceAndCreateReference() throws Exception { 
		execute("CRUD.new");
		execute("Reference.search", "keyProperty=alternateSeller.number");
		execute("ReferenceSearch.choose", "row=0");
		execute("Reference.createNew", "model=Seller,keyProperty=alternateSeller.number");
		execute("NewCreation.cancel");
		assertExists("alternateSeller.number");
	}

	
	public void testDialogChangesPreviousView() throws Exception {		
		execute("CRUD.new");
		assertValue("address.street", "");
		assertValue("address.zipCode", "");
		assertValue("address.city", "");
		assertValue("address.state.id", "");
		assertNoDialog();
		execute("Address.addFullAddress");
		assertActions(new String[] {
			"AddFullAddress.add", "Dialog.cancel"	
		});
		assertDialog();
		assertDialogTitle("Entry the full address");		
		setValue("fullAddress", "AV. BARON DE CARCER, 48 - 12E 46001 VALENCIA CA");		
		execute("AddFullAddress.add");
		assertNoErrors();
		assertNoDialog();
		assertValue("address.street", "AV. BARON DE CARCER, 48 - 12E");
		assertValue("address.zipCode", "46001");
		assertValue("address.city", "VALENCIA");
		assertValue("address.state.id", "CA");		
		
		execute("Address.addFullAddress");
		assertDialog();
		execute("Dialog.cancel");
		assertNoDialog();
	}
	
	public void testCancelActionAfterChangeImageAction() throws Exception {   
		addImage();
		assertExists("telephone");
		assertAction("EditableOnOff.setOn");
		execute("Reference.createNew", "model=Seller,keyProperty=seller.number");		
		execute("NewCreation.cancel");
		assertExists("telephone");
		assertAction("EditableOnOff.setOn");		
	}
				
	public void testTELEPHONE_EMAIL_WEBURLstereotypes() throws Exception {
		assertTrue("website column must have a clickable link", getHtml().contains("<a href=\"http://www.openxava.org\">"));
		execute("Mode.detailAndFirst");
		setValue("telephone", "asf");
		setValue("email", "pepe");
		setValue("website", "openxava");
		execute("Customer.save");		
		assertError("Telephone must be a valid number"); 
		assertError("eMail must be a valid email address");
		assertError("Web site must be a valid url");
		setValue("telephone", "123");
		setValue("email", "pepe@mycompany");
		setValue("website", "www.openxava.org");
		execute("Customer.save");
		assertError("Telephone must be at least 8 Digits long");
		assertError("eMail must be a valid email address");
		assertError("Web site must be a valid url");
		assertValue("email", "pepe@mycompany"); // not converted to uppercase
		assertValue("website", "www.openxava.org"); // not converted to uppercase
		setValue("telephone", "961112233");
		setValue("email", "pepe@mycompany.com");
		setValue("website", "http://www.openxava.org");
		execute("Customer.save");
		assertNoErrors();
	}
	
	public void testOrderAndFilterInNestedCollection() throws Exception {
		execute("CRUD.new");
		setValue("number", "4");
		execute("CRUD.refresh");		
		assertValue("name", "Cuatrero");
		
		assertCollectionRowCount("deliveryPlaces", 1);
		execute("Collection.edit", "row=0,viewObject=xava_view_section0_deliveryPlaces");
		
		assertCollectionRowCount("receptionists", 2);
		assertValueInCollection("receptionists", 0, 0, "JUAN");
		assertValueInCollection("receptionists", 1, 0, "PEPE");
		
		execute("List.orderBy", "property=name,collection=receptionists"); 
		execute("List.orderBy", "property=name,collection=receptionists");
		assertValueInCollection("receptionists", 0, 0, "PEPE");
		assertValueInCollection("receptionists", 1, 0, "JUAN");
		
		setConditionValues("receptionists", new String[] { "J"} ); 
		execute("List.filter", "collection=receptionists"); 
		assertCollectionRowCount("receptionists", 1); 
		assertValueInCollection("receptionists", 0, 0, "JUAN");				
	}
	
	public void testModifyFromReference() throws Exception {
		execute("CRUD.new");
		execute("Reference.modify", "model=Seller,keyProperty=xava.Customer.seller.number");
		assertError("Impossible to modify an empty reference");
		setValue("number", "1");
		execute("CRUD.refresh");		
		assertValue("name", "Javi");
		assertValue("seller.name", "MANUEL CHAVARRI");
		execute("Reference.modify", "model=Seller,keyProperty=xava.Customer.seller.number");		
		assertValue("Seller", "number", "1");
		assertValue("Seller", "name", "MANUEL CHAVARRI");
		execute("Modification.cancel");
		assertValue("seller.name", "MANUEL CHAVARRI");
		execute("Reference.modify", "model=Seller,keyProperty=xava.Customer.seller.number");
		assertValue("Seller", "number", "1");
		assertValue("Seller", "name", "MANUEL CHAVARRI");
		setValue("Seller", "name", "MANOLO");
		execute("Modification.update");
		assertValue("seller.name", "MANOLO");
		execute("Reference.modify", "model=Seller,keyProperty=xava.Customer.seller.number");
		setValue("Seller", "name", "MANUEL CHAVARRI");
		execute("Modification.update");
		assertValue("seller.name", "MANUEL CHAVARRI");
	}
	
	public void testChooseInReferenceWithoutSelecting() throws Exception {
		execute("CRUD.new");
		execute("Reference.search", "keyProperty=xava.Customer.alternateSeller.number");		
		execute("ReferenceSearch.choose");
		assertNoErrors();		
		assertAction("ReferenceSearch.choose"); // Because no row is selected we keep in searching list
	}
	
	public void testCustomizeReferenceListDoesNotReturnToListModeOfModule() throws Exception {
		execute("CRUD.new");
		execute("Reference.search", "keyProperty=xava.Customer.alternateSeller.number");
		assertListColumnCount(3);
		execute("List.customize");
		execute("List.addColumns");
		execute("AddColumns.restoreDefault");
		assertListColumnCount(3); // To test that it's still is the tab of sellers, not the customer's one
	}
	
	public void testDefaultValidator() throws Exception {
		execute("CRUD.new");
		setValue("name", "x");
		execute("Customer.save");
		assertNoError("Person name MAKARIO is not allowed for Name in Customer");
		setValue("name", "MAKARIO");
		execute("Customer.save");
		assertError("Person name MAKARIO is not allowed for Name in Customer");
	}
	
	public void testCreatedFromReferenceIsChosenAndThrowsOnChange() throws Exception {
		execute("CRUD.new");
		execute("Reference.createNew", "model=Seller,keyProperty=xava.Customer.alternateSeller.number");
		setValue("Seller", "number", "66");
		setValue("Seller", "name", "SELLER JUNIT 66");
		execute("NewCreation.saveNew");
		assertNoErrors(); 
		assertValue("alternateSeller.number", "66");
		assertValue("alternateSeller.name", "DON SELLER JUNIT 66"); // The 'DON' is added by an on-change action
		deleteSeller(66);
	}	
	
	private void deleteSeller(int number) throws Exception {
		XPersistence.getManager().remove(XPersistence.getManager().find(Seller.class, number));				
	}

	public void testPropertyAction() throws Exception { 
		execute("CRUD.new");
		setValue("address.street", "DOCTOR PESSET");
		assertValue("address.street", "DOCTOR PESSET");		
		execute("Customer.prefixStreet", "xava.keyProperty=address.street"); 
		assertValue("address.street", "C/ DOCTOR PESSET");
	}
	
	public void testAddingToManyToManyCollectionFromANewObject() throws Exception { 
		execute("CRUD.new");
		
		// The minimum data to save a customer
		setValue("number", "66");
		setValue("name", "JUNIT Customer");
		setValue("address.street", "JUNIT Street");
		setValue("address.zipCode", "46540");
		setValue("address.city", "EL PUIG");
		setValue("address.state.id", "CA");
		
		// Trying to add a state
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("states", 0);

		assertAddingStates();		
		assertNoErrors();
		assertNoEditable("number");
		
		execute("CRUD.delete");		
		assertNoErrors();		
	}

	
	public void testManyToManyCollection() throws Exception { 
		execute("Mode.detailAndFirst");
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("states", 0);
		
		assertAddingStates();
		
		// Using Edit + Remove
		/* To remove editing collections of entities not available since OX4m2
		execute("Collection.edit", "row=0,viewObject=xava_view_section1_states");
		execute("Collection.remove", "viewObject=xava_view_section1_states");
		assertCollectionRowCount("states", 1);
		*/
		// Using Check row + Remove selected
		checkRowCollection("states", 0);		
		execute("Collection.removeSelected", "viewObject=xava_view_section1_states");
		assertNoErrors();
		assertCollectionRowCount("states", 1);
		
		checkRowCollection("states", 0);		
		execute("Collection.removeSelected", "viewObject=xava_view_section1_states");
		assertNoErrors();
		assertCollectionRowCount("states", 0);
		
		
		// Verifying if that other part is not removed
		changeModule("StateHibernate");
		assertValueInList(0, 0, "AK");
		assertValueInList(4, 0, "CA");		
	}

	private void assertAddingStates() throws Exception {
		if (usesAnnotatedPOJO()) {
			// In OX3 ManyToMany is supported, then we have a collection of entities
			execute("Collection.add", "viewObject=xava_view_section1_states");
			assertValueInList(0, 0, "AK");
			assertValueInList(4, 0, "CA");
			checkRow(0);
			checkRow(4);			
			execute("AddToCollection.add");
		}
		else {
			// In OX2 many to many is not supported, we simulate it using a collection of aggregates,
			// therefore the User Interface it's not the same (because it's a collection of aggragates)
			execute("Collection.new", "viewObject=xava_view_section1_states");
			setValue("state.id", "AK");
			assertValue("state.name", "ALASKA");
			execute("Collection.save");
			execute("Collection.new", "viewObject=xava_view_section1_states");
			setValue("state.id", "CA");
			assertValue("state.name", "CALIFORNIA");
			execute("Collection.save");			
		}		
		assertCollectionRowCount("states", 2); 
		assertValueInCollection("states", 0, 0, "AK");
		assertValueInCollection("states", 0, 1, "ALASKA");		
		assertValueInCollection("states", 1, 0, "CA");
		assertValueInCollection("states", 1, 1, "CALIFORNIA");
	}
	
	public void testChangeReferenceLabel() throws Exception {
		execute("CRUD.new");
		assertLabel("alternateSeller", "Alternate seller");
		execute("Customer.changeAlternateSellerLabel");
		assertLabel("alternateSeller", "Secondary seller");
	}
	
	public void testCustomizeList() throws Exception { 
		doTestCustomizeList_moveAndRemove(); 
		tearDown();	setUp();
		doTestCustomizeList_generatePDF();
		tearDown();	setUp();
		doTestRestoreColumns_addRemoveTabColumnsDynamically();
	}
	
	private void doTestCustomizeList_moveAndRemove() throws Exception {
		assertActions(listActions);
		execute("List.customize");		
		assertActions(listCustomizeActions);

		assertListColumnCount(7);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "Seller");
		assertLabelInList(3, "City of Address");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "State of Address");
		assertLabelInList(6, "Web site");
		assertTrue("It is needed customers for execute this test", getListRowCount() > 1);
		String name = getValueInList(0, 0);
		String type = getValueInList(0, 1);
		String seller = getValueInList(0, 2);
		String city = getValueInList(0, 3);
		String sellerLevel = getValueInList(0, 4);
		String state = getValueInList(0, 5);
		String site = getValueInList(0, 6);

		// move 2 to 3
		execute("List.moveColumnToRight", "columnIndex=2");
		assertNoErrors();
		assertListColumnCount(7);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "City of Address");
		assertLabelInList(3, "Seller");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "State of Address"); 
		assertLabelInList(6, "Web site");
		assertValueInList(0, 0, name);
		assertValueInList(0, 1, type);
		assertValueInList(0, 2, city);
		assertValueInList(0, 3, seller);
		assertValueInList(0, 4, sellerLevel);
		assertValueInList(0, 5, state);
		assertValueInList(0, 6, site);
		
		// try to move 6, it is the last, do nothing
		execute("List.moveColumnToRight", "columnIndex=6");
		assertNoErrors();
		assertListColumnCount(7);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "City of Address");
		assertLabelInList(3, "Seller");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "State of Address"); 
		assertLabelInList(6, "Web site");
		assertValueInList(0, 0, name);
		assertValueInList(0, 1, type);
		assertValueInList(0, 2, city);
		assertValueInList(0, 3, seller);
		assertValueInList(0, 4, sellerLevel);
		assertValueInList(0, 5, state);
		assertValueInList(0, 6, site);
		
		// move 3 to 2
		execute("List.moveColumnToLeft", "columnIndex=3");
		assertNoErrors();
		assertListColumnCount(7);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "Seller");
		assertLabelInList(3, "City of Address");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "State of Address"); 
		assertLabelInList(6, "Web site");
		assertValueInList(0, 0, name);
		assertValueInList(0, 1, type);
		assertValueInList(0, 2, seller);
		assertValueInList(0, 3, city);
		assertValueInList(0, 4, sellerLevel);
		assertValueInList(0, 5, state);
		assertValueInList(0, 6, site);
		
		// try to move 0 to left, do nothing
		execute("List.moveColumnToLeft", "columnIndex=0");
		assertNoErrors();
		assertListColumnCount(7);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "Seller");
		assertLabelInList(3, "City of Address");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "State of Address"); 
		assertLabelInList(6, "Web site");
		assertValueInList(0, 0, name);
		assertValueInList(0, 1, type);
		assertValueInList(0, 2, seller);
		assertValueInList(0, 3, city);
		assertValueInList(0, 4, sellerLevel);
		assertValueInList(0, 5, state);
		assertValueInList(0, 6, site);

		// remove column 3
		execute("List.removeColumn", "columnIndex=3");
		assertNoErrors();
		assertListColumnCount(6);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");		
		assertLabelInList(2, "Seller");
		assertLabelInList(3, "Seller level");
		assertLabelInList(4, "State of Address"); 
		assertLabelInList(5, "Web site");
		assertValueInList(0, 0, name);
		assertValueInList(0, 1, type);
		assertValueInList(0, 2, seller);
		assertValueInList(0, 3, sellerLevel);
		assertValueInList(0, 4, state); 
		assertValueInList(0, 5, site);
						
		execute("List.customize");
		assertActions(listActions);
	}
	
	private void doTestCustomizeList_generatePDF() throws Exception {
		// Trusts in that testCustomizeList_moveAndRemove is executed before
		execute("List.customize");
		assertListColumnCount(6);
		execute("List.removeColumn", "columnIndex=3");
		assertNoErrors();
		assertListColumnCount(5);		
		execute("Print.generatePdf"); 
		assertContentTypeForPopup("application/pdf");
		
	}
		
	private void doTestRestoreColumns_addRemoveTabColumnsDynamically() throws Exception { 
		// Restoring initial tab setup
		execute("List.customize");
		execute("List.addColumns");							
		execute("AddColumns.restoreDefault");		
		// End restoring
		
		assertListColumnCount(7);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "Seller");
		assertLabelInList(3, "City of Address");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "State of Address"); 
		assertLabelInList(6, "Web site");
		assertTrue("Must to have customers for run this test", getListRowCount() > 1);
		String name = getValueInList(0, 0);
		String type = getValueInList(0, 1);
		String seller = getValueInList(0, 2);
		String city = getValueInList(0, 3);
		String sellerLevel = getValueInList(0, 4);
		String state = getValueInList(0, 5); 
		String site = getValueInList(0, 6);
		
		execute("Customer.hideSellerInList");
		assertNoErrors();
		assertListColumnCount(6);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "City of Address");
		assertLabelInList(3, "Seller level");
		assertLabelInList(4, "State of Address"); 
		assertLabelInList(5, "Web site");
		assertValueInList(0, 0, name);
		assertValueInList(0, 1, type);
		assertValueInList(0, 2, city);
		assertValueInList(0, 3, sellerLevel);
		assertValueInList(0, 4, state); 
		assertValueInList(0, 5, site);
		
		execute("Customer.showSellerInList");
		assertNoErrors();
		assertListColumnCount(7);
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Type");
		assertLabelInList(2, "Seller");		
		assertLabelInList(3, "City of Address");
		assertLabelInList(4, "Seller level");
		assertLabelInList(5, "State of Address"); 
		assertLabelInList(6, "Web site");
		assertValueInList(0, 0, name);
		assertValueInList(0, 1, type);
		assertValueInList(0, 2, seller);
		assertValueInList(0, 3, city);
		assertValueInList(0, 4, sellerLevel);
		assertValueInList(0, 5, state); 
		assertValueInList(0, 6, site);
	}
	
	public void testCustomizeList_addAndResetModule() throws Exception {   
		assertListColumnCount(7);
		String value = getValueInList(0, 0);
		execute("List.customize");
		execute("List.addColumns");		
		checkRow("selectedProperties", "number"); 		
		execute("AddColumns.addColumns");
		assertListColumnCount(8);
		assertValueInList(0, 0, value);
				
		resetModule();
		assertListColumnCount(8);
		assertValueInList(0, 0, value);
		
		execute("List.customize");
		execute("List.removeColumn", "columnIndex=7");
		assertListColumnCount(7);
	}
	
	public void testRowStyle() throws Exception {
		// When testing again a portal styleClass in xava.properties must match with
		// the tested portal in order that this test works fine
		int c = getListRowCount();
		boolean found = false;
		
		for (int i=0; i<c; i++) {
			String type = getValueInList(i, "type");
			if ("Steady".equals(type)) {				
				assertRowStyleInList(i, "row-highlight");				
				found = true;
			}
			else { 
				assertNoRowStyleInList(i);				
			}						
		}
		if (!found) {
			fail("It is required at least one Customer of 'Steady' type for run this test");
		}
	}
	
	public void testImageEditorFromAnotherModule() throws Exception {  	
		// started from a different module because there was a bug in imageEditor when run from a module
		//	that was not the initial
		changeModule("BeforeGoingToCustomer");
		execute("ChangeModule.goCustomer");
		
		// 
		testChangeImage();
	}
		
}
