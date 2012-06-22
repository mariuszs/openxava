package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

class JournalTest extends ModuleTestBase {
	
	JournalTest(String testName) {
		super(testName, "Journal") 		
	}
	
	void testEntityValidatorInACascadeAllCollectionElementWithAReferenceToParentThatNotMatchWithEntityName() {
		execute "Mode.detailAndFirst"
		execute "Collection.edit", "row=0,viewObject=xava_view_entries"
		execute "Collection.save"
		assertNoErrors()
	}
	
}
