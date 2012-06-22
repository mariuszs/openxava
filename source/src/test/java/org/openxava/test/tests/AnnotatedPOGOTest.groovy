package org.openxava.test.tests;

import javax.persistence.*;
import javax.validation.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

class AnnotatedPOGOTest extends TestCase {
	
	static {
		XPersistence.setPersistenceUnit("junit")
		DataSourceConnectionProvider.setUseHibernateConnection(true)
	}
	
	AnnotatedPOGOTest(String name) {
		super(name);
	}
	
	protected void tearDown() throws Exception {
		XPersistence.commit();
	}
	
	void testBeanValidationJSR303() {
		Artist a = new Artist();
		a.name = "TOO OLD ARTIST"
		a.age = 120
		
		XPersistence.getManager().persist(a)
		try {
			XPersistence.commit()
		}
		catch (RollbackException ex) {
						
			if (ex.getCause() instanceof  javax.validation.ConstraintViolationException) {
				javax.validation.ConstraintViolationException vex = (javax.validation.ConstraintViolationException) ex.getCause();				
				assertEquals("1 invalid value is expected", 1, vex.getConstraintViolations().size());
				ConstraintViolation violation = vex.getConstraintViolations().iterator().next();
				assertEquals("Bean", "Artist", violation.getRootBeanClass().getSimpleName());				
				assertEquals("Message text", "must be less than or equal to 90", violation.getMessage());
				return;
			}
					
		}
		fail "An invalid state exception should be thrown"
	}
		
}
