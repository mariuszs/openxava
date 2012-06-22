package org.openxava.test.tests

import org.openxava.jpa.*;
import org.openxava.tests.ModuleTestBase 

/**
 * 
 * @author Javier Paniza
 */
class AsEmbedContainerTest extends ModuleTestBase {
	
	AsEmbedContainerTest(String testName) {
		super(testName, "AsEmbedContainer")		
	}

	void test3LevelAsEmbeddedEntities() {
		deleteData()
		execute "CRUD.new"
		setValue "value", "CONTAINER"
		execute "Collection.new", "viewObject=xava_view_children"
		setValue "value1", "VALUE 1"
		setValue "asEmbed2.value2", "VALUE 2"
		setValue "asEmbed2.asEmbed3.value3", "VALUE 3"
		execute "Collection.save"
		assertNoErrors()
		assertCollectionRowCount "children", 1
		execute "Collection.edit", "row=0,viewObject=xava_view_children"
		assertValue "value1", "VALUE 1"
		assertValue "asEmbed2.value2", "VALUE 2"
		assertValue "asEmbed2.asEmbed3.value3", "VALUE 3"

		changeModule "AsEmbed2"
		assertListRowCount 1
		
		changeModule "AsEmbed3"
		assertListRowCount 1
	}
	
	void deleteData() {
		XPersistence.getManager().with {			
			createQuery("delete from AsEmbed1").executeUpdate()
			createQuery("delete from AsEmbed2").executeUpdate()
			createQuery("delete from AsEmbed3").executeUpdate()
			createQuery("delete from AsEmbedContainer").executeUpdate()
		}
	}

}
