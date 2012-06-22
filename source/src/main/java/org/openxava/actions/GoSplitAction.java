package org.openxava.actions;

/**
 * 
 * @since 4m5
 * @author Javier Paniza
 */

public class GoSplitAction extends BaseAction {
	
	public void execute() throws Exception {
		setNextMode(SPLIT);
	}

}
