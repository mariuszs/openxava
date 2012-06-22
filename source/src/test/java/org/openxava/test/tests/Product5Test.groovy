package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class Product5Test extends ModuleTestBase {
	
	Product5Test(String testName) {
		super(testName, "Product5")		
	}

	void testDescriptionsListDependsOnEnum() throws Exception {
		
		execute("CRUD.new");
	
		// Verifying initial state
		String [][] familyValues = [
			[ "", "" ],
			[ "0", "NONE" ],
			[ "1", "SOFTWARE" ],
			[ "2", "HARDWARE" ],
			[ "3", "SERVICIOS" ]
		]
		
		assertValidValues("family", familyValues);
		setValue("family", "0");
		
		String [][] voidValues = [
			[ "", "" ]
		];
		
		assertValue("subfamily.number", "");
		assertValidValues("subfamily.number", voidValues);
		
		// Change value
		setValue("family", "2");
		String [][] hardwareValues = [
			[ "", "" ],
			[ "12", "PC" ],
			[ "13", "PERIFERICOS" ],
			[ "11", "SERVIDORES" ]
		];
		assertValue("subfamily.number", "");
		assertValidValues("subfamily.number", hardwareValues);
		
		// Changing the value again
		setValue("family", "1");
		String [][] softwareValues = [
			[ "", "" ],
			[ "1", "DESARROLLO" ],
			[ "2", "GESTION" ],
			[ "3", "SISTEMA" ]
		];
		assertValue("subfamily.number", "");
		assertValidValues("subfamily.number", softwareValues);
	}
	
	
	void testCollectionWithLongNameStoresPreferences() { 
		execute "CRUD.new"
		assertCollectionColumnCount "productDetailsSupplierContactDetails", 2
		execute "List.customize", "collection=productDetailsSupplierContactDetails"
		execute "List.removeColumn", "columnIndex=1,collection=productDetailsSupplierContactDetails"
		assertCollectionColumnCount "productDetailsSupplierContactDetails", 1
		
		resetModule()
		
		execute "CRUD.new"
		assertCollectionColumnCount "productDetailsSupplierContactDetails", 1
		execute "List.customize", "collection=productDetailsSupplierContactDetails"
		execute "List.addColumns", "collection=productDetailsSupplierContactDetails"
		execute "AddColumns.restoreDefault"
		assertCollectionColumnCount "productDetailsSupplierContactDetails", 2
		
		resetModule()
		
		execute "CRUD.new"
		assertCollectionColumnCount "productDetailsSupplierContactDetails", 2
	}
	
	
}
