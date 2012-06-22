package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class WarehouseDetailListTest extends ModuleTestBase {
	
		
	public WarehouseDetailListTest(String testName) {
		super(testName, "WarehouseDetailList");		
	}
		
	public void testDetailListModeController() throws Exception {  
		assertAction("DetailList.detailAndFirst");
		assertNoAction("DetailList.list");
		assertNoAction("Mode.split");
		
		assertAction("List.filter"); // List is shown
		assertNotExists("zoneNumber"); // Detail not is shown
		
		execute("DetailList.detailAndFirst");
		assertNoAction("DetailList.detailAndFirst");
		assertAction("DetailList.list");
		assertNoAction("Mode.split");
		
		assertNoAction("List.filter"); // List is shown
		assertExists("zoneNumber"); // Detail not is shown		
	}
	
}
