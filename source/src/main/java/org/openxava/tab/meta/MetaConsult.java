package org.openxava.tab.meta;


import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.filters.*;
import org.openxava.filters.meta.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;


/**
 * Only used in spanish/swing version.
 * 
 * @author Javier Paniza
 */


public class MetaConsult extends MetaElement {
	
	private static Log log = LogFactory.getLog(MetaConsult.class);
	
	private Collection metaParameters = new ArrayList();
	private String condition;
	private String conditionSQL;
	private MetaTab metaTab;
	private MetaFilter metaFilter;
	private IFilter filter;
	private String label;
	
	
				
	public void addMetaParameter(MetaParameter parameter) {
		metaParameters.add(parameter);
		parameter.setMetaConsult(this);
	}
	
	MetaModel getMetaModel() throws XavaException {
		if (metaTab == null) {
			throw new XavaException("tab_consult_required");
		}		
		return metaTab.getMetaModel();
	} 
	
	/**
	 * @return Not null, of type <tt>MetaParameter</tt> and read only.
	 */
	public Collection getMetaParameters() {
		return Collections.unmodifiableCollection(metaParameters);
	}
	
	/**
	 * @return Null means condition SQL is calculated by default,
	 * 					and empty string means there are no conditions (all objects are selected)
	 */
	public String getCondition() {		
		return condition==null?null:condition.trim();
	}
	public void setCondition(String condition) {
		this.condition = condition;
		this.conditionSQL = null;
	}

	public String getLabel() {
		if (has18nLabel()) return super.getLabel();
		if (Is.emptyString(label)) {
			try {
				label = createDefaultLabel();
			}
			catch (XavaException ex) {				
				if (XavaPreferences.getInstance().isI18nWarnings()) {
					log.warn(XavaResources.getString("label_i18n_warning", getId()),ex);
				}
				label = getName();
			}
		}
		return label;
	}
	
	public void setLabel(String label) {
		super.setLabel(label);
		this.label = label;
	}
	
	/**
	 * Condition but using column name of underlying tables. <p>
	 * If condition is not set, here a default one is created.
	 */
	public String getConditionSQL() throws XavaException {
		if (conditionSQL == null) {
			String condition = getCondition();			
			if (Is.emptyString(condition)) {
				condition = createDefaultCondition();
			}
			if (!condition.trim().toUpperCase().startsWith("SELECT") &&
				(metaTab.hasReferences() || metaTab.hasBaseCondition())) { 
				if (condition.trim().equals("")) {
					condition = metaTab.getSelect(); 
				}
				else {
					String selectTab = metaTab.getSelect();
					String union = selectTab.toUpperCase().indexOf("WHERE")<0?" WHERE ":" AND ";
					condition = selectTab + union + condition; 
				}
			}
			conditionSQL = getMetaTab().getMapping().changePropertiesByColumns(condition);
		}
		return conditionSQL;
	}
	
	
	private String createDefaultCondition() throws XavaException {
		Iterator it = getMetaParameters().iterator();		
		StringBuffer condition = new StringBuffer();
		while (it.hasNext()) {
			MetaParameter parameter = (MetaParameter) it.next();			
			condition.append("${");
			condition.append(parameter.getPropertyName());
			if (parameter.isRange()) {
				condition.append("} between ? and ?");
			}
			else if (parameter.isLike()) {
				condition.append("} like ?");						
			}			
			else {
				condition.append("} = ?");
			}
			if (it.hasNext()) {
				condition.append(" AND ");
			}
		}
		return condition.toString();
	}
	
	
	private String createDefaultLabel() throws XavaException {
		Collection metaParameters = getMetaParameters();
		Iterator it = metaParameters.iterator();		
		int count = metaParameters.size();
		StringBuffer label = new StringBuffer(XavaResources.getString("por"));
		label.append(' ');
		int c=0;
		while (it.hasNext()) {
			MetaParameter parameter = (MetaParameter) it.next();					
			c++;			
			label.append(parameter.getMetaProperty().getLabel());
			if (c < count) {
				if (c == count -1) {
					label.append(' ');
					label.append(XavaResources.getString("y"));
					label.append(' ');
				}
				else {
					label.append(", ");
				}
			}
		}
		return label.toString();
	}

	MetaTab getMetaTab() {
		return metaTab;
	}
	void setMetaTab(MetaTab tab) {
		this.metaTab = tab;
	}
		

	public MetaFilter getMetaFilter() {
		return metaFilter;
	}
	public void setMetaFilter(MetaFilter metaFilter) {
		this.metaFilter = metaFilter;
	}
	
	/**
	 * Apply filter associated to this consult if it is,
	 * and of the container tab too. <p>
	 */
	public Object filterParameters(Object o) throws XavaException {
		Object result = o;		
		if (getMetaFilter() != null) {
			result = getFilter().filter(result);
		}
		return getMetaTab().filterParameters(result);
	}
	
	private IFilter getFilter() throws XavaException {
		if (filter == null) {
			filter = getMetaFilter().createFilter();
		}
		return filter;
	}
	public boolean useOrderBy() {
		return condition != null && condition.toUpperCase().indexOf("ORDER BY") >= 0;
	}
	
	public Collection getOrderByPropertiesNames() {
		Collection result = new ArrayList();
		if (!useOrderBy()) return result;
		int i = condition.toUpperCase().indexOf("ORDER BY");
		String r = condition.substring(i+8);
		i = r.indexOf("${");
		int f = 0;		
		while (i >= 0) {			
			f = r.indexOf("}", i+2);
			if (f < 0) break;
			String property = r.substring(i+2, f);
			result.add(property);
			i = r.indexOf("${", f);
		}
		return result;
	}

	public String getId() {
		if (Is.emptyString(getName())) return null;				
		return getMetaTab().getId() + ".consultas." + getName();
	}
	
}


