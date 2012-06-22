package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class FilterBySubfamilyAndYearTest extends ModuleTestBase {
	
	FilterBySubfamilyAndYearTest(String testName) {
		super(testName, "FilterBySubfamilyAndYear")		
	}
	
	void testTransientClassInheritance() {
		assertExists "year"
		assertExists "subfamily.number"		
	}
		
}
