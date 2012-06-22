package org.openxava.test.tests;

import org.openxava.tests.*;
import org.openxava.util.*;



/**
 * @author Javier Paniza
 */

public class AverageSpeedTest extends ModuleTestBase {

	
	public AverageSpeedTest(String testName) {
		super(testName, "AverageSpeed");		
	}
	
	public void testSearchingInAReferenceByANonIdDoesNotUseLike() throws Exception {		
		execute("CRUD.new");
		assertEditable("vehicle.code"); 
		setValue("vehicle.code", "VLV40");
		assertValue("vehicle.model", "S40 T5");
		assertEditable("vehicle.code"); 
		setValue("vehicle.code", "");
		assertValue("vehicle.model", "");
		setValue("vehicle.code", "VLV");
		assertValue("vehicle.model", "");		
	}
	
	// To test the classic way of URL modules
	protected String getModuleURL() throws XavaException {
		if (isLiferayEnabled() || isJetspeed2Enabled()) {
			return super.getModuleURL();
		}		
		return "http://" + getHost() + ":" + getPort() + "/OpenXavaTest/xava/module.jsp?application=OpenXavaTest&module=AverageSpeed";
	}
	
}
