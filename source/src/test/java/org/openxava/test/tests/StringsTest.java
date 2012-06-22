package org.openxava.test.tests;

import org.openxava.util.*;

import junit.framework.*;

/**
 * 
 * @author Javier Paniza
 */

public class StringsTest extends TestCase {
	
	public void testJavaIdentifierToNaturalLabel() throws Exception {
		assertEquals("Number", Strings.javaIdentifierToNaturalLabel("number"));
		assertEquals("Product number", Strings.javaIdentifierToNaturalLabel("productNumber"));
		assertEquals("RELEASE ONE", Strings.javaIdentifierToNaturalLabel("RELEASE_ONE"));
		assertEquals("VAT", Strings.javaIdentifierToNaturalLabel("VAT"));
		assertEquals("Total VAT", Strings.javaIdentifierToNaturalLabel("totalVAT"));
		assertEquals("Total VAT in invoice", Strings.javaIdentifierToNaturalLabel("totalVATinInvoice"));
	}

}
