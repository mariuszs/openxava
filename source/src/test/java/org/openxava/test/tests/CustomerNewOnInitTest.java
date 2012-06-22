package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CustomerNewOnInitTest extends ModuleTestBase {

	public CustomerNewOnInitTest(String testName) {
		super(testName, "CustomerNewOnInit");		
	}
	
	public void testNewOnInit() throws Exception {
		assertNoErrors();
		assertAction("Mode.list");
		assertNoAction("Mode.detailAndFirst");
	}
	
	public void testGetValueFromAGroupInSectionAfterNew() throws Exception {
		setValue("name", "Juanillo");
		execute("CustomerNewOnInit.getName");		
		assertMessage("The name is Juanillo");
	}
				
}
