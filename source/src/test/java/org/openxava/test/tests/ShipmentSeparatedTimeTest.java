package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */ 
public class ShipmentSeparatedTimeTest extends ModuleTestBase {
	
	public ShipmentSeparatedTimeTest(String testName) {
		super(testName, "ShipmentSeparatedTime");		
	}
	
	public void testDateTimeSeparatedCalendarEditor() throws Exception {
		execute("Mode.detailAndFirst");
		String [] emptyTime = { "", "" };
		assertValues("time", emptyTime);
		String [] time = { "5/27/09", "11:59 AM" };
		setValues("time", time);
		execute("CRUD.save");
		assertValues("time", emptyTime);
		execute("Mode.list");
		execute("Mode.detailAndFirst");
		assertValues("time", time);
		setValues("time", emptyTime);
		execute("CRUD.save");
		assertNoErrors();
	}
					
}
