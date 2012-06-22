package org.openxava.test.tests

import org.openxava.jpa.*;
import org.openxava.tests.ModuleTestBase 

/**
 * 
 * @author Javier Paniza
 */
class BuildingTest extends ModuleTestBase {
	
	BuildingTest(String testName) {
		super(testName, "Building")		
	}

	void testAttributeOverridesOnEmbeddable() {		
		assertValueInList(0, "name", "MY OFFICE")
		assertValueInList(0, "address.street", "CUBA")
		assertValueInList(0, "address.zipCode", "49003")
		assertValueInList(0, "address.city", "VALENCIA")		
	}
	
}
