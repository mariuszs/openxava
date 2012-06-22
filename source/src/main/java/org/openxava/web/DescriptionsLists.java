package org.openxava.web;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

/**
 * Created on 21/08/2009
 * @autor Ana Andres
 */
public class DescriptionsLists {
	
	public static void resetDescriptionsCache(HttpSession session) {
		Enumeration e = session.getAttributeNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			if (name.endsWith(".descriptionsCalculator")) {
				session.removeAttribute(name);
			}
		}		
	}
}
