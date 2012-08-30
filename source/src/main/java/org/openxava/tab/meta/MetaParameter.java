package org.openxava.tab.meta;



import org.openxava.filters.meta.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;

/**
 * 
 * @author Javier Paniza
 */
public class MetaParameter extends MetaElement {
	
	private boolean hasLabel = false;
	private String propertyName;
	private String labelId;
	private MetaConsult metaConsult;
	private boolean range=false;
	private boolean like=false;
	private MetaFilter metaFilter;
	
	
	
	public MetaProperty getMetaProperty() throws XavaException {
		if (metaConsult == null) {
			throw new XavaException("parameter_consult_required");
		}
		return metaConsult.getMetaModel().getMetaProperty(getPropertyName());
	}
		
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public MetaConsult getMetaConsult() {
		return metaConsult;
	}
	public void setMetaConsult(MetaConsult consult) {
		this.metaConsult = consult;
	}

	public boolean isRange() {
		return range;
	}
	public void setRange(boolean range) {
		this.range = range;
	}

	public boolean isLike() {
		return like;
	}
	public void setLike(boolean like) {
		this.like = like;
	}

	public MetaFilter getMetaFilter() {
		return metaFilter;
	}
	public void setMetaFilter(MetaFilter metaFilter) {
		this.metaFilter = metaFilter;
	}

	public void setLabel(String newLabel) {		
		super.setLabel(newLabel);
		hasLabel = !Is.emptyString(newLabel);
	}


	public String getLabelId() {
		return labelId;
	}

	public void setLabelId(String id) {
		labelId = id;
		hasLabel = !Is.emptyString(id);
	}

	public String getId() {
		return labelId;		
	}
	
	/**
	 * If has own label, and it does not use the property's label.
	 */
	public boolean hasLabel() {
		return hasLabel;
	}
	
}


