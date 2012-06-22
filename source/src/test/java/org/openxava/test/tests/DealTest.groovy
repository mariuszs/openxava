package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class DealTest extends ModuleTestBase {
	
	DealTest(String testName) {
		super(testName, "Deal")		
	}
	
	void testListWithOneToOneWithPrimaryKeyJoinColumn() {
		assertValueInList 0, 0, "1" 
		assertValueInList 0, 1, "THE BIG DEAL"
		assertValueInList 0, 2, "JUAN"
	}
	
	void testIdInsideASection() {
		execute "Mode.detailAndFirst"
		assertNoErrors() // The first attempt does not fail when the test was written, but just in case it would fail 
		execute "Mode.list"		
		execute "Mode.detailAndFirst"
		assertNoErrors()
		assertValue "id", "1"
		assertValue "name", "THE BIG DEAL"
	}
	
}
