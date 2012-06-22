package org.openxava.test.tests;

import java.text.*;
import java.util.*;

import org.openxava.hibernate.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * @author Javier Paniza
 */

public class WarehouseTest extends WarehouseSplitTestBase {
	
		
	public WarehouseTest(String testName) {
		super(testName, "Warehouse");		
	}
	
	public void testSortByTwoColumns() throws Exception {
		execute("List.orderBy", "property=number");
		execute("List.orderBy", "property=zoneNumber");
		assertValueInList(0, 0, "1"); assertValueInList(0, 1, "1");
		assertValueInList(1, 0, "1"); assertValueInList(1, 1, "2");
		assertValueInList(2, 0, "1"); assertValueInList(2, 1, "3");
		assertValueInList(3, 0, "2"); assertValueInList(3, 1, "1");
		assertValueInList(4, 0, "3"); assertValueInList(4, 1, "1");
		assertValueInList(5, 0, "4"); assertValueInList(5, 1, "2");
		assertValueInList(6, 0, "4"); assertValueInList(6, 1, "3");
		assertValueInList(7, 0, "4"); assertValueInList(7, 1, "4");
		assertValueInList(8, 0, "4"); assertValueInList(8, 1, "5");		
		assertValueInList(9, 0, "4"); assertValueInList(9, 1, "6");

		execute("List.orderBy", "property=zoneNumber");
		assertValueInList(0, 0, "10"); assertValueInList(0, 1, "10");
		assertValueInList(1, 0, "7"); assertValueInList(1, 1, "1");
		assertValueInList(2, 0, "7"); assertValueInList(2, 1, "2");
		assertValueInList(3, 0, "7"); assertValueInList(3, 1, "3");
		assertValueInList(4, 0, "7"); assertValueInList(4, 1, "4");
		assertValueInList(5, 0, "7"); assertValueInList(5, 1, "5");
		assertValueInList(6, 0, "7"); assertValueInList(6, 1, "6");
		assertValueInList(7, 0, "7"); assertValueInList(7, 1, "7");
		assertValueInList(8, 0, "7"); assertValueInList(8, 1, "8");		
		assertValueInList(9, 0, "7"); assertValueInList(9, 1, "9");

		execute("List.orderBy", "property=number");
		assertValueInList(0, 0, "7"); assertValueInList(0, 1, "1");
		assertValueInList(1, 0, "6"); assertValueInList(1, 1, "1");
		assertValueInList(2, 0, "5"); assertValueInList(2, 1, "1");
		assertValueInList(3, 0, "3"); assertValueInList(3, 1, "1");
		assertValueInList(4, 0, "2"); assertValueInList(4, 1, "1");
		assertValueInList(5, 0, "1"); assertValueInList(5, 1, "1");
		assertValueInList(6, 0, "7"); assertValueInList(6, 1, "2");
		assertValueInList(7, 0, "6"); assertValueInList(7, 1, "2");
		assertValueInList(8, 0, "5"); assertValueInList(8, 1, "2");		
		assertValueInList(9, 0, "4"); assertValueInList(9, 1, "2");
		
		execute("List.orderBy", "property=zoneNumber");
		assertValueInList(0, 0, "10"); assertValueInList(0, 1, "10");
		assertValueInList(1, 0, "7"); assertValueInList(1, 1, "1");
		assertValueInList(2, 0, "7"); assertValueInList(2, 1, "2");
		assertValueInList(3, 0, "7"); assertValueInList(3, 1, "3");
		assertValueInList(4, 0, "7"); assertValueInList(4, 1, "4");
		assertValueInList(5, 0, "7"); assertValueInList(5, 1, "5");
		assertValueInList(6, 0, "7"); assertValueInList(6, 1, "6");
		assertValueInList(7, 0, "7"); assertValueInList(7, 1, "7");
		assertValueInList(8, 0, "7"); assertValueInList(8, 1, "8");		
		assertValueInList(9, 0, "7"); assertValueInList(9, 1, "9");		
	}
	
	public void testChangePageRowCount() throws Exception {
		assertChangeRowCount(10, 5);		
		tearDown(); setUp();
		assertChangeRowCount(5, 10);
	}

	private void assertChangeRowCount(int initialRowCount, int finalRowCount) throws Exception, InterruptedException {
		HtmlSelect combo = (HtmlSelect) getHtmlPage().getElementById(decorateId("list_rowCount"));
		assertListRowCount(initialRowCount);
		String comboRowCount = combo.getSelectedOptions().get(0).getAttribute("value");
		assertEquals(String.valueOf(initialRowCount), comboRowCount);
		combo.setSelectedAttribute(String.valueOf(finalRowCount), true);
		Thread.sleep(3000);
		assertListRowCount(finalRowCount);
		comboRowCount = combo.getSelectedOptions().get(0).getAttribute("value");
		assertEquals(String.valueOf(finalRowCount), comboRowCount);
	}
	
	public void testNewNotChangedToDetailFromSplit() throws Exception { 
		execute("Mode.split");
		execute("CRUD.new");
		assertAction("Mode.detailAndFirst");
		assertAction("Mode.list");
		assertNoAction("Mode.split");
		assertAction("List.filter"); // List is shown
		assertExists("zoneNumber"); // Detail is shown
	}
	
	public void testSplitMode() throws Exception {
		assertAction("Mode.detailAndFirst");
		assertNoAction("Mode.list");
		assertAction("Mode.split");		
		assertAction("List.filter"); // List is shown
		assertNotExists("zoneNumber"); // Detail is not shown

		execute("Mode.split");
		assertAction("Mode.detailAndFirst");
		assertAction("Mode.list");
		assertNoAction("Mode.split");
		assertAction("List.filter"); // List is shown
		assertExists("zoneNumber"); // Detail is shown
		
		super.testSplitMode(); 
		
		execute("Mode.detailAndFirst");
		assertNoAction("Mode.detailAndFirst");
		assertAction("Mode.list");
		assertAction("Mode.split");
		assertNoAction("List.filter"); // List is not shown
		assertExists("zoneNumber"); // Detail is shown		
	}
	
	public void testDefaultAction() throws Exception {
		assertListRowCount(10);
		setConditionValues(new String [] { "1" });
		executeDefaultAction();
		assertListRowCount(3);
		
		execute("CRUD.new");
		executeDefaultAction();
		assertError("Value for Name in Warehouse is required"); // It tried to execute "CRUD.save", the default action
	}
	
	public void testChooseUnselectedRow() throws Exception { 
		checkRow(0);
		String warehouseName=getValueInList(1, "name");	
		assertTrue("Warehouse of row 1 must have name", !Is.empty(warehouseName));
		execute("List.viewDetail", "row=1");		
		assertNoErrors();
		assertValue("name", warehouseName);
	}
	
	public void testPage7InList() throws Exception {
		execute("List.goPage", "page=6");
		execute("List.goPage", "page=7");
		assertListRowCount(3);
		execute("CRUD.new");
		execute("Mode.list");
		assertListRowCount(3);
	}
	
	public void testChangePageRowCountInTab_listTitle() throws Exception {
		assertListTitle("Warehouse report"); 
		assertListRowCount(10);
		execute("Warehouse.changePageRowCount");
		assertListRowCount(20);
		assertChangeRowCount(20, 10); 
	}
	
	/**
	 * Needs the project AccessTracking deployed in the application server. <p>
	 * 
	 * In addition of AccessTracking and CRUD also it test:
	 * <ul>
	 * <li> Aspects to defining calculators.
	 * <li> postload-calculator
	 * <li> preremove-calculator
	 * </ul>
	 * @throws Exception
	 */	
	public void testAccessTracking_createReadUpdateDelete() throws Exception { 
		XHibernate.getSession().createQuery("delete from Access").executeUpdate();		
		XHibernate.commit();
		
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
		
		// Create
		execute("CRUD.new");
		assertNoAction("Warehouse.toLowerCase");
		assertAction("Warehouse.changeZone");
		
		setValue("zoneNumber", "66");
		setValue("number", "666");
		setValue("name", "WAREHOUSE JUNIT");
		execute("CRUD.save");
		assertNoErrors();
		
		// Verifying form is clean
		assertValue("zoneNumber", "");
		assertValue("number", "");		
		assertValue("name", "");
		// Search
		setValue("zoneNumber", "66");
		setValue("number", "666");
		execute("CRUD.refresh");
		assertValue("zoneNumber", "66");
		assertValue("number", "666");		
		assertValue("name", "WAREHOUSE JUNIT");
		// Modify
		setValue("name", "WAREHOUSE JUNIT MODIFIED");
		execute("CRUD.save");
		// Verifying form is clean
		assertValue("zoneNumber", "");
		assertValue("number", "");		
		assertValue("name", "");
		// Verifying modified
		setValue("zoneNumber", "66");
		setValue("number", "666");
		execute("CRUD.refresh");
		assertValue("zoneNumber", "66");
		assertValue("number", "666");		
		assertValue("name", "WAREHOUSE JUNIT MODIFIED");
				
		// Delete
		execute("CRUD.delete");
		assertNoAction("Warehouse.toLowerCase");
		assertAction("Warehouse.changeZone");		
		assertMessage("Warehouse deleted successfully");
		
		// Verifying is deleted
		execute("CRUD.new");
		setValue("zoneNumber", "66");
		setValue("number", "666");				
		execute("CRUD.refresh");		
		assertError("Object of type Warehouse does not exists with key Warehouse number:666, Zone:66");
		assertErrorsCount(1);
		
		// Date, time and table
		
		DateFormat timeFormat = new SimpleDateFormat("HH:mm");
		String time = timeFormat.format(new Date());
		
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		String date = dateFormat.format(new Date());
		
		String table = MetaModel.get("Warehouse").getMapping().getTable();
				
		// Verifying the entries in access tracking		
		changeModule("AccessTracking", "Accesses"); 
		assertListRowCount(6);
		
		String firstRecordId = getValueInList(0, "recordId");
		String expectedRecordId= firstRecordId.startsWith("{zoneNumber")?
				"{zoneNumber=66, number=666}":"{number=666, zoneNumber=66}";
		
		assertValueInList(0, "application", "test");
		assertValueInList(0, "model", "Warehouse");
		assertValueInList(0, "table", table);
		assertTrue("User must to have value", !Is.emptyString(getValueInList(0, "user"))); // Usually 'nobody' or 'UNAUTHENTICATED'
		assertValueInList(0, "date", date);
		assertValueInList(0, "time", time);  
		assertValueInList(0, "type", "Create");
		assertValueInList(0, "authorized", "Yes");
		assertValueInList(0, "recordId", expectedRecordId);		
		
		assertValueInList(1, "application", "test");
		assertValueInList(1, "model", "Warehouse");
		assertValueInList(1, "table", table);
		assertTrue("User must to have value", !Is.emptyString(getValueInList(1, "user"))); // Usually 'nobody' or 'UNAUTHENTICATED'
		assertValueInList(1, "date", date);
		assertValueInList(1, "time", time);
		assertValueInList(1, "type", "Read");
		assertValueInList(1, "authorized", "Yes");
		assertValueInList(1, "recordId", expectedRecordId);		
				
		assertValueInList(2, "application", "test");
		assertValueInList(2, "model", "Warehouse");
		assertValueInList(2, "table", table);
		assertTrue("User must to have value", !Is.emptyString(getValueInList(2, "user"))); // Usually 'nobody' or 'UNAUTHENTICATED'
		assertValueInList(2, "date", date);
		assertValueInList(2, "time", time);
		assertValueInList(2, "type", "Update");
		assertValueInList(2, "authorized", "Yes");
		assertValueInList(2, "recordId", expectedRecordId);		
		
		assertValueInList(3, "application", "test");
		assertValueInList(3, "model", "Warehouse");
		assertValueInList(3, "table", table);
		assertTrue("User must to have value", !Is.emptyString(getValueInList(3, "user"))); // Usually 'nobody' or 'UNAUTHENTICATED'
		assertValueInList(3, "date", date);
		assertValueInList(3, "time", time);
		assertValueInList(3, "type", "Read");
		assertValueInList(3, "authorized", "Yes");
		assertValueInList(3, "recordId", expectedRecordId);		
		
		assertValueInList(4, "application", "test");
		assertValueInList(4, "model", "Warehouse");
		assertValueInList(4, "table", table);
		assertTrue("User must to have value", !Is.emptyString(getValueInList(4, "user"))); // Usually 'nobody' or 'UNAUTHENTICATED'
		assertValueInList(4, "date", date);
		assertValueInList(4, "time", time);
		assertValueInList(4, "type", "Delete");
		assertValueInList(4, "authorized", "Yes");
		assertValueInList(4, "recordId", expectedRecordId);		
		
		assertValueInList(5, "application", "test");
		assertValueInList(5, "model", "Warehouse");
		assertValueInList(5, "table", table);
		assertTrue("User must to have value", !Is.emptyString(getValueInList(5, "user"))); // Usually 'nobody' or 'UNAUTHENTICATED'
		assertValueInList(5, "date", date);
		assertValueInList(5, "time", time);
		assertValueInList(5, "type", "Read");
		assertValueInList(5, "authorized", "Yes");
		
		assertTrue("The key of displayed data must be not empty", !Is.emptyString(getValueInList(5, "recordId")));
		assertTrue("The key of displayed data must be different", !getValueInList(5, "recordId").equals("{zoneNumber=66, number=666}"));
	}
		
	public void testNavigateInListWithALotOfObjects() throws Exception { 
		assertListRowCount(10);
		execute("List.goPage", "page=6");
		assertListRowCount(10);
		execute("List.goNextPage");
		assertListRowCount(3); // It assumes 63 objects
	}
				
	public void testNotLoseFilterOnChangeMode() throws Exception {
		assertListRowCount(10);
		setConditionValues(new String [] {"1"} );
		execute("List.filter");
		assertListRowCount(3);
		execute("Mode.detailAndFirst");
		execute("Mode.list");
		assertListRowCount(3);
	}
	
	public void testFilterFromNoFirstPage() throws Exception { 
		execute("List.goPage", "page=2");
		String [] condition = {
				"", "2"
		};
		setConditionValues(condition);
		execute("List.filter");
		assertListRowCount(5); 
	}
	
	public void testRememberListPage() throws Exception { 
		assertListRowCount(10);
		assertNoAction("List.goPreviousPage");
		execute("List.goPage", "page=2");
		assertListRowCount(10);
		assertAction("List.goPreviousPage");
		execute("Mode.detailAndFirst");
		execute("Mode.list");
		assertListRowCount(10);
		assertAction("List.goPreviousPage");
	}
	
	public void testCheckUncheckRows() throws Exception { 
		checkRow(1);
		execute("List.goNextPage");
		assertNoErrors();
		checkRow(12);
		execute("List.goPreviousPage");
		assertNoErrors();
		assertRowChecked(1);
		uncheckRow(1);
		assertRowUnchecked(1);
		execute("List.goNextPage");
		assertNoErrors();
		assertRowChecked(12);
		execute("List.goPreviousPage");
		assertNoErrors();
		assertRowUnchecked(1);		
	}
	
	public void testSaveExisting() throws Exception {
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
		
		// Create
		execute("CRUD.new");		
		assertNoAction("Warehouse.toLowerCase");
		assertAction("Warehouse.changeZone");
		setValue("zoneNumber", "66");
		setValue("number", "666");
		setValue("name", "WAREHOUSE JUNIT");		
		execute("CRUD.save");		
		// Verifying form is clean
		assertValue("zoneNumber", "");
		assertValue("number", "");		
		assertValue("name", "");
		// Try to re-create
		execute("CRUD.new");		
		setValue("zoneNumber", "66");
		setValue("number", "666");
		setValue("name", "WAREHOUSE JUNIT");
		execute("CRUD.save");		
		
		assertError("Impossible to create: an object with that key already exists");
		
		// Delete
		setValue("zoneNumber", "66");
		setValue("number", "666");
		execute("CRUD.refresh");		
		execute("CRUD.delete");		
		assertNoAction("Warehouse.toLowerCase");
		assertAction("Warehouse.changeZone");

		// Verifying is deleted
		execute("CRUD.new");		
		setValue("zoneNumber", "66");
		setValue("number", "666");				
		execute("CRUD.refresh");				
		assertError("Object of type Warehouse does not exists with key Warehouse number:666, Zone:66");		
	}
				
	public void testClickOneInListMode() throws Exception {
		// In list mode on start
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");

		String zoneNumber = getValueInList(3, "zoneNumber");
		String number = getValueInList(3, "number");
		String name = getValueInList(3, "name");
		execute("List.viewDetail", "row=3");
		assertNoAction("Warehouse.toLowerCase");
		assertAction("Warehouse.changeZone");
		assertValue("zoneNumber", zoneNumber);
		assertValue("number", number);
		assertValue("name", name);
	}
	
	public void testListNavigation_ChooseVarious_NavigateInChoosed() throws Exception {		 
		// In list mode on start
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
		String zoneNumber1 = getValueInList(0, "zoneNumber");
		String number1 = getValueInList(0, "number");
		String name1 = getValueInList(0, "name");
		checkRow(0);
		execute("List.goNextPage");
		String zoneNumber2 = getValueInList(0, "zoneNumber");
		String number2 = getValueInList(0, "number");
		String name2 = getValueInList(0, "name");
		checkRow(10);
		execute("List.goNextPage");
		String zoneNumber3 = getValueInList(1, "zoneNumber");
		String number3 = getValueInList(1, "number");
		String name3 = getValueInList(1, "name");
		checkRow(21);
		String zoneNumber4 = getValueInList(3, "zoneNumber");
		String number4 = getValueInList(3, "number");
		String name4 = getValueInList(3, "name");
		checkRow(23);
		execute("Mode.detailAndFirst");
		assertValue("zoneNumber", zoneNumber1);
		assertValue("number", number1);
		assertValue("name", name1);
		execute("Navigation.next");
		assertValue("zoneNumber", zoneNumber2);
		assertValue("number", number2);
		assertValue("name", name2);
		execute("Navigation.next");
		assertValue("zoneNumber", zoneNumber3);
		assertValue("number", number3);
		assertValue("name", name3);
		execute("Navigation.next");
		assertValue("zoneNumber", zoneNumber4);
		assertValue("number", number4);
		assertValue("name", name4);			
		execute("Navigation.next");
		assertError("No more elements in list");
		execute("Navigation.previous"); // In 3
		execute("Navigation.previous"); // In 2
		execute("Navigation.previous"); // In 1
		assertValue("zoneNumber", zoneNumber1);
		assertValue("number", number1);
		assertValue("name", name1);
		execute("Navigation.previous");
		assertError("We already are at the beginning of the list");
		execute("Navigation.next");
		assertValue("zoneNumber", zoneNumber2);
		assertValue("number", number2);
		assertValue("name", name2);
		execute("Navigation.first");
		assertValue("zoneNumber", zoneNumber1);
		assertValue("number", number1);
		assertValue("name", name1);							
	}
	
	public void testRememberSelected() throws Exception { 
		// In list mode on start
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
		checkRow(0);
		execute("List.goNextPage");
		checkRow(10);
		checkRow(12);
		execute("List.goPreviousPage");
		assertRowChecked(0);
		execute("List.goNextPage");
		assertRowsChecked(10, 12);
	}
	
	public void testDefaulActionInListNotReturnToDetail() throws Exception {  
		// In list mode on start
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
		executeDefaultAction(); // Execute search and not new 
		assertNoErrors(); 
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
	}
	
	public void testValidation() throws Exception { 
		assertAction("Warehouse.toLowerCase");
		assertNoAction("Warehouse.changeZone");
		
		// Create
		execute("CRUD.new");
		assertNoAction("Warehouse.toLowerCase");
		assertAction("Warehouse.changeZone");
		setValue("zoneNumber", "66");
		setValue("number", "666");
		setValue("name", ""); // and the name is required
		execute("CRUD.save");
		
		assertError("Value for Name in Warehouse is required");		
	}	
		
}
