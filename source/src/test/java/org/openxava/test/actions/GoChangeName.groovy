package org.openxava.test.actions

import org.openxava.actions.*;

class GoChangeName extends ViewBaseAction {
	
	void execute() {
		showDialog()
		getView().setModelName "Name"		
		setControllers "ChangeName"		
	}

}
