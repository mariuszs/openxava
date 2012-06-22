package org.openxava.actions;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class MoveColumnToLeftAction extends TabBaseAction {
	
	private int columnIndex;

	public void execute() throws Exception {
		if (!XavaPreferences.getInstance().isCustomizeList()) return; 
		getTab().movePropertyToLeft(columnIndex);
	}

	public int getColumnIndex() {
		return columnIndex;
	}
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}
	
}
