package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class CarrierWithReadOnlyCalculatedFellowsTest extends ModuleTestBase {
	
	public CarrierWithReadOnlyCalculatedFellowsTest(String testName) {
		super(testName, "CarrierWithReadOnlyCalculatedFellows");		
	}
	
	public void testCheckboxAlwaysPresentEvenInCalculatedCollectionWithoutListActions() throws Exception {
		execute("Mode.detailAndFirst");
		checkRowCollection("fellowCarriersCalculated", 0);
	}
		
}
