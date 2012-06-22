package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class CommentTest extends ModuleTestBase {
	
	CommentTest(String testName) {
		super(testName, "Comment")		
	}
	
	void testRequiredReference() throws Exception { 
		execute "CRUD.new"			
		execute "CRUD.save"
		assertError "Value for Issue in Comment is required"
	}
			
}
