package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceNoSectionsTest extends ModuleTestBase {
	
	public InvoiceNoSectionsTest(String testName) {
		super(testName, "InvoiceNoSections");		
	}
	
	public void testSumInCollection() throws Exception { 
		execute("CRUD.new");
		execute("CRUD.search");
		setValue("year", "2004");
		setValue("number", "9");
		execute("Search.search");
		// Defined by developer
		assertCollectionRowCount("details", 2);
		assertValueInCollection("details", 0, "product.unitPrice", "11");
		assertValueInCollection("details", 1, "product.unitPrice", "20");
		assertTotalInCollection("details", "product.unitPrice", "31");
				
		// Defined by the user
		execute("List.removeColumnSum", "property=product.unitPrice,collection=details");
		assertTotalInCollection("details", "product.unitPrice", "");
		
		assertValueInCollection("details", 0, "quantity", "6");
		assertValueInCollection("details", 1, "quantity", "2");		
		assertTotalInCollection("details", "quantity", "");
		execute("List.sumColumn", "property=quantity,collection=details");
		assertTotalInCollection("details", "quantity", "8");
		
		// Stores preferences
		resetModule();
		execute("CRUD.new");
		execute("CRUD.search");
		setValue("year", "2004");
		setValue("number", "9");
		execute("Search.search");
		assertTotalInCollection("details", "product.unitPrice", "");
		assertTotalInCollection("details", "quantity", "8");
		
		// Restore initial values
		execute("List.customize", "collection=details");
		execute("List.addColumns", "collection=details");
		execute("AddColumns.restoreDefault");
		assertTotalInCollection("details", "product.unitPrice", "31");
		assertTotalInCollection("details", "quantity", "");
		
		resetModule();
		execute("CRUD.new");
		execute("CRUD.search");
		setValue("year", "2004");
		setValue("number", "9");
		execute("Search.search");
		assertTotalInCollection("details", "product.unitPrice", "31");
		assertTotalInCollection("details", "quantity", "");
	}
	
	public void testCalculatedPropertyDependingOnCollectionAndOtherProperties() throws Exception { 
		// Initial values
		execute("CRUD.new");
		execute("CRUD.search");
		setValue("year", "2004");
		setValue("number", "12");
		execute("Search.search");
		assertCollectionRowCount("details", 2);
		assertValueInCollection("details", 0, "quantity", "5");
		assertValueInCollection("details", 0, "amount", "50.00");
		assertValueInCollection("details", 1, "quantity", "5");
		assertValueInCollection("details", 1, "amount", "60.00");
		assertValue("amountsSum", "110.00");
		assertValue("vatPercentage", "13");
		assertValue("vat", "14.30");
		assertValue("total", "124.30");
		
		// If the calculated values change correctly
		setValue("vatPercentage", "14");
		assertValue("vat", "15.40");
		assertValue("total", "125.40");
		execute("Collection.edit", "row=0,viewObject=xava_view_details");
		setValue("quantity", "6");
		execute("Collection.save");
		assertValueInCollection("details", 0, "quantity", "6");
		assertValueInCollection("details", 0, "amount", "60.00");
		assertValueInCollection("details", 1, "quantity", "5");
		assertValueInCollection("details", 1, "amount", "60.00");
		assertValue("amountsSum", "120.00");
		assertValue("vatPercentage", "14");
		assertValue("vat", "16.80");
		assertValue("total", "136.80");		
		
		// Restoring original values
		execute("Collection.edit", "row=0,viewObject=xava_view_details");
		setValue("quantity", "5");
		execute("Collection.save");
		assertValueInCollection("details", 0, "quantity", "5");
		assertValueInCollection("details", 0, "amount", "50.00");
		assertValueInCollection("details", 1, "quantity", "5");
		assertValueInCollection("details", 1, "amount", "60.00");
		assertValue("amountsSum", "110.00");
		assertValue("vatPercentage", "14");
		assertValue("vat", "15.40");
		assertValue("total", "125.40");		
	}
	
}
