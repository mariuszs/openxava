package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class GoDescriptionTest extends ModuleTestBase {
	
	GoDescriptionTest(String testName) {
		super(testName, "GoDescription")		
	}
	
	void testForwardActionOnEachRequestOnLoadModuleFirstTime() {
		if (isPortalEnabled()) return // This feature is available in portals, but the portal app for testing is not ready (it has no the destination URL), moreover testing in a non-portal environment is enough
		assertTrue (getHtml().contains("is used to test all OpenXava features"))
	}
	
}
