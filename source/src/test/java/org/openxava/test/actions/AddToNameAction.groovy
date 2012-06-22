package org.openxava.test.actions

import org.openxava.actions.*;

/**
 *  
 * @author Javier Paniza 
 */

class AddToNameAction extends ViewBaseAction {

	String stringToAdd	
	
	void execute() {
		String name = getView().getValue("name")
		getView().setValue("name", name + stringToAdd)
	}
	
}
