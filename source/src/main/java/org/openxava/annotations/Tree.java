package org.openxava.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * With <code>@Tree</code> you can instruct OpenXava to visualize collections 
 * as a tree instead of a list. <p> 
 * 
 * Applies to collections.
 * 
 * Example:
 * <pre>
 * &nbsp;@OneToMany(mappedBy="parentContainer", cascade = CascadeType.REMOVE)
 * &nbsp;@Editor("TreeView")
 * &nbsp;@ListProperties("description")
 * &nbsp;@OrderBy("path, treeOrder")
 * &nbsp;private Collection<TreeItem> treeItems;
 * </pre>	
 *  
 * @author Federico Alcantara
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface Tree {
	/**
	 * List of comma separated view names where this annotation applies. <p>
	 * 
	 * Exclusive with notForViews.<br>
	 * If both forViews and notForViews are omitted then this annotation
	 * apply to all views.<br>
	 * You can use the string "DEFAULT" for referencing to the default
	 * view (the view with no name).
	 */	
	String forViews() default "";
	
	/**
	 * List of comma separated view names where this annotation does not apply. <p>
	 * 
	 * Exclusive with forViews.<br>
	 * If both forViews and notForViews are omitted then this annotation
	 * apply to all views.<br>
	 * You can use the string "DEFAULT" for referencing to the default
	 * view (the view with no name).
	 */ 	
	String notForViews() default "";

	/**
	 * Optional. Defaults to path.
	 * Property used for the path, must be a String type with 
	 * a size appropriate to the task at hand.
	 * @return property name
	 */
	String pathProperty() default "path";
	
	/**
	 * Optional.
	 * Comma separated list of properties used for identifying the tree node. By default
	 * the id of the entity is used. If more than one property is used, 
	 * their values will be nodeIdSeparator separated and enclosed in brackets.
	 * @return node property name.
	 */
	String idProperties() default ""; 
	
	/**
	 * Optional.
	 * String to be used to separate multiple Id elements. 
	 */
	String idSeparator() default ",";
			
	/**
	 * Optional.
	 * Indicates how to render the tree when the expandedPropertyName is
	 * not defined. It's default value is true. 
	 * @return expanded state.
	 */
	boolean initialExpandedState() default true;

	/**
	 * Optional.
	 * Defines the increment used for the keys when orderProperty is used
	 * The default value is 2. The minimum is 2.
	 * @return order increment.
	 */
	int orderIncrement() default 2;
	
	/**
	 * Optional.
	 * Defines the separator for the path elements. Default value is / 
	 * @return path separator character.
	 */
	String pathSeparator() default "/";
	
}
