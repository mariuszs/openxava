package org.openxava.test.validators

import org.openxava.test.model.*;
import org.openxava.provaox.model.*;
import org.openxava.validators.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
class PhoneNumberValidator implements IValidator { 

	Country phoneCountry 
	String phone
	 	
	void validate(Messages errors) { 	
		phoneCountry.name += "X"	
	}
	 
}