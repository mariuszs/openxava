package org.openxava.test.tests

import javax.persistence.*

import org.openxava.tests.*
import org.openxava.util.*

import com.gargoylesoftware.htmlunit.html.*

import static org.openxava.jpa.XPersistence.*


/**
 * @author Javier Paniza
 */

class OrderIssueTest extends ModuleTestBase {
	
	OrderIssueTest(String testName) {
		super(testName, "OrderIssue");		
	}
	
	void testLastSearchKeyWithReadOnlyShowsReferenceActions() {
		execute "CRUD.new"		
		execute "Reference.createNew", "model=Order,keyProperty=order.number"
		assertDialog()
	}
	
	void testSearchKeyReferenceMustBeEditable() { 
		execute "Mode.detailAndFirst"
		assertAction "Reference.search"
		assertEditable "order.year"
		assertEditable "order.number"				
		assertNoEditable "order.date"
	} 
	
}
