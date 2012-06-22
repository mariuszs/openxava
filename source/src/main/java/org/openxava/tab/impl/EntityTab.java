package org.openxava.tab.impl;

import java.rmi.*;
import java.util.*;

import javax.ejb.*;
import javax.rmi.*;



import org.apache.commons.logging.*;
import org.openxava.component.*;
import org.openxava.converters.*;
import org.openxava.ejbx.*;
import org.openxava.mapping.*;
import org.openxava.model.meta.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;


/**
 * Local Tab ({@link IEntityTabImpl}) implementation configured from an OpenXava component. <p> 
 * 
 * This tab is valid for all components, you only need to indicate
 * the model name on create. <br>
 * 
 * @author Javier Paniza
 */
public class EntityTab implements IEntityTabImpl, java.io.Serializable {
	
	private static final long serialVersionUID = -3967727741162794493L;
	
	private static Log log = LogFactory.getLog(EntityTab.class);
	private static final int DEFAULT_CHUNK_SIZE = 50;
	
	private int chunkSize = DEFAULT_CHUNK_SIZE;
	private static Map dataProviders;	 
	private String selectBase;
	private String componentName;
	private String tabName;

	private JDBCTabProvider tabProvider;
	private TableModelBean table;
	private MetaTab metaTab;
	private String modelName;
	private transient MetaModel metaModel = null;
	private transient ModelMapping mapping = null;
	private int[] indexesPK = null;	
	private List propertiesNames;
	private Map keyIndexes = null;

	private List tabCalculators;
	private Collection tabConverters;
	
	private boolean	knowIfHasPropertiesWithValidValues = false;
	private boolean _hasPropertiesWithValidValues;
	
	
	
	public void search(int index, Object key)
		throws FinderException, RemoteException {
		tabProvider.search(index, key);
	}
	
	public void search(String condition, Object key) throws FinderException, RemoteException {
		try {				
			StringBuffer select = new StringBuffer(getSelectBase());
			if (!Is.emptyString(condition)) {				
				if (!condition.toUpperCase().trim().startsWith("ORDER BY")) {
					if (select.toString().toUpperCase().indexOf("WHERE") < 0) select.append(" WHERE "); 
					else select.append(" AND "); 								
				}				
				select.append(condition); 
			}																		
			tabProvider.search(select.toString(), key);
		}
		catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(XavaResources.getString("tab_search_error", ex.getLocalizedMessage()));
		}		
	}
	
	private String getSelectBase() throws XavaException {
		if (selectBase == null) {
			selectBase = insertKeyFields(metaTab.getSelectSQL());
		}
		return selectBase;
	}
	
	/**
	 * Return a map with key values.
	 */
	public Object findEntity(Object[] key) throws FinderException, RemoteException {
		try {
			Map result = new HashMap();
			for (int i = 0; i < key.length; i++) {
				int iProperty = getIndexesPK()[i];
				String name = (String) getPropertiesNames().get(iProperty);
				result.put(name, key[i]);
			}			
			return result;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(
				XavaResources.getString("tab_entity_find_error"));
		}
	}


	private String[] getHeading() throws XavaException {
		String[] result = new String[getProperties().size() + getIndexesPK().length];
		Iterator it = getProperties().iterator();		
		int i = getIndexesPK().length;
		while (it.hasNext()) {
			MetaProperty p = (MetaProperty) it.next();
			result[i++] = p.getLabel();
		}
		return result;
	}

	/**
	 * Associated class with each column table. <br>
	 * Complete class name is specified in String format.<br>
	 */
	private String[] getColumnsClasses() throws XavaException {
		String[] result = new String[getProperties().size() + getIndexesPK().length];
		Iterator it = getProperties().iterator();
		int i = getIndexesPK().length;
		while (it.hasNext()) {
			MetaProperty p = (MetaProperty) it.next();
			result[i++] = getColumnClass(p.getType());
		}
		return result;
	}

	private String getColumnClass(Class type) throws XavaException {
		String name = type.getName();
		if (!type.isPrimitive())
			return name;

		// It's primitive
		if (name.equals("boolean")) {
			return "java.lang.Boolean";
		}
		else if (name.equals("byte")) {
			return "java.lang.Byte";
		}
		else if (name.equals("char")) {
			return "java.lang.Character";
		}
		else if (name.equals("short")) {
			return "java.lang.Short";
		}
		else if (name.equals("int")) {
			return "java.lang.Integer";
		}
		else if (name.equals("long")) {
			return "java.lang.Long";
		}
		else if (name.equals("float")) {
			return "java.lang.Float";
		}
		else if (name.equals("double")) {
			return "java.lang.Double";
		}
		throw new XavaException("primitive_type_not_recognized", name);
	}


	public IXTableModel getTable()  throws RemoteException { 
		try {
			table.setEntityTab(this);
			return new HiddenXTableModel(table, getIndexesPK()); 
		}
		catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(XavaResources.getString("tab_tablemodel_error", ex.getLocalizedMessage()));
		}   
	}

	public void init() throws InitException {
		try {
			if (Is.emptyString(componentName)) {
				throw new InitException("tab_component_required");
			}
			tabProvider = new JDBCTabProvider();
			table = new TableModelBean();
			table.setTranslateHeading(false);
			this.mapping = null;
			this.indexesPK = null;
			if (this.metaTab == null) {			
				this.metaTab = MetaComponent.get(componentName).getMetaTab(tabName);
			}			
			this.metaModel = metaTab.getMetaModel();
			this.modelName = metaModel.getQualifiedName();			
			table.setHeading(getHeading());
			table.setColumnsClasses(getColumnsClasses());
			table.setPropertiesNames(getPropertiesNames()); 
			tabProvider.setFields(getFields());
			tabProvider.setConditions(getConditions());			
			tabProvider.setTable(getTableNameDB());
			tabProvider.setChunkSize(getChunkSize());
			table.setPKIndexes(getIndexesPK());
			table.invariant();
			//tabProvider.invariant(); // It is not possible because still lacks connectionProvider
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new InitException("tab_init_error", modelName);
		}
	}

	private String[] getFields() throws XavaException {
		Collection c = new ArrayList();
		// First the key
		Iterator itKeyNames = getKeyNames().iterator();
		while (itKeyNames.hasNext()) {
			c.add(getMapping().getQualifiedColumn((String) itKeyNames.next()));
		}
				
		// Then the others
		c.addAll(metaTab.getTableColumns());
		c.addAll(metaTab.getHiddenTableColumns());
				
		String[] result = new String[c.size()];
		c.toArray(result);		
		return result;
	}
	

	private String[] getConditions() throws RemoteException {
		try {
			Collection metaConsults = metaTab.getMetaConsults();
			String[] conditions = new String[metaConsults.size()];
			Iterator it = metaConsults.iterator();
			int i = 0;
			while (it.hasNext()) {
				MetaConsult consult = (MetaConsult) it.next();
				conditions[i++] = insertKeyFields(consult.getConditionSQL());
			}
			return conditions;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(
					XavaResources.getString("tab_conditions_error", this.modelName));
		}
	}
	
	private String insertKeyFields(String select) throws XavaException {				
		String s = select.trim(); 
		String sUpperCase = s.toUpperCase(); 
		if (!(sUpperCase.startsWith("SELECT ") || (sUpperCase.startsWith("SELECT\t")))) return select;				
		StringBuffer result = new StringBuffer("SELECT ");		
		Iterator itKeyNames = getKeyNames().iterator();
		while (itKeyNames.hasNext()) {
			result.append(getMapping().getQualifiedColumn((String) itKeyNames.next()));
			result.append(", ");
		}
		result.append(s.substring(7));						
		return result.toString();
	}

	// Always the firsts fields that are hidden too
	private int[] getIndexesPK() throws XavaException {		
		if (indexesPK == null) {
			indexesPK = new int[getKeyNames().size()];
			for (int i = 0; i < indexesPK.length; i++) {
				indexesPK[i]=i;
			}
		}
		return indexesPK;
	}

	private String getTableNameDB() throws XavaException {
		return getMapping().getTable();
	}

	public DataChunk nextChunk() throws RemoteException {		
		Collection tabCalculators = null;
		Map keyIndexes = null;
		List propertiesNames = null;
		try {		
			if (metaTab.hasCalculatedProperties()) {
				tabCalculators = getTabCalculators();
				keyIndexes = getKeyIndexes();
				propertiesNames = getPropertiesNames();
			}		
			Collection tabConverters = getTabConverters();
			if (tabConverters.isEmpty()) tabConverters = null;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(XavaResources.getString("tab_next_chunk_error"));
		}
		DataChunk tv = null; 		
		try {
			tv = getDataProvider(getComponentName()).nextChunk(tabProvider, modelName, propertiesNames, tabCalculators, keyIndexes, tabConverters);
		}
		catch (Exception ex) {
			cancelDataProvider(getComponentName());
			tv = getDataProvider(getComponentName()).nextChunk(tabProvider, modelName, propertiesNames, tabCalculators, keyIndexes, tabConverters);						getDataProvider(getComponentName()).nextChunk(tabProvider, modelName, propertiesNames, tabCalculators, keyIndexes, tabConverters);
		}
		tabProvider.setCurrent(tv.getIndexNext());				
		List data = tv.getData();
		int l = data.size();
		
		// valid-values conversion
		try {
			if (hasValidValuesProperties()) {
				for (int i = 0; i < l; i++) {
					data.set(i, doValidValuesConversions((Object[]) data.get(i)));
				}
			}
		}
		catch (XavaException ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(XavaResources.getString("tab_valid_values_error"));
		}
		return tv;		
	}

	private boolean hasValidValuesProperties() throws XavaException {
		if (!knowIfHasPropertiesWithValidValues) {
			_hasPropertiesWithValidValues = calculateIfHasValidValuesProperties();
			knowIfHasPropertiesWithValidValues = true;
		}
		return _hasPropertiesWithValidValues;
	}
	
	private boolean calculateIfHasValidValuesProperties() throws XavaException {
		Iterator it = getProperties().iterator();
		while (it.hasNext()) {
			MetaProperty metaProperty = (MetaProperty) it.next();
			if (metaProperty.hasValidValues()) {
				return true;
			}			
		}
		return false;		
	}

	private Object[] doValidValuesConversions(Object[] row) throws XavaException {
		int size = getPropertiesNames().size();
		for (int i = indexesPK.length; i < size; i++) {			
			String name = (String) getPropertiesNames().get(i);
			MetaProperty metaProperty = getMetaModel().getMetaProperty(name);			
			if (metaProperty.hasValidValues()) {
				if (row[i] instanceof Number) {
					Number value = (Number) row[i];
					int validValue = value.intValue();
					row[i] = metaProperty.getValidValue(validValue);					
				}
				else {
					row[i] = null;
				}
			}
		}
		return row;
	}

	/**
	 * @return Of TabCalculator
	 */
	private Collection getTabCalculators() throws XavaException {
		if (tabCalculators == null) {
			tabCalculators = new ArrayList();			
			Iterator it = metaTab.getMetaPropertiesHiddenCalculated().iterator();
			
			while (it.hasNext()) {
				MetaProperty metaProperty = (MetaProperty) it.next();
				int propertyIndex = getPropertyIndex(metaProperty.getQualifiedName());
				tabCalculators.add(new TabCalculator(metaProperty, propertyIndex));
			}			
			it = metaTab.getMetaPropertiesCalculated().iterator();
			while (it.hasNext()) {
				MetaProperty metaProperty = (MetaProperty) it.next();
				int propertyIndex = getPropertyIndex(metaProperty.getQualifiedName());
				tabCalculators.add(new TabCalculator(metaProperty, propertyIndex));
			}
		}
		return tabCalculators;
	}
	
	/**
	 * @return Of TabConverter
	 */
	private Collection getTabConverters() throws XavaException {
		if (tabConverters == null) {
			tabConverters = new ArrayList();			
			Iterator it = getPropertiesNames().iterator();
			int i=0;
			String table = getMapping().getTableToQualifyColumn(); 
			while (it.hasNext()) {
				String propertyName = (String) it.next();
				try {
					MetaProperty property = getMetaModel().getMetaProperty(propertyName);
					PropertyMapping propertyMapping = property.getMapping();
					if (propertyMapping != null) {
						IConverter converter = propertyMapping.getConverter();
						if (converter != null) {
							tabConverters.add(new TabConverter(propertyName, i,  converter));
						}
						else {							
							IMultipleConverter multipleConverter =  propertyMapping.getMultipleConverter();
							if (multipleConverter != null) {							
								tabConverters.add(new TabConverter(propertyName, i, multipleConverter, propertyMapping.getCmpFields(), getFields(), table));
							}
							else {
								// This is the case of a key without converter of type int or long
								// It's for suporting int and long as key and NUMERIC in database
								// without to declare an explicit converter
								if (property.isKey()) {
									if (property.getType().equals(int.class) || property.getType().equals(Integer.class)) {
										tabConverters.add(new TabConverter(propertyName, i,  IntegerNumberConverter.getInstance()));
									}
									else if (property.getType().equals(long.class) || property.getType().equals(Long.class)) {
										tabConverters.add(new TabConverter(propertyName, i,  LongNumberConverter.getInstance()));
									}
								}
							}	
						}
					}
				}
				catch (ElementNotFoundException ex) {
					// Thus we exclude the property out of mapping
				}
				i++;
			}
		}
		return tabConverters;
	}
	
	private int getPropertyIndex(String propertyName)
		throws XavaException {
		return getPropertiesNames().indexOf(propertyName);
	}

	/**
	 * @return Of <tt>MetaProperty</tt>.
	 */
	private Collection getProperties() throws XavaException {
		return metaTab.getMetaProperties();
	}

	/**
	 * @return Of <tt>String</tt>.
	 */
	private List getPropertiesNames() throws XavaException {
		if (propertiesNames == null) {
			propertiesNames = new ArrayList();
			propertiesNames.addAll(getKeyNames());
			propertiesNames.addAll(metaTab.getPropertiesNames());
			propertiesNames.addAll(metaTab.getHiddenPropertiesNames());
		}
		return propertiesNames;
	}
	
	private Collection getKeyNames() throws XavaException {		
		return getMetaModel().getAllKeyPropertiesNames();
	}
	
	private MetaModel getMetaModel() throws XavaException {
		if (metaModel == null) {
			metaModel = MetaModel.get(this.modelName);
		}
		return metaModel;
	}

	private Map getKeyIndexes()
		throws FinderException, XavaException, RemoteException {
		if (keyIndexes == null) {	
			Iterator it = getPropertiesNames().iterator();
			keyIndexes = new HashMap();
			int i = 0;
			while (it.hasNext()) {
				String propertyName = (String) it.next();
				if (getMetaModel().isKey(propertyName)) {					
					keyIndexes.put(propertyName, new Integer(i));
				}
				i++;
			}		
		}
		return keyIndexes;
	}

	private ModelMapping getMapping() throws XavaException {
		if (mapping == null) {
			mapping = getMetaModel().getMapping();
		}
		return mapping;
	}

	public String getComponentName() {
		return componentName;
	}

	public String getTabName() {
		return tabName;
	}

	public void setComponentName(String string) {
		componentName = string;
		metaModel = null;
		keyIndexes = null;
		selectBase = null;
	}

	public void setTabName(String string) {
		tabName = string;
		selectBase = null;
	}
	
	
	private static IEntityTabDataProvider getDataProvider(String componentName) throws RemoteException {
		try {
			String packageName = MetaComponent.get(componentName).getPackageNameWithSlashWithoutModel();			
			IEntityTabDataProvider dataProvider = (IEntityTabDataProvider) getDataProviders().get(packageName);
			if (dataProvider == null) {
				if (XavaPreferences.getInstance().isTabAsEJB()) { 
					Object ohome = BeansContext.get().lookup("ejb/"+packageName+"/EntityTab");
					EntityTabHome home = (EntityTabHome) PortableRemoteObject.narrow(ohome, EntityTabHome.class);
					dataProvider = home.create();
				}
				else {
					EntityTabDataProvider dp = new EntityTabDataProvider();																			
					dp.setComponentName(componentName);
					dataProvider = dp;
				}
				getDataProviders().put(packageName, dataProvider);				
			}		
			return dataProvider;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException("tab_remote_error");
		}						
	}
		
	private static Map getDataProviders() {
		if (dataProviders == null) {
			dataProviders = new HashMap();
		}
		return dataProviders;
	}
	
	private static void cancelDataProvider(String componentName) {
		try {
			String packageName = MetaComponent.get(componentName).getPackageNameWithSlashWithoutModel();
			getDataProviders().remove(packageName);			
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("warning_cancel_data_provider"),ex);
		}		
	}	
	
	public int getResultSize() throws RemoteException {
		if (!XavaPreferences.getInstance().isShowCountInList()) {
			return table.getRowCount(); 
		}		
		return getDataProvider(getComponentName()).getResultSize(tabProvider);
	}
	
	public Number getSum(String property) throws RemoteException { 
		String column = getMapping().getQualifiedColumn(property);		
		return getDataProvider(getComponentName()).getSum(tabProvider, column);
	}

	public void reset() throws RemoteException {
		tabProvider.reset();
	}

	public MetaTab getMetaTab() {
		return metaTab;
	}

	public void setMetaTab(MetaTab tab) {
		metaTab = tab;
	}

	public int getChunkSize() {
		return chunkSize;
	}
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	
}
