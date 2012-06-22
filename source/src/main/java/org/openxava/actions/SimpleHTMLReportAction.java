package org.openxava.actions;

import java.lang.reflect.*;
import java.text.*;
import java.util.*;

import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;
import org.openxava.web.servlets.*;

/**
 * To create reports from simple html templates. <p> 
 * 
 * How to use the SimpleHtmlReportAction<br>
 * <br>
 * Process<br>
 * Create an action which extends SimpleHtmlReportAction. <br>
 * Create a report template<br>
 * Add the action in controllers.xml, <br>
 * <br>
 * Action<br>
 * Create an action which extends SimpleHtmlReportAction. <br>
 * If your entity does not contain collections, you don't have to create
 * this action, you can use SimpleHtmlReportAction <br>
 * <pre><br>
 * public class ReportProjectAction extends SimpleHtmlReportAction {<br><br>
 * 
 * public Map&lt;String, Object&gt; getParameters() throws Exception {         
 * <br>		Project p = (Project)MapFacade.findEntity(getModelName(), getView().getKeyValuesWithValue());
 * <br>		return getParameters(p);
 * <br>	}<br>
 * <br>	public static Map&lt;String, Object&gt; getParameters(Project p) throws Exception {         
 * <br>		Map&lt;String, Object&gt; parameters = new HashMap&lt;String, Object&gt;();        
 * <br>		// get all the field contents of the entity
 * <br>		parameters.putAll(getEntityParameters(p));
 * <br>		// get the field contents of the collections
 * <br>		parameters.put("milestones", getCollectionParametersList(p.getMilestones()));
 * <br>		parameters.put("actions", getCollectionParametersList(p.getActions()));
 * <br>		return parameters;
 * <br>	}
 * <br>}<br>
 * </pre>
 * In most cases, you just have to call
 * getEntityParameters(your_entity) and
 * getCollectionParametersList(your_collection) in the action.<br>
 * By defining a static function to get the parameters, you can reuse the
 * code for an extension of ReportProjectAction.<br>
 * <br>
 * Report<br>
 * Create a report template in /reports (if this folder does not exist,
 * create it as a source folder). You can create it with any WYSIWYG
 * editor (SeaMonkey is a good free one).<br>
 * This template should be called entity_name.html, but you can create
 * others as you want. The (very simple) syntax for the report is
 * explained at the end.<br>
 * <pre>&lt;!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"&gt;<br>&lt;html xmlns="http://www.w3.org/1999/xhtml"&gt;<br>&lt;head&gt;<br>  &lt;meta http-equiv="content-type" content="text/html; charset=UTF-8"&gt;<br>  &lt;title&gt;Project - ${reference}&lt;/title&gt;<br>  &lt;link href="/OpenXavaTest/xava/style/report.css" rel="stylesheet" type="text/css"&gt;<br>&lt;/head&gt;<br>&lt;body&gt;<br>&lt;table&gt;<br>	&lt;tr&gt;&lt;td&gt;Name:&lt;/td&gt;&lt;td&gt;${name}&lt;/td&gt;&lt;/tr&gt;<br>	&lt;tr&gt;&lt;td&gt;Reference:&lt;/td&gt;&lt;td&gt;${reference}&lt;/td&gt;&lt;/tr&gt;<br>	&lt;tr&gt;&lt;td&gt;Owner:&lt;/td&gt;&lt;td&gt;${owner.firstName} ${owner.lastName}&lt;/td&gt;&lt;/tr&gt;<br>&lt;!-- $$if(customers) --&gt;<br>	&lt;tr&gt;&lt;td&gt;Customers:&lt;/td&gt;&lt;td&gt;${customer}&lt;/td&gt;&lt;/tr&gt;<br>&lt;!-- $$endif(customers) --&gt;<br>&lt;/table&gt;<br>&lt;table&gt;<br>	&lt;tr&gt;<br>		&lt;td&gt;Milestone&lt;/td&gt;<br>		&lt;td&gt;Target&lt;/td&gt;<br>		&lt;td&gt;Achieved&lt;/td&gt;<br>	&lt;/tr&gt;<br>&lt;!-- $$for(milestones) --&gt;<br>	&lt;tr&gt;<br>		&lt;td&gt;${milestone.name}&lt;/td&gt;<br>		&lt;td&gt;${targetDate}&lt;/td&gt;<br>		&lt;td&gt;${achievedDate}&lt;/td&gt;<br>	&lt;/tr&gt;<br>&lt;!-- $$endfor(milestones) --&gt;<br>&lt;/table&gt;<br>&lt;/body&gt;<br>&lt;/html&gt;<br></pre>
 * controllers.xml<br>
 * <pre>	&lt;controller name="Project"&gt;	<br>		&lt;extends controller="Typical"/&gt;<br>        	&lt;action name="datasheet" image="images/report.gif" mode="detail"<br>			class="org.openxava.actions.ReportProjectAction" &gt;<br>			&lt;set property="template" value="/Project.html" /&gt;<br>		&lt;/action&gt;<br>	        &lt;action name="actionsReport" image="images/report.gif" mode="detail"<br>			class="org.openxava.actions.ReportProjectAction" &gt;<br>			&lt;set property="template" value="/ProjectActions.html" /&gt;<br>		&lt;/action&gt;<br>	&lt;/controller&gt;<br><br>	&lt;controller name="Company"&gt;	<br>		&lt;extends controller="Typical"/&gt;<br>        	&lt;action name="report" image="images/report.gif" mode="detail"<br>			class="org.openxava.actions.SimpleHtmlReportAction" /&gt;<br>	&lt;/controller&gt;<br><br>	&lt;controller name="SimpleHtmlReport"&gt;	<br>        	&lt;action name="report" image="images/report.gif" mode="detail"<br>			class="org.openxava.actions.SimpleHtmlReportAction" /&gt;<br>	&lt;/controller&gt;<br><br></pre>
 * Here we have used twice the same report action with 2 different
 * templates for Project to report on different part of the entity.<br>
 * We also can use the SimpleHtmlReportAction if we don't want to display
 * collections in the report, such as described in the Company controller.<br>
 * <br>
 * <br>
 * Developping reports very fast<br>
 * <br>
 * 1. In application.xml, add the SimpleHtmlReport controller to the
 * module you want to report on.<br>
 * 2. In /reports, create a report named entity_name.html, open it in
 * Eclipse and just write ${fields} inside, save<br>
 * 3. Start Tomcat, launch your browser, select your module and click on
 * Report, a report will be generated with all the available fields<br>
 * 4. Save this report under /reports/entity_name.html<br>
 * 5. Done! If you refresh the reports folder in Eclipse and wait a little
 * bit, when you click on report in the browser you will see a complete
 * report of your entity (without the collections)<br>
 * <br>
 * <br>
 * Syntax for the template<br>
 * <br>
 * Field names<br>
 * ${field_name} is replaced in the template by the value contained in the
 * Map returned by getParameters()<br>
 * field_name can contain references such as ${parent.child.name} with a
 * default (adjustable) depth of 5.<br>
 * <br>
 * Control<br>
 * There are 3 types of control blocks<br>
 * if<br>
 * Syntax: &lt;!-- $$if(field_name) --&gt;Some text which can contain
 * control blocks&lt;!-- $$endif(field_name) --&gt;<br>
 * The content of the block will only appear if field_name IS in the
 * getParameters() Map and is not empty.<br>
 * <br>
 * ifnot<br>
 * Syntax: &lt;!-- $$ifnot(field_name) --&gt;Some text which can contain
 * control blocks&lt;!-- $$endifnot(field_name) --&gt;<br>
 * The content of the block will only appear if field_name is NOT in the
 * getParameters() Map or is empty.<br>
 * <br>
 * for<br>
 * Syntax: &lt;!-- $$for(field_name) --&gt;Some text which can contain
 * control blocks&lt;!-- $$endfor(field_name) --&gt;<br>
 * The content of the block will be repeated as many times as there are
 * items in the Vector&lt;Map&lt;String, Object&gt;&gt; returned by
 * getParameters().get(field_name)<br>
 *  
 * @author Laurent Wibaux 
 */

public class SimpleHTMLReportAction extends ViewBaseAction implements IForwardAction, IModelAction {
	
	private static final String COLLECTION = "__COLLECTION__"; 	
	private static final int MAX_DEPTH = 5; 
		
	@SuppressWarnings("unused")
	private String modelName;
	
	private String template;
	
	private String depth;
				
	public void execute() throws Exception {
		if (getView().isKeyEditable()) {
			addError("save_before_reporting");
			return;
		}
        Messages errors = MapFacade.validate(getModelName(), getView().getValues());
        if (errors.contains()) throw new ValidationException(errors); 
		String tpl = getTemplate();
		if (tpl == null || tpl.equals("")) tpl = "/" + getModelName() + ".html";
		getRequest().getSession().setAttribute(GenerateSimpleHTMLReportServlet.SESSION_REPORT, 
			SimpleTemplater.getInstance().buildOutputUsingResourceTemplate(tpl, getParameters()));
	}
	
	protected Map<String, Object> getParameters() 
	throws Exception {
		Map<String, Object> parameters = new HashMap<String, Object>();
		Object entity = MapFacade.findEntity(getModelName(), getView().getKeyValuesWithValue());
		parameters.putAll(getEntityParameters(entity, getIntDepth())); 
		parameters.put("fields", getFieldsTable(entity, getIntDepth()));
		parameters.put("values", getValuesTable(entity, getIntDepth()));	
		return parameters;
	}
	
	public String getForwardURI() {		
		return "/xava/report.html?time=" + System.currentTimeMillis();
	}

	public boolean inNewWindow() {
		return true;
	}	
	
	public void setModel(String modelName) { 
		this.modelName = modelName;
	}

	public String getFieldsTable(Object entity, int depth) 
	throws Exception {
		return getFieldsOrValuesTable(entity, depth, true);
	}

	public String getValuesTable(Object entity, int depth) 
	throws Exception {
		return getFieldsOrValuesTable(entity, depth, false);
	}

	
	private String getFieldsOrValuesTable(Object entity, int depth, boolean fields) throws Exception {
		Map<String, String> parameters = getEntityParameters(entity, depth);
		TreeSet<String> ordered = new TreeSet<String>();
		for (String key : parameters.keySet()) ordered.add(key);
		String table = "<table>\r\n";
		for (String key : ordered) {
			String value = parameters.get(key);
			if (fields || (!value.equals(COLLECTION) && !value.equals(""))) {
				table += "\t<tr><td>" + key + "</td><td>";
				if (parameters.get(key).equals(COLLECTION)) table += COLLECTION;
				else if (fields) table += "${" + key + "}";
				else table += parameters.get(key);
				table += "</td></tr>\r\n";
			}
		}
		table += "</table>\r\n";
		return table;
	}


	public String getCollectionTable(Class<?> collectionEntityClass, String collectionName) 
	throws Exception {
		return getCollectionTable(collectionEntityClass, collectionName, 1);
	}
	
	
	public String getCollectionTable(Class<?> collectionEntityClass, String collectionName, int maxDepth) 
	throws Exception {
		Map<String, String> parameters = getClassParameters(collectionEntityClass, maxDepth);
		String table = "<table>\r\n";
		table += "\t<tr>";
		for (String key : parameters.keySet()) {
			if (!parameters.get(key).equals(COLLECTION)) table += "<th>" + key + "</th>";
		}
		table += "</tr>";
		table += "<!-- $$for(" + collectionName + ") --><tr>\r\n";
		for (String key : parameters.keySet()) {
			if (!parameters.get(key).equals(COLLECTION)) table += "<td>${" + key + "}</td>";
		}
		table += "</tr><!-- $$endfor(" + collectionName + ") -->\r\n";
		table += "</table>\r\n";
		return table;
	}
	
	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public void setDepth(String depth) {
		this.depth = depth;
	}

	public String getDepth() {
		return depth;
	}

	private int getIntDepth() {
		try {
			return Integer.parseInt(getDepth());
		} catch (NumberFormatException nfe) {
			return 4;
		}
	}
	
	/**
	 * Get a map of [name, value] for the passed object
	 * @param 	entity - the Object to parse
	 * @param	maxDepth - the maximum depth while parsing the ManyToOne fields
	 * @return  the map of field names and values
	 */
	protected Map<String, String> getEntityParameters(Object entity) throws Exception {
		return getEntityParameters(entity, entity.getClass(), "", 0);
	}		
	
	/**
	 * Get a map of [name, value] for the passed object
	 * @param 	entity - the Object to parse
	 * @param	maxDepth - the maximum depth while parsing the ManyToOne fields
	 * @return  the map of field names and values
	 */	
	private Map<String, String> getEntityParameters(Object entity, int maxDepth) throws Exception { 
		return getEntityParameters(entity, entity.getClass(), "", MAX_DEPTH-maxDepth);
	}
		
	
	/**
	 * Get a map of [name, ""] for the passed class
	 * @param 	class - the Class to parse
	 * @param	maxDepth - the maximum depth while parsing the ManyToOne fields
	 * @return  the map of field names and values
	 */
	private Map<String, String> getClassParameters(Class<?> aClass, int maxDepth) throws Exception { 
		return getEntityParameters(null, aClass, "", MAX_DEPTH-maxDepth);
	}
	
	private Map<String, String> getEntityParameters(Object entity, Class<?> entityClass, String parentName, int depth) throws Exception { 
		Map<String, String> parameters = new HashMap<String, String>();
		if (depth >= MAX_DEPTH) return parameters;
		Field fields[] = entityClass.getDeclaredFields();
		for (int i=0; i<fields.length; i++) {
			if (Modifier.isStatic(fields[i].getModifiers())) continue;
			if (fields[i].getName().indexOf('$') != -1) continue;
			String name = fields[i].getName();
			Object value = getValue(entity, name);
			if (value != null && isPrintable(value)) {
				parameters.put(parentName + name, getPrintableValue(value));
			} else if (fields[i].isAnnotationPresent(javax.persistence.ManyToOne.class)) {
				Map<String, String> mtoParameters = getEntityParameters(value, fields[i].getType(), 
						parentName + name + ".", depth + 1);
				if (mtoParameters.size() > 0) parameters.putAll(mtoParameters);
			} else if (fields[i].isAnnotationPresent(javax.persistence.OneToMany.class) || 
					fields[i].isAnnotationPresent(javax.persistence.ManyToMany.class)) {
				parameters.put(parentName + name, COLLECTION);			
			} else {
				parameters.put(parentName + name, "");
			}
		}
		return parameters;
	}
		
	private Object getValue(Object o, String name) { 
		if (o == null) return null;
		String method = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
		try {
			Method m = o.getClass().getMethod(method);
			return m.invoke(o);
		} catch (Exception e) {
			return null;
		}
	}
	
	private String getPrintableValue(Object value) { 
		if (value == null) return "";
		if (value instanceof Date) return DateFormat.getInstance().format((Date) value);
		String sValue = "" + value.toString();
		if (sValue.endsWith(".0")) sValue = sValue.substring(0, sValue.length()-2);
		return sValue.trim();
	}	
	
	private boolean isPrintable(Object o) { 
		if (o instanceof Date) return true;
		if (o instanceof String) return true;
		if (o instanceof Float) return true;
		if (o instanceof Double) return true;
		if (o instanceof Integer) return true;
		if (o instanceof Long) return true;
		if (o instanceof Boolean) return true;
		if (o instanceof Byte) return true;
		if (o instanceof Character) return true;
		if (o instanceof Short) return true;
		if (o instanceof Enum<?>) return true;
		return false;
	}
	
	/**
	 * Get a list of maps of [name, value] for each one of the objects in the passed collection
	 * @param 	collection - the Collection to parse
	 * @return  the list of maps
	 */	
	protected Collection<Map<String, String>> getCollectionParametersList(Collection<?> collection) throws Exception {
		return getCollectionParametersList(collection, 2);
	}
	
	
	/**
	 * Get a list of maps of [name, value] for each one of the objects in the passed collection
	 * @param 	collection - the Collection to parse
	 * @param	maxDepth - the maximum depth while parsing the ManyToOne fields
	 * @return  the list of maps
	 */
	private Collection<Map<String, String>> getCollectionParametersList(Collection<?> collection, int maxDepth) throws Exception {
		Collection<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (collection != null) {
			for (Object entity : collection) {
				list.add(getEntityParameters(entity, entity.getClass(), "", MAX_DEPTH-maxDepth));
			}
		}
		return list;
	}	

}