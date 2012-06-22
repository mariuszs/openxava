package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class Invoice2Test extends ModuleTestBase {
	
	public Invoice2Test(String testName) {
		super(testName, "Invoice2");		
	}
	
	public void testDependentEditorsForHiddenPropertiesInCollectionElement() throws Exception {
		execute("CRUD.new");
		execute("InvoiceDetail2.new", "viewObject=xava_view_details");
		assertNotExists("familyList");
		assertNotExists("productList");
		execute("InvoiceDetail2.showProductList");
		assertExists("familyList");
		assertExists("productList");
		assertValidValuesCount("productList", 1);
		setValue("familyList", "1");
		assertValidValuesCount("productList", 7);
	}
	
	public void testTouchContainerFromCallback() throws Exception {
		if (!usesAnnotatedPOJO()) return; // This case is only implemented in JPA
		execute("CRUD.new");
		setValue("number", "66");
		setValue("vatPercentage", "16");
		setValue("customer.number", "1");
		assertCollectionRowCount("details", 0);
		
		// Creating a new detail
		execute("InvoiceDetail2.new", "viewObject=xava_view_details");
		assertNotExists("details.invoice.year"); 
		setValue("quantity", "7");
		setValue("unitPrice", "8");
		assertValue("amount", "56.00");
		setValue("product.number", "1");
		assertValue("product.description", "MULTAS DE TRAFICO");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("details", 1);
		execute("CRUD.refresh");
		assertValue("amountsSum", "56.00");
		
		// Creating another one
		execute("InvoiceDetail2.new", "viewObject=xava_view_details");
		setValue("quantity", "10");
		setValue("unitPrice", "10");
		assertValue("amount", "100.00");
		setValue("product.number", "1");
		assertValue("product.description", "MULTAS DE TRAFICO");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("details", 2);
		execute("CRUD.refresh");
		assertValue("amountsSum", "156.00");
		
		// Modifiying
		execute("Collection.edit", "row=1,viewObject=xava_view_details");
		setValue("quantity", "20");
		setValue("unitPrice", "10");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("details", 2);
		execute("CRUD.refresh");
		assertValue("amountsSum", "256.00");
		
		// Removing
		execute("Collection.edit", "row=1,viewObject=xava_view_details");
		setValue("quantity", "20");
		setValue("unitPrice", "10");
		execute("Collection.remove");
		assertNoErrors();
		assertCollectionRowCount("details", 1);
		execute("CRUD.refresh");
		assertValue("amountsSum", "56.00");
		
		execute("CRUD.delete");
		assertNoErrors();		
	}
	
	public void testInjectPropertiesOfContainerInOnCreateCalculatorOfAggregate() throws Exception {
		execute("CRUD.new");
		setValue("number", "66");
		setValue("vatPercentage", "16");
		setValue("customer.number", "1");
		assertCollectionRowCount("details", 0);
		execute("InvoiceDetail2.new", "viewObject=xava_view_details");
		setValue("quantity", "7");
		setValue("unitPrice", "8");
		assertValue("amount", "56.00");
		setValue("product.number", "1");
		assertValue("product.description", "MULTAS DE TRAFICO");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("details", 1);
		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	public void testCollectionOrderedByAPropertyOfAReference_valueOfNestedRerenceInsideAnEmbeddedCollection() throws Exception {
		execute("CRUD.new");
		setValue("year", "2002");
		setValue("number", "1");
		execute("CRUD.refresh");
		assertCollectionRowCount("details", 2);
		assertValueInCollection("details", 0, "product.description", "XAVA");
		assertValueInCollection("details", 1, "product.description", "IBM ESERVER ISERIES 270");
		
		execute("Collection.edit", "row=0,viewObject=xava_view_details");
		assertValue("product.description", "XAVA");
		assertValue("product.family.description", "SOFTWARE");
		closeDialog();
		
		execute("Collection.edit", "row=1,viewObject=xava_view_details");
		assertValue("product.description", "IBM ESERVER ISERIES 270");
		assertValue("product.family.description", "HARDWARE");		
	}
	
	public void testMinSizeForCollections() throws Exception {
		execute("CRUD.new");
		setValue("number", "66");
		setValue("vatPercentage", "18");
		setValue("customer.number", "1");
		execute("CRUD.save");
		assertError("Value for Details in Invoice 2 is required");
	}
							
}
