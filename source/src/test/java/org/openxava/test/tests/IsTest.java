package org.openxava.test.tests;

import java.math.*;

import org.openxava.util.*;

import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

public class IsTest extends TestCase {
	
	public void testEmptyForBigDecimal() {
		BigDecimal fraction = new BigDecimal("0.1");
		BigDecimal zero = new BigDecimal("0.0");
		assertTrue(!Is.empty(fraction));
		assertTrue(Is.empty(zero));
	}

}
