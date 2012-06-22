package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class CustomerMinimumTest extends ModuleTestBase {
	
	CustomerMinimumTest(String testName) {
		super(testName, "CustomerMinimum")		
	}
	
	void testReadAnEmbeddedWithAllItsMembersNull() { 
		execute "CRUD.new"
		setValue "number", "66"		
		setValue "name", "JUNIT TEST"
		execute "CRUD.save"
		assertNoErrors();
		
		changeModule "CustomerSimple"
		execute "CRUD.new"
		setValue "number", "66"
		execute "CRUD.refresh"
		assertNoErrors()
		assertValue "name", "Xunit Test"
		
		execute "CRUD.delete"
		assertNoErrors()
	}
			
}
