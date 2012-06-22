package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class GoToSellerJSPTest extends ModuleTestBase {
	
	GoToSellerJSPTest(String testName) {
		super(testName, "GoToSellerJSP")		
	}
	
	void testDialogInAModuleWithCustomizedViewCalledFromAnotherModule() {
		assertValue "name", "" 
		execute "GoToSellerJSP.goSellerJSP"
		execute "Mode.detailAndFirst"
		assertValue "name", "MANUEL CHAVARRI"
		assertNoDialog()
		execute "SellerJSP.changeName"
		assertDialog()
		setValue "name", "MANOLO"
		execute "ChangeName.change"
		assertNoDialog()
		assertValue "name", "MANOLO"
	}
			
}
