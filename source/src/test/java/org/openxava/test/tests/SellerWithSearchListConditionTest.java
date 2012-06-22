package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase;


/**
 * 
 * @author Federico Alcantara
 */

public class SellerWithSearchListConditionTest extends ModuleTestBase {
		
	public SellerWithSearchListConditionTest(String testName) {
		super(testName, "SellerSearchListCondition");		
	}
	
	public void testSearchListCondition() throws Exception {
		changeModule("SellerSearchListCondition");
		execute("Mode.detailAndFirst");
		execute("Reference.search", "keyProperty=level.id");
		assertListRowCount(2);
		closeDialog();

		execute("Collection.add", "viewObject=xava_view_customers");
		assertListRowCount(4);
	}

	public void testSearchListConditionOff() throws Exception {
		changeModule("SellerSearchListConditionOff");
		execute("Mode.detailAndFirst");
		execute("Reference.search", "keyProperty=level.id");
		assertListRowCount(3);
		closeDialog();

		execute("Collection.add", "viewObject=xava_view_customers");
		assertListRowCount(5);
	}

	public void testSearchListConditionBlank() throws Exception {
		changeModule("SellerSearchListConditionBlank");
		execute("Mode.detailAndFirst");
		execute("Reference.search", "keyProperty=level.id");
		assertListRowCount(3);
		closeDialog();

		execute("Collection.add", "viewObject=xava_view_customers");
		assertListRowCount(4);
	}
	
}
