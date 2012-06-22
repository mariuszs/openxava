package org.openxava.test.actions

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

class SearchAuthorAction extends SearchByViewKeyAction {
	
	
	void execute() {
		super.execute()
		addMessage "showing_author", view.getValue("author")
	}

}
