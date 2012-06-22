package org.openxava.test.actions

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
class GoProposeNameAction extends BaseAction implements ICustomViewAction {
	
	void execute() {
		setControllers "ProposeName"
	}
	
	String getCustomView() {
		return "custom-jsp/proposeName";
	}

}
