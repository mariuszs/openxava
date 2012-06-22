package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class GoCustomerInvoicesTest extends ModuleTestBase {
	
	GoCustomerInvoicesTest(String testName) {
		super(testName, "GoCustomerInvoices")		
	}
	
	void testChangeModuleFromDialog() { 
		execute "GoCustomerInvoices.showDialog"
		assertDialog()
		assertExists "number"
		assertNoAction "List.filter"
		assertNoAction "CustomerInvoices.return"
		execute "GoCustomerInvoices.goCustomer"
		assertNoDialog()
		assertNotExists "number"
		assertAction "List.filter"
		execute "CustomerInvoices.return"
		assertDialog()
		assertExists "number"
		assertNoAction "List.filter"
		assertNoAction "CustomerInvoices.return"
		closeDialog()
		assertNoDialog()
		assertExists "number"
	}		
	
}
