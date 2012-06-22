package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class ArtistTest extends ModuleTestBase {
	
	ArtistTest(String testName) {
		super(testName, "Artist")		
	}
	
	void testBeanValidationJSR303() { 
		execute "Mode.detailAndFirst"
		setValue "age", "99"		
		execute "CRUD.save"
		assertError "99 is not a valid value for Age of Artist: must be less than or equal to 90"
		assertErrorImage();
	}
			
	private void assertErrorImage() {
		assertTrue "Error image not present", getHtml().contains("/OpenXavaTest/xava/images/error.gif");
	}
	
}
