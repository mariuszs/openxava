package org.openxava.validators;

import java.math.*;



import org.openxava.util.*;

/**
 * 
 * 
 * @author Javier Paniza
 */
public class PositiveValidator implements IPropertyValidator {

	private static BigDecimal CERO_BIGDECIMAL = new BigDecimal("0");
	private static Double CERO_DOUBLE = new Double("0");
	private static Float CERO_FLOAT = new Float("0");	
	
	

	public void validate(
		Messages errors,
		Object object,		
		String propertyName,
		String modelName) {
		if (object == null) {
			errors.add("positive_not_null", propertyName, modelName);
		}
		Number n = null;
		if (object instanceof Number) {
			n = (Number) object;
		}
		else if (object instanceof String) {
			try {
				n = new BigDecimal((String) object);
			}
			catch (NumberFormatException ex) {
				errors.add("numeric", propertyName, modelName);
				return;
			}
		}
		else {
			errors.add("numeric", propertyName, modelName);
			return;
		}
		if (n instanceof BigDecimal) {
			BigDecimal bd = (BigDecimal) n;
			if (bd.compareTo(CERO_BIGDECIMAL) <= 0) {
				errors.add("positive", propertyName, modelName);
			}
		}
		else if (n instanceof Double) {
			Double db = (Double) n;
			if (db.compareTo(CERO_DOUBLE) <= 0) {
				errors.add("positive", propertyName, modelName);
			}
		}
		else if (n instanceof Float) {
			Float fl = (Float) n;
			if (fl.compareTo(CERO_FLOAT) <= 0) {
				errors.add("positive", propertyName, modelName);
			}
		}		
		else if (n.intValue() <= 0) {
			errors.add("positive", propertyName, modelName);
		}
	}
}
