package org.openxava.test.tests;

import java.util.Iterator;

import org.openxava.jpa.XPersistence;
import org.openxava.test.model.TreeContainer;
import org.openxava.test.model.TreeItem;
import org.openxava.test.model.TreeItemTwo;
import org.openxava.tests.ModuleTestBase;
import org.openxava.util.XavaResources;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;

/**
 * 
 * @author Federico Alcantara
 */

public abstract class TreeViewTestBase extends ModuleTestBase {
	
	public TreeViewTestBase(String testName, String application, String module) {
		super(testName, application, module);
	}
	
	public TreeViewTestBase(String testName, String module) {
		super(testName, module);		
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		populateTree();
		populateTreeTwo();
	}
	
	@Override
	protected void tearDown() throws Exception {
		populateTree();
		populateTreeTwo();
		super.tearDown();
	}
	
	
	protected String getValueInTreeView(String collection, int row) {
		DomElement element = getTreeViewElementInRow(collection, row);
		String value = element.getElementsByTagName("span").item(0)
				.getTextContent().toString();
		return value.replace((char) 160, (char) 32); 
	}
		
	protected int getTreeViewRowCount(String collection) throws Exception {
		HtmlDivision div = (HtmlDivision) getForm().getPage().getElementById("openxavaInput_" + collection);
		return div.getElementsByTagName("input").getLength();
	}
	
	
	protected int getTreeViewTabRow(String collection, int row) {
		HtmlElement element = getTreeViewXavaElement(collection, row);
		String tabRow = element.getAttribute("value");
		tabRow = tabRow.substring(tabRow.indexOf(":") + 1);
		return Integer.parseInt(tabRow);
	}
	
	protected void executeOnTreeViewItem(String collection, String action, int row) throws Exception {
		int tabRow = getTreeViewTabRow(collection, row);
		execute(action, "row=" + tabRow + ",viewObject=xava_view_" + collection);
	}
	
	protected void checkRowTreeView(String collection, int row) throws Exception {
		HtmlInput input = getTreeViewXavaElement(collection, row);
		input.setChecked(true);
	}
	
	/** 
	 * Find the tree view element in a treeview collection
	 * @param collection name of the collection represented as tree
	 * @param row row to be found
	 * @return DomElement containing the tree item details
	 * @throws ElementNotFoundException
	 */
	protected DomElement getTreeViewElementInRow(String collection, int row) throws ElementNotFoundException {
		HtmlDivision div = (HtmlDivision) getHtmlPage().getElementById("tree_" + collection);
		Iterator<HtmlElement> it = div.getHtmlElementDescendants().iterator(); 
		int count = 0;
		while (it.hasNext()) {
			HtmlElement element = it.next();
			if (element.getId().startsWith("ygtvcontentel")) {
				if (count++ == row) {
					return element;
				}
			}
		}
		throw new ElementNotFoundException("tree_" + collection, "row", Integer.toString(row));
	}

	/**
	 * Finds the mapping of the treeview item to the IXTableModel 
	 * @param collection name of the collection
	 * @param row row to be look up for
	 * @return the HtmlInput element containg the details of the mapping
	 * @throws ElementNotFoundException
	 */
	protected HtmlInput getTreeViewXavaElement(String collection, int row) throws ElementNotFoundException {
		HtmlDivision div = (HtmlDivision) getForm().getPage().getElementById("openxavaInput_" + collection);
		Iterator<HtmlElement> elements = div.getHtmlElementDescendants().iterator(); 		
		int count = 0;
		while (elements.hasNext()) {
			HtmlElement element = elements.next();
			if (element instanceof HtmlInput) {
				if (count++ == row ) {
					return (HtmlInput) element;
				}
			}
		}
		throw new ElementNotFoundException("tree_" + collection, "row", Integer.toString(row));
	}

	protected void assertTreeViewRowCount(String collection, int expectedCount) throws Exception {
		assertEquals(XavaResources.getString("collection_row_count", collection), expectedCount, getTreeViewRowCount(collection));
	}
	
	protected void assertValueInTreeView(String collection, int row, String value) throws Exception {
		String rowValue = getValueInTreeView(collection, row);
		assertEquals(XavaResources.getString("unexpected_value_in_collection", new Integer(0), new Integer(row), collection), value, rowValue);
	}
	protected void assertValueInTreeViewIgnoreCase(String collection, int row, String value) throws Exception {
		String rowValue = getValueInTreeView(collection, row);
		assertEquals(XavaResources.getString("unexpected_value_in_collection", new Integer(0), new Integer(row), collection), value.toUpperCase(), rowValue.toUpperCase());
	}

	@Override
	protected void resetModule() throws Exception {
		super.resetModule();
	}

	protected void populateTree() throws Exception {
		XPersistence.getManager().createQuery("delete from TreeItem").executeUpdate();
		XPersistence.commit();
		TreeContainer parent = XPersistence.getManager().find(TreeContainer.class, 1);		
		TreeItem root = createTreeItem(parent, null, "ROOT ITEM 1",    0);
		TreeItem child1 = createTreeItem(parent, root, "CHILD ITEM 1",   2);
		createTreeItem(parent, root, "CHILD ITEM 2",   4);
		TreeItem child3 = createTreeItem(parent, root, "CHILD ITEM 3",   6);		
		createTreeItem(parent, child1, "SUBITEM 1 OF 1", 2);
		createTreeItem(parent, child1, "SUBITEM 2 OF 1", 4);
		createTreeItem(parent, child3, "SUBITEM 1 OF 3", 6);		
		
		XPersistence.commit();
	}

	protected void populateTreeTwo() throws Exception {
		XPersistence.getManager().createQuery("delete from TreeItemTwo").executeUpdate();
		XPersistence.commit();
		TreeContainer parent = XPersistence.getManager().find(TreeContainer.class, 1);		
		TreeItemTwo root = createTreeItemTwo(parent, null, "ROOT ITEM 1",    0);
		TreeItemTwo child1 = createTreeItemTwo(parent, root, "CHILD ITEM 1",   2);
		createTreeItemTwo(parent, root, "CHILD ITEM 2",   4);
		TreeItemTwo child3 = createTreeItemTwo(parent, root, "CHILD ITEM 3",   6);		
		createTreeItemTwo(parent, child1, "SUBITEM 1 OF 1", 2);
		createTreeItemTwo(parent, child1, "SUBITEM 2 OF 1", 4);
		createTreeItemTwo(parent, child3, "SUBITEM 1 OF 3", 6);		
		
		XPersistence.commit();
	}

	private TreeItemTwo createTreeItemTwo(TreeContainer container, TreeItemTwo parentTree, String description, int treeOrder) throws Exception { 
		TreeItemTwo item = new TreeItemTwo();
		String path = "";
		if (parentTree != null) {
			path = parentTree.getFolder() + "/" + parentTree.getId();
		}
		item.setFolder(path);
		item.setDescription(description);
		item.setTreeOrder(treeOrder);
		item.setParentContainer(container);
		return XPersistence.getManager().merge(item);
	}		

	private TreeItem createTreeItem(TreeContainer container, TreeItem parentTree, String description, int treeOrder) throws Exception { 
		TreeItem item = new TreeItem();
		String path = "";
		if (parentTree != null) {
			path = parentTree.getPath() + "/" + parentTree.getId();
		}
		item.setPath(path);
		item.setDescription(description);
		item.setTreeOrder(treeOrder);
		item.setParentContainer(container);
		return XPersistence.getManager().merge(item);
	}		
}
