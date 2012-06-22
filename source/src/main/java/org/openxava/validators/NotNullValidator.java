package org.openxava.validators;



import org.openxava.util.*;


/**
 * 
 * 
 * @author Javier Paniza
 */
public class NotNullValidator implements IPropertyValidator {

	
	
	public void validate(
		Messages errors,
		Object object,
		String propertyName,
		String modelName) {
		if (object == null) {
			errors.add("required", propertyName, modelName);
		}
	}
	
}
