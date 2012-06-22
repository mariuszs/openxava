package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class StudioTest extends ModuleTestBase {
	
	StudioTest(String testName) {
		super(testName, "Studio")		
	}
		
	void testEmbeddedCollectionElementNotShowParentReference_removingCollectionElementWhenParentNameNotMatchEntityName() {
		assertListNotEmpty()
		execute "Mode.detailAndFirst"
		assertCollectionRowCount "artists", 0
		execute "Collection.new", "viewObject=xava_view_artists"
		assertNotExists "artistStudio.name"
		setValue "name", "ALFREDO LANDA"
		execute "Collection.save"
		assertNoErrors()
		assertCollectionRowCount "artists", 1
		execute "Collection.edit", "row=0,viewObject=xava_view_artists"
		execute "Collection.remove"
		assertNoErrors()
		assertCollectionRowCount "artists", 0
	}
	
}
