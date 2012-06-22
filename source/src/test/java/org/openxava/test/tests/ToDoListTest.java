package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class ToDoListTest extends ModuleTestBase {
	
	public ToDoListTest(String testName) {
		super(testName, "ToDoList");		
	}
	
	// Dependent collection == collection with cascade ALL or REMOVE 
	public void testReferenceToADependentCollectionElement() throws Exception {
		// This only for JPA. In XML components it's not possible 
		// reference to an aggregate from outside
		execute("Mode.detailAndFirst");
		assertValue("name", "THE TO DO LIST");
		assertCollectionRowCount("tasks", 2);
		assertValueInCollection("tasks", 0, 0, "TASK 1");
		execute("Collection.edit", "row=0,viewObject=xava_view_tasks");
		assertCollectionRowCount("componentsTasks", 1);
		assertValueInCollection("componentsTasks", 0, 0, "COMPONENT 1");		
	}
	
	public void testSelectAndDeselectAllCollectionElements() throws Exception {
		execute("List.viewDetail", "row=0");
		
		assertAllCollectionUnchecked("tasks");
		checkAllCollection("tasks");
		assertAllCollectionUnchecked("components");
		assertAllCollectionChecked("tasks");
		assertRowCollectionChecked("tasks", 0);
		assertRowCollectionChecked("tasks", 1);
		assertAllCollectionUnchecked("components");
		assertRowCollectionUnchecked("components", 0);
		
		uncheckRowCollection("tasks", 0);
		assertAllCollectionUnchecked("tasks");
		checkRowCollection("tasks", 0);
		assertAllCollectionChecked("tasks");
		
		setConditionValues("tasks", new String[] { "f" });
		execute("List.filter", "collection=tasks");
		assertCollectionRowCount("tasks", 0);
		assertAllCollectionUnchecked("tasks");
	}
	
}
