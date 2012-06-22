package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class BookTest extends ModuleTestBase {
	
	BookTest(String testName) {
		super(testName, "Book")		
	}
	
	void testReferenceNameMatchesIdOfReferencedEntityName() {
		execute "CRUD.new"
		execute "Reference.search", "keyProperty=author.author"		
		assertListNotEmpty()
		String author = getValueInList(0, 0)		
		execute "ReferenceSearch.choose", "row=0"
		assertNoErrors()				
		assertValue "author.author", author
	}
	
	// This test fails in PostgreSQL, but not in Hypersonic
	void testListFilterByBooleanColumnInDB() {
		assertListRowCount 2
		setConditionComparators ([ "=", "=" ])
		setConditionValues (["", "true" ])
		execute "List.filter"
		assertListRowCount 1
	}
	
	
	
}
