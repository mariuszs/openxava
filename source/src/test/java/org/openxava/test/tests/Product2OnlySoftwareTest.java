package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class Product2OnlySoftwareTest extends ModuleTestBase {
	
	public Product2OnlySoftwareTest(String testName) {
		super(testName, "Product2OnlySoftware");		
	}
	
	public void testQualifiedPropertiesInDescriptionsListCondition() throws Exception {
		execute("CRUD.new");
		String [][] softwareValues = {
			{ "", ""},
			{ "1", "DESARROLLO"},
			{ "2", "GESTION"},						  
			{ "3", "SISTEMA"}						
		};		
		assertValidValues("subfamily.number", softwareValues);										
	}	
					
}
