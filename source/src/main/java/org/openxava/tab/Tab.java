package org.openxava.tab;

import java.math.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.prefs.*;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.component.*;
import org.openxava.converters.*;
import org.openxava.filters.*;
import org.openxava.mapping.*;
import org.openxava.model.meta.*;
import org.openxava.tab.impl.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.WebEditors;
import org.openxava.web.Ids;

/**
 * Session object to work with tabular data. <p> 
 * 
 * @author Javier Paniza
 */

public class Tab implements java.io.Serializable {
	
	private static final long serialVersionUID = 1724100598886966704L;
	private static Log log = LogFactory.getLog(Tab.class);

	/**
	 * Prefix used for naming (in session) to the tab objects used for collections.
	 */
	public final static String COLLECTION_PREFIX = "xava_collectionTab_";
	public final static String TAB_RESETED_PREFIX = "xava.tab.reseted."; 
	
	private final static String PROPERTIES_NAMES = "propertiesNames";
	private final static String SUM_PROPERTIES_NAMES = "sumPropertiesNames"; 
	private final static String ROWS_HIDDEN = "rowsHidden";
	private final static String FILTER_VISIBLE = "filterVisible";
	private final static String PAGE_ROW_COUNT = "pageRowCount"; 
	private final static String COLUMN_WIDTH = "columnWidth."; 
	private final static String STARTS_COMPARATOR = "starts_comparator";
	private final static String CONTAINS_COMPARATOR = "contains_comparator";
	private final static String NOT_CONTAINS_COMPARATOR = "not_contains_comparator";
	private final static String YEAR_COMPARATOR = "year_comparator";
	private final static String MONTH_COMPARATOR = "month_comparator";
	private final static String YEAR_MONTH_COMPARATOR = "year_month_comparator";
	private final static String RANGE_COMPARATOR = "range_comparator"; 
	private final static int MAX_PAGE_ROW_COUNT = 20; 
	
	private int pageRowCount = XavaPreferences.getInstance().getPageRowCount();
	private int addColumnsPageRowCount = XavaPreferences.getInstance().getAddColumnsPageRowCount();
	private Object [] titleArguments;
	private List metaPropertiesNotCalculated;
	private ReferenceMapping referencesCollectionMapping;
	private Object[] baseConditionValuesForReference;
	private String baseCondition;
	private String baseConditionForReference;
	private MetaTab metaTab;
	private boolean descendingOrder = false;
	private boolean descendingOrder2 = false; 
	private String orderBy;	
	private String orderBy2; 
	private String condition;
	private String[] conditionComparators;
	private String[] conditionValues;
	private String[] conditionValuesTo;	// to the range: conditionValues like 'from' and conditionValuesTo like 'to'
	private String[] conditionComparatorsToWhere;
	private Object[] conditionValuesToWhere;
	private List metaProperties;
	private int page = 1;
	private int addColumnsPage = 1; 
	private int addColumnsLastPage; 
	private boolean notResetNextTime = false;
	private int initialIndex;	 			
	private transient IXTableModel tableModel;	
	private int [] selected;
	private String modelName;
	private String tabName;
	private transient HttpServletRequest request; 
	private boolean metaTabCloned = false;
	private boolean titleVisible = false;
	private List metaPropertiesKey;
	private boolean customize;
	private String titleId = null;	
	private boolean notResetPageNextTime;
	private boolean sortRemainingProperties;
	private boolean rowsHidden;
	private IFilter filter; 
	private Map styles;
	private View collectionView;
	private boolean filterVisible=XavaPreferences.getInstance().isShowFilterByDefaultInList();
	private Boolean customizeAllowed=null; 
	private Boolean resizeColumns=null; 
	private Map<String, Integer> columnWidths;
	private String [] filterConditionValues = null; 
	private boolean filtered = false;
	private Set<String> sumPropertiesNames;
	private Set<String> totalPropertiesNames; 
	private boolean propertiesChanged;
	private boolean ignorePageRowCount;
	private int additionalTotalsCount = -1;	
	
	private static int nextOid = 0; 
	private int oid = nextOid++; 
	private String tabObject;
	private boolean usesConverters; 
	
	
	
	public List<MetaProperty> getMetaProperties() {
		if (metaProperties == null) {
			if (Is.emptyString(getModelName())) return Collections.EMPTY_LIST;				
			metaProperties = getMetaTab().getMetaProperties();
		}				
		return metaProperties;
	}
	
	private List getRemainingPropertiesNames() throws XavaException {
		if (isSortRemainingProperties()) {
			List result = new ArrayList(getMetaTab().getRemainingPropertiesNames());
			Collections.sort(result);
			return result;
		}
		else {
			return getMetaTab().getRemainingPropertiesNames();
		}
	}
	
	public Collection getColumnsToAdd() throws XavaException { 
		List remainingPropertiesNames = getRemainingPropertiesNames();
		int begin = (getAddColumnsPage() - 1) * getAddColumnsPageRowCount();
		addColumnsLastPage = (remainingPropertiesNames.size() - 1) / getAddColumnsPageRowCount() + 1;
		int end = begin + getAddColumnsPageRowCount();
		if (end > remainingPropertiesNames.size()) end = remainingPropertiesNames.size();
		return remainingPropertiesNames.subList(begin, end);
	}
	
	public List getMetaPropertiesNotCalculated() throws XavaException {
		if (metaPropertiesNotCalculated == null) {
			metaPropertiesNotCalculated = new ArrayList();
			Iterator it = getMetaProperties().iterator();			
			while (it.hasNext()) {
				MetaProperty p = (MetaProperty) it.next();
				if (!p.isCalculated()) {
					metaPropertiesNotCalculated.add(p);
				}								
			}
		}
		return metaPropertiesNotCalculated;
	}
	
	public String getBaseCondition() throws XavaException {
		return baseCondition;
	}
			
	public void setBaseCondition(String condition) throws XavaException { 		
		if (Is.equal(this.baseCondition, condition)) return;
		this.baseCondition = condition;		
		this.condition = null;
	}
		
	/**
	 * This is an alternative to setModelName, and is used when this
	 * tab represent a collection of references.
	 */
	public void setReferencesCollection(String model, String collectionName) throws XavaException {
		MetaModel metaModel = MetaComponent.get(model).getMetaEntity(); 
		MetaReference ref = metaModel.getMetaCollection(collectionName).getMetaReference();
		setModelName(ref.getReferencedModelName());		
		referencesCollectionMapping = ref.getMetaModelReferenced().getMapping().getReferenceMapping(ref.getRole());
		createBaseConditionForReference();
		cloneMetaTab();
		getMetaTab().setPropertiesNames("*");
	}
	
	private void createBaseConditionForReference() throws XavaException {				
		Iterator it = referencesCollectionMapping.getDetails().iterator();
		StringBuffer condition = new StringBuffer();
		while (it.hasNext()) {
			ReferenceMappingDetail detail = (ReferenceMappingDetail) it.next();
			condition.append(detail.getColumn());
			condition.append(" = ?");
			if (it.hasNext()) condition.append(" and "); 
		}
		setBaseConditionForReference(condition.toString());
	}
	
	public void setBaseConditionValuesForReference(Map values) throws XavaException { 
		ReferenceMapping mapping = referencesCollectionMapping;
		Iterator it = mapping.getDetails().iterator();
		baseConditionValuesForReference = new Object[mapping.getDetails().size()];
		for (int i=0; i<baseConditionValuesForReference.length; i++) {
			ReferenceMappingDetail detalle = (ReferenceMappingDetail) it.next();
			baseConditionValuesForReference[i] = values.get(detalle.getReferencedModelProperty());
		}		
	}
		
	private void cloneMetaTab() throws XavaException { 
		if (metaTabCloned) return;		
		metaTab = getMetaTab().cloneMetaTab();		
		metaTabCloned = true;
	}
		
	private MetaTab getMetaTab() throws XavaException  {
		if (metaTab == null) {				
			try {				
				metaTab = MetaComponent.get(getModelName()).getMetaTab(getTabName());
			}
			catch (ElementNotFoundException ex) {
				if (getModelName().indexOf('.') >= 0 || // It's an aggregate
					getTabName().startsWith(COLLECTION_PREFIX) // Used for collection 
				) { 
					metaTab = MetaTab.createDefault(MetaModel.get(getModelName()));
					metaTab.setName(getTabName());			
				}				
				else throw ex;
			}
		}
		return metaTab;
	}
	
	/**
	 * The default properties are the initial ones and those to show when the user 
	 * reset his customizations.
	 * 
	 * @since 4.1
	 */
	public void setDefaultPropertiesNames(String properties) { 
		getMetaTab().setDefaultPropertiesNames(properties); 
	}
	
	/**
	 * @return Comma separate list. <p>
	 */
	public String getPropertiesNamesAsString() {
		StringBuffer names = new StringBuffer();
		for (Iterator it=getMetaProperties().iterator(); it.hasNext();) {
			MetaProperty p = (MetaProperty) it.next();
			names.append(p.getQualifiedName());
			if (it.hasNext()) {
				names.append(",");
			}
		}		
		return names.toString();
	}
	
	public int getColumnWidth(int columnIndex) {
		MetaProperty p = getMetaProperty(columnIndex);
		if (columnWidths == null) return defaultColumnWidth(p); 
		
		Integer result = columnWidths.get(p.getQualifiedName());		
		return result==null?defaultColumnWidth(p):result; 
	}
	
	private int defaultColumnWidth(MetaProperty p) { 
		if (getSumPropertiesSize() < 100) return -1;
		return friendViewGetDefaultColumnWidth(p);
	} 
	
	private int getSumPropertiesSize() { 
		int result = 0;
		for (MetaProperty eachProperty: getMetaProperties()) {
			result += eachProperty.getSize();
		}
		return result;
	}
		
	public static int friendViewGetDefaultColumnWidth(MetaProperty p) {
		return Math.min(p.getSize(), 20) * 7;
	}

	public void setColumnWidth(int columnIndex, int width) {
		if (isResizeColumns()) { 
			if (columnWidths == null) columnWidths = new HashMap<String, Integer>(); 
			columnWidths.put(getMetaProperty(columnIndex).getQualifiedName(), width);
			saveUserPreferences();
		}
	}
	
		
	public MetaProperty getMetaProperty(int i) {
		return (MetaProperty) getMetaProperties().get(i);
	}
	
	/**
	 *  
	 * @since 4.3
	 */
	public MetaProperty getMetaProperty(String qualifiedName) { 
		for (MetaProperty property: getMetaProperties()) {
			if (property.getQualifiedName().equals(qualifiedName)) return property;
		}
		throw new ElementNotFoundException("property_not_found_in_tab", qualifiedName, getTabName(), getModelName()); 
	}
	
	public void setMetaRowStyles(Collection styles) throws XavaException {
		// WARNING! This will change the row style for all tab with this MetaTab
		getMetaTab().setMetaRowStyles(styles);
	}
	
	/**
	 * A table model with on-demand data reading. <p>
	 * 
	 * Suitable for UI.
	 */
	public IXTableModel getTableModel() {		
		if (tableModel == null) {
			try {
				tableModel = createTableModel();			
			}
			catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				restoreDefaultProperties(); // if fails because user customized list uses properties no longer existing
				try {
					tableModel = createTableModel();
				}
				catch (Exception ex2) { 
					log.error(ex2.getMessage(), ex2);
					throw new SystemException(ex2);
				}
			}
		}
		return tableModel;
	}
	
	private IXTableModel createTableModel() throws Exception {
		IXTableModel tableModel = null;
		EntityTab tab = EntityTabFactory.create(getMetaTab()); 
		usesConverters = tab.usesConverters();
		tab.search(getCondition(), getKey());		
		tableModel = tab.getTable();
		
		// To load data, thus it's possible go directly to other page than first
		if (tableModel.getColumnCount() > 0) { // Maybe we have a table model without columns, rare but possible			
			int limit = getPage() * getPageRowCount();
			for (int row=0; row < limit; row += getPageRowCount() ) {
				tableModel.getValueAt(row,0);
			}
		}
		return tableModel;
	}
	
	/**
	 * A table model with load all data at once. <p>
	 * 
	 * Suitable for report generation (for example).
	 */
	public IXTableModel getAllDataTableModel() throws Exception {								
		EntityTab tab = EntityTabFactory.createAllData(getMetaTab());
		usesConverters = tab.usesConverters();
		tab.search(getCondition(), getKey());
		return tab.getTable();					
	}
	
	
	public void setTableModel(IXTableModel tableModel) {
		this.tableModel = tableModel;		
	}
	
	private String getCondition() {
		try {
			if (condition == null) { 			
				condition = createCondition();
			}		
			return condition;
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("tab_condition_warning"),ex);
			condition = "";
			conditionValues = null;
			conditionValuesTo = null;
			conditionComparators = null;
			return condition;
		}

	}
	
	private String createCondition() throws Exception {
		StringBuffer sb = new StringBuffer();
		boolean firstCondition = true;
		metaPropertiesKey = null;

		Collection<Object> valuesToWhere = new ArrayList<Object>();
		Collection<String> comparatorsToWhere = new ArrayList<String>();
		
		if (!Is.emptyString(getBaseConditionForReference())) { 
			sb.append(getBaseConditionForReference()); 
			firstCondition = false;						
		}
		else if (!Is.emptyString(getBaseCondition())) {					
			sb.append(getBaseCondition()); 
			firstCondition = false;			
		}		
		
		if (!(conditionValues == null || conditionValues.length == 0)) {
			MetaProperty pOrder = null;
			MetaProperty pOrder2 = null;
			for (int i = 0; i < this.conditionValues.length; i++) {
				MetaProperty p = (MetaProperty) getMetaPropertiesNotCalculated().get(i);
				if (orderBy != null && p.getQualifiedName().equals(orderBy)) {
					pOrder = p;
				}
				if (orderBy2 != null && p.getQualifiedName().equals(orderBy2)) {
					pOrder2 = p;
				}					
				if (Is.emptyString(this.conditionComparators[i])) {					
					this.conditionValues[i] = "";
					valuesToWhere.add("");
					comparatorsToWhere.add(this.conditionComparators[i]);
				}
				else if (!Is.empty(WebEditors.getEditorURLDescriptionsList(getTabName(), getModelName(), Ids.decorate(request, p.getQualifiedName()), i, getCollectionPrefix(), p.getQualifiedName(), p.getName()))){
					if (Is.empty(this.conditionValues[i])){
						comparatorsToWhere.add(this.conditionComparators[i]);
						valuesToWhere.add(this.conditionValues[i]);
						continue;
					}
					
					String reference = p.getQualifiedName().replace("." + p.getName(), "");
					List<CmpField> fields = (List<CmpField>) getMetaTab().getMetaModel().getMapping().getReferenceMapping(reference).getCmpFields();
					Collections.sort(fields, CMPFieldComparator.getInstance());
					
					ModelMapping mapping = getMetaTab().getMetaModel().getMapping();
					String keyValues = this.conditionValues[i].replaceAll("[\\[\\]]", "");
					StringTokenizer st = new StringTokenizer(keyValues, ".");					
					for (CmpField field : fields) {
						String property = field.getCmpPropertyName().substring(field.getCmpPropertyName().lastIndexOf('_') + 1);
						String value = st.nextToken();
						MetaProperty metaProperty = getMetaTab().getMetaModel().getMetaReference(reference).getMetaModelReferenced().getMetaProperty(property);
						valuesToWhere.add(metaProperty.parse(value.toString(), getLocale()));
						comparatorsToWhere.add(this.conditionComparators[i]);
						
						if (firstCondition) firstCondition = false;
						else sb.append(" and ");
						sb.append("${");
						sb.append(reference);
						sb.append('.');
						sb.append(property);
						sb.append("} ");
						sb.append(convertComparator(p, this.conditionComparators[i]));
						sb.append(" ? ");
						
						if (metaPropertiesKey == null) metaPropertiesKey = new ArrayList();
						metaPropertiesKey.add(metaProperty);						
					}					
				}
				else if (!Is.emptyString(this.conditionValues[i])) {					
					if (firstCondition) firstCondition = false;
					else sb.append(" and ");
					ModelMapping mapping = getMetaTab().getMetaModel().getMapping();										 
					sb.append(decorateConditionProperty(p, i));
					sb.append(' ');
					sb.append(convertComparator(p, this.conditionComparators[i]));
					sb.append(" ? ");
					if (metaPropertiesKey == null) metaPropertiesKey = new ArrayList();
					if (YEAR_MONTH_COMPARATOR.equals(this.conditionComparators[i]) ||
						RANGE_COMPARATOR.equals(this.conditionComparators[i]) ||
						Timestamp.class.equals(p.getType()) && "eq".equals(this.conditionComparators[i])) {
						metaPropertiesKey.add(null);
						metaPropertiesKey.add(null);
					}
					else {
						metaPropertiesKey.add(p);
					}
					
					String value = convertStringArgument(this.conditionValues[i].toString());
					try {				
						if (YEAR_COMPARATOR.equals(this.conditionComparators[i]) ||
							MONTH_COMPARATOR.equals(this.conditionComparators[i]) ||
							YEAR_MONTH_COMPARATOR.equals(this.conditionComparators[i])){
							valuesToWhere.add(value);
							comparatorsToWhere.add(this.conditionComparators[i]);
							continue;
						}
						Object v = p.parse(value.toString(), getLocale());
						if (v instanceof Timestamp && "eq".equals(this.conditionComparators[i])) {
							valuesToWhere.add(Dates.cloneWithoutTime((Timestamp) v));
							valuesToWhere.add(Dates.cloneWith2359((Timestamp) v));
							comparatorsToWhere.add(this.conditionComparators[i]);
							comparatorsToWhere.add(this.conditionComparators[i]);
						}
						else if (RANGE_COMPARATOR.equals(this.conditionComparators[i])){
							valuesToWhere.add(v);
							String valueTo = convertStringArgument(this.conditionValuesTo[i].toString());
							Object vTo = p.parse(valueTo.toString(), getLocale());
							valuesToWhere.add(vTo);
							comparatorsToWhere.add(this.conditionComparators[i]);
							comparatorsToWhere.add(this.conditionComparators[i]);
						}
						else {						
							valuesToWhere.add(v);
							comparatorsToWhere.add(this.conditionComparators[i]);
						}
					}
					catch (Exception ex) {
						log.warn(XavaResources.getString("tab_key_value_warning"),ex);
					}	
					
				}
				else{					
					comparatorsToWhere.add(this.conditionComparators[i]);
					valuesToWhere.add("");
				}
			}	// end for	
			if (pOrder != null) {				
				if (sb.length() == 0) sb.append(" 1=1 ");
				sb.append(" order by ");								
				sb.append("${");
				sb.append(pOrder.getQualifiedName()); 
				sb.append('}');
				if (descendingOrder) {
					sb.append(" desc");
				}
				if (pOrder2 != null) {
					sb.append(", "); 
					sb.append("${");  
					sb.append(pOrder2.getQualifiedName());
					sb.append('}');
					if (descendingOrder2) {
						sb.append(" desc");
					}								
				}
			}				
			else if (getMetaTab().hasDefaultOrder()) {
				if (sb.length() == 0) sb.append(" 1=1 ");
				sb.append(" order by ");								
				sb.append(getMetaTab().getDefaultOrder()); 
			}
		}
		else if (getMetaTab().hasDefaultOrder()) {
			if (sb.length() == 0) sb.append(" 1=1 ");
			sb.append(" order by ");						
			sb.append(getMetaTab().getDefaultOrder()); 
		}		
		
		// 
		if (valuesToWhere != null && valuesToWhere.size() > 0){
			this.conditionValuesToWhere = new Object[valuesToWhere.size()];
			this.conditionComparatorsToWhere = new String[comparatorsToWhere.size()];
			Iterator<Object> itValues = valuesToWhere.iterator();
			Iterator<String> itComparators = comparatorsToWhere.iterator();
			for (int i = 0; i < valuesToWhere.size(); i++){
				this.conditionComparatorsToWhere[i] = itComparators.next();
				this.conditionValuesToWhere[i] = itValues.next();
			}
		}

		return sb.toString();
	}
	
	private String decorateConditionProperty(MetaProperty metaProperty, int i) throws XavaException {
		String property = "${" + metaProperty.getQualifiedName() + "}";
		if ("year_comparator".equals(this.conditionComparators[i])) {
			return metaProperty.getMetaModel().getMapping().yearSQLFunction(property);
		}
		if ("month_comparator".equals(this.conditionComparators[i])) {
			return metaProperty.getMetaModel().getMapping().monthSQLFunction(property);
		}
		if ("year_month_comparator".equals(this.conditionComparators[i])) {
			ModelMapping mapping = metaProperty.getMetaModel().getMapping(); 
			return mapping.yearSQLFunction(property) + " = ? and " + mapping.monthSQLFunction(property);
		}						
		if (java.lang.String.class.equals(metaProperty.getType()) && XavaPreferences.getInstance().isIgnoreAccentsForStringArgumentsInConditions()) { 
			property = metaProperty.getMetaModel().getMetaComponent().getEntityMapping().translateSQLFunction(property);
		}
		if (java.lang.String.class.equals(metaProperty.getType()) && XavaPreferences.getInstance().isToUpperForStringArgumentsInConditions()) { 
			return "upper(" + property + ")"; 
		}
		return property;
	}


	private Object convertComparator(MetaProperty p, String comparator) throws XavaException {
		if (STARTS_COMPARATOR.equals(comparator)) return "like";
		
		if (CONTAINS_COMPARATOR.equals(comparator)) return "like";
		if (NOT_CONTAINS_COMPARATOR.equals(comparator)) return "not like";
		if (YEAR_COMPARATOR.equals(comparator)) return "=";
		if (MONTH_COMPARATOR.equals(comparator)) return "=";
		if (YEAR_MONTH_COMPARATOR.equals(comparator)) return "=";
		if (RANGE_COMPARATOR.equals(comparator)) return "between ? and ";
		if ("eq".equals(comparator)) {
			if (p.getType().equals(Timestamp.class)) {
				return "between ? and ";
			}
			else {
				return "=";
			}
		}
		if ("ne".equals(comparator)) return "<>";
		if ("ge".equals(comparator)) return ">=";
		if ("le".equals(comparator)) return "<=";
		if ("gt".equals(comparator)) return ">";
		if ("lt".equals(comparator)) return "<";
		return comparator;
	}
	
	private boolean isEmpty(Object value){
		if (value instanceof Number) return false;
		if (value instanceof BigDecimal) return false;
		return Is.empty(value);
	}

	private Object [] getKey() throws XavaException {
		if (conditionValuesToWhere == null || conditionValuesToWhere.length == 0) { 
			return filterKey(null);
		}
		Collection key = new ArrayList();
		
		for (int i = 0; i < this.conditionValuesToWhere.length; i++) {
			Object value = this.conditionValuesToWhere[i];
			if (!isEmpty(value)) {
								
				if (STARTS_COMPARATOR.equals(this.conditionComparatorsToWhere[i])) { 
					value = convertStringArgument(value.toString()) + "%";
					key.add(value);
				}
				else if (CONTAINS_COMPARATOR.equals(this.conditionComparatorsToWhere[i]) || 
						NOT_CONTAINS_COMPARATOR.equals(this.conditionComparatorsToWhere[i])) {
					value = "%" + convertStringArgument(value.toString()) + "%";
					key.add(value);
				} 
				else if (YEAR_COMPARATOR.equals(this.conditionComparatorsToWhere[i]) || MONTH_COMPARATOR.equals(this.conditionComparatorsToWhere[i])) {
					value = convertStringArgument(value.toString());
					try {					
						key.add(new Integer(value.toString()));
					}
					catch (Exception ex) {
						log.warn(XavaResources.getString("tab_key_value_warning"),ex);
						key.add(null);
					}										
				}
				else if (YEAR_MONTH_COMPARATOR.equals(this.conditionComparatorsToWhere[i])) { 
					try {				
						StringTokenizer st = new StringTokenizer(value.toString(), "/ ,:;");
						if (st.hasMoreTokens()) key.add(new Integer(st.nextToken()));
						else key.add(null);
						if (st.hasMoreTokens()) key.add(new Integer(st.nextToken()));
						else key.add(null);
					}
					catch (Exception ex) {
						log.warn(XavaResources.getString("tab_key_value_warning"),ex);
						key.add(null);
						key.add(null);
					}															
				}
				else key.add(value);
			}
		}		
		return filterKey(key.toArray());
	}
	
	private String convertStringArgument(String value) {
		if (XavaPreferences.getInstance().isIgnoreAccentsForStringArgumentsInConditions()){
			value = Strings.removeAccents(value);
		}
		if (XavaPreferences.getInstance().isToUpperForStringArgumentsInConditions()) {
			return value.trim().toUpperCase(); 
		}
		else {
			return value.trim();
		}
	}

	private Locale getLocale() {
		return XavaResources.getLocale(request);
	}

	private Object[] filterKey(Object[] key) throws XavaException {
		// first, for references
		if (baseConditionValuesForReference != null && baseConditionValuesForReference.length > 0) {
			if (key==null) key=new Object[0];
			Object [] newKey = new Object[baseConditionValuesForReference.length + key.length];
			int j = 0;
			for (int i = 0; i < baseConditionValuesForReference.length; i++) {
				newKey[j++] = baseConditionValuesForReference[i];
			}
			for (int i = 0; i < key.length; i++) {
				 newKey[j++] = key[i];
			}			
			key = newKey;
		}		
		
		// Filter of meta tabs
		int indexIncrement = 0;

		IFilter filter = getFilter();
		if (filter != null) {			
			if (filter instanceof IRequestFilter) {				
				((IRequestFilter) filter).setRequest(request);
			}
			int original = key == null?0:key.length;			
			key = (Object[]) filter.filter(key);
			indexIncrement = key == null?0:key.length - original;
		}		
		
		// To db format
		if (usesConverters && key != null && metaPropertiesKey != null) {			
			for (int i = indexIncrement; i < key.length; i++) {
				MetaProperty p = (MetaProperty) metaPropertiesKey.get(i - indexIncrement);
				// If has a converter, apply
												
				if (p != null && p.getMapping().hasConverter()) { 
					try {	
						key[i] = p.getMapping().getConverter().toDB(key[i]);
					}
					catch (ConversionException ex) {
						if (!java.util.Date.class.isAssignableFrom(p.getType())) {
							// because Dates are special, maybe a year or a month and this
							// is not convertible by a date converter 
							throw ex;
						}
					}
				}
			}									
		}
				
		return key;
	}

	public void reset() {
		if (notResetNextTime) {
			notResetNextTime = false;
			return;
		}		
		tableModel = null;				
		if (!notResetPageNextTime) { 
			notResetPageNextTime = false;
			initialIndex = 0; 		
			page = 1;	
		}
	}
	
	public int [] getSelected() {
		return selected;
	}
	
	public boolean hasSelected() {
		return selected != null && selected.length > 0;
	}
	
	/**
	 * Change all selected. <p>
	 *
	 * Postcondition: <tt>this.selected == values</tt>
	 */
	public void setAllSelected(int [] values) {
		this.selected = values;
	}
	
	public void deselectVisualizedRows() {
		List r = new ArrayList(); 				
		if (selected != null) {	
			for (int i=0; i<selected.length; i++) {
				int num = selected[i];
				if (num< getInitialIndex() || num>getFinalIndex()-1) {					
					r.add(new Integer(num));
				}
			}
		}

		Collections.sort(r);		
		selected = new int[r.size()];		
		for (int i=0; i<selected.length; i++) {			
			selected[i] = ((Integer) r.get(i)).intValue();			
		}				
	}

	/**
	 * Change the selected ones only within the current page range. <p>
	 * 
	 * Postcondition <tt>this.selected == values</tt> <b>is not fulfilled</b> 	 
	 */
	public void setSelected(int [] values) {
		List r = new ArrayList();
		if (selected != null) {	
			for (int i=0; i<selected.length; i++) {
				int num = selected[i];
				if (num< getInitialIndex() || num>getFinalIndex()-1) {					
					r.add(new Integer(num));
				}
			}
		}				
		for (int i=0; i<values.length; i++) {			
			r.add(new Integer(values[i]));			
		}		
		Collections.sort(r);		
		selected = new int[r.size()];		
		for (int i=0; i<selected.length; i++) {			
			selected[i] = ((Integer) r.get(i)).intValue();			
		}		
	}
	
	/**
	 * Same that {@link #setSelectec(int [] values)} but from String []. <p>
	 */
	public void setSelected(String [] values) {
		if (values == null) {
			if (selected != null && selected.length > 0) deselectedInCurrentPage();
			return;
		}
		int [] intValues = new int[values.length];
		for (int i=0; i<values.length; i++) {
			intValues[i] = Integer.parseInt(values[i]);
		}
		setSelected(intValues);
	}
	
	private void deselectedInCurrentPage() { 
		List rest = new ArrayList();
		for (int i = 0; i < selected.length; i++){
			int value = selected[i];
			if (value >= getInitialIndex() && value <= getFinalIndex()) continue;
			rest.add(value);
		}
		
		Collections.sort(rest);		
		selected = new int[rest.size()];		
		for (int i=0; i<selected.length; i++) {			
			selected[i] = ((Integer) rest.get(i)).intValue();
		}
	}
	
	public void deselectAll() {
		selected = new int[0];
	}
	
	public boolean isSelected(int row) {	
		if (selected == null || selected.length == 0) return false;
		return Arrays.binarySearch(selected, row) >= 0;
	}
	
	public int getInitialIndex() {		
		return initialIndex;
	}
	
	public int getFinalIndex() {		
		return initialIndex + getPageRowCount();
	}
	
	public boolean isLastPage() {
		return getFinalIndex() >= tableModel.getRowCount();
	}
	
	public void pageForward() {		
		goPage(page+1);		
	}

	public boolean isNotResetNextTime() {
		return notResetNextTime;
	}

	public void setNotResetNextTime(boolean b) {
		notResetNextTime = b;
	}
	
	public int getLastPage() {		
		return (tableModel.getRowCount() - 1) / getPageRowCount() + 1;
	}
	
	public int getAddColumnsLastPage() {
		return addColumnsLastPage; 		
	}
	
	public int getAddColumnsPage() { 
		return addColumnsPage;
	}
	
	public void goAddColumnsPage(int page) {
		if (page < 1) addColumnsPage = 1;
		else if (page > getAddColumnsLastPage()) addColumnsPage = getAddColumnsLastPage();
		else addColumnsPage = page;
	}
	
	public void pageBack() {
		if (page < 1) page = 1;		
		goPage(page-1);		
	}


	/**
	 * 1 is the first page
	 * @param page
	 */
	public void goPage(int page) {
		this.page = page;
		recalculateIndices();
		notResetPageNextTime = true; 
	}
	
	private void recalculateIndices() {
		initialIndex = (page - 1) * getPageRowCount();		
	}


	public int getPage() {
		return page;
	}
	
	public int getTotalSize() {
		try {
			return getTableModel().getTotalSize(); 			
		}
		catch (Throwable ex) {
			log.warn(XavaResources.getString("tab_size_warning"),ex);
			return -1;
		}
	}
	
	private void setConditionValuesImpl(String [] values) throws XavaException {
		if (Arrays.equals(this.conditionValues, values)) return;
		if (getMetaPropertiesNotCalculated().size() != values.length) return; // to avoid problems on changing module
		this.conditionValues = values;
		condition = null;
	}

	private void setConditionValuesToImpl(String [] values) throws XavaException {
		if (Arrays.equals(this.conditionValuesTo, values)) return;
		if (getMetaPropertiesNotCalculated().size() != values.length) return; // to avoid problems on changing module
		this.conditionValuesTo = values;				
		condition = null;
	}
	
	private void setConditionComparatorsImpl(String [] comparators) throws XavaException {  
		if (Arrays.equals(this.conditionComparators, comparators)) return;		
		if (getMetaPropertiesNotCalculated().size() != comparators.length) return;		
		this.conditionComparators = comparators;
		condition = null;						
	}
		
	public String [] getConditionValues() {
		setFilteredConditionValues(); 
		return conditionValues; 
	}
	
	public String [] getConditionValuesTo() {
		setFilteredConditionValues(); 
		return conditionValuesTo; 
	}
		
	public String [] getConditionComparators() {
		return conditionComparators;
	}
	
	public void orderBy(String property) {
		if (Is.equal(property, orderBy)) {
			descendingOrder = !descendingOrder;
		}
		else {
			if (Is.equal(property, orderBy2)) {
				boolean originalDescendingOrder = descendingOrder;
				descendingOrder = descendingOrder2;
				descendingOrder2 = originalDescendingOrder; 			
			}
			else {
				descendingOrder2 = descendingOrder; 			
				descendingOrder = false;
			}
			orderBy2 = orderBy; 
			orderBy = property;
		}
		condition = null;		
	}
	
	public String getOrderBy() {
		return orderBy;
	}
	
	public boolean isOrderAscending(String name) {
		return !descendingOrder && Is.equal(name, orderBy);
	}
	
	public boolean isOrderDescending(String name) {
		return descendingOrder && Is.equal(name, orderBy);
	}
	
	public boolean isOrderAscending2(String name) { 
		return !descendingOrder2 && Is.equal(name, orderBy2);
	}
	
	public boolean isOrderDescending2(String name) { 
		return descendingOrder2 && Is.equal(name, orderBy2);
	}
		
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String newModelName) {				
		if (Is.equal(modelName, newModelName)) return;
		modelName = newModelName;
		tabName = null; 
		reinitState();
		loadUserPreferences();
	}

	private void reinitState() {
		descendingOrder = false;
		orderBy = null;	
		condition = null;		
		conditionComparators = null;
		conditionValues = null;
		conditionValuesTo = null;
		metaProperties = null;
		metaPropertiesNotCalculated = null;		
		notResetNextTime = false;		 	 			
		tableModel  = null;		
		selected  = null;
		metaTab = null;
		metaTabCloned = false; 
	}
	
	public String getTabName() {		
		return tabName; 		
	}

	public void setTabName(String newTabName) {		
		if (Is.equal(tabName, newTabName)) return;
		tabName = newTabName;
		reinitState();		
		loadUserPreferences();
	}

	public synchronized void setRequest(HttpServletRequest request) {		
		this.request = request;
		String collectionPrefix = getCollectionPrefix();
		setConditionComparators(collectionPrefix);
		setConditionValues(collectionPrefix);
		setConditionValuesTo(collectionPrefix);
	}

	private String getCollectionPrefix() {
		String tabObject = request.getParameter("tabObject");
		if (tabObject == null) tabObject = this.tabObject; 
		return tabObject == null?"":tabObject + "_";
	}
	
	public void setTabObject(String tabObject) { 
		this.tabObject = tabObject;
	}

	private void setConditionComparators(String collectionPrefix) { 		
		setConditionComparatorsImpl(
			getConditionFilterParameters(collectionPrefix + "conditionComparator.")
		);
	}
	
	private void setConditionValues(String collectionPrefix) {
		setConditionValuesImpl(
			getConditionFilterParameters(collectionPrefix + "conditionValue.")
		);
	}
	
	private void setConditionValuesTo(String collectionPrefix) { 		
		setConditionValuesToImpl(
			getConditionFilterParameters(collectionPrefix + "conditionValueTo.")
		);
	}

	private String[] getConditionFilterParameters(String prefix) {
		String conditionComparator = request.getParameter(prefix + "0");
		Collection conditionComparators = new ArrayList();
		for (int i=1; conditionComparator != null; i++) {
			conditionComparators.add(conditionComparator);
			conditionComparator = request.getParameter(prefix + i);
		}
		String [] result = new String[conditionComparators.size()];
		conditionComparators.toArray(result);
		return result;
	}
	
	private Preferences getPreferences() throws BackingStoreException {
		String application = getRequest() == null ? "" : getRequest().getParameter("application");
		if (Is.empty(application)) application = getCollectionView().getRequest() == null ? "" : getCollectionView().getRequest().getParameter("application");
		String module = getRequest() == null ? "" : getRequest().getParameter("module");
		if (Is.empty(module)) module = getCollectionView().getRequest() == null ? "" : getCollectionView().getRequest().getParameter("module");
		
		String nodeName = "tab." + application + "." + module + "." + getMetaTab().getMetaModel().getName() + "." + getTabName() + ".";
		if (nodeName.length() > Preferences.MAX_NAME_LENGTH) {
			nodeName = "tab." + (application + "." + module + "." + getMetaTab().getMetaModel().getName() + "." + getTabName() + ".").hashCode(); 		
		}
		return Users.getCurrentPreferences().node(nodeName);
	}
	
	
	public HttpServletRequest getRequest() {
		return request;
	}
	
	public boolean isTitleVisible() {
		return titleVisible;
	}

	public void setTitleVisible(boolean b) {
		titleVisible = b;
	}
	
	public void setTitleArgument(Object v1) {
		this.titleArguments = new Object [] { v1 };
	}
	public void setTitleArguments(Object v1, Object v2) {
		this.titleArguments = new Object [] { v1, v2 };
	}
	public void setTitleArguments(Object v1, Object v2, Object v3) {
		this.titleArguments = new Object [] { v1, v2, v3 };
	}
	public void setTitleArguments(Object v1, Object v2, Object v3, Object v4) {
		this.titleArguments = new Object [] { v1, v2, v3, v4 };
	}
	public void setTitleArguments(Object [] valores) {
		this.titleArguments = valores;
	}
	
	public String getTitle() throws XavaException {
		if (getCollectionView() != null) return getCollectionTitle(); 
		String modelName = getModelName();
		String tabName = getTabName();
		Locale locale = XavaResources.getLocale(request);
		String title = titleId==null?getTitleI18n(locale, modelName, tabName):Labels.get(titleId, locale);
		if (title != null) return putTitleArguments(locale, title);		
		String modelLabel = MetaModel.get(modelName).getLabel(locale); 
		return XavaResources.getString(request, "report_title", modelLabel);					
	}
	
	private String getCollectionTitle() throws XavaException {		
		Locale locale = XavaResources.getLocale(request);
		View parentView = getCollectionView().getParent();
		MetaModel metaModel = parentView.getMetaModel(); 
		String modelLabel = metaModel.getLabel(locale);		
		String collectionLabel = metaModel.getMetaCollection(getCollectionView().getMemberName()).getLabel(locale);
		Map membersNames = parentView.getMembersNames();
		StringBuffer id = new StringBuffer();		
		if (membersNames.containsKey("id")) {
			id.append(parentView.getValue("id"));
		}
		else if (membersNames.containsKey("number")) {
			id.append(parentView.getValue("number"));
		} 
		else if (membersNames.containsKey("codigo")) {
			id.append(parentView.getValue("codigo"));
		}
		else if (membersNames.containsKey("numero")) {
			id.append(parentView.getValue("numero"));
		}
		
		if (id.length() > 0) {
			id.append(" - ");
		}

		if (membersNames.containsKey("name")) {
			id.append(parentView.getValue("name"));
		}
		else if (membersNames.containsKey("description")) {
			id.append(parentView.getValue("description"));
		}		
		else if (membersNames.containsKey("nombre")) {
			id.append(parentView.getValue("nombre"));
		}
		else if (membersNames.containsKey("descripcion")) {
			id.append(parentView.getValue("descripcion"));
		}
		
		if (id.length() == 0) {
			Map key = parentView.getKeyValuesWithValue();
			for (Iterator it = key.values().iterator(); it.hasNext(); ) {
				id.append(it.next());
				if (it.hasNext()) id.append(" - ");
			}
		}
		
		return XavaResources.getString(locale, "collection_report_title", collectionLabel, modelLabel, id.toString());
	}

	private String putTitleArguments(Locale locale, String title) {
		if (titleArguments == null || titleArguments.length == 0) return title;
		MessageFormat formateador = new MessageFormat("");
		formateador.setLocale(locale);
		formateador.applyPattern(title);
		return formateador.format(titleArguments);		
	}

	public static String getTitleI18n(Locale locale, String modelName, String tabName) throws XavaException {
		return MetaTab.getTitleI18n(locale, modelName, tabName);
	}
		
	public String getBaseConditionForReference() {
		return baseConditionForReference;
	}
	public void setBaseConditionForReference(String baseConditionForReference) {
		this.baseConditionForReference = baseConditionForReference;
	}

	public void addProperty(String propertyName) throws XavaException {
		cloneMetaTab();
		getMetaTab().addProperty(propertyName);
		resetAfterChangeProperties();
		saveUserPreferences();
	}
	
	public void addProperty(int index, String propertyName) throws XavaException {
		cloneMetaTab();
		getMetaTab().addProperty(index, propertyName);
		resetAfterChangeProperties();
		saveUserPreferences();
	}
	
	public void addProperties(Collection properties) throws XavaException {
		cloneMetaTab();
		for (Iterator it=properties.iterator(); it.hasNext();) {
			getMetaTab().addProperty((String)it.next());
		}		
		resetAfterChangeProperties();
		saveUserPreferences();
	}
	
	

	public void removeProperty(String propertyName) throws XavaException {
		cloneMetaTab();
		getMetaTab().removeProperty(propertyName);
		resetAfterChangeProperties();
		saveUserPreferences();
	}
	
	public void removeProperty(int index) throws XavaException {
		cloneMetaTab();
		getMetaTab().removeProperty(index);
		resetAfterChangeProperties();
		saveUserPreferences();
	}	
	
	public void movePropertyToRight(int index) throws XavaException {		
		cloneMetaTab();		
		getMetaTab().movePropertyToRight(index);		
		resetAfterChangeProperties();
		saveUserPreferences();
	}
	
	public void movePropertyToLeft(int index) throws XavaException {
		cloneMetaTab();
		getMetaTab().movePropertyToLeft(index);
		resetAfterChangeProperties();
		saveUserPreferences();
	}
		
	public void clearProperties() throws XavaException {	
		cloneMetaTab();
		getMetaTab().clearProperties();		
		resetAfterChangeProperties();
		saveUserPreferences();
	}
	
	public void restoreDefaultProperties() throws XavaException { 	
		cloneMetaTab();
		getMetaTab().restoreDefaultProperties();		
		resetAfterChangeProperties();
		sumPropertiesNames = null;		
		removeUserPreferences();
	}
	
	
	private void resetAfterChangeProperties() {
		reset(); 
		metaProperties = null;
		metaPropertiesNotCalculated = null;		
		metaPropertiesKey = null;
		conditionValues = null;
		conditionValuesTo = null;
		orderBy = null;			
		condition = null;
		conditionComparators = null;	
		propertiesChanged = true;
		additionalTotalsCount = -1; 
		totalPropertiesNames = null; 
	}
	
	/** @since 4m5 */
	public boolean isCustomizeAllowed() { 
		if (customizeAllowed == null) return XavaPreferences.getInstance().isCustomizeList();
		return customizeAllowed;
	}
	/** @since 4m5 */
	public void setCustomizeAllowed(boolean customizeAllowed) { 
		this.customizeAllowed = customizeAllowed;
	}
	
	/** @since 4m5 */
	public boolean isResizeColumns() { 
		if (resizeColumns == null) 	return XavaPreferences.getInstance().isResizeColumns(); 
		return resizeColumns;
	}
	/** @since 4m5 */
	public void setResizeColumns(boolean resizeColumns) { 
		this.resizeColumns = resizeColumns;
	}
	
	 	
	public boolean isCustomize() {
		return customize;
	}
	public void setCustomize(boolean customize) {
		this.customize = customize;
	}
	
	public String getTitleId() {
		return titleId;
	}
	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}
	
	private void loadUserPreferences() { 
		try { 
			Preferences preferences = getPreferences();			

			String propertiesNames = preferences.get(PROPERTIES_NAMES, null);
			if (propertiesNames != null) {				
				propertiesNames = removeNonexistentProperties(propertiesNames); // To remove the properties of old versions of the entities 
				setPropertiesNames(propertiesNames);
			}			
			sumPropertiesNames = Strings.toSetNullByPass(preferences.get(SUM_PROPERTIES_NAMES, null));
			rowsHidden = preferences.getBoolean(ROWS_HIDDEN, rowsHidden);			
			filterVisible = preferences.getBoolean(FILTER_VISIBLE, filterVisible);
			pageRowCount = Math.min(preferences.getInt(PAGE_ROW_COUNT, pageRowCount), 20); 			
			if (columnWidths != null) columnWidths.clear();
			for (MetaProperty property: getMetaProperties()) {
				int value = preferences.getInt(COLUMN_WIDTH + property.getQualifiedName(), -1);
				if (value >= 0) {
					if (columnWidths == null) columnWidths = new HashMap<String, Integer>();
					columnWidths.put(property.getQualifiedName(), value);
				}
			}
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("warning_load_preferences_tab"),ex);
		}
	}

	private String removeNonexistentProperties(String properties) {
		if (propertiesExists(properties)) return properties; // It is the usual case, so we save the below code most times
		StringBuffer sb = new StringBuffer();
		for (String property: properties.split(",")) {
			if (propertyExists(property)) {
				if (sb.length() > 0) sb.append(',');
				sb.append(property);
			}
		}
		return sb.toString();
	}
	
	private boolean propertiesExists(String properties) { 
		for (String property: properties.split(",")) {
			if (!propertyExists(property)) return false; 
		}
		return true;
	}
	
	private boolean propertyExists(String property) { 
		try {
			getMetaTab().getMetaModel().getMetaProperty(property);
			return true;
		}
		catch (ElementNotFoundException ex) {
			return false;
		}
	}

	private void saveUserPreferences() { 		
		try { 
			Preferences preferences = getPreferences();			

			if (propertiesChanged) { 
				preferences.put(PROPERTIES_NAMES, getPropertiesNamesAsString());
				propertiesChanged = false;
			}
			preferences.put(SUM_PROPERTIES_NAMES, Strings.toString(getSumPropertiesNames()));
			preferences.putBoolean(ROWS_HIDDEN, rowsHidden);
			preferences.putBoolean(FILTER_VISIBLE, filterVisible);
			preferences.putInt(PAGE_ROW_COUNT, pageRowCount); 
			if (columnWidths != null) {
				for (Map.Entry<String, Integer> columnWidth: columnWidths.entrySet()) {
					preferences.putInt(
						COLUMN_WIDTH + columnWidth.getKey(),
						columnWidth.getValue()
					);
				}
			}
			preferences.flush();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("warning_save_preferences_tab"),ex);
		}	
	}
	
	private void removeUserPreferences() { 		
		try { 
			Preferences preferences = getPreferences();			
			preferences.clear();
			preferences.flush();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("warning_save_preferences_tab"),ex);
		}		
	}
				
	/**
	 * The CSS style associated to the specified row. <p>
	 * 
	 * @return A string with the CSS style suitable to use in a 'class' attribute in HTML. 
	 */
	public String getStyle(int row) { 
		try {
			if (styles != null && !styles.isEmpty()) {
				String result = (String) styles.get(new Integer(row));
				if (result != null) return result;
			}
			if (!getMetaTab().hasRowStyles()) return null;
			for (Iterator it = getMetaTab().getMetaRowStyles().iterator(); it.hasNext();) {
				MetaRowStyle rowStyle = (MetaRowStyle) it.next();
				String result = getStyle(rowStyle, row);
				if (result != null) return result;
			}
			return null;
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("warning_row_style"),ex);
			return null;
		}
	}

	/**
	 * Set the CSS style associated to the specified row. <p>
	 * 
	 * @param  row   Row number affected by this style.
	 * @param  style  A string with the CSS style suitable to use in a 'class' attribute in HTML. 
	 */	
	public void setStyle(int row, String style) {
		if (styles == null) styles = new HashMap();
		styles.put(new Integer(row), style);
	}
	
	/**
	 * Clear the effect of all calls to {@link #setStyle(int, String)}
	 *
	 */
	public void clearStyle() {
		if (styles != null) styles.clear();
	}
	
	private String getStyle(MetaRowStyle rowStyle, int row) {
		try {
			int column = getMetaTab().getPropertiesNames().indexOf(rowStyle.getProperty());
			if (column < 0) return null;			
			Object value = getTableModel().getValueAt(row, column);			
			if (Is.equalAsStringIgnoreCase(value, rowStyle.getValue())) {
				return rowStyle.getStyle();
			}
			return null;
		}
		catch (Exception ex) {			
			log.warn(XavaResources.getString("warning_row_style"));
			return null;
		}
	}
		
	public boolean isSortRemainingProperties() {
		return sortRemainingProperties;
	}
	public void setSortRemainingProperties(boolean sortRemainingProperties) {
		this.sortRemainingProperties = sortRemainingProperties;
	}
	
	/**
	 * An array with the keys (in <code>Map</code> format) of the selected
	 * rows. <p> 
	 * 
	 * @return Never null
	 */
	public Map [] getSelectedKeys() { 
		if (selected == null) return new Map[0];
		Map [] keys = new Map[selected.length];
		for (int i = 0; i < keys.length; i++) {
			try {
				keys[i] = (Map) getTableModel().getObjectAt(selected[i]); 
			}
			catch (Exception ex) {
				keys[i] = Collections.EMPTY_MAP;
				log.warn(XavaResources.getString("tab_row_key_warning", new Integer(i)),ex);
			}
		}
		return keys;
	}

	/**
	 * An array with the keys (in <code>Map</code> format) of the all
	 * rows. <p> 
	 * 
	 * @return Never null
	 */	
	public Map [] getAllKeys() { 
		Collection allKeys = new ArrayList();
		for (int i = 0; i < getTableModel().getRowCount(); i++) { 					
			try {
				allKeys.add(getTableModel().getObjectAt(i)); 				
			}
			catch (Exception ex) {
				allKeys.add(Collections.EMPTY_MAP);
				log.warn(XavaResources.getString("tab_row_key_warning", new Integer(i)),ex);
			}
		}
		Map [] keys = new Map[allKeys.size()];
		allKeys.toArray(keys);
		return keys;		
	}
	
	public void hideRows() {		
		this.rowsHidden = true;
		saveUserPreferences();
	}	
	
	public void showRows() {
		this.rowsHidden = false;
		saveUserPreferences();
	}
	
	public boolean isRowsHidden() {
		return rowsHidden;
	}
	
	/**
	 * If you like show or hide rows is better calling to {@link #showRows} and {@link #hideRows}. <p>	 
	 */
	public void setRowsHidden(boolean rowsHidden) {
		this.rowsHidden = rowsHidden;
	}

	public int getPageRowCount() {
		if (ignorePageRowCount) return MAX_PAGE_ROW_COUNT; 
		return pageRowCount;
	}	

	public void setPageRowCount(int pageRowCount) {		
		this.pageRowCount = pageRowCount;
		saveUserPreferences(); 
	}

	/**
	 * Filter used currently by this tab. <p>
	 * 
	 * By default the filter is the defined one in the &lt;tab/&gt; of the 
	 * component or using the filter attribute of @Tab annotation.<br>
	 * But, it's possible to assign in runtime other filter using the
	 * {@link #setFilter(IFilter)} method.<br>  
	 * 
	 * @return Can be null.
	 */
	public IFilter getFilter() throws XavaException { 
		if (filter != null) return filter;
		if (getMetaTab().hasFilter()) {
			return getMetaTab().getMetaFilter().getFilter();
		}		
		return null;
	}

	/**
	 * Sets the filter for this tab in runtime. <p>
	 * 
	 * This override the filter defined using &lt;tab&gt; or @Tab.<br>
	 */
	public void setFilter(IFilter filter) { 
		this.filter = filter;
	}
	
	
	/**
	 * Set the properties to be displayed by this <code>Tab</code> in runtime. <p>
	 * 
	 * This override the properties defined using &lt;tab&gt; or @Tab.<br>
	 */
	public void setPropertiesNames(String propertiesNames) throws XavaException { 
		cloneMetaTab();
		getMetaTab().setPropertiesNames(propertiesNames);		
		resetAfterChangeProperties();				
	}
	
	/**
	 * Set the default order for this <code>Tab</code> in runtime. <p>
	 * 
	 * This override the default order defined using &lt;tab&gt; or @Tab.<br>
	 */	
	public void setDefaultOrder(String defaultOrder) throws XavaException {  
		cloneMetaTab();
		getMetaTab().setDefaultOrder(defaultOrder);		
		resetAfterChangeProperties();				
	}

	/**
	 * If this tab represents a collection the collection view of that collection. <p>
	 * 
	 * If this tab does not represents a collection collectionView will be null.<br>
	 */
	public View getCollectionView() {
		return collectionView;
	}

	/**
	 * If this tab represents a collection the collection view of that collection. <p>
	 * 
	 * If this tab does not represents a collection collectionView will be null.<br>
	 */	
	public void setCollectionView(View collectionView) {
		this.collectionView = collectionView;
		this.filterVisible = XavaPreferences.getInstance().isShowFilterByDefaultInCollections();
	}
			
	public boolean isFilterVisible() {
		return filterVisible;
	}
	public void setFilterVisible(boolean filterVisible) { 
		this.filterVisible = filterVisible;
		saveUserPreferences();
	}
	
	
	public String toString() {
		return "Tab:" + oid;
	}

	public int getAddColumnsPageRowCount() {
		return addColumnsPageRowCount;
	}

	public void setAddColumnsPageRowCount(int addColumnsPageRowCount) {
		this.addColumnsPageRowCount = addColumnsPageRowCount;
	}

	public void clearSelected() {
		selected = null;
	}
	
	/** @since 4m6 */
	public void setConditionValue(String property, Object value) {
		List metaPropertiesNC = getMetaPropertiesNotCalculated();
		int size = metaPropertiesNC.size();
		if (size > 0) {
			filterConditionValues = new String[size];
			for (int i = 0; i < size; i++) {
				filterConditionValues[i] = "";
				if (((MetaProperty) metaPropertiesNC.get(i)).getName().equals(property)) {
					filterConditionValues[i] = value==null?null:value.toString(); // A little rundimentary, maybe would be better to use a formatter
					filtered = true;
				}
			}
		}	
	}
	
	private void setFilteredConditionValues() {
		if (filtered && filterConditionValues != null) {
			filtered = false;
			if (conditionValues == null) {
				conditionValues = new String[filterConditionValues.length];
			}
			if (conditionComparators == null) {
				conditionComparators = new String[filterConditionValues.length];
			}
			if (conditionValuesTo == null){
				conditionValuesTo = new String[filterConditionValues.length];
			}
			int size = (filterConditionValues.length < conditionValues.length)?
					filterConditionValues.length:conditionValues.length;
			for (int i = 0; i < size; i++) {
				if (conditionValues[i] == null) {
					conditionValues[i] = "";
				}
				if (conditionComparators[i] == null) {
					conditionComparators[i] = "";
				}
				if (conditionValuesTo[i] == null){
					conditionValuesTo[i] = "";
				}
				
				if (!filterConditionValues[i].equals("")) {
					conditionValues[i] = filterConditionValues[i];
					conditionValuesTo[i] = "";
					conditionComparators[i] = "eq";
				}
			}
			
		}
		
	}
	
	/**
	 * @since 4.1
	 */
	public boolean hasTotal(int column) {
		if (column >= getMetaProperties().size()) return false; 
		MetaProperty p = getMetaProperty(column);
		if (getTotalProperties().containsKey(p.getQualifiedName())) return true;
		if (!getSumPropertiesNames().contains(p.getQualifiedName())) return false;
		if (p.isCalculated()) {
			log.warn(XavaResources.getString("sum_not_for_calculated_properties", p.getQualifiedName(), p.getMetaModel().getName())); 
			return false;
		}
		if (!p.isNumber() || p.hasValidValues()) { 
			log.warn(XavaResources.getString("sum_not_for_not_numeric_properties", p.getQualifiedName(), p.getMetaModel().getName())); 
			return false;
		}		
		return true;
	}
	
	private Map<String, List<String>> getTotalProperties() {  
		if (getCollectionView() == null) return Collections.EMPTY_MAP;
		return getCollectionView().getTotalProperties();
	}
	
	/**
	 * @since 4.3
	 */
	public boolean hasTotal(int row, int column) { 
		if (row == 0) return hasTotal(column);		
		MetaProperty p = getMetaProperty(column);
		if (getTotalProperties().containsKey(p.getQualifiedName())) {
			return row < getTotalProperties().get(p.getQualifiedName()).size(); 
		}
		return false;
	}
		
	
	/**
	 * Add total property. <p>
	 * 
	 * It was created in v4.1 with the name addTotalProperty()
	 * 
	 * @since 4.3
	 */
	public void addSumProperty(String property) { 	
		getSumPropertiesNames().add(property);
		totalPropertiesNames = null;
		saveUserPreferences();
	}
	
	/**
	 * Remove total property. <p>
	 * 
	 * It was created in v4.1 with the name removeTotalProperty()
	 * 
	 * @since 4.1
	 */	
	public void removeSumProperty(String property) { 
		getSumPropertiesNames().remove(property);
		totalPropertiesNames = null;
		saveUserPreferences();
	}
	
	/**
	 * 
	 * @since 4.3
	 */
	public Set<String> getSumPropertiesNames() {   
		if (sumPropertiesNames == null) {			
			sumPropertiesNames = new HashSet(getMetaTab().getSumPropertiesNames());			
		}		
		return sumPropertiesNames;
	}

	/**
	 * 
	 * @since 4.1
	 */	
	public Collection<String> getTotalPropertiesNames() {
		if (totalPropertiesNames == null) {
			totalPropertiesNames = new HashSet<String>();
			int count = getMetaProperties().size();
			for (int i=0; i<count; i++) {
				if (hasTotal(i)) {
					totalPropertiesNames.add(getMetaProperties().get(i).getQualifiedName());
				}
			}
		}
		return totalPropertiesNames;		
	}
	
	/**
	 * @since 4.1
	 */
	public boolean isTotalCapable(int column) {  
		MetaProperty p = getMetaProperty(column);			
		return !p.isCalculated() && p.isNumber() && !p.hasValidValues();
	}
	
	/**
	 * @since 4.3
	 */
	public boolean isFixedTotal(int column) { 
		MetaProperty p = getMetaProperty(column);
		return getTotalProperties().containsKey(p.getQualifiedName());
	}
	
	/**
	 * @since 4.1
	 */
	public Object getTotal(int column) { 
		return getTotal(getMetaProperty(column).getQualifiedName());
	}
	
	/**
	 * @since 4.3
	 */
	public Object getTotal(int row, int column) {  
		return getTotal(getMetaProperty(column).getQualifiedName(), row);
	}
	
	/**
	 * @since 4.3
	 */
	public String getTotalLabel(int row, int column) { 
		try {
			String columnProperty = getMetaProperty(column).getQualifiedName();			
			if (!getTotalProperties().containsKey(columnProperty)) return ""; // When it is a sum property
			String totalProperty = removeTotalPropertyPrefix(getTotalProperties().get(columnProperty).get(row)); 
			return getCollectionView().getParent().getMetaModel().getMetaProperty(totalProperty).getLabel();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("total_label_warning"), ex);
			return "";
		}
	}		
	
	/**
	 * @since 4.1
	 */	
	public Object getTotal(String qualifiedPropertyName) {   
		return getTotal(qualifiedPropertyName, 0);
	}
	
	
	private Object getTotal(String qualifiedPropertyName, int index) { 
		try {
			if (getCollectionView() != null && 					
				getCollectionView().getTotalProperties().containsKey(qualifiedPropertyName)) 	
			{ 
				return getCollectionView().getCollectionTotal(qualifiedPropertyName, index); 
			}
			return getTableModel().getSum(qualifiedPropertyName);
		}
		catch (Throwable ex) {
			log.warn(XavaResources.getString("total_problem"),ex); 
			return null;
		} 
	}
	
	
	
	
	/**
	 * @since 4.3
	 */
	public int getAdditionalTotalsCount() { 
		if (additionalTotalsCount < 0) {
			additionalTotalsCount = 0;
			for (String property: getTotalPropertiesNames()) { 
				Collection<String> list = getTotalProperties().get(property);
				if (list != null) {
					additionalTotalsCount = Math.max(additionalTotalsCount, list.size() - 1);
				}
			}
		}
		return additionalTotalsCount;
	}

	private String removeTotalPropertyPrefix(String totalProperty) {
		return getCollectionView().getMetaCollection().removeTotalPropertyPrefix(totalProperty);
	}
		
	/**
	 * @since 4.1
	 */		
	public String getTotalPropertiesNamesAsString() { 
		return Strings.toString(getTotalPropertiesNames()); 
	}
	

	public void setIgnorePageRowCount(boolean ignorePageRowCount) {
		this.ignorePageRowCount = ignorePageRowCount;
	}

	public boolean isIgnorePageRowCount() {
		return ignorePageRowCount;
	}
		
}
