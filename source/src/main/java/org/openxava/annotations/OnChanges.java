package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link OnChange}</code> associated to the same member. <p>
 * 
 * Applies to properties and references.<p>
 * 
 * It allows to define a value different for <code>@{@link OnChange}</code> 
 * in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@OnChanges({
 * &nbsp;&nbsp;&nbsp;@OnChange(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@OnChange(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@OnChange(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface OnChanges {
	
	OnChange [] value();
	
}
