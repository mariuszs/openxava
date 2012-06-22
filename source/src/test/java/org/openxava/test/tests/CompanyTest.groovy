package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class CompanyTest extends ModuleTestBase {
	
	CompanyTest(String testName) {
		super(testName, "Company")		
	}
	
	void testReferenceAndCascadeRemoveCollectionToSameEntity() { 
		execute "Mode.detailAndFirst"
		assertNoErrors();
		assertValue "name", "MY COMPANY"
		assertValue "mainBuilding.name", "MY OFFICE"
		assertCollectionRowCount "buildings", 1
		assertValueInCollection "buildings", 0, 0, "BUILDING A"
		execute "Collection.edit", "row=0,viewObject=xava_view_buildings"
		assertNoErrors()
		assertValue "name", "BUILDING A"
		execute "Collection.save"
		assertNoErrors()
	}
	
	void testCollectionElementInsideAGroup() {
		execute "CRUD.new"
		execute "Collection.new", "viewObject=xava_view_buildings"
		assertNoErrors() // For verifying that really works
		assertMessagesCount 1
		setValue "function", "Factory" // For verifying that onchange is thrown only once
		assertMessagesCount 1		
	}	
	
	void testErrorOnCommitFromADialog() {
		execute "Mode.detailAndFirst"
		assertNoDialog()		
		execute "Collection.edit", "row=0,viewObject=xava_view_buildings"
		assertDialog()
		execute "Company.saveBuildingFailing"		
		assertError "Impossible to execute Save building failing action: Transaction marked as rollbackOnly"
		assertNoDialog() // The dialog is closed because the exception is produced on commit when the
			// dialog has been already closed by the action. If you want to produce exceptions on commit
			// without closing the dialog use mapFacadeAutoCommit=true in xava.properties, or create your own actions
		execute "Reference.modify", "model=Building,keyProperty=mainBuilding.name"
		assertNoErrors()
		assertDialog()
	}
	
}
