package org.openxava.validators.hibernate;

import java.util.*;
import org.hibernate.validator.*;
import org.openxava.annotations.*;
import org.openxava.annotations.parse.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;
import org.openxava.validators.*;
import org.openxava.validators.meta.*;

/**
 * Implements a EntityValidator of OpenXava as a Hibernate validator. <p>
 *  
 * @author Javier Paniza
 */

public class EntityValidatorValidator implements Validator<EntityValidator> {
	
	private MetaValidator metaValidator;

	public void initialize(EntityValidator entityValidator) {
		metaValidator = AnnotatedClassParser.createEntityValidator(entityValidator);
	}

	public boolean isValid(Object entity) {	
		if (HibernateValidatorInhibitor.isInhibited()) return true;  // Usually when saving from MapFacade, MapFacade already has done the validation
		if (metaValidator.isOnlyOnCreate()) return true;
		try {			
			Iterator itSets =  metaValidator.getMetaSetsWithoutValue().iterator();			 
			IValidator v = metaValidator.createValidator();
			PropertiesManager validatorProperties = new PropertiesManager(v);
			PropertiesManager entityProperties = new PropertiesManager(entity);
			while (itSets.hasNext()) {
				MetaSet set = (MetaSet) itSets.next();					
				Object value = entityProperties.executeGet(set.getPropertyNameFrom());								
				validatorProperties.executeSet(set.getPropertyName(), value);									
			}							
			v.validate(FailingMessages.getInstance());
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
