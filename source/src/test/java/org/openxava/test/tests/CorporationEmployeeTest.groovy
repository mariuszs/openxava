package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class CorporationEmployeeTest extends ModuleTestBase {
	
	CorporationEmployeeTest(String testName) {
		super(testName, "CorporationEmployee")		
	}
	
	void testSimpleHTMLReport() {
		execute "Mode.detailAndFirst"
		execute "CorporationEmployee.report"
		assertNoError()
		assertTrue getPopupText().contains("<tr><td>Corporation:</td><td>RANONE</td></tr>")
	}
	
}
