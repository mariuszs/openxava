package org.openxava.actions;

import java.util.*;

import javax.inject.*;

import org.openxava.tab.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class ViewDetailAction extends TabBaseAction implements IChainAction, IModelAction {
	
	@Inject  
	private int row = Integer.MIN_VALUE; 
	private boolean explicitRow = false;
	private int increment;
	private Map key;	
	private boolean goFirst = false;
	private String nextAction;
	private boolean atListBegin;
	private boolean noElementsInList;
	private String model;
	@Inject
	private Tab mainTab;
	
	@Override
	protected Tab getTab() throws XavaException {
		return getMainTab() != null ? getMainTab() : super.getTab();
	}
	
	public void execute() throws Exception {		
		getView().setModelName(model); 
		setAtListBegin(false);
		setNoElementsInList(false);				
		if (increment < 0 && row == 0) {
			setAtListBegin(true);
			addError("at_list_begin");			
			return;
		}		
		int previous = row;
		row = goFirst?0:row + increment;		
		int [] selectedOnes = getTab().getSelected();
		boolean lastSelectedPassed = false;
		if (!explicitRow && selectedOnes != null && selectedOnes.length > 0) {
			if (increment >= 0) {				
				int last = selectedOnes[selectedOnes.length - 1];				
				if (row > last) lastSelectedPassed = true;
				else {					
					while (Arrays.binarySearch(selectedOnes, row) < 0 && row < last) { 
						row++;
					} 		
				}	
			}
			else {
				int first = selectedOnes[0];
				if (row < first) lastSelectedPassed = true;
				else {
					while (Arrays.binarySearch(selectedOnes, row) < 0 && row > first) { 
						row--;
					} 		
				}					
			}			
		}		
		if (lastSelectedPassed) {		
			key = null;
		}
		else {
			if (row < 0) row = 0; 
			key = (Map) getTab().getTableModel().getObjectAt(row);			
		}
		if (key == null) {
			setNoElementsInList(true);
			addError("no_list_elements");
			row = previous;
		}		
		if (key != null) {			
			getView().setValues(key);									
		}	
				
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int i) {
		// If row is injected twice, the second time is an explict row assignament
		//   that is, not from xava_row session object but using row=7 in action call
		if (row != Integer.MIN_VALUE) explicitRow = true;   
		row = i;		
	}
		
	
	public String getNextAction() throws XavaException {
		if (Is.emptyString(nextAction) || getManager().isSplitMode()) { 
			return getEnvironment().getValue("XAVA_SEARCH_ACTION");
		} 		
		return nextAction;
	}
	
	public void setNextAction(String string) {
		nextAction = string;
	}
	
	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int i) {
		increment = i;
	}

	public boolean isGoFirst() {
		return goFirst;
	}

	public void setGoFirst(boolean b) {
		goFirst = b;
	}


	public boolean isNoElementsInList() {
		return noElementsInList;
	}

	public boolean isAtListBegin() {
		return atListBegin;
	}

	private void setNoElementsInList(boolean b) {
		noElementsInList = b;
	}

	private void setAtListBegin(boolean b) {
		atListBegin = b;
	}

	public void setModel(String modelName) { 
		this.model = modelName;		
	}

	public Tab getMainTab() {
		return mainTab;
	}

	public void setMainTab(Tab mainTab) {
		this.mainTab = mainTab;
	}

}