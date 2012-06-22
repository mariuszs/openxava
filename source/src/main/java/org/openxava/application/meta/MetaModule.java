package org.openxava.application.meta;

import java.util.*;



import org.openxava.controller.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;

/**
 * @author Javier Paniza
 */
public class MetaModule extends MetaElement implements java.io.Serializable {
	
	private Map environmentVariables;
	private Environment environment;
	private String folder;
	private String modelName;
	private String swingViewClass;
	private String webViewURL;
	private String viewName;
	private String tabName;
	private String docURL; 
	private String docLanguages;	
	private MetaApplication metaApplication;	
	private Collection controllersNames = new ArrayList();
	private String modeControllerName;	
	private MetaReport metaReport;
	

	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public MetaApplication getMetaApplication() {
		return metaApplication;
	}
	public void setMetaApplication(MetaApplication application) {
		this.metaApplication = application;
	}

	public Collection getControllersNames() {
		return controllersNames;
	}		
	public void addControllerName(String controller) {
		controllersNames.add(controller);
	}

	public String getSwingViewClass() {
		return swingViewClass;
	}
	public void setSwingViewClass(String swingViewClass) {
		this.swingViewClass = swingViewClass;
	}

	public MetaReport getMetaReport() {
		return metaReport;
	}
	public void setMetaReport(MetaReport metaReport) {
		this.metaReport = metaReport;
	}
	
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getTabName() {
		return tabName==null?"":tabName;
	}

	public void setTabName(String string) {
		tabName = string;
	}
	
	public String getId() {
		return getMetaApplication().getId() + "." + getName();		
	}
	
	protected String getLabel(Locale locale, String id) {
		String moduleId = id + ".module";
		if (Labels.exists(moduleId)) return super.getLabel(locale, moduleId);
		return super.getLabel(locale, id);
	}
		
	public String getDescription(Locale locale, String id) {
		String moduleId = id + ".module[description]";
		if (Labels.exists(moduleId)) return Labels.get(moduleId, locale);
		return super.getDescription(locale, id);
	}
	

	public String getModeControllerName() {
		return modeControllerName;
	}
	public void setModeControllerName(String string) {
		modeControllerName = string;
	}

	public String getWebViewURL() {
		return webViewURL;
	}
	public void setWebViewURL(String string) {
		webViewURL = string;
	}
	
	public Environment getEnvironment() {
		if (environment == null) {
			environment = new Environment(environmentVariables);
		}
		return environment;
	}

	public void addEnvironmentVariable(String name, String value) {
		if (environmentVariables == null) environmentVariables = new HashMap();
		environmentVariables.put(name, value);
	}
	
	public boolean isDoc() {
		return !Is.emptyString(docURL);
	}
	
	public String getDocURL() {
		return docURL;
	}
	public void setDocURL(String docURL) {
		this.docURL = docURL;
	}
	public String getDocLanguages() {
		return docLanguages==null?"":docLanguages;
	}
	public void setDocLanguages(String docLanguages) {
		this.docLanguages = docLanguages;
	}
	public String getFolder() {		
		return folder==null?"":folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}	

}
