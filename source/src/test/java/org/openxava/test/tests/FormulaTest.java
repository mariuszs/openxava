package org.openxava.test.tests;

import java.net.URL;

import javax.persistence.Query;

import org.openxava.jpa.XPersistence;
import org.openxava.test.model.Formula;
import org.openxava.test.model.FormulaIngredient;
import org.openxava.test.model.Ingredient;
import org.openxava.tests.ModuleTestBase;
import org.openxava.util.Strings;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;



/**
 * @author Javier Paniza
 */

public class FormulaTest extends ModuleTestBase {
	
	public FormulaTest(String testName) {
		super(testName, "Formula");		
	}
	
	public void testPagingInCollection() throws Exception {
		// create objects in database
		Formula formula = Formula.findByName("HTML TEST");
		Ingredient ingredient = Ingredient.findByName("LECHE");
		for (int x = 0; x <= 12; x++){
			FormulaIngredient fi = new FormulaIngredient();			
			fi.setFormula(formula);
			fi.setIngredient(ingredient);
			XPersistence.getManager().persist(fi);
		}
		XPersistence.commit(); 
		
		//
		execute("Mode.detailAndFirst");
		assertValue("name", "HTML TEST");
		assertCollectionRowCount("ingredients", 10);
		checkRowCollection("ingredients", 0);
		execute("List.goNextPage", "collection=ingredients");
		execute("List.goPreviousPage", "collection=ingredients");
		assertRowCollectionChecked("ingredients", 0);
		
		// remove objects from database
		String sentencia = " DELETE FROM FormulaIngredient WHERE ingredient.oid = :ingredient ";
		Query query = XPersistence.getManager().createQuery(sentencia);
		query.setParameter("ingredient", ingredient.getOid());
		query.executeUpdate();
		XPersistence.commit();
	}
	
	public void testOnSelectElementActionFromAnotherModule() throws Exception {
		changeModule("BeforeGoingToFormula");
		execute("ChangeModule.goFormula");
		
		//
		testOnSelectElementAction();
	}
	
	public void testOnSelectElementAction() throws Exception { 
		execute("Mode.detailAndFirst");
		assertValue("name", "HTML TEST");
		assertCollectionRowCount("ingredients", 2);
		assertValue("selectedIngredientSize", "");
		// selected
		checkRowCollection("ingredients", 0);
		assertNoErrors();
		assertValue("selectedIngredientSize", "1");
		assertValue("selectedIngredientNames", "AZUCAR");
		checkAllCollection("ingredients");
		assertRowCollectionChecked("ingredients", 1);
		assertValue("selectedIngredientSize", "2");
		assertValue("selectedIngredientNames", "AZUCAR,CAFE");
		// deselected
		uncheckRowCollection("ingredients", 0);	
		assertValue("selectedIngredientSize", "1");
		assertValue("selectedIngredientNames", "CAFE");
		uncheckRowCollection("ingredients", 1);	
		assertValue("selectedIngredientSize", "0");
		assertValue("selectedIngredientNames", "");
		// fails to deselect the last selected
		assertRowCollectionUnchecked("ingredients", 0);
		assertRowCollectionUnchecked("ingredients", 1);
		
		// not execute the associated actions if there are no items in the collection
		setConditionValues("ingredients", new String[] { "", "03C6B61AC0A8011600000000AB4E7ACB"} );	// id milk
		execute("List.filter", "collection=ingredients");
		assertCollectionRowCount("ingredients", 0);
		uncheckAllCollection("ingredients");
		checkAllCollection("ingredients");
		assertNoErrors();
		assertNoMessages();
	}
	
	public void testImageInsideCollection() throws Exception {
		execute("CRUD.new");		
		execute("Collection.new", "viewObject=xava_view_section0_ingredients");
		execute("ImageEditor.changeImage", "newImageProperty=image"); 
		assertNoErrors();
		assertAction("LoadImage.loadImage");		
		String imageUrl = System.getProperty("user.dir") + "/test-images/cake.gif";
		setFileValue("newImage", imageUrl);
		execute("LoadImage.loadImage");
		assertNoErrors();
		
		HtmlPage page = (HtmlPage) getWebClient().getCurrentWindow().getEnclosedPage();		
		URL url = page.getWebResponse().getRequestSettings().getUrl();
		
		String urlPrefix = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
		
		HtmlImage image = (HtmlImage) page.getElementsByName(decorateId("image")).get(0); 
		String imageURL = null;
		if (image.getSrcAttribute().startsWith("/")) {
			imageURL = urlPrefix + image.getSrcAttribute();
		}
		else {
			String urlBase = Strings.noLastToken(url.getPath(), "/");
			imageURL = urlPrefix + urlBase + image.getSrcAttribute();
		}				
		WebResponse response = getWebClient().getPage(imageURL).getWebResponse();		
		assertTrue("Image not obtained", response.getContentAsString().length() > 0);
		assertEquals("Result is not an image", "image", response.getContentType());		
	}
	
	public void testDependentReferencesAsDescriptionsListWithHiddenKeyInCollection_aggregateCanHasReferenceToModelOfContainerType() throws Exception {		
		execute("CRUD.new");		
		execute("Collection.new", "viewObject=xava_view_section0_ingredients");
		assertExists("anotherFormula.oid"); // Reference to a model of 'Formula' type, the same of the container
		
		String [][] ingredients = {
			{ "", "" },
			{ "03C5C64CC0A80116000000009590B64C", "AZUCAR" },
			{ "03C59CF0C0A8011600000000618CC74B", "CAFE" },
			{ "03C6E1ADC0A8011600000000498BC537", "CAFE CON LECHE" },
			{ "03C6B61AC0A8011600000000AB4E7ACB", "LECHE" }, 
			{ "03C6C61DC0A801160000000076765581", "LECHE CONDENSADA"} 
		};
		
		String [][] empty = {
			{ "", "" }
		};
		
		String [][] cafeConLeche = {
				{ "", "" },
				{ "03C5C64CC0A80116000000009590B64C", "AZUCAR" },
				{ "03C59CF0C0A8011600000000618CC74B", "CAFE" },		
				{ "03C6B61AC0A8011600000000AB4E7ACB", "LECHE" }, 		 				
		};
		
		assertValidValues("ingredient.oid", ingredients);
		assertValidValues("accentuate.oid", empty);
		
		setValue("ingredient.oid", "03C6E1ADC0A8011600000000498BC537");
		assertValidValues("ingredient.oid", ingredients);
		assertValidValues("accentuate.oid", cafeConLeche);
	}
	
	public void testHtmlTextStereotype() throws Exception {		
		execute("Mode.detailAndFirst");
		assertValue("name", "HTML TEST");
		execute("Sections.change", "activeSection=1");
		assertTrue("Expected HTML token not found", getHtml().indexOf("Y largo</strong>,<span style=\"background-color: rgb(153, 204, 0);\"> verde </span>") >= 0);
	}
	
	public void testSingleQuotationMarkAsHtmlValue() throws Exception {
		execute("CRUD.new");
		setValue("name", "L'AJUNTAMENT");
		execute("CRUD.refresh");
		execute("Sections.change", "activeSection=1");
		assertValue("recipe", "L'Ajuntament");
	}
		
}
