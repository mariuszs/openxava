package org.openxava.test.filters;

import org.openxava.filters.*;

/**
 * @author Javier Paniza
 */

class InventFilter implements IFilter {

	private final static int VALUE = 1; 

	Object filter(Object o) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date());		
		Object [] r = null;
		if (o == null) {
			r = new Object[1];
			r[0] = VALUE;					
		}
		else if (o instanceof Object []) {
			Object [] a = (Object []) o; 
			r = new Object[a.length + 1];
			r[0] = VALUE;
			for (int i = 0; i < a.length; i++) {
				r[i+1]=a[i];
			}			
		}
		else {
			r = new Object[2];
			r[0] = VALUE;
			r[1] = o;			
		}		
		return r;
	}

}
