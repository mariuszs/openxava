package org.openxava.test.actions

import org.openxava.actions.*

/**
 * 
 * @author Javier Paniza 
 */
class ShowSellerAction extends ViewBaseAction {
	
	void execute() {
		int number = view.values.number
		showDialog()
		view.modelName = "Seller"
		view.viewName = "Complete"
		view.setValue "number", number
		setControllers "ModifySeller", "Dialog" 
		view.refresh()		
	}

}
