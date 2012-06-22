package org.openxava.test.tests;

import java.util.*;

import javax.persistence.*;

import org.openxava.test.model.*;
import org.openxava.tests.*;
import org.openxava.web.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * For testing that only the needed parts are reload by AJAX. <p>
 * 
 * Because this is very technology dependent we put all this test here
 * and not scattered for the module tests, in order to remove when 
 * we'll go with other presentation technology.<br> 
 * 
 * @author Javier Paniza
 */

public class AJAXTest extends ModuleTestBase {
	
	public AJAXTest(String nameTest) {
		super(nameTest, null);
	}
	
	public void testListReloadInSplitMode() throws Exception { 
		changeModule("DeliveryType");
		execute("Mode.split");
		assertLoadedParts("core");
		execute("CRUD.new");		
		assertLoadedParts("editor_comboDeliveries," + // Because it is set editable
				"editor_description," + // Because it is set editable
				"editor_number," + // Because it is set editable
				"list_view," + // Because CRUD.new is also a list action 
				"errors, messages");
		execute("CRUD.new");
		assertLoadedParts("errors, messages, list_view");
		execute("Navigation.next");
		assertLoadedParts("editor_description," + // list_view is not loaded: Good!
				// "editor_comboDeliveries," + // Only with XML components, because in XML is a view property, and in JPA is a transient property 
				"editor_number," +  
				"errors, messages");
		assertListRowCount(6);
		execute("CRUD.new");
		setValue("number", "66");
		setValue("description", "JUNIT DELIVERY TYPE");
		execute("DeliveryType.saveNotReset");
		assertListRowCount(7);
		assertLoadedParts("editor_description," +
				// "editor_comboDeliveries," + // Only with XML components 
				"list_view," + // Because DeliveryType.saveNotReset changes data that can be in the list
				"editor_number," +  
				"errors, messages");	
		execute("CRUD.delete");
		assertListRowCount(6);
		assertLoadedParts("editor_description," +
				"list_view," + // Because CRUD.delete changes data that can be in the list
				"editor_number," +  
				"errors, messages");		
	}
	
	public void testAlwaysReloadEditor() throws Exception { 
		changeModule("Warehouse");
		execute("CRUD.new");
		HtmlElement el = getForm().getElementById("ox_OpenXavaTest_Warehouse__editor_time");		
		String time = el.asText().trim();		
		execute("CRUD.new");
		String otherTime = el.asText().trim();		
		assertNotEquals("Time must be changed", time, otherTime);
		assertLoadedParts("editor_time, errors, messages");
	}
	
	
	public void testSetMemberEditable() throws Exception {
		changeModule("ChangeProductsPrice");
		execute("Mode.detailAndFirst");
		assertNoEditable("description");
		execute("ChangeProductsPrice.editDescription");
		assertEditable("description");
		assertLoadedParts("editor_description, " +
				"errors, messages,");
	}
	
	public void testCollectionsInsideReferences() throws Exception { 
		changeModule("Office");
		execute("CRUD.new");
		assertCollectionRowCount("defaultCarrier.fellowCarriers", 0);
		assertCollectionRowCount("defaultCarrier.fellowCarriersCalculated", 0);
		setValue("defaultCarrier.number", "1");
		assertCollectionRowCount("defaultCarrier.fellowCarriers", 3); 
		assertCollectionRowCount("defaultCarrier.fellowCarriersCalculated", 3);		
		assertLoadedParts("collection_defaultCarrier.fellowCarriers.," + 
				"collection_defaultCarrier.fellowCarriersCalculated.," +
				"frame_defaultCarrier.fellowCarriersCalculatedheader," + 
				"frame_defaultCarrier.fellowCarriersheader," + 				
				"reference_editor_defaultCarrier.drivingLicence," +
				"editor_defaultCarrier.calculated," +	 
				"editor_defaultCarrier.name," +
				"editor_defaultCarrier.warehouse.name," +
				"editor_defaultCarrier.warehouse.number," +
				"editor_defaultCarrier.warehouse.zoneNumber," +
				"editor_mainWarehouse.time," + // it's always-reload
				"editor_officeManager.arrivalTime," + // it's formatted
				"editor_officeManager.endingTime," + // it's formatted
				"errors, messages,");		
	}
	
	public void testRefreshViewProperties() throws Exception { 
		changeModule("Customer");
		execute("CRUD.new");
		assertValue("address.viewProperty", "");
		execute("Address.fillViewProperty", "xava.keyProperty=address.viewProperty"); 
		assertValue("address.viewProperty", "THIS IS AN ADDRESS");
		assertLoadedParts("errors, " +
				"editor_address.viewProperty," +
				"messages");		
	}
	
	public void testOnlyLoadModifiedParts() throws Exception {
		changeModule("Customer"); 
		assertLoadedParts(""); 
		execute("List.filter");
		assertLoadedParts("errors, view, messages");
		execute("Mode.detailAndFirst");
		assertLoadedParts("core, ");
		execute("Navigation.next");
		assertLoadedParts("editor_type, errors, " + 
				"editor_number, " +
				"editor_alternateSeller.number, " +
				"editor_address.asString, " + 
				"editor_address.city, " +
				"editor_address.zipCode, " +
				"editor_alternateSeller.name, " +
				"editor_name, " +
				"editor_address.street, " +
				"editor_city, " +
				"editor_website, " +
				"editor_relationWithSeller, " +
				"reference_editor_address.state, " +
				"editor_email, " +
				"editor_telephone, " +
				"collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " + 
				"editor_photo, messages, ");		
		setValue("seller.number", "2");				
		assertLoadedParts("errors, editor_seller.name, " +
				"messages,");
		setValue("seller.number", "1");		
		assertLoadedParts("errors, editor_seller.name, " +
			"messages,");
		execute("Customer.changeNameLabel");		
		assertLoadedParts("label_name, errors, messages, ");
		execute("CRUD.new");
		assertLoadedParts("errors, editor_seller.name, " +
				"editor_number, " +
				"editor_alternateSeller.number, " +
				"editor_address.asString, " + 
				"editor_address.city, " +
				"editor_address.zipCode, " +
				"editor_alternateSeller.name, " +
				"editor_name, " +
				"editor_address.street, " +
				"editor_city, " +
				"editor_relationWithSeller, " +
				"reference_editor_address.state, " +
				"editor_seller.number, " +
				"collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " + 
				"messages,");
		setValue("number", "4");
		execute("CRUD.refresh");
		assertLoadedParts("errors, editor_number, " +
				"editor_address.asString, " + 
				"editor_address.city, " +
				"editor_address.zipCode, " +
				"editor_name, " +
				"editor_address.street, " +
				"editor_city, " +
				"editor_relationWithSeller, " +				
				"reference_editor_address.state, " +
				"collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " + 
				"editor_photo, messages, ");		
		// Collections 
		execute("List.orderBy", "property=name,collection=deliveryPlaces");
		assertLoadedParts("errors, collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " + 
				"messages");		
		execute("List.orderBy", "property=name,collection=deliveryPlaces");
		assertLoadedParts("errors, collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " + 
				"messages");
		execute("List.filter", "collection=deliveryPlaces");
		assertLoadedParts("errors, collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " + 
				"messages");
		execute("Collection.new", "viewObject=xava_view_deliveryPlaces");
		assertLoadedParts("dialog1");
		closeDialog();
		assertLoadedParts("core");
		execute("Collection.edit", "row=0,viewObject=xava_view_deliveryPlaces");		
		assertLoadedParts("dialog1");
		execute("List.orderBy", "property=name,collection=receptionists");
		assertLoadedParts("errors, collection_receptionists., " +
				"frame_receptionistsheader, " + 
				"messages");
		execute("List.filter", "collection=receptionists");
		assertLoadedParts("errors, collection_receptionists., " +
				"frame_receptionistsheader, " + 
				"messages");
		execute("Collection.new", "viewObject=xava_view_receptionists");
		assertLoadedParts("dialog2");
		closeDialog();
		assertLoadedParts("dialog1");
		execute("Collection.edit", "row=0,viewObject=xava_view_receptionists");
		assertLoadedParts("dialog2");
		closeDialog();
		assertLoadedParts("dialog1");
		closeDialog();
		assertLoadedParts("core");
		
		// Hide/show members
		execute("Customer.hideSeller");
		assertLoadedParts("errors, view, messages");
		execute("Customer.showSeller");
		assertLoadedParts("errors, view, messages");
		
		// Actions of properties are hidden when editable state changes
		assertAction("Customer.changeNameLabel"); 		
		execute("EditableOnOff.setOff");
		assertNoAction("Customer.changeNameLabel"); 
		assertLoadedParts("editor_type, " +
				"editor___ACTION__Customer_changeNameLabel, " +
				"editor_address.__ACTION__Address_addFullAddress, " +
				"editor_number, errors, " +
				"editor_alternateSeller.number, " +
				"property_actions_address.street, " +
				"editor_address.asString, " +
				"editor_address.city, " +
				"property_actions_seller.number, " +
				"editor_address.zipCode, " +
				"editor_name, " +
				"editor_address.street, " +
				"editor_address.viewProperty, " +
				"editor_city, " +
				"editor_website, " +
				"property_actions_address.viewProperty, " +
				"property_actions_alternateSeller.number, " +
				"editor_remarks, " +
				"editor_relationWithSeller, " +
				"editor_email, " +
				"reference_editor_address.state, " +
				"editor_seller.number, " +
				"editor_telephone, " +
				"collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " + 
				"editor_photo, messages, ");		
		
		execute("EditableOnOff.setOn");
		assertAction("Customer.changeNameLabel"); 
		assertLoadedParts("editor_type, " +
				"editor___ACTION__Customer_changeNameLabel, " +
				"editor_address.__ACTION__Address_addFullAddress, " +
				"editor_number, errors, " +
				"editor_alternateSeller.number, " +
				"property_actions_address.street, " +
				"editor_address.asString, " + 
				"editor_address.city, " +
				"property_actions_seller.number, " +
				"editor_address.zipCode, " +
				"editor_name, " +
				"editor_address.street, " +
				"editor_address.viewProperty, " +
				"editor_city, " +
				"editor_website, " +
				"property_actions_address.viewProperty, " +
				"property_actions_alternateSeller.number, " +
				"editor_remarks, " +
				"editor_relationWithSeller, " +
				"editor_email, " +
				"reference_editor_address.state, " +
				"editor_seller.number, " +
				"editor_telephone, " +
				"collection_deliveryPlaces., " +
				"frame_deliveryPlacesheader, " + 
				"editor_photo, messages, ");
		
		// Change view programatically
		execute("Customer.changeToSimpleView");
		assertLoadedParts("errors, view, messages");
	}
	
	public void testEditorForReference() throws Exception {		
		changeModule("Product2");
		assertEditorForReference();		
		changeModule("Product2ColorWithFrame");
		assertEditorForReference();				
	}
	
	private void assertEditorForReference() throws Exception {
		execute("List.viewDetail", "row=4");
		assertValue("number", "5");
		assertValue("color.number", "1");
		assertLoadedPart("core");
		assertNotLoadedPart("reference_editor_color");
		
		execute("Navigation.next");
		assertValue("number", "6");
		assertValue("color.number", "1");
		assertNotLoadedPart("view");
		assertNotLoadedPart("core");
		assertLoadedPart("editor_number");
		assertNotLoadedPart("reference_editor_color");

		execute("Navigation.next");
		assertValue("number", "7");
		assertValue("color.number", "28");
		assertNotLoadedPart("view");
		assertNotLoadedPart("core");
		assertLoadedPart("editor_number");
		assertLoadedPart("reference_editor_color");		
	}
	
	public void testEditorForCollection() throws Exception {
		if (!usesAnnotatedPOJO()) return;
		changeModule("Blog");
		execute("Mode.detailAndFirst");
		/* Before 4m2 when dialog was not used for editing collection details
		execute("Collection.new", "viewObject=xava_view_comments");
		assertLoadedParts("collection_comments., errors, messages,");
		setValue("comments.body", "Me too");
		execute("Collection.save", "viewObject=xava_view_comments");
		assertLoadedParts("collection_comments., errors, messages,");		
		*/
		// Since 4m2, with dialogs
		execute("Collection.new", "viewObject=xava_view_comments");
		assertLoadedParts("dialog1");
		setValue("body", "Me too");
		execute("Collection.save");
		assertLoadedParts("core");				
	}
 
	
	public void testDescriptionsList() throws Exception {
		changeModule("Customer");
		execute("Mode.detailAndFirst");
		assertValue("number", "1"); 
		assertDescriptionValue("address.state.id", "New York");
		
		execute("Navigation.next");
		assertValue("number", "2");
		assertDescriptionValue("address.state.id", "Colorado");		
		assertLoadedPart("reference_editor_address___state"); 
		
		execute("Navigation.next");
		assertValue("number", "3");
		assertDescriptionValue("address.state.id", "New York");
		assertLoadedPart("reference_editor_address___state"); 
		
		execute("Navigation.next");
		assertValue("number", "4");
		assertDescriptionValue("address.state.id", "New York");
		assertNotLoadedPart("reference_editor_address___state");
		
		execute("Navigation.next");
		assertValue("number", "43");
		assertDescriptionValue("address.state.id", "Kansas");				
		assertLoadedPart("reference_editor_address___state"); 
	}
			
	public void testDependentDescriptionsList_resetDescriptionsCache_setEditable() throws Exception { 
		changeModule("Product2"); 
		// Dependent descriptions list 
		assertLoadedParts(""); 
		execute("CRUD.new");
		assertLoadedParts("core, ");
		setValue("family.number", "1");
		setValue("family.number", "2");
		assertLoadedParts("errors, reference_editor_subfamily, " +
				"messages");		
		setValue("family.number", "1");
		assertLoadedParts("errors, reference_editor_subfamily, " +
				"messages");
		
		// Reset descriptions cache
		execute("Product2.changeLimitZone");
		assertLoadedParts("reference_editor_family, errors, " +
				"reference_editor_subfamily, " +
				"reference_editor_warehouse, " +
				"messages,");
		
		// setEditable
		execute("Product2.deactivateType");
		assertLoadedParts("reference_editor_family, errors, " +
				"reference_editor_subfamily, " +
				"messages,");
	}
		
	public void	testShowingHiddingPartsReloadsFullView() throws Exception {
		changeModule("Product2"); 
		execute("Mode.detailAndFirst");
		assertNotExists("zoneOne");
		assertLoadedParts("core, ");
		execute("Navigation.next");
		assertExists("zoneOne");
		assertLoadedParts("errors, view, messages");
		execute("Navigation.next");
		assertExists("zoneOne");
		assertLoadedParts("reference_editor_family, errors, " +
				"reference_editor_color, " +
				"reference_editor_subfamily, " +
				"editor_photos, " +
				"editor_unitPriceInPesetas, " +
				"editor_unitPriceWithTax, " +
				"reference_editor_warehouse, " +
				"editor_description, " +
				"editor_number, " +
				"messages, editor_unitPrice, ");
		execute("Navigation.next");
		assertNotExists("zoneOne");
		assertLoadedParts("errors, view, messages");
	}
	
	public void testFirstDetailActionExecutingLoadAllButOnlyFirstTime() throws Exception {
		changeModule("Carrier"); 		
		execute("CRUD.new");
		assertLoadedParts("core, ");
		execute("CRUD.new");
		assertLoadedParts("errors, messages");
		execute("Mode.list");
		execute("CRUD.new");
		assertLoadedParts("core, ");
		execute("CRUD.new");
		assertLoadedParts("errors, messages"); // Perfect: From now on only the needed parts are reloaded
	}
		
	public void testCollections() throws Exception { 
		changeModule("Carrier"); 
		execute("Mode.detailAndFirst");
		assertCollectionRowCount("fellowCarriers", 3); 
		setValue("warehouse.number", "2");
		assertCollectionRowCount("fellowCarriers", 0);
		setValue("warehouse.number", "1");
		assertCollectionRowCount("fellowCarriers", 3);
		assertLoadedParts("errors, collection_fellowCarriers., " + 
				"frame_fellowCarriersheader, " + 
				"messages, editor_warehouse.name, ");
		execute("Collection.view", "row=0,viewObject=xava_view_fellowCarriersCalculated");
		assertLoadedParts("dialog1");
		closeDialog();
		assertLoadedParts("core");
		execute("Navigation.next");
		assertLoadedParts("errors, editor_number, " +
				"editor_remarks, collection_fellowCarriers., " +
				"collection_fellowCarriersCalculated., " +
				"frame_fellowCarriersheader, " + 
				"frame_fellowCarriersCalculatedheader, " + 
				"editor_name, messages, ");		
		
		execute("Carrier.translateAll"); // The first time more changes because null in some db columns
		execute("Carrier.translateAll");
		assertLoadedParts("errors, collection_fellowCarriers., " +
				"collection_fellowCarriersCalculated., " +
				"frame_fellowCarriersheader, " + 
				"frame_fellowCarriersCalculatedheader, " + 				
				"messages");
	}
	
	public void testSections() throws Exception {
		changeModule("InvoiceNestedSections");
		execute("CRUD.new");
		execute("Sections.change", "activeSection=2");
		execute("Sections.change", "activeSection=1");
		assertLoadedParts("errors, sections_xava_view, " +
				"messages");
		execute("Sections.change", "activeSection=1,viewObject=xava_view_section1");
		assertLoadedParts("errors, sections_xava_view_section1, " +
				"messages");
		execute("Sections.change", "activeSection=1,viewObject=xava_view_section1_section1");
		assertLoadedParts("errors, sections_xava_view_section1_section1, " +
				"messages");		
	}
	
	public void testSectionsInsideSubview() throws Exception {
		changeModule("TransportCharge");
		execute("CRUD.new");
		execute("Sections.change", "activeSection=4,viewObject=xava_view_delivery");
		assertLoadedParts("errors, sections_xava_view_delivery, " +
				"messages, ");
	}
	
	public void testDetailCollection() throws Exception { 
		changeModule("Invoice");
		execute("CRUD.new");
		execute("Sections.change", "activeSection=1");
		execute("Collection.new", "viewObject=xava_view_section1_details");
		
		/* Until 4m1
		// First time that the detail is used all collection is reloaded
		assertLoadedParts("errors, collection_details., " +
				"messages");
		*/
		assertLoadedParts("dialog1"); // Since 4m2

		setValue("product.number", "1");
		assertLoadedParts("errors, " + 
				"editor_product.warehouseKey, " +
				"editor_product.familyNumber, " +
				"editor_product.photos, " +
				"editor_product.unitPrice, " +
				"editor_product.subfamilyNumber, " +
				"editor_product.unitPriceInPesetas, " +
				"editor_product.description, " +
				"messages");		
		setValue("product.number", "2");
		assertLoadedParts("errors, " +
				"editor_product.warehouseKey, " +
				"editor_product.familyNumber, " +
				"editor_product.photos, " +
				"editor_product.subfamilyNumber, " +
				"editor_product.unitPrice, " +
				"editor_product.unitPriceInPesetas, " +
				"editor_product.description, " +
				"messages");		
	}	
	
	public void testErrorImagesForPropertiesAndDescriptionsLists() throws Exception {
		changeModule("Product2");
		execute("CRUD.new");
		execute("CRUD.save");
		assertErrorsCount(4);
		assertLoadedParts("errors, reference_editor_subfamily, " +
				"error_image_Product2.description, " +
				"error_image_Product2.subfamily, " +
				"error_image_Product2.unitPrice, " +
				"error_image_Product2.number, " +
				"messages");
		HtmlPage page = (HtmlPage) getWebClient().getCurrentWindow().getEnclosedPage();
		HtmlSpan description = (HtmlSpan) page.getElementById(decorateId("error_image_Product2.description"));		
		assertTrue("description has no image error", description.asXml().indexOf("/xava/images/error.gif") >= 0);
		
		setValue("description", "z");
		execute("CRUD.save");
		assertErrorsCount(3);		
		assertLoadedParts("errors, reference_editor_subfamily, " +
				"error_image_Product2.description, " +				
				"error_image_Product2.subfamily, " +
				"error_image_Product2.unitPrice, " +
				"error_image_Product2.number, " +
				"editor_description, " + // Needed to refresh it because a converter transforms it to uppercase
				"messages");		
		assertTrue("description has image error", description.asXml().indexOf("/xava/images/error.gif") < 0);
		
		HtmlSpan number = (HtmlSpan) page.getElementById(decorateId("error_image_Product2.number"));
		assertTrue("number has no image error", number.asXml().indexOf("/xava/images/error.gif") >= 0);
		execute("CRUD.new");
		assertTrue("number has image error", number.asXml().indexOf("/xava/images/error.gif") < 0);
		
		// To test that Number:999 is not used as member name
		setValue("number", "999");
		execute("CRUD.refresh");
		assertError("Object of type Product does not exists with key Number:999");
	}
	
	public void testChangingViewAndController() throws Exception {
		changeModule("Carrier");
		execute("CRUD.new");
		execute("WarehouseReference.createNewNoDialog"); 
		assertLoadedParts("errors, view, bottom_buttons, " +
				"button_bar, messages");
		execute("NewCreation.saveNew");
		assertLoadedParts("errors, error_image_Warehouse.zoneNumber, " +
				"messages, error_image_Warehouse.number, editor_time,");		
	}
	
	public void testShowDialog() throws Exception { 
		changeModule("Carrier");
		execute("CRUD.new");
		execute("Reference.createNew", "model=Warehouse,keyProperty=xava.Carrier.warehouse.number");
		assertLoadedParts("dialog1");
		execute("NewCreation.saveNew");
		assertLoadedParts("errors, error_image_Warehouse.zoneNumber, " +
				"messages, error_image_Warehouse.number, editor_time,");		
	}

	
	public void testCustomView_uploadFile() throws Exception {
		changeModule("Product2");
		execute("Mode.detailAndFirst");
		execute("Gallery.edit", "galleryProperty=photos");
		assertLoadedParts("errors, view, bottom_buttons, " +
				"button_bar, messages");
		execute("Gallery.addImage");
		// assertLoadedParts("core, "); When no dialog
		assertLoadedParts("dialog1, "); // When dialog
		String imageUrl = System.getProperty("user.dir") + "/test-images/foto_javi.jpg";
		setFileValue("newImage", imageUrl);
		execute("LoadImageIntoGallery.loadImage");
		assertNoErrors();
		assertMessage("Image added to the gallery"); 
		assertLoadedParts(""); 
		
		String imageOid = getForm().getInputByName("xava.GALLERY.images").getValueAttribute();
		execute("Gallery.removeImage", "oid="+imageOid);
		assertLoadedParts("errors, view, messages");
		execute("Gallery.return");
		assertLoadedParts("errors, view, bottom_buttons, " +
				"button_bar, messages");
	}
	
	public void testHandmadeWebView() throws Exception {
		changeModule("SellerJSP");
		execute("Mode.detailAndFirst");
		assertLoadedParts("core, ");
		setValue("level.id", "B");
		assertLoadedParts("errors, view, messages");
		execute("Navigation.next");
		assertLoadedParts("errors, view, messages");
	}
	
	public void testChangingModelOfView() throws Exception {
		if (!usesAnnotatedPOJO()) return;
		changeModule("Human");
		execute("Mode.detailAndFirst");		
		assertLoadedParts("core,");
		
		execute("Navigation.next");		
		assertLoadedParts("editor_sex, errors, " +
				"editor_name, " +
				"messages,");
		
		// Now the model changes...
		execute("Navigation.next"); 
		assertLoadedParts("errors, view, " + // ...so we reload the full view
				"messages,");		
	}
	

	private void assertLoadedParts(String expected) throws Exception {
		assertEquals("Loaded parts are not the expected ones", order(expected), order(getLoadedParts()));
	}
	
	private String order(String parts) {		
		StringTokenizer st = new StringTokenizer(parts, ",");
		SortedSet ordered = new TreeSet();
		while (st.hasMoreTokens()) {
			String part = st.nextToken().trim();
			if (!"".equals(part)) ordered.add(Ids.undecorate(part));
		}
		StringBuffer result = new StringBuffer();
		for (Iterator it=ordered.iterator(); it.hasNext(); ) {
			result.append(it.next());
			result.append(",\n");
		}
		
		return result.toString();
	}

	private void assertLoadedPart(String expected) throws Exception {
		assertTrue("Loaded part not found", getLoadedParts().indexOf(expected + ",") >= 0);
	}
	
	private void assertNotLoadedPart(String expected) throws Exception {
		assertTrue("Loaded part found", getLoadedParts().indexOf(expected + ",") < 0);
	}
		
	private String getLoadedParts() { 
		HtmlPage page = (HtmlPage) getWebClient().getCurrentWindow().getEnclosedPage();
		HtmlInput input = (HtmlInput) page.getHtmlElementById(decorateId("loaded_parts"));
		return input.getValueAttribute();
	}
	
	@Override
	protected boolean usesAnnotatedPOJO() {		
		// Because usesAnnotatedPOJO() of ModuleTestBase requires a module name in constructor
		return Invoice.class.isAnnotationPresent(Entity.class);
	}
		
}
