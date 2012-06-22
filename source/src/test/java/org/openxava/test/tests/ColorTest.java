package org.openxava.test.tests;

import java.io.*;

import javax.persistence.*;

import org.apache.commons.logging.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

import com.lowagie.text.*;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.*;



/**
 * @author Javier Paniza
 */

public class ColorTest extends ModuleTestBase {
	private static Log log = LogFactory.getLog(ColorTest.class);
	
	public ColorTest(String testName) {
		super(testName, "Color");		
	}
	
	public void testKeysWithZeroValue() throws Exception {
		assertValueInList(0, "number", "0");
		assertValueInList(0, "name", "ROJO");
		execute("Mode.detailAndFirst");
		assertNoErrors();
		assertValue("number", "0");
		assertValue("name", "ROJO");
		assertValue("sample", "RED");
	}		
	
	public void testMessageScapedWithQuotes() throws Exception{
		assertListNotEmpty();
		execute("List.viewDetail", "row=0");
		execute("Color.seeMessage");
		assertMessage("Message: A.B.C");
	}

	public void testIdentityCalculator() throws Exception {
		execute("CRUD.new");
		assertNoErrors(); 
		setValue("number", "-1"); // needed in this case because 0 is an existing key
		setValue("name", "JUNIT COLOR " + (int) (Math.random() * 200));
		execute("TypicalNotResetOnSave.save");
		assertNoErrors();						
		String last = getValue("number");
		
		execute("CRUD.new");
		assertNoErrors(); 
		setValue("number", "-1"); // needed in this case because 0 is an existing key
		setValue("name", "JUNIT COLOR " + (int) (Math.random() * 200));
		execute("TypicalNotResetOnSave.save");
		assertNoErrors();		
		String next = String.valueOf(Integer.parseInt(last) + 1);
		assertValue("number", next);		
	}
	
	public void testOptimisticConcurrency() throws Exception {
		// Must be called 2 times in order to fix some problems on second time
		modifyColorFromFirstUser(1);
		modifyColorFromFirstUser(2);
	}

	public void testFilterByNumberZero() throws Exception {
		setConditionValues(new String[] { "0" });
		execute("List.filter");
		assertListRowCount(1);
	}
	
	public void testFilterDescriptionsList_keyReferenceWithSameNameThatPropertyFather() throws Exception{
		changeModule("Color2");
		assertLabelInList(4, "Name of Used to");
		assertValueInList(0, 4, "CAR");
		setConditionValues(new String[] { "", "", "", "1"} );
		execute("List.filter");
		assertListNotEmpty();
	}
	
	public void modifyColorFromFirstUser(int id) throws Exception {		
		// First user
		execute("List.viewDetail", "row=2");		
		assertNotExists("version");
		setValue("name", "COLOR A" + id);
		
		// Second user, it's faster, he wins
		ColorTest otherSession = new ColorTest("Color2");
		otherSession.modifyColorFromSecondUser(id);
		
		// The first user continues		
		execute("TypicalNotResetOnSave.save");
		assertError("Impossible to execute Save action: Another user has modified this record");
		execute("Mode.list");		
		assertValueInList(2, "name", "COLOR B" + id); // The second user won
	}
	
	private void modifyColorFromSecondUser(int id) throws Exception {
		setUp();
		execute("List.viewDetail", "row=2");
		setValue("name", "COLOR B" + id);
		execute("TypicalNotResetOnSave.save");
		assertNoErrors();		
		tearDown();
	}	
	
	public void testFilterDescriptionsList_forTabsAndNotForTabs() throws Exception{
		try{
			CharacteristicThing.findByNumber(2);	
		}
		catch(NoResultException ex){
			fail("It must to exist");
		}
		
		// Color: 'usedTo' without descriptionsList and 'characteristicThing' without descriptionsList
		assertLabelInList(4, "Name of Used to");
		assertLabelInList(5, "Description of Characteristic thing");
		assertValueInList(0, 4, "CAR");
		assertValueInList(0, 5, "3 PLACES");
		setConditionValues(new String[] { "", "", "", "CAR", "3 PLACES" } );
		execute("List.filter");
		assertNoErrors();
		assertListRowCount(1);
		
		// Color2: 'usedTo' with descriptionsList and 'characteristicThing' with descriptionsList and condition
		changeModule("Color2");
		assertLabelInList(4, "Name of Used to");
		assertLabelInList(5, "Description of Characteristic thing");
		assertValueInList(0, 4, "CAR");
		assertValueInList(0, 5, "3 PLACES");
		setConditionValues(new String[] { "", "", "", "1", "0" } );
		execute("List.filter");
		assertNoErrors();
		assertListRowCount(1);

		try{
			setConditionValues(new String[] { "", "", "", "", "2"} );	// descriptionsList has a condition: number < 2
		}
		catch(IllegalArgumentException ex){
			assertTrue(ex.getMessage().equals("No option found with value: 2"));
		}
	}
	
	public void testShowActionOnlyInEachRow() throws Exception{
		// confirmMessage with row
		String html = getHtml();
		assertTrue(html.contains("Delete record on row 2: Are you sure?"));
		
		// action with mode=NONE: it display only in each row
		assertAction("CRUD.deleteRow");
		setConditionValues(new String[] { "", "ZZZZZ"});
		execute("List.filter");
		assertListRowCount(0);
		assertNoAction("CRUD.deleteRow");
	}
	
	public void testIgnoreAccentsForStringArgumentsInTheFilter() throws Exception{ 
		// create record with name 'marrón'
		execute("CRUD.new");
		setValue("name", "marrón");
		execute("TypicalNotResetOnSave.save");
		assertNoErrors();
		
		// filter by 'marron'
		execute("Mode.list");
		setConditionValues("", "marron");
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, 1, "MARRÓN");
		
		// delete
		checkAll();
		execute("CRUD.deleteSelected");
		assertNoErrors();
		assertListRowCount(0);
	}
	
	public void testChangeModelNameByTableNameInConditions() throws Exception{
		execute("CRUD.new");
		assertNoErrors();
		assertExists("anotherCT.number");
		assertValidValuesCount("anotherCT.number", 3);
		String [][] validValues = { 
			{ "", "" },
			{ "0", "3 PLACES" },
			{ "1", "5 PLACES" }
		};
		assertValidValues("anotherCT.number", validValues);
	}
	
	public void testDescriptionsListWithMultipleKeyAndOneValueInBlank() throws Exception{
		execute("List.viewDetail", "row=0");
		assertExists("mixture.KEY");
		String [][] validValues = { 
			{ "", "" },
			{ "[.          .VERDE     .]", "----------&-----VERDE:complicated" },
			{ "[.ROJO      .          .]", "------ROJO&----------:simple" }
		};
		assertValidValues("mixture.KEY", validValues);
		
		setValue("mixture.KEY", "[.          .VERDE     .]");
		execute("TypicalNotResetOnSave.save");
		assertNoErrors();
		assertMessage("Color modified successfully");
		assertValue("mixture.KEY", "[.          .VERDE     .]");
		
		setValue("mixture.KEY", "");
		execute("TypicalNotResetOnSave.save");
		assertNoErrors();
		assertMessage("Color modified successfully");
		assertValue("mixture.KEY", "");
	}
	
	public void testFilterNotContains() throws Exception{
		assertLabelInList(1, "Name");
		assertLabelInList(5, "Description of Characteristic thing");
		setConditionValues("", "", "", "", "3 places");
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, 1, "ROJO");
		
		setConditionComparators("=", "not_contains_comparator", "starts_comparator", "starts_comparator", "contains_comparator");
		setConditionValues("", "ROJO", "", "", "");
		execute("List.filter");
		assertListNotEmpty();
		
		setConditionComparators("=", "not_contains_comparator", "starts_comparator", "starts_comparator", "contains_comparator");
		setConditionValues("", "ROJO", "", "", "3 places");
		execute("List.filter");
		assertListRowCount(0);
	}
	
}