package org.openxava.actions;

/**
 * @author Javier Paniza
 */

public class OrderByAction extends TabBaseAction  {
	
	private String property;
	
	public void execute() throws Exception {		
		getTab().orderBy(property);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String string) {
		property = string;
	}

}
