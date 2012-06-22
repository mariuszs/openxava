package org.openxava.test.actions

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
class GoLoginAction extends ViewBaseAction { 
	
	void execute() {
		showDialog() 
		view.modelName = "Login" 
		view.reset() 
		setControllers "BlogLogin"
		removeActions "BlogLogin.notWanted"
	}

}
