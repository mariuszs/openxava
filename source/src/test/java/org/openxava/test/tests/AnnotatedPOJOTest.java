package org.openxava.test.tests;

import java.math.*;

import javax.persistence.*;
import javax.validation.*;

import org.hibernate.validator.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

public class AnnotatedPOJOTest extends TestCase {
	
	static {
		XPersistence.setPersistenceUnit("junit");
		DataSourceConnectionProvider.setUseHibernateConnection(true);
	}
	
	public AnnotatedPOJOTest(String name) {
		super(name);
	}
	
	protected void tearDown() throws Exception {
		XPersistence.commit();
	}
	
	public void testRequiredAsHibernateAnnotation() throws Exception {
		DrivingLicence dl = new DrivingLicence();
		dl.setType("X");
		dl.setLevel(1);
		dl.setDescription(""); // This is annotated with @Required
		
		XPersistence.getManager().persist(dl);
		try {
			XPersistence.commit();
		}
		catch (RollbackException ex) {
			if (ex.getCause() instanceof InvalidStateException) {
				InvalidStateException iex = (InvalidStateException) ex.getCause();
				assertEquals("1 invalid value is expected", 1, iex.getInvalidValues().length);
				assertEquals("Property", "description", iex.getInvalidValues()[0].getPropertyName());
				assertEquals("Message text", "required", iex.getInvalidValues()[0].getMessage());
				return;
			}		
		}
		fail("An invalid state exception should be thrown");		
	}
	
	public void testPropertyValidatorAsHibernateAnnotation() throws Exception {
		Product p = new Product();
		p.setNumber(66);
		p.setDescription("JUNIT");
		p.setFamilyNumber(1);
		p.setSubfamilyNumber(1);
		p.setWarehouseKey(new Warehouse());  
		p.setUnitPrice(new BigDecimal("1200")); // An UnitPriceValidator does not permit this
		
		XPersistence.getManager().persist(p);
		try {
			XPersistence.commit();
		}
		catch (RollbackException ex) {			
			if (ex.getCause() instanceof InvalidStateException) {
				InvalidStateException iex = (InvalidStateException) ex.getCause();
				assertEquals("1 invalid value is expected", 1, iex.getInvalidValues().length);
				assertEquals("Property", "unitPrice", iex.getInvalidValues()[0].getPropertyName());
				assertEquals("Message text", "openxava.propertyValidator", iex.getInvalidValues()[0].getMessage());
				return;
			}		
		}
		fail("An invalid state exception should be thrown");
	}
	
	public void testPropertyValidatorsAsHibernateAnnotation() throws Exception {
		Product p = new Product();
		p.setNumber(66);
		p.setDescription("MOTO"); 
		p.setFamilyNumber(1);
		p.setSubfamilyNumber(1);
		p.setWarehouseKey(new Warehouse());
		p.setUnitPrice(new BigDecimal("900")); 
		
		XPersistence.getManager().persist(p);
		try {
			XPersistence.commit();
		}
		catch (RollbackException ex) {			
			if (ex.getCause() instanceof InvalidStateException) {
				InvalidStateException iex = (InvalidStateException) ex.getCause();
				assertEquals("1 invalid value is expected", 1, iex.getInvalidValues().length);
				assertEquals("Property", "description", iex.getInvalidValues()[0].getPropertyName());
				assertEquals("Message text", "openxava.propertyValidator", iex.getInvalidValues()[0].getMessage());
				return;
			}		
		}
		fail("An invalid state exception should be thrown");
	}
	
	public void testEntityValidatorsAsHibernateAnnotation() throws Exception {
		Product p = new Product();
		p.setNumber(66);
		p.setDescription("BUENO, BONITO, BARATO"); // It's cheap ('BARATO') thus...
		p.setFamilyNumber(1);
		p.setSubfamilyNumber(1);
		p.setWarehouseKey(new Warehouse());
		p.setUnitPrice(new BigDecimal("900")); // ... it cannot cost 900 (max 100) 
		
		XPersistence.getManager().persist(p);
		try {
			XPersistence.commit();
		}
		catch (RollbackException ex) {			
			if (ex.getCause() instanceof InvalidStateException) {
				InvalidStateException iex = (InvalidStateException) ex.getCause();
				assertEquals("1 invalid value is expected", 1, iex.getInvalidValues().length);
				assertEquals("Bean", "Product", iex.getInvalidValues()[0].getBeanClass().getSimpleName());
				assertEquals("Message text", "openxava.entityValidator", iex.getInvalidValues()[0].getMessage());

				return;
			}		
		}
		fail("An invalid state exception should be thrown");
	}
		
	
	public void testFinderThrowsNoResult() throws Exception {
		try {
			Customer.findByNumber(66); // 66 doesn't exist
			fail("EntityNotFoundException expected"); 
		}
		catch (javax.persistence.NoResultException ex) {
			// All fine
		}
	}	

	
}
