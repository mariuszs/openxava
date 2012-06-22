package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class WarehouseDetailOnlyTest extends ModuleTestBase {
	
		
	public WarehouseDetailOnlyTest(String testName) {
		super(testName, "WarehouseDetailOnly");		
	}
			
	public void testDetailOnlyModeController() throws Exception {  
		assertNoAction("Mode.detailAndFirst");
		assertNoAction("Mode.list");
		assertNoAction("Mode.split");
		
		assertNoAction("List.filter"); // List is not shown
		assertExists("zoneNumber"); // Detail is shown
	}
	
}
