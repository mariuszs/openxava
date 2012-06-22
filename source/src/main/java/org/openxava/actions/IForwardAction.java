package org.openxava.actions;

/**
 * It allows to do a <i>forward</i> to any URI.
 * 
 * It can be a resource inside the same application, such as a jsp or servlet, 
 * or an absolute URL in the internet. The absolute URL is supported since v4m1. 
 * 
 * @author Javier Paniza
 */

public interface IForwardAction extends IAction {
	
	/**
	 * The URI to go. <p>
	 * 
	 * If it starts with "http://" or "http://" the action will forward to the
	 * absolute URL in internet (since v4m1), if it starts with "javascript:" the
	 * corresponding code will executed by the browser (since 4.0.1), 
	 * otherwise it will forward to a resource inside this application. 
	 */
	String getForwardURI();
	boolean inNewWindow();

}
