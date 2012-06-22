package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class DeliveryGroupsInSectionsTest extends ModuleTestBase {
	
		
	public DeliveryGroupsInSectionsTest(String testName) {
		super(testName, "DeliveryGroupsInSections");		
	}
	
	public void testOnChangeActionOnSearchNotMoreThanOneTime() throws Exception {
		execute("Mode.detailAndFirst");
		assertNoErrors();
		assertMessagesCount(1);		
	}
	
}
