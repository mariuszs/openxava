package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link CollectionView}</code> associated to the same collection. <p>
 * 
 * Applies to collections.<p>
 * 
 * It allows to define a value different for <code>@{@link CollectionView}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@CollectionViews({
 * &nbsp;&nbsp;&nbsp;@CollectionView(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@CollectionView(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@CollectionView(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface CollectionViews {
	
	CollectionView [] value();
	
}
