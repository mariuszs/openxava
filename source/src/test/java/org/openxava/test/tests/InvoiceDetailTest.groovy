package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * Testing InvoiceDetail as a module is somewhat unorthodox, 
 * but practical for testing some case. 
 * 
 * @author Javier Paniza
 */

class InvoiceDetailTest extends ModuleTestBase {
	
	InvoiceDetailTest(String testName) {
		super(testName, "InvoiceDetail") 		
	}
	
	void testImagesGalleryInsideAReference() {
		execute "CRUD.new"
		execute "Gallery.edit", "galleryProperty=photos,viewObject=xava_view_product"
		assertNoErrors()
		assertMessage "No images"
		assertAction "Return.return"
	}
	
}
