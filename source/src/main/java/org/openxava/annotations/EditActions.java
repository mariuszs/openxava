package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link EditAction}</code> associated to the same collection. <p>
 * 
 * Applies to collections.<p>
 * 
 * It allows to define a value different for <code>@{@link EditAction}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@EditActions({
 * &nbsp;&nbsp;&nbsp;@EditAction(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@EditAction(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@EditAction(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface EditActions {
	
	EditAction [] value();
	
}
