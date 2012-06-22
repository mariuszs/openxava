package org.openxava.test.tests

import org.openxava.tests.*

/**
 * @author Javier Paniza
 */

class MotoTest extends ModuleTestBase {
	
	MotoTest(String testName) {
		super(testName, "Moto")		
	}
	
	void testEntityInAPackageNotNamedModel() throws Exception {
		assertListNotEmpty()
		String make = getValueInList(0, 0)
		String model = getValueInList(0, 1)
		
		execute "Mode.detailAndFirst"
		assertValue "make", make
		assertValue "model", model		
	}
	
}
