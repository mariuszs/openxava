package org.openxava.test.actions

import org.openxava.actions.*


class ChangeYearConditionAction extends TabBaseAction {

	int year;

	void execute() throws Exception {
		tab.setConditionValue("year", year) 		
	}

}
