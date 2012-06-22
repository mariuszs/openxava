package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class DrivingLicenceTest extends ModuleTestBase {
		
	public DrivingLicenceTest(String testName) {
		super(testName, "DrivingLicence");		
	}
	
	public void testHibernateValidator() throws Exception { 
		execute("CRUD.new");
		setValue("type", "X");
		setValue("level", "3"); // This breaks @Max(2) hibernate validation
		setValue("description", "JUNIT TEST");
		execute("CRUD.save");
		// The chunk in Spanish if for a bug of Hibernate Validator, see at
		// http://opensource.atlassian.com/projects/hibernate/browse/HV-31
		assertError("3 is not a valid value for Level of Driving licence: debe ser menor o igual a 2");		
	}
	
}
