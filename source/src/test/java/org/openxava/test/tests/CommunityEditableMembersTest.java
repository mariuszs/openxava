package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class CommunityEditableMembersTest extends ModuleTestBase {
	
	public CommunityEditableMembersTest(String testName) {
		super(testName, "CommunityEditableMembers");		
	}
		
	public void testManyToManyEditElement() throws Exception {
		execute("Mode.detailAndFirst");
		assertValue("name", "PROGRAMMERS");
		assertCollectionRowCount("members", 3);
		execute("ManyToMany.edit", "row=0,viewObject=xava_view_members");
		assertValue("name", "JAVI");
		assertValue("favouriteFramework", "OPENXAVA");
		setValue("name", "JAVI MODIFIED");
		execute("ManyToManyUpdateElement.save");
		assertCollectionRowCount("members", 3);
		assertValueInCollection("members", 0, "name", "JAVI MODIFIED");

		// Restoring
		execute("ManyToMany.edit", "row=0,viewObject=xava_view_members");
		assertValue("name", "JAVI MODIFIED");
		setValue("name", "JAVI");
		execute("ManyToManyUpdateElement.save");
		assertCollectionRowCount("members", 3);
		assertValueInCollection("members", 0, "name", "JAVI");
	}
	
}
