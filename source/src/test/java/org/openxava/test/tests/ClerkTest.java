package org.openxava.test.tests;

import java.text.*;
import java.util.*;

import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class ClerkTest extends ModuleTestBase {
	
	public ClerkTest(String testName) {
		super(testName, "Clerk");		
	}
	
	public void testTextFieldsWithQuotationMarks() throws Exception {
		assertListNotEmpty();
		execute("Mode.detailAndFirst");
		String name = getValue("name");		
		String quotedName = name + "\"EL BUENO\"";
		setValue("name", quotedName);				
		execute("CRUD.save");
		execute("Mode.list");
		execute("Mode.detailAndFirst");
		assertValue("name", quotedName);
				
		// Restoring
		setValue("name", name);
		execute("CRUD.save");
		assertNoErrors();
	}
	
	public void testTimeStereotypeAndSqlTimeAndStringAsByteArrayInDB() throws Exception {
		assertListNotEmpty();
		execute("Mode.detailAndFirst");
		String time = getCurrentTime();
		setValue("arrivalTime", time);
		setValue("endingTime", time);
		setValue("comments", "Created at " + time);
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertValueInList(0, "arrivalTime", time + ":00");
		assertValueInList(0, "endingTime", time);
		assertValueInList(0, "comments", "Created at " + time);
		
		setConditionValues(new String [] { "", "", "", "", time });
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, "arrivalTime", time + ":00");
		assertValueInList(0, "endingTime", time);
		
		// Asserting that java.sql.Time works in JasperReport
		execute("Print.generatePdf"); 		
		assertContentTypeForPopup("application/pdf");		
	}

	private String getCurrentTime() {
		return new SimpleDateFormat("HH:mm").format(new Date());
	}
}
