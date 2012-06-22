package org.openxava.test.tests;

import org.openxava.jpa.*;
import org.openxava.model.meta.*;
import org.openxava.test.model.*;


import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class TransportChargeWithDescriptionsListTest extends ModuleTestBase {
	
	TransportChargeWithDescriptionsListTest(String testName) {
		super(testName, "TransportChargeWithDescriptionsList")		
	}
		
	void testNestedCompositeKeysInDescriptionsList()  {
		deleteAllTransportCharges();		
		assertListRowCount 0 
		execute "CRUD.new"
		Delivery delivery = new Delivery([ 
			invoice: [ year: 2004, number: 2], 
			type: [ number: 2],
			number: 777 ])
		String key = MetaModel.getForPOJO(delivery).toString(delivery);		
		setValue "delivery.KEY", key 
		setValue "amount", "324.28" 
		execute "CRUD.save"
		assertNoErrors()
		execute "Mode.list"
		assertListRowCount 1
		execute "Mode.detailAndFirst"
		assertValue "delivery.KEY", key
		assertValue "amount", "324.28"
		execute "CRUD.delete"
		assertNoErrors()
	}	
	
	private void deleteAllTransportCharges() {
		checkAll()
		execute "CRUD.deleteSelected"
	}
	
}
