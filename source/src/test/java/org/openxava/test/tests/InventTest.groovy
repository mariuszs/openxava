package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class InventTest extends ModuleTestBase {
	
	InventTest(String testName) {
		super(testName, "Invent")		
	}
	
	void testTabDefaultValuesNotAffectCollections() { 
		assertListRowCount 1 
		execute "Mode.detailAndFirst"
		assertValue "description", "INVENT 1"
		assertCollectionRowCount "details", 2		
	}
	
}
