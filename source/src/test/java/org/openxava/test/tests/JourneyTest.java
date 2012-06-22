package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class JourneyTest extends ModuleTestBase {
	
	public JourneyTest(String testName) {
		super(testName, "Journey");		
	}	
	
	public void testSearchKeyWithReferences() throws Exception {
		execute("CRUD.new");
		
		assertEditable("averageSpeed.driver.number");
		assertEditable("averageSpeed.vehicle.code");
		assertNoEditable("averageSpeed.speed");
		
		setValue("averageSpeed.driver.number", "1");
		assertValue("averageSpeed.driver.name", "ALONSO");
		
		assertFocusOn("averageSpeed.vehicle.code");		
		setValue("averageSpeed.vehicle.code", "VLV40");
		assertValue("averageSpeed.vehicle.model", "S40 T5");
		
		assertValue("averageSpeed.speed", "240");
		assertFocusOn("description");
	}
	
}
