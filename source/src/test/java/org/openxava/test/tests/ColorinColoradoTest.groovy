package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class ColorinColoradoTest extends ModuleTestBase {
	
	ColorinColoradoTest(String testName) {
		super(testName, "ColorinColorado")		
	}
	
	void testAfterEachRequestAction() throws Exception {
		assertValue "name", "NULLCOLORADO" 
		setValue "name", ""
		execute "ColorinColorado.fillName"		
		assertValue "name", "COLORIN COLORADO"
	}
	
}
