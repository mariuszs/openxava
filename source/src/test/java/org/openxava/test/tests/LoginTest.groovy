package org.openxava.test.tests;

import org.apache.commons.logging.*;
import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class LoginTest extends ModuleTestBase {
	
	private static Log log = LogFactory.getLog(LoginTest.class)
	
	LoginTest(String testName) {
		super(testName, "Login")		
	}
	
	void testPasswordStereotype_defaultModuleForTransientClassIsDetailOnly() {
		if (isPortalEnabled()) {
			// The portlet is not generated for Login because Login is not defined
			// explicitly as a module in application.xml, and Login class is not
			// an @Entity, just a plain class. Obviously, OpenXava cannot generate 
			// a default portlet for each regular class in the application
			log.warn("Login tests are not executed against portal, because the portlet is not generated")
			return;
		}
		assertNoAction "Mode.list"
		assertNoAction "Mode.detailAndFirst"
		assertNoAction "Mode.split"
	
		setValue "user", "JAVI"
		setValue "password", "x942JlmkK"
		execute "Login.login"
		assertErrorsCount 1
		
		setValue "user", "JAVI"
		setValue "password", "x8Hjk37mm"
		execute "Login.login"
		assertNoErrors()
		assertMessage "OK"
	}
	
}
