package org.openxava.test.validators

import org.openxava.test.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * 
 * @author Javier Paniza 
 */
class JournalEntryValidator implements IValidator {

	Journal theJournal
	
	void validate(Messages errors)  {			
		if (theJournal == null) {
			errors.add "journal_required" 
		}				
	}

}
