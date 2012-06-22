package org.openxava.test.tests;

import org.openxava.test.model.*;
import org.openxava.jpa.*;
import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class PhoneTest extends ModuleTestBase {
	
	PhoneTest(String testName) {
		super(testName, "Phone")		
	}
	
	void testEntityValidatorInjectingAPropertyWithTheSameNameOfTheEntity() {
		Country country = XPersistence.manager.find(Country.class, "ff8080822d278d29012d27909d220002")
		assertEquals "ALEMANIA", country.name  
		execute "CRUD.new"
		setValue "phoneCountry.id", country.id  		
		setValue "phone", "147 00 98"
		setValue "phoneExtension", "96"
		execute "CRUD.save"
		assertNoErrors()
		XPersistence.manager.refresh(country)
		assertEquals "ALEMANIAX", country.name
		country.name = "ALEMANIA"
	}
	
}
