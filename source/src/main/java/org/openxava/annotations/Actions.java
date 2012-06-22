package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link Action}</code> associated to the same member. <p>
 * 
 * Applies to properties and references.<p>
 * 
 * It allows to define a value different for <code>@{@link Action}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@Actions({
 * &nbsp;&nbsp;&nbsp;@Action(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@Action(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@Action(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 *   
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Actions {
	
	Action [] value();
	
}
