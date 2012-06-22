package org.openxava.test.tests;

import org.openxava.tests.*;
import com.gargoylesoftware.htmlunit.html.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceDetailsWithTotalsTest extends ModuleTestBase {
	
	public InvoiceDetailsWithTotalsTest(String testName) {
		super(testName, "InvoiceDetailsWithTotals");		
	}
	
	public void testTotalsInCollection() throws Exception { 		
		execute("Mode.detailAndFirst");		
		
		assertTotalsInCollection("details", "", "");
		
		// The .00 is because unitPrice is not formatted in the same way in persistent and
		// calculated collection. Possibly it is a bug, but until we'll fix it we send the .00 
		assertTotalsInCollection("calculatedDetails", ".00", "Amounts sum"); 		
		
		assertNoAction("List.removeColumnSum");		
		
		execute("Navigation.next");

		assertValueInCollection("details", 0, "deliveryDate", "7/26/04");
		assertTotalInCollection("details", "deliveryDate", "7/26/04"); 		

		assertValueInCollection("details", 0, "product.unitPrice", "11");
		assertTotalInCollection("details", "product.unitPrice", "11");
		
		assertValueInCollection("details", 0, "amount", "11.00");
		assertTotalInCollection("details",    "amount", "11.00");
		assertTotalInCollection("details", 1, "amount",  "4.62");
		assertTotalInCollection("details", 2, "amount", "15.62");
		
		execute("Navigation.previous");
		
		execute("List.customize", "collection=details");
		execute("List.removeColumn", "columnIndex=2,collection=details");
		
		assertTotalInCollection("details", 1, 3,   "400.00");
		assertTotalInCollection("details", 2, 3, "2,900.00");		
		
		execute("Print.generatePdf", "viewObject=xava_view_details");
		assertContentTypeForPopup("application/pdf");
		
		execute("List.removeColumn", "columnIndex=3,collection=details");
		assertTotalInCollection("details", "deliveryDate", "12/15/10");			
	}
	
	public void testTotalsAndAddActionInCollectionFrame() throws Exception { 
		execute("Mode.detailAndFirst");
		execute("List.sumColumn", "property=quantity,collection=details");
		assertTotalsInFrameOfCollection("details", "(2)    Delivery date: 12/15/10    Product unit price sum: 20.00    Amounts sum: 2,500.00    V.A.T.: 400.00    Total: 2,900.00    Sum of Quantity: 150", false);
		execute("List.removeColumnSum", "property=quantity,collection=details");
		assertTotalsInFrameOfCollection("calculatedDetails", "(2)    Delivery date: 12/15/10    Product unit price sum: 20.00    Amounts sum: 2,500.00    V.A.T.: 400.00    Total: 2,900.00", true);
		
		// Calling to a collection action does not show the header
		HtmlElement header = getHtmlPage().getElementById("ox_OpenXavaTest_InvoiceDetailsWithTotals__frame_detailsheader");
		assertEquals("", header.asText());
		execute("List.filter", "collection=details");
		assertEquals("", header.asText());
	}
		
	public void testFrameTotalsUpdated() throws Exception { 
		execute("CRUD.new");
		execute("CRUD.search");
		setValue("year", "2004");
		setValue("number", "10");
		execute("Search.search");
		assertTotalInCollection("details", 2, "amount", "1,403.02");
		hideCollection("details");
		HtmlElement header = getHtmlPage().getElementById("ox_OpenXavaTest_InvoiceDetailsWithTotals__frame_detailsheader");
		assertTrue(header.asText().endsWith("1,403.02"));
		showCollection("details");
		execute("Collection.edit", "row=0,viewObject=xava_view_details");
		setValue("quantity", "65");
		execute("Collection.save");		
		assertTotalInCollection("details", 2, "amount", "1,390.04");
		hideCollection("details");
		header = getHtmlPage().getElementById("ox_OpenXavaTest_InvoiceDetailsWithTotals__frame_detailsheader");
		assertTrue("Unexpected end: " + header.asText(),  header.asText().endsWith("1,390.04"));
		showCollection("details");
		execute("Collection.edit", "row=0,viewObject=xava_view_details");
		setValue("quantity", "66");
		execute("Collection.save");
		assertTotalInCollection("details", 2, "amount", "1,403.02");
		hideCollection("details");
		header = getHtmlPage().getElementById("ox_OpenXavaTest_InvoiceDetailsWithTotals__frame_detailsheader");
		assertTrue("Unexpected end: " + header.asText(),  header.asText().endsWith("1,403.02"));
		execute("Navigation.previous");
		header = getHtmlPage().getElementById("ox_OpenXavaTest_InvoiceDetailsWithTotals__frame_detailsheader");
		assertTrue("Unexpected end: " + header.asText(), !header.asText().endsWith("1,403.02"));
		showCollection("details");
	}	
	
	private void assertTotalsInFrameOfCollection(String collection, String totals, boolean addAction) throws Exception { 
		HtmlElement header = getHtmlPage().getElementById("ox_OpenXavaTest_InvoiceDetailsWithTotals__frame_" + collection + "header");
		assertEquals("", header.asText());
		hideCollection(collection); 
		assertEquals(totals, header.asText());
		reload(); 
		header = getHtmlPage().getElementById("ox_OpenXavaTest_InvoiceDetailsWithTotals__frame_" + collection + "header");
		assertEquals(totals, header.asText());
		if (addAction) assertTrue(header.asXml().contains("executeAction('OpenXavaTest', 'InvoiceDetailsWithTotals', '', false, 'Collection.new', 'viewObject=xava_view_" + collection));
		else assertTrue(!header.asXml().contains("executeAction('OpenXavaTest', 'InvoiceDetailsWithTotals', '', false, 'Collection.new', 'viewObject=xava_view_" + collection));
		showCollection(collection);
	}
	
	private void showCollection(String collection) throws Exception { 
		clickOn(collection, "show");
	}
	
	private void hideCollection(String collection) throws Exception { 
		clickOn(collection, "hide");
	}
	
	private void clickOn(String collection, String button) throws Exception { 
		HtmlElement link = getHtmlPage().getElementById("ox_OpenXavaTest_InvoiceDetailsWithTotals__frame_" + collection + button).getChildElements().iterator().next();
		link.click();				
	}
	
	private void assertTotalsInCollection(String collection, String unitPriceSuffix, String amountsSumLabel) throws Exception {
		assertCollectionRowCount(collection, 2);
		
		assertValueInCollection(collection, 0, "deliveryDate", "11/11/11");
		assertValueInCollection(collection, 1, "deliveryDate", "12/15/10");
		assertTotalInCollection(collection, "deliveryDate", "12/15/10"); 		

		assertValueInCollection(collection, 0, "product.unitPrice", "20" + unitPriceSuffix);
		assertValueInCollection(collection, 1, "product.unitPrice", "0" + unitPriceSuffix);
		assertTotalInCollection(collection, "product.unitPrice", "20" + unitPriceSuffix);
		
		assertValueInCollection(collection, 0, "amount", "2,000.00");
		assertValueInCollection(collection, 1, "amount",   "500.00");
		assertTotalInCollection(collection,    "amount", "2,500.00");
		assertTotalInCollection(collection, 1, "amount",   "400.00");
		assertTotalInCollection(collection, 2, "amount", "2,900.00");
		
		// Labels
		assertTotalInCollection(collection, "product.description", "Product unit price sum");
		assertTotalInCollection(collection, "quantity", amountsSumLabel);
		assertTotalInCollection(collection, 1, "quantity", "V.A.T.");
		assertTotalInCollection(collection, 2, "quantity", "Total");
	}
	
}
