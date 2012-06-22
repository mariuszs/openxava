package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class Product4Test extends ModuleTestBase {
	
		
	public Product4Test(String testName) {
		super(testName, "Product4");		
	}
	
	public void testCalculatedPropertyWhenAnnotatedGetters_genericI18nForTabs() throws Exception {
		assertLabelInList(2, "Family");
		assertLabelInList(3, "Subfamily");
		
		execute("CRUD.new");
		assertEditable("unitPrice");
		assertNoEditable("unitPriceInPesetas");
		assertValue("unitPrice", "");
		assertValue("unitPriceInPesetas", "");
		setValue("unitPrice", "10");
		assertValue("unitPriceInPesetas", "1,664");		
	}
	
	public void testValidationFromPrePersist() throws Exception {
		execute("CRUD.new");
		setValue("number", "666");
		setValue("description", "OPENXAVA");
		setValue("subfamily.number", "12");
		setValue("unitPrice", "300");
		execute("CRUD.save");
		assertError("You cannot sell OpenXava");		
		setValue("description", "WEBSPHERE");
		execute("CRUD.save");
		assertError("666 is not a valid value for Number of Product 4: It's number of man");
	}
		
}
