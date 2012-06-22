package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link PropertyValidator}</code> associated to the same property. <p>
 * 
 * Applies to properties.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@PropertyValidators ({
 * &nbsp;&nbsp;&nbsp;@PropertyValidator(value=org.openxava.test.validators.ExcludeStringValidator.class, properties=
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@PropertyValue(name="string", value="MOTO")
 * &nbsp;&nbsp;&nbsp;),
 * &nbsp;&nbsp;&nbsp;@PropertyValidator(value=org.openxava.test.validators.ExcludeStringValidator.class, properties=
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@PropertyValue(name="string", value="COCHE")
 * &nbsp;&nbsp;&nbsp;),		
 * &nbsp;&nbsp;&nbsp;@PropertyValidator(value=org.openxava.test.validators.ExcludeStringValidator.class, properties=			
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@PropertyValue(name="string", value="CUATRE"),
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;onlyOnCreate=true
 * &nbsp;&nbsp;&nbsp;)		
 * &nbsp;})
 * &nbsp;private String description;
 * </pre>
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface PropertyValidators {
	
	PropertyValidator [] value();
	
}
