package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase;
import org.openxava.util.Is;

/**
 * Create on 07/04/2008 (12:16:03)
 * @autor Ana Andrés
 */
public class CarrierWithSectionsTest extends ModuleTestBase {
	
	public CarrierWithSectionsTest(String testName) {
		super(testName, "CarrierWithSections");
	}
		
	public void testCarrierSelected() throws Exception{
		assertListNotEmpty();
		execute("List.viewDetail", "row=0");
		execute("CarrierWithSections.fellowCarriersSelected");
		assertTrue(Is.empty(getValue("fellowCarriersSelected")));
		execute("Sections.change", "activeSection=1");
		checkRowCollection("fellowCarriersCalculated", 0);
		checkRowCollection("fellowCarriersCalculated", 1);
		execute("Sections.change", "activeSection=0");
		execute("CarrierWithSections.fellowCarriersSelected");
		assertTrue(getValue("fellowCarriersSelected").equalsIgnoreCase("DOS TRES"));
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		execute("CarrierWithSections.fellowCarriersSelected");
		assertTrue(Is.empty(getValue("fellowCarriersSelected")));
	}
	
	public void testSetControllers() throws Exception {
		String [] defaultActions = {
			"List.hideRows",
			"List.filter",
			"List.customize",
			"List.orderBy",
			"List.viewDetail",
			"List.sumColumn",
			"Mode.detailAndFirst",			
			"Mode.split",
			"Print.generatePdf",
			"Print.generateExcel",
			"CRUD.new",
			"CRUD.deleteSelected",
			"CRUD.deleteRow", 
			"Carrier.translateAll",
			"Carrier.deleteAll",
			"CarrierWithSections.setTypicalController",
			"CarrierWithSections.setPrintController",
			"CarrierWithSections.setDefaultControllers",
			"CarrierWithSections.returnToPreviousControllers"
		};
		String [] printActions = {
			"List.hideRows",
			"List.filter",
			"List.customize",
			"List.orderBy",
			"List.viewDetail",
			"List.sumColumn",
			"Mode.detailAndFirst",							
			"Mode.split",
			"Print.generatePdf",
			"Print.generateExcel",
			"CarrierWithSections.setTypicalController",
			"CarrierWithSections.setPrintController",
			"CarrierWithSections.setDefaultControllers",
			"CarrierWithSections.returnToPreviousControllers"			
		};		
		String [] typicalActions = {
			"List.hideRows",
			"List.filter",
			"List.customize",
			"List.orderBy",
			"List.viewDetail",
			"List.sumColumn", 
			"Mode.detailAndFirst",							
			"Mode.split",
			"Print.generatePdf",
			"Print.generateExcel",
			"CRUD.new",
			"CRUD.deleteSelected",
			"CRUD.deleteRow", 
			"CarrierWithSections.setTypicalController",
			"CarrierWithSections.setPrintController",
			"CarrierWithSections.setDefaultControllers",
			"CarrierWithSections.returnToPreviousControllers"			
		};		
		
		// Returning with returnToPreviousController
		assertActions(defaultActions);
		execute("CarrierWithSections.setTypicalController");
		assertActions(typicalActions);
		execute("CarrierWithSections.setPrintController");
		assertActions(printActions);
		execute("CarrierWithSections.returnToPreviousControllers");
		assertActions(typicalActions);
		execute("CarrierWithSections.returnToPreviousControllers");
		assertActions(defaultActions);
		
		// Returning with setDefaultControllers()
		assertActions(defaultActions);
		execute("CarrierWithSections.setTypicalController");
		assertActions(typicalActions);
		execute("CarrierWithSections.setPrintController");
		assertActions(printActions);
		execute("CarrierWithSections.setDefaultControllers");
		assertActions(defaultActions);
		execute("CarrierWithSections.returnToPreviousControllers");
		assertActions(defaultActions); // Verifies that setDefaultControllers empties the stacks
		
	}
	
}
