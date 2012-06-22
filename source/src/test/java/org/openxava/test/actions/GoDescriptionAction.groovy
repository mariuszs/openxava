package org.openxava.test.actions

import org.openxava.actions.*

/**
 * 
 * @author Javier Paniza 
 */
class GoDescriptionAction extends BaseAction implements IForwardAction {
	
	void execute() {
		println "[GoDescriptionAction.execute()] "	
	}

	String getForwardURI() {
		return "/doc/description_en.html"
	}
	
	public boolean inNewWindow() {
		return false
	}
	
}
