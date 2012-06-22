package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class SalesRepresentativePersonOnlyNamesTest extends ModuleTestBase {
	
	SalesRepresentativePersonOnlyNamesTest(String testName) {
		super(testName, "SalesRepresentativePersonOnlyNames")		
	}
	
	void testEditorForViewsInReference()  { 
		execute "CRUD.new"
		setValue "person.personFirstName", "javi"		
		execute "CRUD.save"
		assertValue "person.personFirstName", "Javi" // Because of PersonName editor
	}
			
}
