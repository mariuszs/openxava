package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class EmployeeTest extends ModuleTestBase {
	
	EmployeeTest(String testName) {
		super(testName, "Employee")		
	}
	
	void testListWithOneToOneWithPrimaryKeyJoinColumns() {
		assertValueInList 0, 0, "1" 
		assertValueInList 0, 1, "JUANITO"
		assertValueInList 0, 2, "DEVELOPER"
		assertValueInList 0, 3, "12 YEARS"
	}
	
}
