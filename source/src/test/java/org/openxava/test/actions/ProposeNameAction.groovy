package org.openxava.test.actions

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
class ProposeNameAction extends ViewBaseAction {
	
	void execute() {
		String name = getView().getValue("name")
		addMessage "name_confirmation", name	
	}

}
