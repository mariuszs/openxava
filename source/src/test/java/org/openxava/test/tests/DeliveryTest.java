package org.openxava.test.tests;

import java.text.*;
import java.util.*;

import javax.persistence.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class DeliveryTest extends ModuleTestBase {

	private static Log log = LogFactory.getLog(DeliveryTest.class);
	
	private String [] listActions = {
		"Print.generatePdf",
		"Print.generateExcel",
		"CRUD.new",
		"CRUD.deleteSelected", 
		"CRUD.deleteRow",
		"Remarks.hideRemarks",
		"Mode.detailAndFirst",
		"Mode.split",
		"List.filter",
		"List.customize",
		"List.orderBy",
		"List.viewDetail",
		"List.hideRows",
		"List.sumColumn"
	};
		
	public DeliveryTest(String testName) {
		super(testName, "Delivery");		
	}
	
	public void testModifyEmptyReferenceFromADialog() throws Exception { 
		execute("CRUD.new");
		setValue("deliveredBy", usesAnnotatedPOJO()?"1":"2");
		setValue("carrier.number", "1");
		execute("Reference.modify", "model=Carrier,keyProperty=carrier.number");
		assertDialog();
		assertValue("drivingLicence.KEY", "");
		assertExists("warehouse.name"); // We are in the Carrier dialog
		assertNoAction("CRUD.new");
		assertAction("Modification.update");
		execute("Reference.modify", "model=DrivingLicence,keyProperty=drivingLicence__KEY__");
		assertDialog();
		assertExists("warehouse.name"); // We still are in the Carrier dialog
		assertError("Impossible to modify an empty reference");
		assertNoAction("CRUD.new");
		assertAction("Modification.update");		
	}
	
	public void testSaveElementInCollectionWithUseObjectView() throws Exception {  
		execute("CRUD.new");
		execute("Sections.change", "activeSection=2");
		execute("DeliveryDetail.new", "viewObject=xava_view_section2_details_details");
		execute("DeliveryDetail.saveFailing");
		assertNotExists("invoice.year");
	}
	
	public void testSearchUsesSearchView() throws Exception { 
		execute("CRUD.new");
		execute("CRUD.search");
		assertDialog();
		assertNotExists("advice");
		assertNotExists("vehicle");
		
		setValue("description", "DELIVERY JUNIT 666");
		execute("Search.search");
		assertNoDialog();
		assertNoErrors();
		assertValue("description", "DELIVERY JUNIT 666");
		
	}

	
	public void testModifyEmptyReferenceNotMustShowTheDialog() throws Exception{
		execute("CRUD.new");
		assertValue("type.number", "");
		execute("Reference.modify", "model=DeliveryType,keyProperty=type.number");
		assertNoDialog();
		assertError("Impossible to modify an empty reference");
		assertTrue(!getHtml().contains("Modify - Delivery")); 
	}
	
	public void testSecondLevelDialogReturningWithCancelButton() throws Exception { 
		assertSecondLevelDialogReturning(false); 
	}
	
	public void testSecondLevelDialogReturningWithCloseDialogButton() throws Exception { 
		assertSecondLevelDialogReturning(true);
	}	
	
	private void assertSecondLevelDialogReturning(boolean closeDialogButton) throws Exception { 
		execute("CRUD.new");
		assertExists("vehicle"); // Only in Delivery, no dialog
		assertNotExists("customerDiscount"); // Only in Invoice, first dialog level
		assertNotExists("website"); // Only in Customer, second dialog level
		assertNoDialog();
		
		execute("Reference.createNew", "model=Invoice,keyProperty=invoice.number");
		assertNotExists("vehicle"); 
		assertExists("customerDiscount"); 
		assertNotExists("website"); 
		assertDialog();
		
		execute("Reference.createNew", "model=Customer,keyProperty=customer.number");
		assertNotExists("vehicle");
		assertNotExists("customerDiscount"); 
		assertExists("website"); 
		assertDialog();
				
		if (closeDialogButton) closeDialog(); 
		else execute("NewCreation.cancel");
		
		assertNotExists("vehicle"); 
		assertExists("customerDiscount"); 
		assertNotExists("website"); 
		assertDialog();
		
		if (closeDialogButton) closeDialog(); 
		else execute("NewCreation.cancel");

		assertExists("vehicle"); 
		assertNotExists("customerDiscount"); 
		assertNotExists("website"); 
		assertNoDialog();
	}
	
	
	public void testCreateEntityWithCollectionFromReference_secondLevelDialog() throws Exception { 
		execute("CRUD.new");
		execute("Reference.createNew", "model=Invoice,keyProperty=xava.Delivery.invoice.number");		
		assertDialog();
		
		setValue("year", "2002");
		setValue("number", "1");		
		execute("Reference.search", "keyProperty=customer.number");		
		execute("ReferenceSearch.choose", "row=1"); 
		assertValue("customer.name", "Juanillo"); 
		setValue("customer.number", "1");
		assertValue("customer.name", "Javi"); 
		execute("Sections.change", "activeSection=2");
		setValue("vatPercentage", "16");
		execute("NewCreation.saveNew");
		assertError("Impossible to create: an object with that key already exists");
		
		setValue("year", "2009");
		setValue("number", "66");
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("details", 0);		
		execute("Collection.new", "viewObject=xava_view_section1_details");		
		setValue("quantity", "20");
		setValue("unitPrice", "1");
		setValue("product.number", "1");
		execute("Collection.save");	
		assertNoErrors();
		assertMessage("Invoice detail created successfully");
		assertMessage("Invoice created successfully"); 
		assertNoErrors();		
		assertCollectionRowCount("details", 1);
		execute("Sections.change", "activeSection=2");
		setValue("vatPercentage", "17");
		execute("NewCreation.saveNew");
		assertNoErrors();		
		assertNoDialog();
		assertValue("invoice.year", "2009");
		assertValue("invoice.number", "66");
		
		changeModule("Invoice");
		execute("CRUD.new");
		setValue("year", "2009");
		setValue("number", "66");
		execute("CRUD.refresh");
		execute("Sections.change", "activeSection=2");
		assertValue("vatPercentage", "17");
		execute("CRUD.delete");
		assertMessage("Invoice deleted successfully");
	}
	
	public void testMinimunInCollection_overrideCollectionActions() throws Exception {
		// minimunCollection
		execute("CRUD.new");
		setValue("invoice.year", "2004");
		setValue("invoice.number", "2");
		setValue("type.number", "1");
		setValue("number", "666");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("description", "DELIVERY JUNIT 666"); 
		
		execute("Sections.change", "activeSection=2");
		assertCollectionRowCount("details", 3);
		
		execute("DeliveryDetail.new", "viewObject=xava_view_section2_details_details");
		setValue("number", "14");
		setValue("description", "JUNIT DETAIL 14");
		execute("DeliveryDetail.save");
		
		assertError("More than 3 items in Details of Delivery are not allowed");
		closeDialog();
		assertCollectionRowCount("details", 3);
		
		checkRowCollection("details", 2);
		execute("DeliveryDetail.removeSelected", "viewObject=xava_view_section2_details_details");
		assertNoErrors();
		assertMessage("Delivery detail deleted from database");
		assertMessage("Delivery detail 13 deleted successfully"); // This message is by the override action for removeSelected		
		assertCollectionRowCount("details", 2);
		
		execute("DeliveryDetail.new", "viewObject=xava_view_section2_details_details");
		setValue("number", "13");
		setValue("description", "DETAIL 13");
		execute("DeliveryDetail.save");
		assertNoErrors(); 
		assertMessage("The action Save for delivery detail executed");
		assertCollectionRowCount("details", 3);
		
		execute("Collection.edit", "row=2,viewObject=xava_view_section2_details_details");
		execute("DeliveryDetail.save");
		assertNoErrors();
		
		// checkboxNotInCollectionWhenNotEditable, this test only work in a HTML UI
		/* Since v2.1.4 the check box is always present in all collections (because is implemented uses a Tab)
		assertTrue("Check box must be present", getHtml().indexOf("xava.Delivery.details.__SELECTED__") >= 0);
		execute("EditableOnOff.setOff");
		assertTrue("Check box must not be present", getHtml().indexOf("xava.Delivery.details.__SELECTED__") < 0);
		*/
	}
	
	public void testFocusWhenSectionsAndGroupsInHeader() throws Exception {
		execute("CRUD.new");
		assertFocusOn("invoice.year");
					
		setValue("shortcut", "DY");
		
		assertValue("remarks", "Delayed");
		assertFocusOn("remarks");		
	}
	
	public void testZeroValueOnChange() throws Exception {
		createDeliveryType(0, "JUNIT DELIVERY TYPE 0");
		execute("CRUD.new");
		assertMessage("type=null");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");
		assertValue("invoice.date", "1/1/02"); 						
		setValue("type.number", "0");
		assertMessage("type=0"); // Verifies zero as value for on change action
		deleteDeliveryType(0);
	}
	
		
	public void testNonExistentReferenceUsedAsKey() throws Exception {
		if (usesAnnotatedPOJO()) { // This case is not supported since 4m6, because Hibernate 3.6 does not support it 
			log.warn("testNonExistentReferenceUsedAsKey() case not supported in JPA version");
			return;
		}
		createDeliveryType(0, "JUNIT DELIVERY TYPE 0"); 
		execute("CRUD.new");
		assertMessage("type=null");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");
		assertValue("invoice.date", "1/1/02");						
		setValue("type.number", "0");
		setValue("number", "66");
		setValue("description", "JUNIT");		
		execute("CRUD.save");
		assertNoErrors();
		
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");							
		setValue("type.number", "0");
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("description", "JUNIT"); 
		
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("description", "JUNIT");

		deleteDeliveryType(0);
		execute("CRUD.refresh");
		assertNoErrors(); 
		assertValue("description", "JUNIT");

		execute("CRUD.delete");
		assertMessage("Delivery deleted successfully");
	}
	

	private void createDeliveryType(int number, String description) {
		DeliveryType type = new DeliveryType();
		type.setNumber(number);
		type.setDescription(description);		
		XPersistence.getManager().persist(type);
		XPersistence.commit();
	}
	
	private void deleteDeliveryType(int number) {
		DeliveryType type = XPersistence.getManager().find(DeliveryType.class, number);				
		XPersistence.getManager().remove(type);
		XPersistence.commit();
	}
	
	public void testSearchingByAnyPropertyUsingRefresh() throws Exception { 
		// One result
		execute("CRUD.new");
		assertValue("number", "");
		assertValue("description", "");
		setValue("description", "%SEARCHING");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("number", "777"); 
		assertValue("description", "FOR TEST SEARCHING BY DESCRIPTION");
		
		// There are more than one match, returns the first
		execute("CRUD.new");
		assertValue("number", "");
		assertValue("description", "");
		setValue("driverType", "");
		setValue("description", "DEL");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("description", "DELIVERY JUNIT 666");				
	}
	
	public void testSearchingByAnyProperty() throws Exception {
		// One result
		execute("CRUD.new");
		assertValue("number", "");
		assertValue("description", "");
		execute("CRUD.search");
		setValue("description", "%SEARCHING");
		execute("Search.search");
		assertNoErrors();
		assertValue("number", "777"); 
		assertValue("description", "FOR TEST SEARCHING BY DESCRIPTION");
		
		// There are more than one match, returns the first
		execute("CRUD.new");
		assertValue("number", "");
		assertValue("description", "");
		execute("CRUD.search");
		setValue("description", "DEL");
		execute("Search.search");
		assertNoErrors();
		assertValue("description", "DELIVERY JUNIT 666");				
	}	
	
		
	public void testDateCalendarEditor() throws Exception {
		execute("CRUD.new");
		assertExists("invoice.date");
		assertNotExists("invoice.date_CALENDAR_BUTTON_");
		assertExists("date_CALENDAR_BUTTON_");		
	}
	
	public void testAggregateInCollectionWithVisibleKeyDoesNotTryToSearchOnChangeKey() throws Exception {
		execute("CRUD.new");
		execute("Sections.change", "activeSection=2");
		execute("DeliveryDetail.new", "viewObject=xava_view_section2_details_details");
		setValue("number", "66");
		assertNoErrors();
	}
	
	public void testOnChangeActionOnlyOnce() throws Exception {
		execute("CRUD.new");
		assertValue("driverType", "X");
	}
	
	public void testAggregateInCollectionWithNotHiddenKey_setFocusInDialog() throws Exception {
		assertListNotEmpty();
		execute("Mode.detailAndFirst");
		execute("Sections.change", "activeSection=2"); 
		
		// The bucle is for choosing a delivery with less than 3 details		
		while (getCollectionRowCount("details") >= 3) {
			execute("Navigation.next");
		}		
		String number = getValue("number");
		executeClicking("DeliveryDetail.new", "viewObject=xava_view_section2_details_details");  
		assertMessage("The action New for delivery detail executed");
		assertValue("description", "DETAIL FOR DELIVERY " + number + "/" + number);		
		assertFocusOn("description"); 
		setValue("number", "66");
		setValue("description", "JUNIT DELIVERY DETAIL");
		execute("DeliveryDetail.save");
		assertMessage("The action Save for delivery detail executed");
		assertNoErrors();				
		
		execute("Collection.edit", "row=0,viewObject=xava_view_section2_details_details");
		assertValue("number", "66");
		execute("DeliveryDetail.hideDetail");
		assertMessage("The action Close for delivery detail executed");
		execute("Collection.edit", "row=0,viewObject=xava_view_section2_details_details");
		assertValue("number", "66");
		closeDialog();
		assertMessage("The action Close for delivery detail executed");		
		execute("Collection.edit", "row=0,viewObject=xava_view_section2_details_details");
		assertValue("number", "66");
		execute("DeliveryDetail.remove");
		assertMessage("The action Remove for delivery detail executed");
		assertNoErrors();
	}
	
	public void testEntityWithCollectionOfAggregatesWithNotHiddenKey() throws Exception { 
		execute("CRUD.new");
		
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");
		assertValue("invoice.date", "1/1/02");					
		setValue("type.number", "1");
		setValue("number", "66");
		setValue("description", "JUNIT");
		
		execute("Sections.change", "activeSection=2");
		assertCollectionRowCount("details", 0);
		execute("DeliveryDetail.new", "viewObject=xava_view_section2_details_details");		
		setValue("number", "66");
		setValue("description", "JUNIT DELIVERY DETAIL");
		execute("DeliveryDetail.save");		
		assertNoErrors();				
		assertCollectionRowCount("details", 1);
		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	public void testReferenceAsDescriptionsListWithValidValuesInKey_validateViewPropertiesOnModify() throws Exception { 
		execute("Mode.detailAndFirst");
		assertValue("shipment.KEY", ""); 
		Shipment shipment = (Shipment) Shipment.findAll().iterator().next();
		setValue("shipment.KEY", toKeyString(shipment));
		execute("CRUD.save");
		assertError("Value for Advice in Delivery is required");
		setValue("advice", "Modifying");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		execute("Mode.detailAndFirst");
		assertValue("shipment.KEY", toKeyString(shipment)); 
		assertDescriptionValue("shipment.KEY", shipment.getDescription());
		// Restoring		
		setValue("shipment.KEY", "");
		setValue("advice", "Restoring");
		execute("CRUD.save");
		assertNoErrors();
	}
	
	public void testWhenStereotypeWithoutFormatterUseTypeFormatter() throws Exception {
		// date: Without stereotype, use date formatter
		String date = getValueInList(0, "date");		
		// dataAsLabel: With stereotype, but it has no formatter,
		// hence it must to use date formatter		
		String dateAsLabel = getValueInList(0, "dateAsLabel");		
		assertEquals(date, dateAsLabel);
	}
	
	public void testSecondLevelCalculatedPropertyAndDependenOf3LevelPropertyInList() throws Exception {
		int c = getListRowCount();
		boolean withoutDiscount = false;
		boolean withDiscount = true;
		for (int i=0; i<c; i++) {
			String value = getValueInList(i, "invoice.sellerDiscount");			
			if ("0.00".equals(value)) withoutDiscount = true;
			else if ("20.00".equals(value)) withDiscount = true;
			else fail("Only 0.00 or 20.00 are valid values for invoice.sellerDiscount");
		}
		assertTrue("It's required deliveries with invoices with and without seller discount", withDiscount && withoutDiscount);
	}
		
	public void testUseListWithOtherModelAndReturnToModuleList() throws Exception {
		execute("CRUD.new");
		execute("Delivery.viewCurrentYearInvoices");
		assertNoErrors();
		execute("Return.return");
		assertNoErrors();
		execute("Mode.list");
	}
	
	public void testCreateObjectInvalidateDescriptionsCache() throws Exception {
		execute("CRUD.new");
		assertNoType("66");
		changeModule("DeliveryType");
		execute("CRUD.new");
		setValue("number", "66");
		setValue("description", "JUNIT TEST");
		execute("CRUD.save");
		assertNoErrors();
		changeModule("Delivery");
		assertType("66");
		changeModule("DeliveryType");
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();
		execute("CRUD.delete");		
		assertMessage("Delivery type deleted successfully");
		changeModule("Delivery");
		assertNoType("66");
	}
	

	public void testEntityValidatorWithKeyReference() throws Exception {		
		assertListNotEmpty();
		execute("Mode.detailAndFirst");
		assertNoErrors(); 
		setValue("advice", "Validating"); 
		execute("CRUD.save");
		assertNoErrors();
	}
	
	public void testReadHiddenValuesFromServer() throws Exception { 				
		// Create one new
		execute("CRUD.new");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");
		assertValue("invoice.date", "1/1/02"); 						
		setValue("type.number", "1");
		setValue("number", "66");
		setValue("description", "JUNIT");
		setValue("remarks", "HIDDEN REMARK");
		execute("CRUD.save");
		assertNoErrors(); 
		assertValue("invoice.year", "");
		assertValue("invoice.number", "");						
		assertValue("type.number", "");	
		assertValue("number", "");
		assertValue("description", "");
		assertValue("remarks", "No remarks");

		// Hide remarks		
		execute("Remarks.hideRemarks");
		assertNotExists("remarks");
		
		// Search the just created
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");						
		setValue("type.number", "1");		
		setValue("number", "66");				
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("invoice.year", "2002"); 
		assertValue("invoice.number", "1");				
		assertValue("invoice.date", "1/1/02");		
		assertValue("type.number", "1");
		assertValue("number", "66");		
		assertValue("description", "JUNIT");				
		assertNotExists("remarks");
		
		// Show remarks
		execute("Remarks.showRemarks");
		assertExists("remarks");
		assertValue("remarks", "HIDDEN REMARK");
																
		// Delete it
		execute("CRUD.delete");													
		assertNoErrors();
		assertMessage("Delivery deleted successfully");		
	}
	
	
	public void testNavigationActionCanReturnPreviousController() throws Exception {
		String [] initialActions = {
			"Navigation.previous",
			"Navigation.first",
			"Navigation.next",
			"CRUD.new",
			"CRUD.save",
			"CRUD.delete",
			"CRUD.search",						
			"CRUD.refresh",
			"Mode.list",
			"Mode.split",
			"Reference.search",
			"Reference.createNew",
			"Reference.modify",
			"Sections.change",
			"Delivery.setDefaultInvoice",
			"Delivery.setDefaultType",
			"Delivery.generateNumber",
			"Delivery.generateNumber88",
			"Delivery.activateDeactivateSection",
			"Delivery.hideActions",
			"Delivery.viewCurrentYearInvoices",
			"Delivery.hideAdvice",
			"Delivery.hideShortcut",
			"EditableOnOff.setOn",
			"EditableOnOff.setOff",
			"Remarks.hideRemarks",
			"Remarks.showRemarks",
			"Remarks.setRemarks"			
		};
		
		String [] minimumActions = {
			"CRUD.new",
			"CRUD.save",
			"CRUD.delete",
			"CRUD.search",
			"CRUD.refresh",
			"Mode.list",
			"Mode.split",
			"Reference.search",
			"Reference.createNew",
			"Reference.modify",
			"Sections.change",
			"Delivery.setDefaultInvoice",
			"Delivery.setDefaultType",			
			"Delivery.generateNumber",
			"Delivery.generateNumber88",
		};
		
		String [] creatingNewActions = {
			"NewCreation.saveNew",
			"NewCreation.cancel"
		};
				
		execute("CRUD.new");
		assertActions(initialActions);  
		
		execute("Delivery.hideActions");
		assertActions(minimumActions);
		
		execute("Reference.createNew", "model=DeliveryType,keyProperty=xava.Delivery.type.number");
		assertActions(creatingNewActions);
		
		execute("NewCreation.cancel");
		assertActions(minimumActions);	
	}
	
	public void testPropertyAndReferenceActions() throws Exception {
		execute("Mode.detailAndFirst");
		assertNoErrors(); 
		assertNoAction("Delivery.generateNumber"); // of property
		assertNoAction("Delivery.setDefaultType"); // of reference as descriptions-list
		assertNoAction("Delivery.setDefaultInvoice"); // of reference 		
		execute("CRUD.new"); 
		assertAction("Delivery.generateNumber");
		assertAction("Delivery.setDefaultType");
		assertAction("Delivery.setDefaultInvoice");
		assertValue("number", "");
		assertValue("type.number", "");
		assertValue("invoice.year", "");
		assertValue("invoice.number", "");
		execute("Delivery.generateNumber88", "xava.keyProperty=number");
		assertNoErrors();
		assertValue("number", "88"); 
		execute("Delivery.generateNumber", "xava.keyProperty=number"); 
		assertValue("number", "77");
		execute("Delivery.setDefaultType");
		assertValue("type.number", "1");
		execute("Delivery.setDefaultInvoice");
		assertValue("invoice.year", "2002");
		assertValue("invoice.number", "1");
		assertValue("invoice.date", "1/1/02"); 		
	}
				
	public void testActivateDeactivateSection() throws Exception {
		execute("CRUD.new");
		assertEditable("advice");
		assertEditable("remarks");
		execute("Delivery.activateDeactivateSection");
		assertNoEditable("advice");
		assertNoEditable("remarks");
		execute("Delivery.activateDeactivateSection");
		assertEditable("advice");
		assertEditable("remarks");		
	}
	
	public void testCreateAndReadWithKeyReferences() throws Exception { 				
		// Create new one 
		execute("CRUD.new");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");
		assertValue("invoice.date", "1/1/02");						
		setValue("type.number", "1");
		setValue("number", "66");
		setValue("description", "JUNIT");
		execute("CRUD.save");
		assertNoErrors(); 
		assertValue("invoice.year", "");
		assertValue("invoice.number", "");						
		assertValue("type.number", "");	
		assertValue("number", "");
		assertValue("description", "");		
		// Searching the just created
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");						
		setValue("type.number", "1");		
		setValue("number", "66");				
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("invoice.year", "2002");
		assertValue("invoice.number", "1");				
		assertValue("invoice.date", "1/1/02");		
		assertValue("type.number", "1");
		assertValue("number", "66");		
		assertValue("description", "JUNIT");				
		assertNoEditable("invoice.year");
		assertNoEditable("invoice.number");
		assertNoEditable("type");
		assertNoEditable("number");
		assertEditable("description");
																
		// Delete it
		execute("CRUD.delete");													
		assertNoErrors();
		assertMessage("Delivery deleted successfully");
	}
	
	public void testConverterWithMetaSets() throws Exception { 				
		// Creating new
		execute("CRUD.new");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");						
		setValue("type.number", "1");
		setValue("number", "66");
		setValue("description", "JUNIT");
		setValue("distance", usesAnnotatedPOJO()?"1":"2"); // National, in database 'N'
		execute("CRUD.save");
		assertNoErrors(); 
		assertValue("invoice.year", "");
		assertValue("invoice.number", "");						
		assertValue("type.number", "");	
		assertValue("number", "");
		assertValue("description", "");
		assertValue("distance", usesAnnotatedPOJO()?"":"0");		
		// Search just created
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");						
		setValue("type.number", "1");		
		setValue("number", "66");				
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("invoice.year", "2002");
		assertValue("invoice.number", "1");						
		assertValue("type.number", "1");
		assertValue("number", "66");		
		assertValue("description", "JUNIT");		
		assertValue("distance", usesAnnotatedPOJO()?"1":"2");
		assertNoErrors();
		
		// Verifying database value
		Query query = XPersistence.getManager().createNativeQuery("select d.distance from XAVATEST.Delivery as d where "
				+ "invoice_year=2002 and invoice_number=1 and type=1 and number=66");		
		String distanceDB = (String) query.getSingleResult();
		assertEquals("distance in database incorrect", "N", distanceDB);
																		
		// Delete
		execute("CRUD.delete");													
		assertNoErrors();
		assertMessage("Delivery deleted successfully");
	}
	
	
	public void testDeleteSelectedOnesAndOrderBy() throws Exception {  
		// Creating new
		execute("CRUD.new");
		setValue("invoice.year", "2009");
		setValue("invoice.number", "1");						
		setValue("type.number", "1");
		setValue("number", "1");
		setValue("description", "JUNIT");
		execute("CRUD.save");
		assertNoErrors(); 
		
		// To list mode and order
		execute("Mode.list");
		assertActions(listActions); 
		execute("List.orderBy", "property=invoice.year"); // ascending
		execute("List.orderBy", "property=invoice.year"); // descending
		assertNoErrors();
		
		// Delete					
		assertValueInList(0, "invoice.year", "2009");
		assertValueInList(0, "invoice.number", "1");
		assertValueInList(0, "type.number", "1");
		assertValueInList(0, "number", "1");
		
		checkRow(0);
		
		execute("CRUD.deleteSelected");
		assertNoErrors();
		assertRowUnchecked(0);
		
		// Verifying that it is deleted		
		Query query = XPersistence.getManager().createQuery("from Delivery d where "
				+ "d.invoice.year=2009 and d.invoice.number=1 and d.type.number=1 and d.number=1");		
		if (!query.getResultList().isEmpty()) {
			fail("Delivery would be deleted and it is not the case");
		}
	}
		
	public void testInEntityReferencesNoDefaultValues() throws Exception { 
		execute("CRUD.new");
		assertValue("invoice.year", "");
		assertValue("invoice.number","");
		assertValue("invoice.date", "");
		assertValue("invoice.yearDiscount", "");
		assertNoErrors();
	}
	
	public void testReferencesIfKeyNotExists() throws Exception { 
		execute("CRUD.new");		
		setValue("invoice.year", "2004"); // We supose that not exists
		assertValue("invoice.yearDiscount", "400.00"); 		
		setValue("invoice.number", "907"); // We supose that not exists		
		assertError("Invoice with key {year=2004, number=907} not found");
				
		// The reference datas are deleted in screen
		assertValue("invoice.year", "");
		assertValue("invoice.number","");
		assertValue("invoice.date", "");
		assertValue("invoice.yearDiscount", "");		
	}
	
	public void testViewPropertyAndHideMembers() throws Exception { 
		execute("CRUD.new");
		assertValue("deliveredBy", usesAnnotatedPOJO()?"":"0");
		assertNotExists("employee");
		assertNotExists("carrier.number");
		
		setValue("deliveredBy", usesAnnotatedPOJO()?"0":"1");
		assertExists("employee");
		assertNotExists("carrier.number");
		
		setValue("deliveredBy", usesAnnotatedPOJO()?"1":"2");
		assertNotExists("employee");		
		assertExists("carrier.number");		
		
		setValue("deliveredBy", usesAnnotatedPOJO()?"":"0");
		assertNotExists("employee");
		assertNotExists("carrier.number");
		
		setValue("deliveredBy", usesAnnotatedPOJO()?"1":"2");
		assertNotExists("employee");
		assertExists("carrier.number");
				
		execute("CRUD.new");
		assertValue("deliveredBy", usesAnnotatedPOJO()?"":"0");
		assertNotExists("employee");
		assertNotExists("carrier.number");			
	}
	
	public void testEnvironmentVariablesModule() throws Exception {   
		// Verifying if works the action search special for this module 

		// Creating
		execute("CRUD.new");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");						
		setValue("type.number", "1");
		setValue("number", "61");
		setValue("description", "JUNIT WITHOUT DELIVEREDBY");
		execute("CRUD.save");
		assertNoErrors();
		
		execute("CRUD.new");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");						
		setValue("type.number", "1");
		setValue("number", "62");
		setValue("description", "JUNIT BY EMPLOYEE");
		setValue("deliveredBy", usesAnnotatedPOJO()?"0":"1");
		setValue("employee", "JUNIT EMPLOYEE");		
		execute("CRUD.save");
		assertNoErrors(); 
				
		execute("CRUD.new");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");						
		setValue("type.number", "1");
		setValue("number", "63");
		setValue("description", "JUNIT BY CARRIER");
		setValue("deliveredBy", usesAnnotatedPOJO()?"1":"2");
		setValue("carrier.number", "1"); 		
		execute("CRUD.save");
		assertNoErrors();
				
		// Reading and verifying
		execute("CRUD.new");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");						
		setValue("type.number", "1");
		setValue("number", "63");
		execute("CRUD.refresh");		
		assertValue("description", "JUNIT BY CARRIER");		
		assertExists("carrier.number");						    
		assertNotExists("employee");
		assertValue("carrier.number", "1");
				
		execute("CRUD.new");
		setValue("invoice.year", "2002"); 
		setValue("invoice.number", "1");						
		setValue("type.number", "1");
		setValue("number", "62");
		execute("CRUD.refresh");		
		assertValue("description", "JUNIT BY EMPLOYEE");
		assertNotExists("carrier.number");
		assertExists("employee");
		assertValue("employee", "JUNIT EMPLOYEE");
				
		execute("CRUD.new");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");						
		setValue("type.number", "1");
		setValue("number", "61");
		execute("CRUD.refresh");		
		assertValue("description", "JUNIT WITHOUT DELIVEREDBY");		
		assertNotExists("carrier.number");
		assertNotExists("employee");
						
		// Delete
		execute("CRUD.delete");
		assertMessage("Delivery deleted successfully");
		
		execute("CRUD.new");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");						
		setValue("type.number", "1");
		setValue("number", "62");
		execute("CRUD.refresh");				
		execute("CRUD.delete");
		assertMessage("Delivery deleted successfully");
		
		execute("CRUD.new");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");						
		setValue("type.number", "1");
		setValue("number", "63");
		execute("CRUD.refresh");						
		execute("CRUD.delete");			
		assertMessage("Delivery deleted successfully");
	}
	
	public void testMultipleMappingProperty() throws Exception {   				
		// Creating new
		execute("CRUD.new");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");						
		setValue("type.number", "1");
		setValue("number", "66");
		setValue("date", "2/22/97");
		setValue("description", "JUNIT");
		execute("CRUD.save");
		assertNoErrors();  
		assertValue("invoice.year", "");
		assertValue("invoice.number", "");						
		assertValue("type.number", "");	
		assertValue("number", "");
		assertValue("date", getCurrentDate());
		assertValue("description", "");		
		// Search just created
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");						
		setValue("type.number", "1");		
		setValue("number", "66");	
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("invoice.year", "2002");
		assertValue("invoice.number", "1");						
		assertValue("type.number", "1");
		assertValue("number", "66");		
		assertValue("date", "2/22/97");
		assertValue("description", "JUNIT");		
		assertNoErrors();

		// Verifying if date property is well in list 
		// Only works if there are lest than 11 object (because see in first page)		
		execute("Mode.list"); 
		assertActions(listActions);   
		assertNoErrors();
		int quantity = getListRowCount();
		boolean found = false;
		int i = 0;
		for (i = 0; i < quantity; i++) { 
			String number = getValueInList(i, "number");						
			if ("66".equals(number)) {				
				assertValueInList(i, "date", "2/22/97");
				found = true;
				break;
			}			
		}
		if (!found) {
			fail("It is necessary that exists delivery 66 in list and there are al least 11 deliveries");		
		}
				
		execute("List.viewDetail", "row=" + i);
																		
		// Delete
		execute("CRUD.delete");
		assertNoErrors();
		assertMessage("Delivery deleted successfully");
	}
	
	public void testCalculatedValueDependentOnChangePropertyOnChangeAndPropertyOnChangeDepedentOnPropertyOnChange() throws Exception { 
		execute("CRUD.new");
		assertValue("distance", usesAnnotatedPOJO()?"":"0");
		assertValue("vehicle", "");
		assertValue("transportMode", "");
		setValue("distance", usesAnnotatedPOJO()?"0":"1"); // Local
		assertValue("distance", usesAnnotatedPOJO()?"0":"1");
		assertValue("vehicle", "MOTORBIKE");
		assertValue("transportMode", "STREET/ROAD"); 
		assertValue("driverType", "ANY");
		setValue("distance", usesAnnotatedPOJO()?"1":"2"); // National
		assertValue("distance", usesAnnotatedPOJO()?"1":"2");
		assertValue("vehicle", "CAR");
		assertValue("transportMode", "HIGHWAY");
		assertValue("driverType", "DRIVER");
		setValue("distance", usesAnnotatedPOJO()?"":"0"); // Void
		assertValue("distance", usesAnnotatedPOJO()?"":"0");
		assertValue("vehicle", "");
		assertValue("transportMode", "");
		assertValue("driverType", "DRIVERX");	
	}

	public void testOnChangeWithQualifiedProperty() throws Exception {  
		execute("CRUD.new");		
		// Left from field
		assertValue("remarks", "No remarks");
		setValue("remarks", "");
		setValue("invoice.year", "2004");		
		setValue("invoice.number", "2"); 
		assertValue("remarks", "No remarks");
		setValue("remarks", "");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");
		assertValue("remarks", "First invoice of year");
		setValue("remarks", "");
		setValue("invoice.number", "2");
		assertValue("remarks", "No remarks");
		
		// Searching with reference search button		
		setValue("remarks", "");
		searchInvoiceWithList("2004", "2");
		assertValue("invoice.year", "2004");		
		assertValue("invoice.number", "2"); 
		assertValue("remarks", "No remarks");
		setValue("remarks", "");
		
		searchInvoiceWithList("2002", "1");
		assertValue("invoice.year", "2002");		
		assertValue("invoice.number", "1"); 
		assertValue("remarks", "First invoice of year");
		setValue("remarks", "");
		
		searchInvoiceWithList("2004", "2");
		assertValue("invoice.year", "2004"); 		
		assertValue("invoice.number", "2"); 
		assertValue("remarks", "No remarks");							
	}
	
	public void testOnChangeDescriptionsListKey_messagesInChangeAction() throws Exception { 
		execute("CRUD.new");		
		assertValue("remarks", "No remarks");
		setValue("deliveredBy", usesAnnotatedPOJO()?"1":"2");
		assertNoMessages();
		setValue("carrier.number", "3"); 
		assertMessagesCount(1);
		assertMessage("Carrier changed");		
		assertValue("remarks", "The carrier is 3");				
		setValue("carrier.number", "2");		
		assertValue("remarks", "The carrier is 2");				
	}
	
	public void testHideInSection() throws Exception { 
		execute("CRUD.new");		
		assertExists("remarks");
		execute("Remarks.hideRemarks");
		assertNotExists("remarks");
		execute("Remarks.showRemarks");
		assertExists("remarks");
		
		execute("Remarks.hideRemarks");
		assertNoErrors();
		assertNotExists("remarks");
		assertExists("advice");
		assertExists("shortcut");
		
		execute("Delivery.hideAdvice");
		assertNoErrors();
		assertNotExists("remarks");
		assertNotExists("advice");
		assertExists("shortcut");		
		
		execute("Delivery.hideShortcut");
		assertNoErrors();
		assertNotExists("remarks");
		assertNotExists("advice");
		assertNotExists("shortcut");
		assertAction("Delivery.hideShortcut"); // Because when it failed there are no errors
						// but the sections and bottom actions are not displayed		
	}
	
	public void testI18nOfValidValues_descriptionsListWithOrderAndNoCondition() throws Exception {
		// I18n of ValidValues
		execute("CRUD.new");
		String [][] distanceValues = {
			{usesAnnotatedPOJO()?"":"0", ""},
			{usesAnnotatedPOJO()?"0":"1", "Lokal"},			
			{usesAnnotatedPOJO()?"1":"2", "Nachional"}, 
			{usesAnnotatedPOJO()?"2":"3", "Internachional"}
		};
		assertValidValues("distance", distanceValues);
		
		// DescriptionsList order			
		String [] types = getKeysValidValues("type.number");		
		int previous = Integer.MAX_VALUE;
		for (int i=1; i<types.length; i++) { // 0 position is empty
			int current = Integer.parseInt(types[i]);
			assertTrue("delivery types must be in descending order by number", current < previous);
			previous = current;
		}
		
	}
	
	public void testViewPropertyInSectionDefaultCalcultarAndValidators() throws Exception { 
		execute("CRUD.new");		
		assertExists("advice");
		assertValue("advice", "IF YOU DRINK DO NOT DRIVE");
		setValue("advice", "");
		execute("CRUD.save");
		assertError("Value for Advice in Delivery is required");
	}
	
	public void testEditableAffectsSection() throws Exception {
		execute("Mode.detailAndFirst");
		assertEditable("description"); // out of section  
		assertEditable("advice"); // in section
		execute("EditableOnOff.setOff");
		assertNoEditable("description"); // out of section
		assertNoEditable("advice"); // in section				
	}
	
	public void testValidValuesInList() throws Exception {  
		int quantity = getListRowCount();
		assertTrue("For this test is needed at least one created delivery", quantity > 0);
		Collection values = new ArrayList();
		values.add("Lokal");
		values.add("Nachional");
		values.add("Internachional");
		boolean thereIsOne = false;
		for (int i=0; i<quantity; i++) {
			String value = getValueInList(i, "distance");			
			if (Is.emptyString(value)) continue;
			if (values.contains(value)) {
				thereIsOne = true;
				continue;
			}
			fail("Only the next values are valid: " + values);
		}
		assertTrue("For this test is need at least one delivery with value in 'distance' property", thereIsOne);   
	}
	 
	public void testSetValueAgainstPropertiesOfSectionsHiddenAndShowed() throws Exception {
		execute("Remarks.hideRemarks");
		execute("CRUD.new");
		assertNotExists("remarks");
		execute("Remarks.showRemarks");
		assertExists("remarks");		
		execute("Remarks.setRemarks");
		assertValue("remarks", "Hell in your eyes");	
	}
	
	public void testGeneratePdf() throws Exception {
		execute("Print.generatePdf");		
		assertContentTypeForPopup("application/pdf");
	}
		
	private String getCurrentDate() {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		return df.format(new java.util.Date());
	}
		
	private void searchInvoiceWithList(String year, String number) throws Exception {
		execute("Reference.search", "keyProperty=xava.Delivery.invoice.number");
		setConditionValues(
			new String [] { year, number, "", "true" }
		);		
		execute("List.filter");		
		assertListRowCount(1); 
		execute("ReferenceSearch.choose", "row=0");							
	}
	
	private void assertNoType(String type) throws Exception {
		String [] types = getKeysValidValues("type.number");		
		assertTrue(type + " not expected", !Arrays.asList(types).contains(type));
	}

	private void assertType(String type) throws Exception {
		String [] types = getKeysValidValues("type.number");		
		assertTrue(type + " expected", Arrays.asList(types).contains(type));
	}
	
	public void testNewGoFirstSection() throws Exception {
		execute("CRUD.new");
		assertExists("advice");
		assertNotExists("incidents");
		execute("Sections.change", "activeSection=1");
		assertExists("incidents");
		assertNotExists("advice");
		execute("CRUD.new");
		assertExists("advice");
		assertNotExists("incidents");
	}
	
	public void testFilterDescriptionsListAndEnumLetterType() throws Exception { 
		assertLabelInList(3, "Description of Type");
		assertLabelInList(7, "Distance");
		if (usesAnnotatedPOJO()) { 
			setConditionValues(new String[] { "", "", "", "1", "", "", "", "0"} );	// For annotated POJOs
		}
		else {
			setConditionValues(new String[] { "", "", "", "1", "", "", "", "1"} );	// For XML components
		}
		execute("List.filter");
		assertListRowCount(1);
	}
}
