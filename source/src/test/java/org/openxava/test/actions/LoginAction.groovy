package org.openxava.test.actions

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */

class LoginAction extends ViewBaseAction {

	void execute() throws Exception {
		String user = view.getValue("user")
		String password = view.getValue("password")		
		if (user == "JAVI" && password == "x8Hjk37mm") addMessage("ok")
		else addError("error") 	
	}
	
}
