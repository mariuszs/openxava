package org.openxava.actions;

import java.util.*;



import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 */

public class DeleteAction extends ViewDetailAction {
	
	
	
	public DeleteAction() {
		setIncrement(0);
	}

	public void execute() throws Exception { 
		if (getView().isKeyEditable()) {
			addError("no_delete_not_exists");
			return;
		}
		try {
			MapFacade.remove(getModelName(), getView().getKeyValues());
			resetDescriptionsCache();
		}
		catch (ValidationException ex) {
			addErrors(ex.getErrors());	
			return;
		}		
		addMessage("object_deleted", getModelName());
		getView().clear();
		boolean selected = false;
		if (getTab().hasSelected()) {
			removeSelected();
			selected = true;
		}
		else getTab().reset();		 		
		super.execute(); // viewDetail
		if (isNoElementsInList()) {
			if (
				(!selected && getTab().getTotalSize() > 0) ||
				(selected && getTab().getSelected().length > 0)
			) {				
				setIncrement(-1);
				getErrors().remove("no_list_elements");								
				super.execute();													
			}
			else {							
				getView().setKeyEditable(false);
				getView().setEditable(false);
			}
		}
		getErrors().clearAndClose(); // If removal is done, any additional error message may be confused
	}

	private void removeSelected() throws XavaException {
		int row = getRow();		
		int [] selectedOnes = getTab().getSelected();
		if (Arrays.binarySearch(selectedOnes, row) < 0) return;		
		int [] news = new int[selectedOnes.length-1];
		int j = 0;		
		for (int i = 0; i < news.length; i++) {
			int v = selectedOnes[j];
			if (v == row) {
				j++; i--;				
			} 
			else  {				
				news[i] = v;
				j++;
			}					
		}
		getTab().setAllSelected(news);
	}

}


