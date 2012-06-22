package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class CorporationTest extends ModuleTestBase {
	
	CorporationTest(String testName) {
		super(testName, "Corporation")		
	}
	
	void testSimpleHTMLReportWithCollections() {
		execute "Mode.detailAndFirst"
		execute "Corporation.report"
		assertNoError()
		assertTrue getPopupText().contains("<tr><td>Name:</td><td>RANONE</td></tr>") 
	}
	
}
