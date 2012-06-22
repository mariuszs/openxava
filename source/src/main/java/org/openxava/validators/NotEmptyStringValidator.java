package org.openxava.validators;



import org.openxava.util.*;


/**
 * 
 * 
 * @author Javier Paniza
 */
public class NotEmptyStringValidator implements IPropertyValidator {

	
	
	public void validate(
		Messages errors,
		Object object,
		String propertyName,
		String modelName) {
		try {
			if (Is.emptyString((String) object)) {
				errors.add("required", propertyName, modelName);
			}
		}
		catch (ClassCastException ex) {
			errors.add("expected_type", propertyName, modelName, "string");
		}
	}

}
