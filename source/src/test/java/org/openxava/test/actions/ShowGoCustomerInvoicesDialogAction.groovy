package org.openxava.test.actions

import org.openxava.actions.*

/**
 * 
 * @author Javier Paniza 
 */
class ShowGoCustomerInvoicesDialogAction extends ViewBaseAction {
	
	void execute() {
		showDialog()
		setControllers "GoCustomerInvoices"
		view.modelName = "Customer"
		view.viewName = "Intermediate"
	}

}
