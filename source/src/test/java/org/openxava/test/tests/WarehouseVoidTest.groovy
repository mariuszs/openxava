package org.openxava.test.tests;

import org.openxava.tests.*;
import org.openxava.test.model.*;

/**
 * @author Javier Paniza
 */

class WarehouseVoidTest extends ModuleTestBase {
	
	WarehouseVoidTest(String testName) {
		super(testName, "WarehouseVoid")		
	}
	
	void testFilterActionNotShownInDetail() { // A bug when before-each-request="true"
		assertNoAction "List.filter"
	}
	
}
