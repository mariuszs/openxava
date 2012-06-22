package org.openxava.actions;

import java.util.*;

import javax.inject.*;



import org.apache.commons.logging.*;
import org.openxava.calculators.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.session.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * 
 * @author Javier Paniza
 */

public class EditGalleryAction extends /*View*/BaseAction implements INavigationAction {
		
	private static Log log = LogFactory.getLog(EditGalleryAction.class);
	
	private String galleryProperty;
	private String viewObject; 
	private View view; 

	@Inject
	private Gallery gallery;	
	
	
	
	public void execute() throws Exception {
		String oid = getView().getValueString(galleryProperty);
		if (Is.emptyString(oid)) {
			UUIDCalculator cal = new UUIDCalculator();  
			oid = (String) cal.calculate();
			getView().setValue(galleryProperty, oid);
			if (!getView().isKeyEditable()) { // Modifying
				updateOidInObject(oid);
			}
		}
		gallery.setOid(oid);
		gallery.loadAllImages();		
		gallery.setTitle(XavaResources.getString("gallery_title", 
				Labels.get(galleryProperty, Locales.getCurrent()), 
				Labels.get(getView().getModelName(), Locales.getCurrent()), 
				getObjectDescription()));				
		if (gallery.isEmpty()) {
			addMessage("no_images");
		}
		gallery.setReadOnly(!isEditable());
	}

	private void updateOidInObject(String oid) throws Exception {
		Map values = new HashMap();
		values.put(galleryProperty, oid);
		MapFacade.setValues(getView().getModelName(), getView().getKeyValues(), values);		
	}

	private String getObjectDescription() {
		try {
			StringBuffer result = new StringBuffer();
			for (Iterator it=getView().getMetaModel().getMetaPropertiesKey().iterator(); it.hasNext();) {
				MetaProperty p = (MetaProperty) it.next();
				if (!p.isHidden()) {
					Object value = getView().getValue(p.getName());
					if (value != null) {
						if (result.length() > 0) result.append('/');
						result.append(value);
					}
				}
			}

			String [] descriptionProperties = { "name", "nombre", "description", "descripcion" };
			for (int i = 0; i < descriptionProperties.length; i++) {
				String des = (String) getView().getValue(descriptionProperties[i]);
				if (!Is.emptyString(des)) {
					if (result.length() > 0) result.append(" - ");
					result.append(des);
					break;
				}				
			}
			
			return result.toString();
		}
		catch (Exception ex) { 
			log.error("[EditGalleryAction.getObjectDescription] " + XavaResources.getString("object_description_warning"),ex);  
			return XavaResources.getString("object_description_warning");
		}		
	}
	
	private View getView() { 
		if (view == null) {
			view = (View) getContext().get(getRequest(), viewObject==null?"xava_view":viewObject);
		}
		return view;
	}

	private boolean isEditable() throws XavaException {
		return getView().isEditable(galleryProperty);		
	}

	public String getGalleryProperty() {
		return galleryProperty;
	}

	public void setGalleryProperty(String galleryOID) {
		this.galleryProperty = galleryOID;
	}

	public String[] getNextControllers() throws Exception {
		return isEditable()?new String [] { "Gallery" }:new String [] { "Return" };
	}

	public String getCustomView() throws Exception {
		return "xava/editors/gallery";
	}

	public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}

	public String getViewObject() {
		return viewObject;
	}

	public void setViewObject(String viewObject) {
		this.viewObject = viewObject;
	}

}
