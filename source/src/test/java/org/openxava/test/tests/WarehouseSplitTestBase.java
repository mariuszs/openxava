package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

abstract public class WarehouseSplitTestBase extends ModuleTestBase {
		
	public WarehouseSplitTestBase(String testName, String moduleName) {
		super(testName, moduleName);		
	}	
	
	public void testSplitMode() throws Exception {
		// Selecting rows
		assertListRowCount(10);
		assertValue("zoneNumber", "");
		assertValue("number", "");
		assertValue("name", "");
		assertNoEditable("zoneNumber");
		assertNoEditable("number");
		assertNoEditable("name");		
		assertSelectRow(0);
		assertSelectRow(3);
		assertListRowCount(10); // Still the list is there
		
		// List actions 
		String name = getValue("name");
		setConditionValues("1");
		execute("List.filter");
		assertListRowCount(3);
		assertValue("name", name);
		
		// Navigation
		execute("Navigation.first");
		assertRowInDetail(0);
		execute("Navigation.next");
		assertRowInDetail(1);
		
		assertHideActionsInSplitMode();
	}
	
	private void assertHideActionsInSplitMode() throws Exception {
		assertAction("Print.generatePdf");
		execute("HidingActions.hideGeneratePdf");
		assertNoAction("Print.generatePdf");
		
		assertAction("HidingActions.hideGeneratePdf");
		execute("HidingActions.hideHideGeneratePdf");
		assertNoAction("HidingActions.hideGeneratePdf");
		
		assertAction("CRUD.save");
		execute("HidingActions.hideSave");
		assertNoAction("CRUD.save");
		
		assertAction("HidingActions.hideSave");
		execute("HidingActions.hideHideSave");
		assertNoAction("HidingActions.hideSave");
	}

	private void assertSelectRow(int row) throws Exception {
		execute("List.viewDetail", "row=" + row);
		assertRowInDetail(row);
	}
	
	private void assertRowInDetail(int row) throws Exception {
		String zoneNumber = getValueInList(row, "zoneNumber");
		String number = getValueInList(row, "number");
		String name = getValueInList(row, "name");		
		assertValue("zoneNumber", zoneNumber);
		assertValue("number", number);
		assertValue("name", name);
		assertNoEditable("zoneNumber");
		assertNoEditable("number");
		assertEditable("name");				
		assertFocusOn("name");
	}
	
	
}
