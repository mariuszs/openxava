package org.openxava.test.actions

import org.openxava.actions.*

/**
 *
 * @author Javier Paniza
 */

class ProduceMessagesAction extends BaseAction {
	
	void execute() {
		addMessage "this_is_a_message"
		addError "this_is_an_error"
		addInfo "this_is_an_info"
		addWarning "this_is_a_warning"
	}

}
