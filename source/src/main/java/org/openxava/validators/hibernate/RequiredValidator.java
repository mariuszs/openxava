package org.openxava.validators.hibernate;

import org.hibernate.validator.*;
import org.openxava.annotations.*;
import org.openxava.validators.*;
import org.openxava.validators.meta.*;

/**
 * Implements Required annotation of OpenXava as a Hibernate validator. <p>
 *  
 * @author Javier Paniza
 */

public class RequiredValidator implements Validator<Required> {
	

	
	public void initialize(Required required) {
	}

	public boolean isValid(Object value) {
		if (HibernateValidatorInhibitor.isInhibited()) return true; // Usually when saving from MapFacade, MapFacade already has done the validation
		if (value == null) return false;		
		try {
			IPropertyValidator validator = 
				MetaValidators.getMetaValidatorRequiredFor(value.getClass().getName()).
					getPropertyValidator();			
			validator.validate(FailingMessages.getInstance(), value, "", "");
			return true;
		} 
		catch (IllegalStateException ex) {
			if (FailingMessages.EXCEPTION_MESSAGE.equals(ex.getMessage())) return false;
			throw ex;
		}
		catch (RuntimeException ex) {
			throw ex;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage(), ex);
		}		
	}

}
