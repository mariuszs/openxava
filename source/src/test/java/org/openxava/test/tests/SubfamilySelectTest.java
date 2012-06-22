package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class SubfamilySelectTest extends ModuleTestBase {
	
	public SubfamilySelectTest(String testName) {
		super(testName, "SubfamilySelect");		
	}
	
	public void testTabWithSelect() throws Exception {
		assertNoErrors();
		assertListNotEmpty();
	}
	
}
