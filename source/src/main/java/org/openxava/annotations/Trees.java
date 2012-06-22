package org.openxava.annotations;

/**
 * A group of <code>@{@link Tree}</code> associated to the same collection. <p>
 * 
 * Applies to collections.<p>
 * 
 * It allows to define a value different for <code>@{@link Tree}</code> in each view.<br>
 * Example:
 * <pre>
 * &nbsp;@Trees({
 * &nbsp;&nbsp;&nbsp;@Tree(forViews="DEFAULT", value= ... ),
 * &nbsp;&nbsp;&nbsp;@Tree(forViews="Simple, VerySimple", value= ... ),
 * &nbsp;&nbsp;&nbsp;@Tree(forViews="Complete", value= ... )
 * &nbsp;})
 * </pre>
 *
 * @author Federico Alcántara
 */

public @interface Trees {
	Tree[] value();
}
