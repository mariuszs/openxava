package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class OnChangeDeliveryTypeAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		addMessage("type=" + getNewValue());		
	}

}
