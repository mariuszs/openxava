package org.openxava.test.tests;



/**
 * @author Javier Paniza
 */

public class TransportChargeWithDistanceTest extends TransportChargeTestBase {
			
	public TransportChargeWithDistanceTest(String testName) {
		super(testName, "TransportChargeWithDistance");		
	}
	
	public void testValidValueInSecondLevelInList() throws Exception {
		deleteAll();
		createSome();
		execute("List.filter");
		assertListRowCount(2);
	}
	
}
