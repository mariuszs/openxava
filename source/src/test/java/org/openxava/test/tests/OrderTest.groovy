package org.openxava.test.tests

import javax.persistence.*

import org.openxava.tests.*
import org.openxava.util.*

import com.gargoylesoftware.htmlunit.html.*

import static org.openxava.jpa.XPersistence.*


/**
 * @author Javier Paniza
 */

class OrderTest extends ModuleTestBase {
	
	OrderTest(String testName) {
		super(testName, "Order");		
	}
	
	void testCalculatedPropertiesFromCollection_generatedValueOnPersistRefreshedInView() throws Exception {
		String nextNumber = getNextNumber()
		execute("CRUD.new")
		assertValue("number", "")
		setValue("customer.number", "1")
		assertValue("customer.name", "Javi")
		assertCollectionRowCount("details", 0)
		execute("Collection.new", "viewObject=xava_view_details")
		setValue("product.number", "1")
		assertValue("product.description", "MULTAS DE TRAFICO")
		assertValue("product.unitPrice", "11.00")
		setValue("quantity", "10")
		assertValue("amount", "110.00")
		execute("Collection.save")
		assertNoErrors()
		assertCollectionRowCount("details", 1)
		assertValue("amount", "110.00")
		assertValue("number", nextNumber)
		execute("CRUD.delete")
		assertNoErrors()		
	}
	
	void testDoubleClickOnlyInsertsACollectionElement() throws Exception {
		boolean doubleClick = false; 
		while (!doubleClick) { 
			execute("CRUD.new")
			setValue("customer.number", "1")
			assertCollectionRowCount("details", 0)
			execute("Collection.new", "viewObject=xava_view_details")
			setValue("product.number", "1")
			setValue("quantity", "10")
			HtmlElement action = getForm().getElementById(decorateId("Collection.save"))
					
			action.click() // Not dblClick(), it does not reproduce the problem
			try {
				getForm().getElementById(decorateId("Collection.save"))
			}
			catch (com.gargoylesoftware.htmlunit.ElementNotFoundException ex) {
				continue // Because sometimes the action is executed very fast and 
					// when the second click happens the dialog is already closed
				    // This case cannot occurs in real life (because with no dialog 
					// there is no button to click) but it can occurs in test 
					// (because we have a reference to the link) 
			}
			action.click()
			doubleClick = true
			Thread.sleep(4000)
					
			assertNoErrors()
			assertCollectionRowCount("details", 1)
			
			execute("CRUD.delete")
			assertNoErrors();
		}
	}
	
	private String getNextNumber() throws Exception {
		Query query = getManager().
			createQuery(
				"select max(o.number) from Order o where o.year = :year") 
		query.setParameter("year", Dates.getYear(new Date()))
		Integer lastNumber = (Integer) query.getSingleResult()
		if (lastNumber == null) lastNumber = 0
		return Integer.toString(lastNumber + 1)
	}

								
}
