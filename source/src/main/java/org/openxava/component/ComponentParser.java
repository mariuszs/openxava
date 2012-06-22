package org.openxava.component;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.annotations.parse.*;
import org.openxava.mapping.*;
import org.openxava.mapping.xmlparse.*;
import org.openxava.model.meta.*;
import org.openxava.model.meta.xmlparse.*;
import org.openxava.tab.meta.xmlparse.*;
import org.openxava.util.*;
import org.openxava.util.xmlparse.*;
import org.openxava.view.meta.xmlparse.*;
import org.w3c.dom.*;

/**
 * @author: Javier Paniza
 */
class ComponentParser extends ParserBase {
	
	private static Log log = LogFactory.getLog(ComponentParser.class); 
	
	private MetaComponent component;	

	private ComponentParser(String name) {
		super(name + ".xml");		
	}
	
	synchronized public static MetaComponent parse(String name) throws XavaException {
		ComponentParser parser = new ComponentParser(name);				
		parser.parse();				
		MetaComponent r = parser.getComponent();
		
		if (r == null) {			
			r = parseAnnotatedClass(name);			
		}		 
		else if (!r.getName().equals(name)) {
			throw new XavaException("component_file_not_match", name, r.getName());
		}		
		return r;
	}
	
	private static MetaComponent parseAnnotatedClass(String name) throws XavaException {
		AnnotatedClassParser parser = new AnnotatedClassParser();		
		try {
			return parser.parse(name);		
		}
		catch (Exception ex) {
			if (ex instanceof RuntimeException) throw (RuntimeException) ex;
			else throw new RuntimeException(ex);
		}				
	}

	private void createAggregate() throws XavaException {
		NodeList l = getRoot().getElementsByTagName(xaggregate[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			component.addMetaAggregate(ModelParser.parseAggregate(l.item(i), component.getMetaEntity(), lang));
		}
	}
	
	private void createAggregateMappings() throws XavaException {
		NodeList l = getRoot().getElementsByTagName(xaggregate_mapping[lang]);
		int c = l.getLength();
		Collection aggregatesForCollection = createNamesAggregateForCollection();
		for (int i = 0; i < c; i++) {
			AggregateMapping aggregateMapping = MappingParser.parseAggregateMapping(this.component, l.item(i), lang); 
			component.addAggregateMapping(aggregateMapping);
			aggregatesForCollection.remove(aggregateMapping.getModelName());
		}		
		for (Iterator it=aggregatesForCollection.iterator(); it.hasNext();) {
			String aggregateName = (String) it.next();
			AggregateMapping aggregateMapping = new AggregateMapping();
			aggregateMapping.setModelName(aggregateName);
			aggregateMapping.setMetaComponent(component); 
			fillDefaultMapping(aggregateMapping, component.getMetaAggregate(aggregateName));
			component.addAggregateMapping(aggregateMapping);
		}		
	}	
	
	private Collection createNamesAggregateForCollection() throws XavaException { 
		Collection result = new ArrayList();
		for (Iterator it = component.getMetaAggregates().iterator(); it.hasNext();) {
			MetaAggregate agg = (MetaAggregate) it.next();
			if (agg instanceof MetaAggregateForCollection) {
				result.add(agg.getName());
			}
		}		
		return result;
	}
	
	private void createEntity() throws XavaException {
		NodeList l = getRoot().getElementsByTagName(xentity[lang]);
		int c = l.getLength();
		if (c != 1) {
			throw new XavaException("component_only_1_entity", component.getName());
		}		
		component.setMetaEntity(ModelParser.parseEntity(l.item(0), component.getName(), lang));
	}
	
	private void createViews() throws XavaException {
		NodeList l = getRoot().getElementsByTagName(xview[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			component.addMetaView(ViewParser.parseView(l.item(i), lang));
		}		
	}
	
	private void createTabs() throws XavaException {
		NodeList l = getRoot().getElementsByTagName(xtab[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			component.addMetaTab(TabParser.parseTab(l.item(i), lang));
		}				
	}
	
	private void createEntityMapping() throws XavaException {
		NodeList l = getRoot().getElementsByTagName(xentity_mapping[lang]);
		int c = l.getLength();
		if (c > 1) { 
			throw new XavaException("component_only_1_entity_mapping");
		}
		
		if (c == 1) {
			component.setEntityMapping(MappingParser.parseEntityMapping(this.component, l.item(0), lang)); 
		}	
		else { // It's 0. Has no explicit mapping
			EntityMapping defaultMapping = new EntityMapping();
			defaultMapping.setMetaComponent(component); 
			fillDefaultMapping(defaultMapping, component.getMetaEntity());
			component.setEntityMapping(defaultMapping);
		}
	}
	
	private void fillDefaultMapping(ModelMapping mapping, MetaModel metaModel) throws XavaException { 
		mapping.setTable(metaModel.getName());
		for (Iterator itProperties = metaModel.getMetaProperties().iterator(); itProperties.hasNext(); ) {
			MetaProperty p = (MetaProperty) itProperties.next();
			if (!p.isCalculated()) {
				PropertyMapping propertyMapping = new PropertyMapping(mapping);
				propertyMapping.setProperty(p.getName());
				propertyMapping.setColumn(p.getName());
				mapping.addPropertyMapping(propertyMapping);
			}
		}
		for (Iterator itReferences = metaModel.getMetaReferencesToEntity().iterator(); itReferences.hasNext();) {
			MetaReference ref = (MetaReference) itReferences.next();
			ReferenceMapping refMapping = new ReferenceMapping();
			refMapping.setReference(ref.getName());
			for (Iterator itReferencedKeys = ref.getMetaModelReferenced().getAllKeyPropertiesNames().iterator(); itReferencedKeys.hasNext();) {
				String referencedKey = (String) itReferencedKeys.next();
				ReferenceMappingDetail detail = new ReferenceMappingDetail();
				detail.setColumn(ref.getName() + "_" + Strings.change(referencedKey, ".", "_"));
				detail.setReferencedModelProperty(referencedKey);
				refMapping.addDetail(detail);
			}
			mapping.addReferenceMapping(refMapping);
		}
	}

	protected void createObjects() throws XavaException {		
		if (this.component != null) {
			if (XavaPreferences.getInstance().isDuplicateComponentWarnings()) {
				log.warn(XavaResources.getString("trying_to_load_component_twice_warning", this.component.getName()));
			}
			return;
		}
		lang = "componente".equals(getRoot().getNodeName())?ESPANOL:ENGLISH;
		createComponent();
		createEntity();
		createAggregate();
		createViews();
		createTabs();
		if (!isPersistent()) {
			this.component.setTransient(true);
		}
		createEntityMapping();
		createAggregateMappings();
		fillDefaultFinders();			

		setContainerModelToAggregateReference(); 
	}
	
	private boolean isPersistent() { 
		return getRoot().getElementsByTagName(xtransient[lang]).getLength() == 0;
	}

	private void setContainerModelToAggregateReference() throws XavaException { 
		setContainerModelToAggregateReference(component.getMetaEntity());		
		for (Iterator it = component.getMetaAggregates().iterator(); it.hasNext(); ) {
			setContainerModelToAggregateReference((MetaModel) it.next());
		}
	}

	private void setContainerModelToAggregateReference(MetaModel metaModel) throws XavaException { 
		Collection references = metaModel.getMetaReferences();
		if (references.isEmpty()) return;		
		for (Iterator it = references.iterator(); it.hasNext();) {
			MetaReference ref = (MetaReference) it.next();
			if (component.hasMetaAggregate(ref.getReferencedModelName())) {
				MetaModel referencedModel = ref.getMetaModelReferenced();
				if (referencedModel instanceof MetaAggregateForReference) {
					referencedModel.setContainerModelName(metaModel.getName());					
				}
			}
		}
	}

	/**
	 * Add finder for the fields of primary key
	 * @throws XavaException
	 */
	private void fillDefaultFinders()	throws XavaException {
		MetaModel model = component.getMetaEntity();
		if (!model.getMetaReferencesKey().isEmpty()) return;
		StringBuffer finderName = new StringBuffer("by");
		StringBuffer arguments = new StringBuffer(); 
		StringBuffer condition = new StringBuffer();
		
		int i = 0;
		for (Iterator it = model.getMetaPropertiesKey().iterator(); it.hasNext(); i++) {
			MetaProperty property = (MetaProperty) it.next(); 
			finderName.append(Strings.firstUpper(property.getName()));			
			arguments.append(property.getCMPTypeName());
			arguments.append(' ');
			arguments.append(property.getName());
			if (it.hasNext()) arguments.append(',');
			condition.append("${");
			condition.append(property.getName());
			condition.append("} = {");
			condition.append(i);
			condition.append("}");
			if (it.hasNext()) condition.append(" and ");
		}
		MetaFinder finder = new MetaFinder();
		finder.setMetaModel(model);
		finder.setName(finderName.toString());
		if (model.getMetaFinders().contains(finder)) return;
		finder.setArguments(arguments.toString());
		finder.setCondition(condition.toString());
		model.addMetaFinder(finder);
	}
	
	
	private void createComponent() throws XavaException {
		this.component = new MetaComponent();
		component.setName(getRoot().getAttribute(xname[lang]));
	}
	
	private MetaComponent getComponent() {
		return component;
	}
	
}