package org.openxava.test.tests;

/**
 * @author Javier Paniza
 */

public class WarehouseSplitTest extends WarehouseSplitTestBase {
	
		
	public WarehouseSplitTest(String testName) {
		super(testName, "WarehouseSplit");		
	}
		
	public void testSplitMode() throws Exception {
		assertNoAction("Mode.detailAndFirst");
		assertNoAction("Mode.list");
		assertNoAction("Mode.split");
		
		assertAction("List.filter"); // List is shown
		assertExists("zoneNumber"); // Detail is shown

		super.testSplitMode();
	}
	
	public void testCheckedRows() throws Exception {
		checkRow(1);
		checkRow(3);
		execute("List.filter");
		assertRowsChecked(1, 3);;		
		uncheckRow(1);
		uncheckRow(3);
		assertRowUnchecked(1);
		assertRowUnchecked(3);
		execute("List.filter");
		assertRowUnchecked(1);
		assertRowUnchecked(3);		
	}
		
}
