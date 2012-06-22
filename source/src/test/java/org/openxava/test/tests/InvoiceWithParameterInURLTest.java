package org.openxava.test.tests;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.rmi.PortableRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.jpa.XPersistence;
import org.openxava.test.calculators.YearInvoiceDiscountCalculator;
import org.openxava.test.model.Delivery;
import org.openxava.test.model.DeliveryType;
import org.openxava.test.model.Invoice;
import org.openxava.test.model.Product;
import org.openxava.tests.ModuleTestBase;
import org.openxava.util.Dates;
import org.openxava.util.Is;
import org.openxava.util.Strings;
import org.openxava.util.XavaPreferences;


/**
 * @author Javier Paniza
 */

public class InvoiceWithParameterInURLTest extends ModuleTestBase {
	
	
	
	public InvoiceWithParameterInURLTest(String testName) {
		super(testName, "InvoiceWithParameterInURL");		
	}
	
	public void testParametersInURL() throws Exception {
		assertListRowCount(1); // Only one Invoice from 2002
		execute("List.filter");
		assertListRowCount(1);
	}
	
	protected String getModuleURL() {
		String parameter = isPortalEnabled()?"?year=2002":"&year=2002";
		return super.getModuleURL() + parameter;
	}
	
}
