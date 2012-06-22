package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class ServiceInvoiceTest extends ModuleTestBase {
	
	public ServiceInvoiceTest(String testName) {
		super(testName, "ServiceInvoice");		
	}
	
	public void testDefaultSchemaInHibernateCfg() throws Exception {
		assertListNotEmpty();
	}
	
	public void testSearchKeyMustBeEditable() throws Exception {
		execute("Mode.detailAndFirst");
		assertEditable("year");
		assertEditable("number");
	}
	
	public void testSumByDeveloperInList() throws Exception { 		
		assertListRowCount(3);
		assertValueInList(0, "amount", "790.00");
		assertValueInList(1, "amount", "1,730.00");
		assertValueInList(2, "amount", "127.86");
		assertTotalInList("amount", "2,647.86"); 
		setConditionValues("2007");
		execute("List.filter");
		assertListRowCount(2);
		assertValueInList(0, "amount", "790.00");
		assertValueInList(1, "amount", "1,730.00");
		assertTotalInList("amount", "2,520.00");
	}
	
	public void testSumByUserInList() throws Exception { 
		assertTotalInList("amount", "2,647.86");
		assertTotalInList("number", "");
		
		execute("List.sumColumn", "property=number");
		assertTotalInList("number", "6");
		execute("List.removeColumnSum", "property=amount");
		assertTotalInList("amount", "");
		
		// Verifying that the changes are stored as user preferences
		resetModule();
		assertTotalInList("amount", "");
		assertTotalInList("number", "6");
		
		// Restoring
		execute("List.customize");
		execute("List.addColumns");
		execute("AddColumns.restoreDefault");
		assertTotalInList("amount", "2,647.86");
		assertTotalInList("number", "");
		resetModule();
		assertTotalInList("amount", "2,647.86");
		assertTotalInList("number", "");		
	}

			
}
