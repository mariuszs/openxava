package org.openxava.validators;



import org.openxava.util.*;


/**
 * 
 * 
 * @author Javier Paniza
 */
public class NotBlankCharacterValidator implements IPropertyValidator {

	
	
	public void validate(
		Messages errors,
		Object object,
		String propertyName,
		String modelName) {
		try {
			if (Character.isWhitespace(((Character) object).charValue())) {
				errors.add("required", propertyName, modelName);
			}
		}
		catch (ClassCastException ex) {
			errors.add("expected_type", propertyName, modelName, "caracter");
		}
	}
}
