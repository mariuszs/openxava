package org.openxava.test.tests;

import java.io.*;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.openxava.web.*;


/**
 * 
 * @author Javier Paniza
 */

public class InvoiceTest extends ModuleTestBase {
	private static Log log = LogFactory.getLog(InvoiceTest.class);
	
	private Invoice invoice;
	private BigDecimal productUnitPriceDB;
	private String productUnitPricePlus10;
	private String productUnitPrice;
	private String productUnitPriceInPesetas;
	private String productDescription;
	private String productNumber;
	private Product product;	
	
	public InvoiceTest(String testName) {
		super(testName, "Invoice");		
	}
	
	private boolean isVisibleConditionValueTo(String name){
		String element = getForm().getElementById(Ids.decorate("OpenXavaTest", "Invoice", name)).toString();
		return element.contains("display: inline;");
	}
	
	public void testFilterByRange() throws Exception{ 
		assertLabelInList(0, "Year");
		assertLabelInList(2, "Date");
		assertLabelInList(6, "Paid");
		// int
		setConditionComparators("range_comparator");
		setConditionValues("2000");
		assertTrue(isVisibleConditionValueTo("conditionValueTo___0"));
		assertFalse(isVisibleConditionValueTo("conditionValueTo___2"));
		setConditionValuesTo("2004");
		execute("List.filter");
		assertListRowCount(6);
		// int & Date
		setConditionComparators("range_comparator", "eq", "range_comparator");
		setConditionValues("2000", "", "01/01/2002");
		setConditionValuesTo("2004", "", "05/01/2004");
		assertTrue(isVisibleConditionValueTo("conditionValueTo___2"));
		execute("List.filter");
		assertListRowCount(3);
		// int & Date & boolean
		setConditionComparators("range_comparator", "eq", "range_comparator", "eq");
		execute("List.filter");
		assertListRowCount(1);
	}
	
	public void testSearchUsesSimpleView() throws Exception { 
		execute("CRUD.new");		
		assertValue("comment", "");
		assertNoDialog();
		execute("CRUD.search");
		assertDialog();
		assertNotExists("paid");
		assertNotExists("customerDiscount");
		assertNotExists("customer.number");
		assertValue("year", "");
		assertValue("number", "");
		setValue("year", "2002");
		setValue("number", "1");
		execute("Search.search");
		assertNoDialog();
		assertNoErrors();
		assertValue("comment", "INVOICE WITH SPACES");
	}
	
	public void testCollectionSelectionIsCleared() throws Exception {
		execute("Mode.detailAndFirst");
		execute("Sections.change", "activeSection=1");
		assertCollectionNotEmpty("details");
		checkRowCollection("details", 0);
		execute("Sections.change", "activeSection=3");
		assertCollectionNotEmpty("deliveries");
		checkRowCollection("deliveries", 0);
		execute("Navigation.next");
		assertCollectionNotEmpty("deliveries");
		assertRowCollectionUnchecked("deliveries", 0);
		execute("Sections.change", "activeSection=1");
		assertRowCollectionUnchecked("details", 0);
	}
	
	public void testGenerateCustomPdfAndPrepareNewAfter() throws Exception {
		execute("Mode.detailAndFirst");
		execute("Invoice.printPdfNewAfter");
		assertNoErrors(); 
		assertMessage("The print was successful");
		assertContentTypeForPopup("application/pdf");
		assertEditable("year");
	}
	
	public void testGenerateCustomPdfExcelRtfOdt() throws Exception { 
		execute("Mode.detailAndFirst");
		execute("Invoice.printPdf");
		assertNoErrors(); 
		assertMessage("The print was successful"); 
		assertContentTypeForPopup("application/pdf");
		
		execute("Invoice.printExcel");
		assertNoErrors(); 
		assertMessage("The print was successful");
		assertContentTypeForPopup("application/vnd.ms-excel");		
		
		execute("Invoice.printRtf");
		assertNoErrors(); 
		assertMessage("The print was successful");
		assertContentTypeForPopup("application/rtf");
		
		execute("Invoice.printOdt");
		assertNoErrors(); 
		assertMessage("The print was successful");
		assertContentTypeForPopup("application/vnd.oasis.opendocument.text");				
	}
	
	public void testGenerateTwoReportsAtOnce() throws Exception {  
		execute("Mode.detailAndFirst");
		execute("Invoice.print2Pdfs");		
		assertNoErrors();		
		assertPopupCount(2); 
		assertContentTypeForPopup(0, "application/pdf");
		assertContentTypeForPopup(1, "application/pdf");
	}
		
	// Only behaves thus when mapFacadeAutocommit=false (the default)
	public void testFailOnSaveFirstCollectionElementNotSaveMainEntity() throws Exception {
		if (XavaPreferences.getInstance().isMapFacadeAutoCommit()) return; 
		execute("CRUD.new");
		setValue("year", "2008");
		setValue("number", "66");
		setValue("comment", "JUNIT INVOICE");
		setValue("customer.number", "1");
		execute("Sections.change", "activeSection=2");
		setValue("vatPercentage", "16");
		execute("Sections.change", "activeSection=1");
		execute("Collection.new", "viewObject=xava_view_section1_details");
		execute("Collection.save");
		assertNoMessage("Invoice created successfully");
		closeDialog(); 
		execute("CRUD.new");
		assertValue("comment", "");
		setValue("year", "2008");
		setValue("number", "66");
		execute("CRUD.refresh");
		assertError("Object of type Invoice does not exists with key Number:66, Year:2008");
		assertValue("comment", "");
	}
	
	public void testPaginationInCollections() throws Exception {
		// The invoice 2007/14 has 14 detail lines
		execute("CRUD.new");
		setValue("year", "2007");
		setValue("number", "14");
		execute("CRUD.refresh");
		assertNoErrors();		
		assertValue("comment", "MORE THAN 10 LINES");
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("details", 10);
		execute("List.goNextPage", "collection=details");
		assertCollectionRowCount("details", 4);
		execute("List.goPreviousPage", "collection=details");
		assertCollectionRowCount("details", 10);
		execute("List.goPage", "page=2,collection=details");
		assertCollectionRowCount("details", 4);
		execute("List.goPage", "page=1,collection=details");
		assertCollectionRowCount("details", 10);
	}
	
	public void testGeneratePdfAggregateCollection() throws Exception {
		execute("Mode.detailAndFirst");
		execute("Sections.change", "activeSection=1");
		execute("Print.generatePdf", "viewObject=xava_view_section1_details");
		assertContentTypeForPopup("application/pdf");
	}
	
	public void testSearchByPropertyWithConverterInDetailMode() throws Exception {
		execute("CRUD.new");
		setValue("year", "");
		setValue("date", "");
		setValue("paid", "true");
		execute("CRUD.refresh");
		assertNoErrors();
	}
	
	public void testI18nOfLabelOfAConcreteView_alwaysEnabledActions() throws Exception {
		execute("CRUD.new");
		assertLabel("customer.number", "Little code");
		assertAction("Customer.changeNameLabel");
		assertAction("Customer.prefixStreet");
	}
	
	public void testTestingCheckBox() throws Exception {
		// Demo for make tests with checkbox
		
		// Create
		execute("CRUD.new");
		
		String year = getValue("year");		
		setValue("number", "66");
		
		setValue("customer.number", "1");
		
		// First vat percentage for no validation error on save first detail
		execute("Sections.change", "activeSection=2");
		setValue("vatPercentage", "23");
				
		createOneDetail(); // Because at least one detail is required
		setValue("paid", "true"); // assign true to checkbox
		
		execute("CRUD.save");
		assertNoErrors();
		assertValue("paid", "false"); // assert if checkbox is false
		
		// Consult
		setValue("year", year);
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("paid", "true");
		
		// Changing the boolean value
		setValue("paid", "false"); // assign false to checkbox
		execute("CRUD.save");
		assertNoErrors();
		
		// Consult again
		setValue("year", year);
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();		
		assertValue("paid", "false"); // assert if checkbox is false
		
		// Delete
		execute("CRUD.delete");		
		assertMessage("Invoice deleted successfully");				
	}
	
	public void testCustomizeListPaging() throws Exception { 
		execute("Invoice.setAddColumnsPageRowCountTo10");
		
		assertListColumnCount(8);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");
		
		execute("List.customize");
		execute("List.addColumns");
		
		assertNoAction("List.goAddColumnsPreviousPage");
		assertAction("List.goAddColumnsNextPage");
		
		execute("AddColumns.sort");
		
		assertCollectionRowCount("xavaPropertiesList", 10);
		assertValueInCollection("xavaPropertiesList",  0, 0, "comment");	
		assertValueInCollection("xavaPropertiesList",  1, 0, "considerable");
		assertValueInCollection("xavaPropertiesList",  2, 0, "customer.address.asString");
		assertValueInCollection("xavaPropertiesList",  3, 0, "customer.address.city");
		assertValueInCollection("xavaPropertiesList",  4, 0, "customer.address.state.fullName");
		assertValueInCollection("xavaPropertiesList",  5, 0, "customer.address.state.fullNameWithFormula");
		assertValueInCollection("xavaPropertiesList",  6, 0, "customer.address.state.id");		
		assertValueInCollection("xavaPropertiesList",  7, 0, "customer.address.state.name");		
		assertValueInCollection("xavaPropertiesList",  8, 0, "customer.address.street");		
		assertValueInCollection("xavaPropertiesList",  9, 0, "customer.address.zipCode");
		
		execute("List.goAddColumnsNextPage");
		assertCollectionRowCount("xavaPropertiesList", 10);
		assertValueInCollection("xavaPropertiesList", 0, 0, "customer.alternateSeller.level.description");
		assertValueInCollection("xavaPropertiesList", 1, 0, "customer.alternateSeller.level.id");			
		assertValueInCollection("xavaPropertiesList", 2, 0, "customer.alternateSeller.name");				
		assertValueInCollection("xavaPropertiesList", 3, 0, "customer.alternateSeller.number");
		assertValueInCollection("xavaPropertiesList", 4, 0, "customer.alternateSeller.regions");		
		assertValueInCollection("xavaPropertiesList", 5, 0, "customer.city");
		assertValueInCollection("xavaPropertiesList", 6, 0, "customer.email");
		assertValueInCollection("xavaPropertiesList", 7, 0, "customer.local");
		assertValueInCollection("xavaPropertiesList", 8, 0, "customer.name");
		assertValueInCollection("xavaPropertiesList", 9, 0, "customer.number");
	
		execute("List.goAddColumnsPage", "page=4");
		assertAction("List.goAddColumnsPreviousPage");
		assertNoAction("List.goAddColumnsNextPage");		
		assertCollectionRowCount("xavaPropertiesList", 9); 
		assertValueInCollection("xavaPropertiesList", 0, 0, "customer.website");
		assertValueInCollection("xavaPropertiesList", 1, 0, "customerDiscount");
		assertValueInCollection("xavaPropertiesList", 2, 0, "customerTypeDiscount");
		assertValueInCollection("xavaPropertiesList", 3, 0, "deliveryDate");
		assertValueInCollection("xavaPropertiesList", 4, 0, "productUnitPriceSum");
		assertValueInCollection("xavaPropertiesList", 5, 0, "sellerDiscount");
		assertValueInCollection("xavaPropertiesList", 6, 0, "total");
		assertValueInCollection("xavaPropertiesList", 7, 0, "vatPercentage");
		assertValueInCollection("xavaPropertiesList", 8, 0, "yearDiscount");
		
		execute("List.goAddColumnsPreviousPage");
		assertCollectionRowCount("xavaPropertiesList", 10);
		assertValueInCollection("xavaPropertiesList", 0, 0, "customer.photo");
		assertValueInCollection("xavaPropertiesList", 1, 0, "customer.relationWithSeller");
		assertValueInCollection("xavaPropertiesList", 2, 0, "customer.remarks");		
		assertValueInCollection("xavaPropertiesList", 3, 0, "customer.seller.level.description");
		assertValueInCollection("xavaPropertiesList", 4, 0, "customer.seller.level.id");				
		assertValueInCollection("xavaPropertiesList", 5, 0, "customer.seller.name");
		assertValueInCollection("xavaPropertiesList", 6, 0, "customer.seller.number");
		assertValueInCollection("xavaPropertiesList", 7, 0, "customer.seller.regions");		
		assertValueInCollection("xavaPropertiesList", 8, 0, "customer.telephone");
		assertValueInCollection("xavaPropertiesList", 9, 0, "customer.type");

		checkRow("selectedProperties", "customer.telephone");
		 		
		execute("AddColumns.addColumns");
		
		assertListColumnCount(9);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");
		assertLabelInList(8, "Telephone of Customer");

		// Restoring, for next time that test execute
		execute("List.removeColumn","columnIndex=8");
		assertListColumnCount(8);		
	}
	
	public void testCustomizeList() throws Exception {
		doTestCustomizeList_addColumns();
		tearDown(); setUp();
		doTestCustomizeList_storePreferences();
	}
	
	private void doTestCustomizeList_addColumns() throws Exception {
		assertListColumnCount(8);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");
		
		execute("List.customize");
		execute("List.addColumns");
		
		assertCollectionRowCount("xavaPropertiesList", 39); 
		execute("AddColumns.sort");		
		
		assertValueInCollection("xavaPropertiesList",  0, 0, "comment");
		assertValueInCollection("xavaPropertiesList",  0, 1, "Comment");
		assertValueInCollection("xavaPropertiesList",  1, 0, "considerable");
		assertValueInCollection("xavaPropertiesList",  2, 0, "customer.address.asString");
		assertValueInCollection("xavaPropertiesList",  3, 0, "customer.address.city");
		assertValueInCollection("xavaPropertiesList",  4, 0, "customer.address.state.fullName");
		assertValueInCollection("xavaPropertiesList",  4, 1, "Full name of State of Address of Customer");
		assertValueInCollection("xavaPropertiesList",  6, 0, "customer.address.state.id");		
		assertValueInCollection("xavaPropertiesList",  7, 0, "customer.address.state.name");		
		assertValueInCollection("xavaPropertiesList",  8, 0, "customer.address.street");		
		assertValueInCollection("xavaPropertiesList",  9, 0, "customer.address.zipCode");
		assertValueInCollection("xavaPropertiesList", 10, 0, "customer.alternateSeller.level.description");
		assertValueInCollection("xavaPropertiesList", 11, 0, "customer.alternateSeller.level.id");			
		assertValueInCollection("xavaPropertiesList", 12, 0, "customer.alternateSeller.name");				
		assertValueInCollection("xavaPropertiesList", 13, 0, "customer.alternateSeller.number");
		assertValueInCollection("xavaPropertiesList", 14, 0, "customer.alternateSeller.regions");		
		assertValueInCollection("xavaPropertiesList", 15, 0, "customer.city");
		assertValueInCollection("xavaPropertiesList", 16, 0, "customer.email");
		assertValueInCollection("xavaPropertiesList", 17, 0, "customer.local");
		assertValueInCollection("xavaPropertiesList", 18, 0, "customer.name");
		assertValueInCollection("xavaPropertiesList", 19, 0, "customer.number");
		assertValueInCollection("xavaPropertiesList", 20, 0, "customer.photo");
		assertValueInCollection("xavaPropertiesList", 21, 0, "customer.relationWithSeller");
		assertValueInCollection("xavaPropertiesList", 22, 0, "customer.remarks");		
		assertValueInCollection("xavaPropertiesList", 23, 0, "customer.seller.level.description");
		assertValueInCollection("xavaPropertiesList", 24, 0, "customer.seller.level.id");				
		assertValueInCollection("xavaPropertiesList", 25, 0, "customer.seller.name");
		assertValueInCollection("xavaPropertiesList", 26, 0, "customer.seller.number");
		assertValueInCollection("xavaPropertiesList", 27, 0, "customer.seller.regions");		
		assertValueInCollection("xavaPropertiesList", 28, 0, "customer.telephone");
		assertValueInCollection("xavaPropertiesList", 29, 0, "customer.type");
		assertValueInCollection("xavaPropertiesList", 30, 0, "customer.website");
		assertValueInCollection("xavaPropertiesList", 31, 0, "customerDiscount");
		assertValueInCollection("xavaPropertiesList", 32, 0, "customerTypeDiscount");
		assertValueInCollection("xavaPropertiesList", 33, 0, "deliveryDate");
		assertValueInCollection("xavaPropertiesList", 34, 0, "productUnitPriceSum");		
		assertValueInCollection("xavaPropertiesList", 35, 0, "sellerDiscount");
		assertValueInCollection("xavaPropertiesList", 36, 0, "total");
		assertValueInCollection("xavaPropertiesList", 37, 0, "vatPercentage");
		assertValueInCollection("xavaPropertiesList", 38, 0, "yearDiscount");
		
		checkRow("selectedProperties", "customer.address.city");
		checkRow("selectedProperties", "yearDiscount"); 
		 		
		execute("AddColumns.addColumns");
		
		
		assertListColumnCount(10);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");
		assertLabelInList(8, "City of Address of Customer");
		assertLabelInList(9, "Year discount");
		
		
		execute("List.addColumns");
		assertCollectionRowCount("xavaPropertiesList", 37); 
		assertValueInCollection("xavaPropertiesList",  0, 0, "comment");
		assertValueInCollection("xavaPropertiesList",  1, 0, "considerable");
		assertValueInCollection("xavaPropertiesList",  2, 0, "customer.address.asString");		
		assertValueInCollection("xavaPropertiesList",  3, 0, "customer.address.state.fullName");
		assertValueInCollection("xavaPropertiesList",  5, 0, "customer.address.state.id");		
		assertValueInCollection("xavaPropertiesList",  6, 0, "customer.address.state.name");		
		assertValueInCollection("xavaPropertiesList",  7, 0, "customer.address.street");		
		assertValueInCollection("xavaPropertiesList",  8, 0, "customer.address.zipCode");
		assertValueInCollection("xavaPropertiesList",  9, 0, "customer.alternateSeller.level.description");
		assertValueInCollection("xavaPropertiesList", 10, 0, "customer.alternateSeller.level.id");			
		assertValueInCollection("xavaPropertiesList", 11, 0, "customer.alternateSeller.name");				
		assertValueInCollection("xavaPropertiesList", 12, 0, "customer.alternateSeller.number");
		assertValueInCollection("xavaPropertiesList", 13, 0, "customer.alternateSeller.regions");		
		assertValueInCollection("xavaPropertiesList", 14, 0, "customer.city");
		assertValueInCollection("xavaPropertiesList", 15, 0, "customer.email");
		assertValueInCollection("xavaPropertiesList", 16, 0, "customer.local");
		assertValueInCollection("xavaPropertiesList", 17, 0, "customer.name");
		assertValueInCollection("xavaPropertiesList", 18, 0, "customer.number");
		assertValueInCollection("xavaPropertiesList", 19, 0, "customer.photo");
		assertValueInCollection("xavaPropertiesList", 20, 0, "customer.relationWithSeller");
		assertValueInCollection("xavaPropertiesList", 21, 0, "customer.remarks");		
		assertValueInCollection("xavaPropertiesList", 22, 0, "customer.seller.level.description");
		assertValueInCollection("xavaPropertiesList", 23, 0, "customer.seller.level.id");				
		assertValueInCollection("xavaPropertiesList", 24, 0, "customer.seller.name");
		assertValueInCollection("xavaPropertiesList", 25, 0, "customer.seller.number");
		assertValueInCollection("xavaPropertiesList", 26, 0, "customer.seller.regions");
		assertValueInCollection("xavaPropertiesList", 27, 0, "customer.telephone");
		assertValueInCollection("xavaPropertiesList", 28, 0, "customer.type");
		assertValueInCollection("xavaPropertiesList", 29, 0, "customer.website");
		assertValueInCollection("xavaPropertiesList", 30, 0, "customerDiscount");
		assertValueInCollection("xavaPropertiesList", 31, 0, "customerTypeDiscount");
		assertValueInCollection("xavaPropertiesList", 32, 0, "deliveryDate");
		assertValueInCollection("xavaPropertiesList", 33, 0, "productUnitPriceSum");		
		assertValueInCollection("xavaPropertiesList", 34, 0, "sellerDiscount");
		assertValueInCollection("xavaPropertiesList", 35, 0, "total");
		assertValueInCollection("xavaPropertiesList", 36, 0, "vatPercentage");		
 
		execute("AddColumns.cancel");
		
		assertListColumnCount(10);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");
		assertLabelInList(8, "City of Address of Customer");
		assertLabelInList(9, "Year discount");
				
	}
	
	private void doTestCustomizeList_storePreferences() throws Exception {
		// This test trusts that 'testCustomizeList_addColumns' is executed before
		assertListColumnCount(10);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");
		assertLabelInList(8, "City of Address of Customer");
		assertLabelInList(9, "Year discount");
				
		
		// Restoring, for next time that test execute
		execute("List.customize");
		execute("List.removeColumn","columnIndex=9");
		execute("List.removeColumn","columnIndex=8");
		
		assertListColumnCount(8);
		assertLabelInList(0, "Year");
		assertLabelInList(1, "Number");
		assertLabelInList(2, "Date");
		assertLabelInList(3, "Amounts sum");
		assertLabelInList(4, "V.A.T.");
		assertLabelInList(5, "Details count");
		assertLabelInList(6, "Paid");
		assertLabelInList(7, "Importance");
	}	
	
	public void testGenerateExcel() throws Exception {
		String year = getValueInList(0, 0);
		String number = getValueInList(0, 1);		
		String date = getValueInList(0, 2);
		String amountsSum = formatBigDecimal(getValueInList(0, 3));
		String vat = formatBigDecimal(getValueInList(0, 4));
		String detailsCount = getValueInList(0, 5);
		String paid = getValueInList(0, 6);
		String importance = Strings.firstUpper(getValueInList(0, 7).toLowerCase());
		String expectedLine = year + ";" + number + ";\"" + 
			date + "\";\"" + amountsSum + "\";\"" + 
			vat + "\";" + detailsCount + ";\"" +
			paid + "\";\"" + importance + "\"";
		
		execute("Print.generateExcel");
		assertContentTypeForPopup("text/x-csv");
		
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		String header = excel.nextToken();
		assertEquals("header", "Year;Number;Date;Amounts sum;V.A.T.;Details count;Paid;Importance", header);		
		String line1 = excel.nextToken();
		assertEquals("line1", expectedLine, line1);		
	}
	
	public void testGenerateExcelForOnlyCheckedRows() throws Exception { 
		checkRow(0);
		checkRow(2); // We assume that there are at least 3 invoices		
		execute("Print.generateExcel");
		assertContentTypeForPopup("text/x-csv");		
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		assertEquals("Must be exactly 3 (1 header + 2 detail) lines in the exported file", 
			3, excel.countTokens());	
	}
	
	public void testFilterByDate() throws Exception {
		String date = getValueInList(0, "date");		
		String [] conditionValues = { " ", " ", date, "true" };
		setConditionValues(conditionValues);
		execute("List.filter");
		assertDateInList(date);
		
		String [] yearComparators = { "=", "=", "year_comparator", ""};
		setConditionComparators(yearComparators);
		
		String [] condition2002 = { " ", " ", "2002", "true" }; // We supussed that there are invoices in 2002
		setConditionValues(condition2002);
		execute("List.filter");
		assertYearInList("02");

		String [] condition2004 = { " ", " ", "2004", "true" }; // We supussed that there are invoices in 2004
		setConditionValues(condition2004);
		execute("List.filter");
		assertYearInList("04");
		
		String [] monthComparators = { "=", "=", "month_comparator", ""};
		setConditionComparators(monthComparators);		
		String [] conditionMonth1 = { " ", " ", "1", "true" }; 
		setConditionValues(conditionMonth1);
		execute("List.filter");
		assertListRowCount(3); // We suppose that there are 3 invoices of month 1
		
		String [] yearMonthComparators = { "=", "=", "year_month_comparator", ""};
		setConditionComparators(yearMonthComparators);		
		String [] conditionYear2004Month1 = { " ", " ", "2004/1", "true" }; 
		setConditionValues(conditionYear2004Month1);
		execute("List.filter");
		assertListRowCount(2); // We suppose that there are 2 invoices of month 1 of year 2004				
	}
	
	public void testFilterByBoolean() throws Exception {
		int total = Invoice.findAll().size();		
		int paidOnes = Invoice.findPaidOnes().size();		
		int notPaidOnes = Invoice.findNotPaidOnes().size();
		
		assertTrue("It has to have invoices for run this test", total > 0);
		assertTrue("It has to have paid invoices for run this test", paidOnes > 0);		
		assertTrue("It has to have not paid invoices for run this test", notPaidOnes > 0);
		assertTrue("The sum of paid and not paid invoices has to match with the total count", total == (paidOnes + notPaidOnes));
		assertTrue("It has to have less than 10 invoices to run this test", total < 10); 
		assertListRowCount(total);
		
		String [] paidComparators = { "=", "=", "=", "="};
		String [] paidConditions = { "", "", "", "true"	};
		setConditionComparators(paidComparators);
		setConditionValues(paidConditions);
		execute("List.filter");
		assertListRowCount(paidOnes);
		
		String [] notPaidComparatos = { "=", "=", "=", "<>"};
		String [] notPaidConditions = { " ", " ", " ", "true" }; // For dark reasons it is necessary to leave a blank space so it runs.
		setConditionComparators(notPaidComparatos);
		setConditionValues(notPaidConditions);		
		execute("List.filter");
		assertNoErrors();
		assertListRowCount(notPaidOnes);		
		
		String [] totalComparators = { "=", "=", "=", ""};
		String [] totalCondition = { " ", " ", " ", "true" }; // Por razones oscuras hay que dejar un espacio en blanco para que funcione.
		setConditionComparators(totalComparators);
		setConditionValues(totalCondition);		
		execute("List.filter");
		assertNoErrors();
		assertListRowCount(total);				
	}
	
	public void testCreateFromReference() throws Exception {
		execute("CRUD.new");		
		execute("Reference.createNew", "model=Customer,keyProperty=xava.Invoice.customer.number");
		assertNoErrors();
		assertAction("NewCreation.saveNew");
		assertAction("NewCreation.cancel");	
		assertValue("Customer", "type", usesAnnotatedPOJO()?"0":"1");
		execute("Reference.search", "keyProperty=xava.Customer.alternateSeller.number");
		assertNoErrors();
		execute("ReferenceSearch.cancel");
		assertAction("NewCreation.saveNew");
		assertAction("NewCreation.cancel");	
		assertValue("Customer", "type", usesAnnotatedPOJO()?"0":"1");		
		execute("NewCreation.cancel");
		assertExists("year");
		assertExists("number");
	}
	
	public void testChangeTab() throws Exception {		
		assertListColumnCount(8);
		execute("Invoice.changeTab");
		assertNoErrors();
		assertListColumnCount(3);
	}	
	
	public void testDateFormatter() throws Exception { 
		// In order to this test works inside Liferay you have to put
		// locale.default.request=true in portal-ext.properties
		setLocale("es");		
		execute("CRUD.new");
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		String originalDate = getValue("date"); // For restore at end
		
		setValue("date", "1/1/2004");
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("date", "01/01/2004");
		
		setValue("date", "02012004");
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("date", "02/01/2004");
		
		setValue("date", "3.1.2004");
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("date", "03/01/2004");
		
		setValue("date", "4-1-2004");
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("date", "04/01/2004");
		
		setValue("date", "4/1/32"); // If current year is 2012
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("date", "04/01/2032");
		
		setValue("date", "040133"); // If current year is 2012
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("date", "04/01/1933");
		
		setValue("date", "30/2/2008");
		execute("CRUD.save");
		assertError("Fecha en Factura no es un dato del tipo esperado"); 		
		
		// Restore original date
		setValue("date", originalDate);
		execute("CRUD.save");
		assertNoErrors();		 				
	}

	public void testValidateExistsRequiredReference() throws Exception { 
		execute("CRUD.new");		
		setValue("number", "66");
		execute("Sections.change", "activeSection=2");
		setValue("vatPercentage", "24");		
		execute("CRUD.save");
		assertError("Value for Customer in Invoice is required");				
	}
	
	public void testNotEditableCustomerData() throws Exception { 
		execute("CRUD.new");		
		assertEditable("customer.number");
		assertNoEditable("customer.name");
		assertNoEditable("customer.address.street");
	}
	
	public void testSearchReferenceWithListInsideSection() throws Exception {		
		execute("CRUD.new");		
				
		execute("Reference.search", "keyProperty=xava.Invoice.customer.number");
		String customerName = getValueInList(0, 0);		
		checkRow(0);		
		execute("ReferenceSearch.choose");
		assertValue("customer.name", customerName);				
	}
	
	public void testSections_aggregateCollection_orderedCollectionsInModel_posdeleteCollectionElement() throws Exception {  		
		// Create
		execute("CRUD.new");					
		assertExists("customer.number");
		assertNotExists("vatPercentage");
		
		String year = getValue("year");		
		setValue("number", "66");
		
		setValue("customer.number", "1");
		assertValue("customer.number", "1");
		assertValue("customer.name", "Javi");
		
		// First vat percentage for no validation error on save first detail
		execute("Sections.change", "activeSection=2");
		assertNotExists("customer.number");
		assertExists("vatPercentage");
		assertValue("amountsSum", "");
		setValue("vatPercentage", "23");
				
		execute("Sections.change", "activeSection=1");
		assertNotExists("customer.number");
		assertNotExists("vatPercentage");
		
		assertCollectionRowCount("details", 0);
		
		assertNoDialog();
		execute("Collection.new", "viewObject=xava_view_section1_details");
		assertDialog();
		setValue("serviceType", usesAnnotatedPOJO()?"":"0");
		setValue("quantity", "20");
		setValue("unitPrice", getProductUnitPrice());		
		assertValue("amount", getProductUnitPriceMultiplyBy("20"));
		setValue("product.number", getProductNumber());
		assertValue("product.description", getProductDescription());
		assertValue("deliveryDate", getCurrentDate()); 
		setValue("deliveryDate", "03/18/04"); // Testing multiple-mapping in aggregate
		setValue("soldBy.number", getProductNumber());
		execute("Collection.save");		
		assertMessage("Invoice detail created successfully");
		assertMessage("Invoice created successfully"); 
		assertNoErrors();		
		// assertExists("serviceType"); // In OX3 it does not hide detail on save, 
		assertNoDialog();			// but since OX4m2 a dialog is used and it's close
		assertCollectionRowCount("details", 1);

		// Next line tests IModelCalculator in an aggregate collection (only apply to XML components)
		assertValueInCollection("details", 0, "free", "0".equals(getProductUnitPrice())?"Yes":"No"); 

		assertNoEditable("year"); // Testing header is saved
		assertNoEditable("number");
		
		// Testing if recalculate dependent properties
		execute("Sections.change", "activeSection=2");
		assertValue("amountsSum", getProductUnitPriceMultiplyBy("20")); 
		setValue("vatPercentage", "23");		
		execute("Sections.change", "activeSection=1");
		// end of recalculate testing
		
		execute("Collection.new", "viewObject=xava_view_section1_details"); 
		setValue("serviceType", usesAnnotatedPOJO()?"0":"1");
		setValue("quantity", "200");
		setValue("unitPrice", getProductUnitPrice());		
		assertValue("amount", getProductUnitPriceMultiplyBy("200"));
		setValue("product.number", getProductNumber());
		assertValue("product.description", getProductDescription());
		setValue("deliveryDate", "3/19/04"); // Testing multiple-mapping in aggregate
		setValue("soldBy.number", getProductNumber());		
		execute("Collection.saveAndStay");
		
		setValue("serviceType", usesAnnotatedPOJO()?"1":"2");
		setValue("quantity", "2");
		setValue("unitPrice", getProductUnitPrice());
		assertValue("amount", getProductUnitPriceMultiplyBy("2"));
		setValue("product.number", getProductNumber());
		assertValue("product.description", getProductDescription());
		assertValue("deliveryDate", getCurrentDate()); 
		setValue("deliveryDate", "3/20/04"); // Testing multiple-mapping in aggregate
		execute("Collection.save");
		assertCollectionRowCount("details", 3); 
				
		assertValueInCollection("details", 0, 0, "Urgent");
		assertValueInCollection("details", 0, 1, getProductDescription());
		assertValueInCollection("details", 0, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 0, 3, "2");
		assertValueInCollection("details", 0, 4, getProductUnitPrice());
		assertValueInCollection("details", 0, 5, getProductUnitPriceMultiplyBy("2"));

		assertValueInCollection("details", 1, 0, "Special");
		assertValueInCollection("details", 1, 1, getProductDescription());
		assertValueInCollection("details", 1, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 1, 3, "200");
		assertValueInCollection("details", 1, 4, getProductUnitPrice());
		assertValueInCollection("details", 1, 5, getProductUnitPriceMultiplyBy("200"));

		assertValueInCollection("details", 2, 0, "");
		assertValueInCollection("details", 2, 1, getProductDescription());
		assertValueInCollection("details", 2, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 2, 3, "20");
		assertValueInCollection("details", 2, 4, getProductUnitPrice());
		assertValueInCollection("details", 2, 5, getProductUnitPriceMultiplyBy("20"));
										
		execute("CRUD.save");
		assertNoErrors();
		assertValue("number", "");
		execute("Sections.change", "activeSection=0");
		assertValue("customer.number", "");
		assertValue("customer.name", "");
		execute("Sections.change", "activeSection=1");				
		assertCollectionRowCount("details", 0);
		execute("Sections.change", "activeSection=2");
		assertValue("vatPercentage", "");
	
		// Consulting	
		setValue("year", year);
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("year", year);
		assertValue("number", "66");
		execute("Sections.change", "activeSection=0");
		assertValue("customer.number", "1");
		assertValue("customer.name", "Javi");
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("details", 3);
		
		assertValueInCollection("details", 0, 0, "Urgent");
		assertValueInCollection("details", 0, 1, getProductDescription());
		assertValueInCollection("details", 0, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 0, 3, "2");
		assertValueInCollection("details", 0, 4, getProductUnitPrice());
		assertValueInCollection("details", 0, 5, getProductUnitPriceMultiplyBy("2"));

		assertValueInCollection("details", 1, 0, "Special");
		assertValueInCollection("details", 1, 1, getProductDescription());
		assertValueInCollection("details", 1, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 1, 3, "200");
		assertValueInCollection("details", 1, 4, getProductUnitPrice());
		assertValueInCollection("details", 1, 5, getProductUnitPriceMultiplyBy("200"));

		assertValueInCollection("details", 2, 0, "");
		assertValueInCollection("details", 2, 1, getProductDescription());
		assertValueInCollection("details", 2, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 2, 3, "20");
		assertValueInCollection("details", 2, 4, getProductUnitPrice());
		assertValueInCollection("details", 2, 5, getProductUnitPriceMultiplyBy("20"));
				
		execute("Sections.change", "activeSection=2");		
		assertValue("vatPercentage", "23");
		
		// Edit line
		execute("Sections.change", "activeSection=1");		
		assertNotExists("product.description");
		assertNotExists("quantity");
		assertNotExists("deliveryDate");
		execute("Invoice.editDetail", "row=1,viewObject=xava_view_section1_details");
		assertValue("product.description", getProductDescription());
		assertValue("quantity", "200");
		assertValue("deliveryDate", "3/19/04");
		setValue("quantity", "234");
		setValue("deliveryDate", "4/23/04");
		execute("Collection.save");
		assertNoErrors();
		assertMessage("Invoice detail modified successfully");
		assertValueInCollection("details", 1, 3, "234");		
		assertNotExists("product.description"); 
		assertNotExists("quantity");
		assertNotExists("deliveryDate");
		execute("Invoice.editDetail", "row=1,viewObject=xava_view_section1_details");
		assertValue("product.description", getProductDescription());
		assertValue("quantity", "234");
		assertValue("deliveryDate", "4/23/04");
		closeDialog(); 
		
		// Return to save and consult for see if the line is edited
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", year);
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();		
		assertValueInCollection("details", 1, 0, "Special");
		assertValueInCollection("details", 1, 1, getProductDescription());
		assertValueInCollection("details", 1, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 1, 3, "234");
		assertValueInCollection("details", 1, 4, getProductUnitPrice());
		assertValueInCollection("details", 1, 5, getProductUnitPriceMultiplyBy("234"));
		assertNotExists("product.description");
		assertNotExists("quantity");
		assertNotExists("deliveryDate");
		execute("Invoice.editDetail", "row=1,viewObject=xava_view_section1_details");
		assertValue("product.description", getProductDescription());
		assertValue("quantity", "234");
		assertValue("deliveryDate", "4/23/04");
		closeDialog();
		
		// Verifying that it do not delete member in collection that not are in list
		execute("CRUD.new");
		setValue("year", year);
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();
		execute("CRUD.save");
		assertNoErrors();
		setValue("year", year);
		setValue("number", "66");		
		execute("CRUD.refresh");
		assertNoErrors();
		execute("Sections.change", "activeSection=1");
		
		assertCollectionRowCount("details", 3); 
		execute("Invoice.editDetail", "row=1,viewObject=xava_view_section1_details");
		assertValue("product.description", getProductDescription());
		assertValue("quantity", "234");
		assertValue("deliveryDate", "4/23/04");
		
		// Remove a row from collection
		execute("Collection.remove");
		assertMessage("Invoice detail deleted from database");
		assertCollectionRowCount("details", 2);
		assertValueInCollection("details", 0, 0, "Urgent");
		assertValueInCollection("details", 0, 1, getProductDescription());
		assertValueInCollection("details", 0, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 0, 3, "2");
		assertValueInCollection("details", 0, 4, getProductUnitPrice());
		assertValueInCollection("details", 0, 5, getProductUnitPriceMultiplyBy("2"));

		assertValueInCollection("details", 1, 0, "");
		assertValueInCollection("details", 1, 1, getProductDescription());
		assertValueInCollection("details", 1, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 1, 3, "20");
		assertValueInCollection("details", 1, 4, getProductUnitPrice());
		assertValueInCollection("details", 1, 5, getProductUnitPriceMultiplyBy("20"));
		
		
		//ejecutar("CRUD.save"); // It is not necessary delete for record the deleted of a row 		
		execute("CRUD.new");
		execute("Sections.change", "activeSection=1");
		assertNoErrors();
		assertCollectionRowCount("details", 0);
		
		// Verifying that line is deleted
		setValue("year", year);
		setValue("number", "66");
		execute("CRUD.refresh");
		assertNoErrors();
		
		assertCollectionRowCount("details", 2); 
		assertValueInCollection("details", 0, 0, "Urgent");
		assertValueInCollection("details", 0, 1, getProductDescription());
		assertValueInCollection("details", 0, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 0, 3, "2");
		assertValueInCollection("details", 0, 4, getProductUnitPrice());
		assertValueInCollection("details", 0, 5, getProductUnitPriceMultiplyBy("2"));

		assertValueInCollection("details", 1, 0, "");
		assertValueInCollection("details", 1, 1, getProductDescription());
		assertValueInCollection("details", 1, 2, getProductUnitPriceInPesetas());
		assertValueInCollection("details", 1, 3, "20");
		assertValueInCollection("details", 1, 4, getProductUnitPrice());
		assertValueInCollection("details", 1, 5, getProductUnitPriceMultiplyBy("20"));
		
		assertValue("comment", "DETAIL DELETED"); // verifying postdelete-calculator in collection
		
		// Testing if recalculate dependent properties on remove using chechbox in collection
		execute("Sections.change", "activeSection=2");		
		assertValue("amountsSum", getSumOf2ProductsUnitPriceMultiplyBy("2", "20"));		
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("details", 2);
		checkRowCollection("details", 0);
		execute("Collection.removeSelected", "viewObject=xava_view_section1_details");
		assertNoErrors();
		assertCollectionRowCount("details", 1);
		assertRowCollectionUnchecked("details", 0); 
		execute("Sections.change", "activeSection=2");		 		
		assertValue("amountsSum", getProductUnitPriceMultiplyBy("20"));
		// end of recalculate testing		
								
		// Delete		
		execute("CRUD.delete");
		assertMessage("Invoice deleted successfully");
	}
	
	public void testAggregateValidatorUsingReferencesToContainer() throws Exception { 		
		// Create
		execute("CRUD.new");				
						
		setValue("number", "66");
		setValue("paid", "true");
		setValue("customer.number", "1");
		
		// First, vat percentage for not validate errors on save first detail  
		execute("Sections.change", "activeSection=2");
		setValue("vatPercentage", "23");
				
		execute("Sections.change", "activeSection=1");
		
		execute("Collection.new", "viewObject=xava_view_section1_details");
		setValue("serviceType", "0");
		setValue("quantity", "20");
		setValue("unitPrice", getProductUnitPrice());
		assertValue("amount", getProductUnitPriceMultiplyBy("20"));
		setValue("product.number", getProductNumber());
		assertValue("product.description", getProductDescription());				
		setValue("deliveryDate", "03/18/04");
		setValue("soldBy.number", getProductNumber());
		execute("Collection.save");		
		assertError("It is not possible to add details, the invoice is paid"); 
		
		if (XavaPreferences.getInstance().isMapFacadeAutoCommit()) {
			execute("CRUD.delete");
			assertMessage("Invoice deleted successfully");
		}		
	}
	
	
	public void testValidationOnSaveAggregateAndModelValidatorReceivesReferenceAndCalculatedProperty() throws Exception {		
		// Create
		execute("CRUD.new");						
		assertExists("customer.number");
		assertNotExists("vatPercentage");
				
		setValue("number", "66");
		
		setValue("customer.number", "1");
		assertValue("customer.number", "1");
		assertValue("customer.name", "Javi");
		
		// First, vat percentage for not validation errors on save first detail
		execute("Sections.change", "activeSection=2");
		assertNotExists("customer.number");
		assertExists("vatPercentage");						
		setValue("vatPercentage", "23");
				
		execute("Sections.change", "activeSection=1");
		assertNotExists("customer.number");
		assertNotExists("vatPercentage");
		
		assertCollectionRowCount("details", 0); 
		
		assertNoDialog(); 
		execute("Collection.new", "viewObject=xava_view_section1_details");
		assertDialog(); 
		setValue("serviceType", "0");
		setValue("quantity", "20");
		setValue("unitPrice", getProductUnitPricePlus10());
		assertValue("amount", "600.00");
		assertValue("product.number", "");
		assertValue("product.description", ""); 
		setValue("deliveryDate", "03/18/04");
		setValue("soldBy.number", getProductNumber());
		execute("Collection.save"); 		
		assertError("It is needed specify a product for a valid invoice detail");
		
		setValue("product.number", getProductNumber()); 
		assertValue("product.description", getProductDescription());
		execute("Collection.save");
		assertError("Invoice price of a product can not be greater to official price of the product");
		assertDialog(); 
		
		setValue("unitPrice", getProductUnitPrice());
		execute("Collection.save");		 
		assertNoErrors();
		assertNoDialog(); 
		
		// Delete
		execute("CRUD.delete");		
		assertMessage("Invoice deleted successfully");
	}
	
	
	public void testDefaultValueCalculation() throws Exception {		
		execute("CRUD.new");
		assertValue("year", getCurrentYear());		
		assertValue("date", getCurrentDate());
		assertValue("yearDiscount", getYearDiscount(getCurrentYear()));
		setValue("year", "2002");
		assertValue("yearDiscount", getYearDiscount("2002"));		
	}
	
	public void testCalculatedValuesFromSubviewToUpperView() throws Exception {
		execute("CRUD.new");		
		assertValue("customerDiscount", "");
		assertValue("customerTypeDiscount", "");
		assertValue("customer.number", "");
		assertValue("customer.name", "");
		setValue("customer.number", "1");
		assertValue("customer.number", "1");
		assertValue("customer.name", "Javi");
		assertValue("customerDiscount", "11.50");
		//assertValue("customerTypeDiscount", "30"); // Still not supported: customer type 
					// changes at same time that number, and to throw the change  
					// of 2 properties at same time still is not supported
		setValue("customer.number", "2");
		assertValue("customer.number", "2");		
		assertValue("customerDiscount", "22.75");
		setValue("customer.number", "3");
		assertValue("customer.number", "3");		
		assertValue("customerDiscount", "0.25");				
	}
	
	public void testCalculatedValueOnChangeBoolean() throws Exception {
		execute("CRUD.new");		
		assertValue("customerDiscount", "");
		setValue("paid", "true");
		assertValue("customerDiscount", "77.00");				
	}
		
	public void testEditableCollectionActions_i18nforMemberOfCollections() throws Exception {
		execute("CRUD.new");
		String [] initialActions = {
			"Navigation.previous",
			"Navigation.first",
			"Navigation.next",
			"CRUD.new",
			"CRUD.save",
			"CRUD.delete",
			"CRUD.search",
			"CRUD.refresh",
			"Invoice.printPdf",
			"Invoice.print2Pdfs",
			"Invoice.printExcel",
			"Invoice.printRtf",
			"Invoice.printOdt",
			"Invoice.removeViewDeliveryInInvoice",
			"Invoice.addViewDeliveryInInvoice",			
			"Invoice.viewCustomer",
			"Sections.change",
			"Customer.changeNameLabel",
			"Customer.prefixStreet",
			"Reference.search",
			"Reference.createNew",			
			"Reference.modify",
			"Mode.list",
			"Mode.split",
			"Invoice.printPdfNewAfter"
		};		
		assertActions(initialActions);
				
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();
		
		execute("Sections.change", "activeSection=1");

		String [] aggregateListActions = {
			"Navigation.previous",
			"Navigation.first",
			"Navigation.next",
			"CRUD.new",
			"CRUD.save",
			"CRUD.delete",
			"CRUD.search",
			"CRUD.refresh",
			"Invoice.printPdf",
			"Invoice.print2Pdfs",
			"Invoice.printExcel",
			"Invoice.printRtf",
			"Invoice.printOdt",
			"Invoice.removeViewDeliveryInInvoice",
			"Invoice.addViewDeliveryInInvoice",									
			"Invoice.viewCustomer",
			"Mode.list",
			"Mode.split",
			"Sections.change",
			"Invoice.editDetail", // because it is overwrite, otherwise 'Collection.edit'
			"Collection.new",
			"Collection.removeSelected",
			"Print.generatePdf", // In collection
			"Print.generateExcel", // In collection
			"List.filter", 
			"List.orderBy", 
			"List.customize",
			"List.sumColumn",
			"Invoice.printPdfNewAfter"			
		};		
		assertActions(aggregateListActions); 
		
		execute("Invoice.editDetail", "row=0,viewObject=xava_view_section1_details");
		
		String [] aggregateDetailActions = { 
			"Reference.createNew",
			"Reference.search",
			"Reference.modify",
			"Gallery.edit",
			"Collection.save",
			"Collection.remove",
			"Collection.hideDetail",
			"Invoice.viewProduct"
		};				
		assertActions(aggregateDetailActions);
		
		assertEditable("serviceType");
		
		closeDialog(); 
		// i18n for member of collections
		// In resource file we have: Invoice.details.product.description=Product
		assertLabelInCollection("details", 1, "Product");
	}
	
	public void testDetailActionInCollection_overwriteEditAction_goAndReturnToAnotherXavaView() throws Exception {
		assertNoListTitle();
		execute("CRUD.new");							
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();		
		execute("Sections.change", "activeSection=1");		
		assertNoDialog(); 
		execute("Invoice.editDetail", "row=0,viewObject=xava_view_section1_details");
		assertDialog(); 
		assertNoErrors();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		assertValue("remarks", "Edit at " + df.format(new java.util.Date())); 
		
		String productNumber = getValue("product.number");
		assertTrue("Detail must have product number", !Is.emptyString(productNumber));
		String productDescription = getValue("product.description");
		assertTrue("Detail must have product description", !Is.emptyString(productDescription));
				
		execute("Invoice.viewProduct"); 
		assertNoErrors();
		assertNoAction("CRUD.new");
		assertAction("ProductFromInvoice.return");
		assertValue("Product", "number", productNumber);
		assertValue("Product", "description", productDescription);
		
		execute("ProductFromInvoice.return");
		assertNoErrors();
		assertAction("CRUD.new");
		assertNoAction("ProductFromInvoice.return");
		assertValue("year", String.valueOf(getInvoice().getYear()));
		assertValue("number", String.valueOf(getInvoice().getNumber()));									
	}
	
	public void testShowNewViewAndReturn() throws Exception { 		
		execute("CRUD.new");							
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();		
		
		String customerNumber = getValue("customer.number");
		assertTrue("Invoice must to have customer number", !Is.emptyString(customerNumber));
		String customerName = getValue("customer.name");
		assertTrue("Detail must to have customer name", !Is.emptyString(customerName));
				
		execute("Invoice.viewCustomer"); 
		assertNoErrors();
		assertNoAction("CRUD.new");
		assertAction("Return.return");
		assertValue("Customer", "number", customerNumber);
		assertValue("Customer", "name", customerName);
		
		execute("Return.return");
		assertNoErrors();
		assertAction("CRUD.new");
		assertNoAction("Return.return");
		assertValue("year", String.valueOf(getInvoice().getYear()));
		assertValue("number", String.valueOf(getInvoice().getNumber()));									
	}
	
	
	
	public void testViewCollectionElementWithKeyWithReference() throws Exception {
		deleteInvoiceDeliveries();
		createDelivery();
		
		execute("CRUD.new");
		setValue("year", String.valueOf(getInvoice().getYear()));
		setValue("number", String.valueOf(getInvoice().getNumber()));
		
		execute("CRUD.refresh");
		assertNoErrors();
		
		execute("Sections.change", "activeSection=3");
		assertCollectionRowCount("deliveries", 1);
		
		assertNoDialog(); 
		execute("Collection.view", "row=0,viewObject=xava_view_section3_deliveries");
		assertDialog(); 
		assertValue("number", "666");		
		assertValue("date", "2/22/04");		
		assertValue("description", "DELIVERY JUNIT 666");
		assertNoEditable("number"); 
		assertNoEditable("date"); 		
		assertNoEditable("description"); 		
	}
	
	public void testDefaultValueInDetailCollection() throws Exception {
		execute("CRUD.new");
		execute("Sections.change", "activeSection=1");		
		execute("Collection.new", "viewObject=xava_view_section1_details");
		assertValue("deliveryDate", getCurrentDate()); 
	}
				
	public void testCalculatedPropertiesInSection() throws Exception {
		execute("Mode.detailAndFirst");
		execute("Sections.change", "activeSection=2");		
		String samountsSum = getValue("amountsSum");		
		BigDecimal amountsSum = stringToBigDecimal(samountsSum);
		assertTrue("Amounts sum not must be zero", amountsSum.compareTo(new BigDecimal("0")) != 0);
		String svatPercentage = getValue("vatPercentage"); 		
		BigDecimal vatPercentage = stringToBigDecimal(svatPercentage);
		BigDecimal newVatPercentage = vatPercentage.add(new BigDecimal("1")).setScale(0);
		setValue("vatPercentage", newVatPercentage.toString());		
		BigDecimal vat = amountsSum.multiply(newVatPercentage).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		String svat = nf.format(vat);
		assertValue("vat", svat);
	}		
	
	private BigDecimal stringToBigDecimal(String s) throws ParseException {
		NumberFormat nf = NumberFormat.getInstance();
		Number n = nf.parse(s);
		return new BigDecimal(n.toString());
	}
		
	private void deleteInvoiceDeliveries() throws Exception {
		// Also delete transport charge, because they can reference to some delivery
		XPersistence.getManager().createQuery("delete from TransportCharge").executeUpdate();
		Iterator it = getInvoice().getDeliveries().iterator();
		while (it.hasNext()) {
			Delivery delivery = (Delivery) PortableRemoteObject.narrow(it.next(), Delivery.class);
			XPersistence.getManager().remove(delivery);	
		}		
	}

	private void createDelivery() throws Exception {
		Delivery delivery = new Delivery();
		delivery.setInvoice(getInvoice());
		DeliveryType type = XPersistence.getManager().find(DeliveryType.class, 1);
		delivery.setType(type); 		
		delivery.setNumber(666);
		delivery.setDate(Dates.create(22,2,2004));
		delivery.setDescription("Delivery JUNIT 666");
		delivery.setRemarks("FOUR\nLINES\nCUATRO\nLINEAS"); // It's used in DeliveriesRemarks2002Test
		DeliveryTypeTest.setDeliveryAdvice(delivery, "JUNIT ADVICE");
				
		XPersistence.getManager().persist(delivery);
		XPersistence.commit();
		
	}
	
	
	private String getProductNumber() throws Exception {
		if (productNumber == null) {
			productNumber = String.valueOf(getProduct().getNumber());
		}
		return productNumber;
	}

	private String getProductDescription() throws Exception {
		if (productDescription == null) {
			productDescription = getProduct().getDescription();
		}
		return productDescription;
	}
		
	private String getProductUnitPriceInPesetas() throws Exception {
		if (productUnitPriceInPesetas == null) {			
			productUnitPriceInPesetas = DecimalFormat.getInstance().format(getProduct().getUnitPriceInPesetas());
		}
		return productUnitPriceInPesetas;
		
	}
	
	private String getProductUnitPrice() throws Exception {
		if (productUnitPrice == null) {			
			productUnitPrice = getMoneyFormat().format(getProductUnitPriceDB());
		}
		return productUnitPrice;		
	}
	
	private BigDecimal getProductUnitPriceDB() throws RemoteException, Exception {
		if (productUnitPriceDB == null) {
			productUnitPriceDB = getProduct().getUnitPrice();
		}
		return productUnitPriceDB;
	}
	
	private String getProductUnitPricePlus10() throws Exception {
		if (productUnitPricePlus10 == null) {			
			productUnitPricePlus10 = getMoneyFormat().format(getProductUnitPriceDB().add(new BigDecimal("10")));
		}
		return productUnitPricePlus10;		
	}
	
	private String getProductUnitPriceMultiplyBy(String quantity) throws Exception {
		return getMoneyFormat().format(getProductUnitPriceDB().multiply(new BigDecimal(quantity)));
	}

	private NumberFormat getMoneyFormat() {
		NumberFormat f = NumberFormat.getNumberInstance();
		f.setMinimumFractionDigits(2);
		f.setMaximumFractionDigits(2);
		return f;
	}
	
	
	private String getSumOf2ProductsUnitPriceMultiplyBy(String quantity1, String quantity2) throws Exception { 
		BigDecimal sum = getProductUnitPriceDB().multiply(new BigDecimal(quantity1)).add(getProductUnitPriceDB().multiply(new BigDecimal(quantity2)));		
		return getMoneyFormat().format(sum);
	}
	
	
	private Product getProduct() throws Exception {
		if (product == null) {
			product = (Product) XPersistence.getManager().find(Product.class, new Long(2));
		}
		return product;
	}
		
	private String getCurrentDate() {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		return df.format(new java.util.Date());
	}
	
	private String getCurrentYear() {
		DateFormat df = new SimpleDateFormat("yyyy");
		return df.format(new java.util.Date());
	}
	
	private String getYearDiscount(String syear) throws Exception {
		int year = Integer.parseInt(syear);
		YearInvoiceDiscountCalculator calculator = new YearInvoiceDiscountCalculator();
		calculator.setYear(year);
		BigDecimal bd = (BigDecimal) calculator.calculate();		
		return getMoneyFormat().format(bd);
	}

	private Invoice getInvoice() throws Exception {
		if (invoice == null) {	
 			Collection invoices = XPersistence.getManager().createQuery("from Invoice").getResultList(); 
			Iterator it = invoices.iterator();
			while (it.hasNext()) {			
				Invoice inv = (Invoice) it.next();
				if (inv.getDetailsCount() > 0) {
					invoice = inv;
					break;
				}			
			}
			if (invoice == null) {
				fail("It must to exists at least one invoice with details for run this test");
			}
		}
		return invoice;
	}
	
	private void assertDateInList(String date) throws Exception {
		int c = getListRowCount();
		for (int i=0; i<c; i++) {
			assertValueInList(i, "date", date);
		}
	}
	
	private void assertYearInList(String year) throws Exception {
		int c = getListRowCount();
		for (int i=0; i<c; i++) {
			String date = getValueInList(i, "date");
			assertTrue(date + " is not of " + year, date.endsWith(year));
		}
	}
	
	private String formatBigDecimal(String value) throws Exception {
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.UK);
		nf.setMinimumFractionDigits(2);
		Number b = nf.parse(value);
		return nf.format(b);
	}
	
	private void createOneDetail() throws Exception {
		Calendar date = Calendar.getInstance();
		String todayDate = (date.get(Calendar.MONTH) + 1) + "/" +
				date.get(Calendar.DAY_OF_MONTH) + "/" +
				(date.get(Calendar.YEAR) - 2000);
		execute("Sections.change", "activeSection=1");
		assertNotExists("customer.number");
		assertNotExists("vatPercentage");

		assertCollectionRowCount("details", 0);

		execute("Collection.new", "viewObject=xava_view_section1_details");
		setValue("serviceType", "0");
		setValue("quantity", "20");
		setValue("unitPrice", getProductUnitPrice());
		assertValue("amount", getProductUnitPriceMultiplyBy("20"));
		setValue("product.number", getProductNumber());
		assertValue("product.description", getProductDescription());
		setValue("deliveryDate", "09/05/2007");
		setValue("soldBy.number", getProductNumber());
		execute("Collection.saveAndStay");
		assertMessage("Invoice detail created successfully");
		// validate if fields are cleared
		assertValue("quantity", "");
		// validate that default values were ran
		assertValue("deliveryDate", todayDate);
		assertAction("Collection.saveAndStay");
		execute("Collection.hideDetail");
		assertNoErrors();
	}

	public void testInvoiceNotFound() throws Exception {
		execute("CRUD.new");
		// with key
		String year = getValue("year");
		execute("CRUD.refresh");
		assertError("Object of type Invoice does not exists with key Year:" + year);
		// without key
		assertTrue(Is.empty(getValue("year")));
		setValue("date", "1/2/2004");
		execute("CRUD.refresh");
		assertError("Object of type Invoice does not exists with key Date:1/2/04, Paid:No");
		// with reference
		setValue("customer.number", "43");
		assertValue("customer.name", "Gonzalo Gonzalez");
		execute("CRUD.refresh");
		assertError("Object of type Invoice does not exists with key Number:43, Customer discount:0.25, Paid:No");
	}
}
