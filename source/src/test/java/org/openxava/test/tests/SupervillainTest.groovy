package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class SupervillainTest extends ModuleTestBase {
	
	SupervillainTest(String testName) {
		super(testName, "Supervillain")		
	}
	
	void testReferenceBetweenEntitiesMappedToTheSameTable() {
		execute "List.orderBy", "property=name"
		assertValueInList 0, 0, "ESCARIANO AVIESO"
		assertValueInList 0, 1, "SUPERLOPEZ"
	}
		
	
}
