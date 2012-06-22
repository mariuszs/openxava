package org.openxava.test.actions

import org.openxava.actions.*;
import org.openxava.jpa.*;

/**
 * 
 * @author Javier Paniza
 */
class SaveElementInCollectionAbortingTransactionAction extends SaveElementInCollectionAction {
	
	void execute() {
		XPersistence.manager.transaction.setRollbackOnly()
		super.execute()		
	}

}
