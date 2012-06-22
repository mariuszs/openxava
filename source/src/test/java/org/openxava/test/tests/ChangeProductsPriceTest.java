package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ChangeProductsPriceTest extends ModuleTestBase {
	
	private String [] detailActions = {
		"Navigation.previous",
		"Navigation.first",
		"Navigation.next",		
		"Mode.list",									
		"Mode.split",
		"ChangeProductsPrice.save",
		"ChangeProductsPrice.editDescription"
	};
	
	private String [] listActions = {
		"Mode.detailAndFirst",
		"Mode.split", 
		"List.filter",
		"List.customize",
		"List.orderBy",
		"List.viewDetail",
		"List.hideRows",
		"List.sumColumn"
	};

	public ChangeProductsPriceTest(String testName) {
		super(testName, "ChangeProductsPrice");		
	}

	

	public void testActionOnInitAndViewSetEditable() throws Exception {
		assertActions(listActions);
		
		execute("Mode.detailAndFirst");
		assertActions(detailActions);

		assertNoEditable("description");
		assertEditable("unitPrice");		
	}	
					
}
