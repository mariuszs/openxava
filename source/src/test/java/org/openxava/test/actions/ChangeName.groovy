package org.openxava.test.actions

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
class ChangeName extends ViewBaseAction {
	
	void execute() {
		String name = getView().getValue("name")
		closeDialog();
		getView().setValue("name", name)
	}

}
