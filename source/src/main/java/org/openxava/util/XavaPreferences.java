package org.openxava.util;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.apache.commons.logging.*;
import org.openxava.view.meta.*;

/**
 * @author Javier Paniza
 */
public class XavaPreferences {

	private final static String FILE_PROPERTIES = "xava.properties";
	private final static String JAVA_LOGGING_LEVEL_DEFAULT_VALUE = "INFO";
	private static Log log = LogFactory.getLog(XavaPreferences.class);

	private static XavaPreferences instance;

	private Properties properties;

	private boolean ejb2PersistenceLoaded = false;
	private boolean ejb2Persistence = false;
	private boolean jpaPersistenceLoaded = false;
	private boolean jpaPersistence = false;
	private boolean hibernatePersistenceLoaded = false;
	private boolean hibernatePersistence = false;
	private boolean duplicateComponentWarningsLoaded = false;
	private boolean duplicateComponentWarnings = false;
	private int maxSizeForTextEditor;
	private Level javaLoggingLevel;
	private Level hibernateJavaLoggingLevel;
	private int addColumnsPageRowCount;
	private int pageRowCount;
	private int defaultLabelFormat = -1;

	private XavaPreferences() {
	}

	public static XavaPreferences getInstance() {
		if (instance == null) {
			instance = new XavaPreferences();
		}
		return instance;
	}

	private Properties getProperties() {
		if (properties == null) {
			PropertiesReader reader = new PropertiesReader(
					XavaPreferences.class, FILE_PROPERTIES);
			try {
				properties = reader.get();
			} catch (IOException ex) {
				log.error(XavaResources.getString("properties_file_error",
						FILE_PROPERTIES), ex);
				properties = new Properties();
			}
		}
		return properties;
	}

	public boolean isReadOnlyAsLabel() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"readOnlyAsLabel", "false").trim());
	}

	public boolean isTabAsEJB() {
		return "true".equalsIgnoreCase(getProperties().getProperty("tabAsEJB",
				"false").trim());
	}

	public boolean isShowCountInList() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"showCountInList", "true").trim());
	}

	public boolean isEMailAsUserNameInPortal() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"emailAsUserNameInPortal", "false").trim());
	}

	public String getSMTPHost() {
		return getProperties().getProperty("smtpHost");
	}

	public int getSMTPPort() {
		return Integer.parseInt(getProperties().getProperty("smtpPort"));
	}

	public String getSMTPUserID() {
		return getProperties().getProperty("smtpUserId");
	}

	public String getSMTPUserPassword() {
		return getProperties().getProperty("smtpUserPassword");
	}

	public String getCSVSeparator() {
		return getProperties().getProperty("csvSeparator", ";");
	}
	
	/**
	 * @since 4.2.1 
	 */
	public String getCSVEncoding() { 
		return getProperties().getProperty("csvEncoding", null);
	}

	public String getReportParametersProviderClass() {
		return getProperties().getProperty("reportParametersProviderClass",
				"org.openxava.util.DefaultReportParametersProvider").trim();
	}
	
	public String getPersistenceProviderClass() {
		return getProperties().getProperty("persistenceProviderClass",
				"org.openxava.model.impl.JPAPersistenceProvider").trim();
	}

	public String getStyleClass() {
		return getProperties().getProperty("styleClass",
				"org.openxava.web.style.Liferay51Style").trim();
	}

	public String getStyleCSS() {
		return getProperties().getProperty("styleCSS",
				"liferay51/css/everything_unpacked.css").trim();
	}

	public String getLiferay51StyleClass() {
		return getProperties().getProperty("liferay51StyleClass",
				"org.openxava.web.style.Liferay51Style").trim();
	}

	public String getLiferay41StyleClass() {
		return getProperties().getProperty("liferay41StyleClass",
				"org.openxava.web.style.Liferay41Style").trim();
	}

	public String getLiferay43StyleClass() {
		return getProperties().getProperty("liferay43StyleClass",
				"org.openxava.web.style.Liferay43Style").trim();
	}

	public String getWebSpherePortal61StyleClass() {
		return getProperties().getProperty("webSpherePortal61StyleClass",
				"org.openxava.web.style.WebSpherePortal61Style").trim();
	}

	public String getWebSpherePortal6StyleClass() {
		return getProperties().getProperty("webSpherePortal6StyleClass",
				"org.openxava.web.style.WebSpherePortal6Style").trim();
	}

	public String getWebSpherePortalStyleClass() {
		return getProperties().getProperty("webSpherePortalStyleClass",
				"org.openxava.web.style.WebSpherePortalStyle").trim();
	}

	public String getJetSpeed2StyleClass() {
		return getProperties().getProperty("JetSpeed2Style",
				"org.openxava.web.style.JetSpeed2Style").trim();
	}

	public boolean isMapFacadeAsEJB() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"mapFacadeAsEJB", "false").trim());
	}

	/**
	 * 
	 * @return true if <code>isMapFacadeAsEJB() == true</code>, otherwise the
	 *         value of <code>mapFacadeAutoCommit</code> property.
	 */
	public boolean isMapFacadeAutoCommit() {
		if (isMapFacadeAsEJB())
			return true;
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"mapFacadeAutoCommit", "false").trim());
	}

	public boolean isEJB2Persistence() {
		if (!ejb2PersistenceLoaded) {
			ejb2PersistenceLoaded = true;
			ejb2Persistence = getPersistenceProviderClass().toUpperCase()
					.indexOf("EJB") >= 0;
		}
		return ejb2Persistence;
	}

	public boolean isJPAPersistence() {
		if (!jpaPersistenceLoaded) {
			jpaPersistenceLoaded = true;
			jpaPersistence = getPersistenceProviderClass().toUpperCase()
					.indexOf("JPA") >= 0;
		}
		return jpaPersistence;
	}

	public boolean isHibernatePersistence() {
		if (!hibernatePersistenceLoaded) {
			hibernatePersistenceLoaded = true;
			hibernatePersistence = getPersistenceProviderClass().toUpperCase()
					.indexOf("HIBERNATE") >= 0;
		}
		return hibernatePersistence;
	}

	public boolean isDetailOnBottomInCollections() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"detailOnBottomInCollections", "false").trim());
	}

	public boolean isJPACodeInPOJOs() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"jpaCodeInPOJOs", Boolean.toString(isJPAPersistence())).trim());
	}

	public boolean isI18nWarnings() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"i18nWarnings", "false").trim());
	}

	public boolean isDuplicateComponentWarnings() {
		if (!duplicateComponentWarningsLoaded) {
			duplicateComponentWarnings = "true"
					.equalsIgnoreCase(getProperties().getProperty(
							"duplicateComponentWarnings", "true").trim());
			duplicateComponentWarningsLoaded = true;
		}
		return duplicateComponentWarnings;
	}

	public boolean isFailOnAnnotationMisuse() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"failOnAnnotationMisuse", "true").trim());
	}

	public int getMaxSizeForTextEditor() {
		if (maxSizeForTextEditor == 0) {
			maxSizeForTextEditor = Integer.parseInt(getProperties()
					.getProperty("maxSizeForTextEditor", "100"));
		}
		return maxSizeForTextEditor;
	}

	public int getPageRowCount() {
		if (pageRowCount == 0) {
			pageRowCount = Integer.parseInt(getProperties().getProperty(
					"pageRowCount", "10"));
		}
		return pageRowCount;
	}

	public int getAddColumnsPageRowCount() {
		if (addColumnsPageRowCount == 0) {
			addColumnsPageRowCount = Integer.parseInt(getProperties()
					.getProperty("addColumnsPageRowCount", "100"));
		}
		return addColumnsPageRowCount;
	}

	public void setDuplicateComponentWarnings(boolean duplicateComponentWarnings) {
		this.duplicateComponentWarnings = duplicateComponentWarnings;
		duplicateComponentWarningsLoaded = true;
	}

	public Level getJavaLoggingLevel() {
		if (javaLoggingLevel == null) {
			String log = getProperties().getProperty("javaLoggingLevel",
					JAVA_LOGGING_LEVEL_DEFAULT_VALUE).trim();
			try {
				javaLoggingLevel = Level.parse(log);
			} catch (Exception ex) {
				// Because it's a log error, we don't use log, but direct
				// System.err
				javaLoggingLevel = Level
						.parse(JAVA_LOGGING_LEVEL_DEFAULT_VALUE);
				System.err.println("[XavaPreferences.getJavaLoggingLevel] "
						+ XavaResources.getString("incorrect_log_level", log,
								JAVA_LOGGING_LEVEL_DEFAULT_VALUE));
			}
		}
		return javaLoggingLevel;
	}

	public Level getHibernateJavaLoggingLevel() {
		if (hibernateJavaLoggingLevel == null) {
			String log = getProperties().getProperty(
					"hibernateJavaLoggingLevel",
					JAVA_LOGGING_LEVEL_DEFAULT_VALUE).trim();
			try {
				hibernateJavaLoggingLevel = Level.parse(log);
			} catch (Exception ex) {
				// Because it's a log error, we don't use log, but direct
				// System.err
				hibernateJavaLoggingLevel = Level
						.parse(JAVA_LOGGING_LEVEL_DEFAULT_VALUE);
				System.err
						.println("[XavaPreferences.getHibernateJavaLoggingLevel] "
								+ XavaResources.getString(
										"incorrect_log_level", log,
										JAVA_LOGGING_LEVEL_DEFAULT_VALUE));
			}
		}
		return hibernateJavaLoggingLevel;
	}

	/**
	 * If <code>true</code> when an action has no image it uses a button for
	 * display it, else it uses a link.
	 * <p>
	 * 
	 * The default value is <code>false</code>, that is, by default links for
	 * displaying no image actions.<br>
	 */
	public boolean isButtonsForNoImageActions() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"buttonsForNoImageActions", "false").trim());
	}

	/**
	 * If <code>true</code> a upper case conversions will applied to string
	 * arguments for conditions in list and collections.
	 * <p>
	 * 
	 * If <code>true</code> the searching using list or collections are more
	 * flexible (the user can use indistinctly upper or lower case) but can be
	 * slower in some databases (because they cannot use index).
	 * 
	 * The default value is <code>true</code>.<br>
	 */
	public boolean isToUpperForStringArgumentsInConditions() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"toUpperForStringArgumentsInConditions", "true").trim());
	}

	/**
	 * If <code>true</code> filter is show by default for list on init.
	 * <p>
	 * 
	 * The user always have the option to show or hide the filter.<br>
	 * The default value is <code>true</code>.<br>
	 */
	public boolean isShowFilterByDefaultInList() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"showFilterByDefaultInList", "true").trim());
	}

	/**
	 * If <code>true</code> filter is show by default for collections on init.
	 * <p>
	 * 
	 * The user always have the option to show or hide the filter.<br>
	 * The default value is <code>true</code>.<br>
	 */
	public boolean isShowFilterByDefaultInCollections() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"showFilterByDefaultInCollections", "true").trim());
	}

	public String getPortletLocales() {
		return getProperties()
				.getProperty("portletLocales",
						"bg, ca, de, en, es, fr, in, it, ja, ko, nl, pl, pt, ru, sv, zh");
	}
	
	public boolean hasPortletLocales() { 
		return !Is.emptyString(getProperties().getProperty("portletLocales"));
	}

	public String getDefaultPersistenceUnit() {
		return getProperties().getProperty("defaultPersistenceUnit", "default");
	}

	public boolean isGenerateDefaultModules() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"generateDefaultModules", "true").trim());
	}

	public int getDefaultLabelFormat() {
		if (defaultLabelFormat >= 0)
			return defaultLabelFormat;
		String labelFormat = getProperties().getProperty("defaultLabelFormat",
				"NORMAL");
		if (labelFormat.equalsIgnoreCase("NORMAL"))
			defaultLabelFormat = MetaPropertyView.NORMAL_LABEL;
		else if (labelFormat.equalsIgnoreCase("SMALL"))
			defaultLabelFormat = MetaPropertyView.SMALL_LABEL;
		else if (labelFormat.equalsIgnoreCase("NO_LABEL"))
			defaultLabelFormat = MetaPropertyView.NO_LABEL;
		else if (labelFormat.equalsIgnoreCase("NOLABEL"))
			defaultLabelFormat = MetaPropertyView.NO_LABEL;
		else {
			defaultLabelFormat = MetaPropertyView.NORMAL_LABEL;
			log.warn(XavaResources.getString(
					"defaultLabelFormat_illegal_value", labelFormat));
		}
		return defaultLabelFormat;
	}

	public String getDefaultLabelStyle() {
		return getProperties().getProperty("defaultLabelStyle", "");
	}

	/** @since 4m5 */
	public String getHelpPrefix() {
		return getProperties().getProperty("helpPrefix", "").trim();
	}

	/** @since 4m5 */
	public String getHelpSuffix() {
		return getProperties().getProperty("helpSuffix", "").trim();
	}

	/** @since 4m5 */
	public String getDefaultModeController() {
		return getProperties().getProperty("defaultModeController", null);
	}

	/** @since 4m5 */
	public boolean isHelpInNewWindow() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"helpInNewWindow", "true").trim());
	}
	
	/** @since 4m5 */
	public boolean isCustomizeList(){
		return "true".equalsIgnoreCase(getProperties().getProperty("customizeList", "true").trim());		
	}
	
	/** @since 4m5 */
	public boolean isResizeColumns(){
		return "true".equalsIgnoreCase(getProperties().getProperty("resizeColumns", "true").trim());
	}
	
	
	/** @since 4m6 */
	public boolean isSaveAndStayForCollections(){
		return "true".equalsIgnoreCase(getProperties().getProperty("saveAndStayForCollections", "true").trim());
	}
	
	/** @since 4m6 */
	public boolean isShowLabelsForToolBarActions(){
		return "true".equalsIgnoreCase(getProperties().getProperty("showLabelsForToolBarActions", "true").trim());
	}
	
	/** @since 4m6 */
	public String getDefaultHelpPrefix(){
		return "http://openxava.wikispaces.com/help_";		
	}

	/** @since 4m6
	 * 
	 * If <code>true</code> it ignore accents to string arguments for conditions in list and collections. <p>
	 * 
	 * The default value is <code>false</code>.<br>
	 */
	public boolean isIgnoreAccentsForStringArgumentsInConditions() {  
		return "true".equalsIgnoreCase(getProperties().getProperty("ignoreAccentsForStringArgumentsInConditions", "false" ).trim()); 
	}
	
	/** @since 4m6 */
	public String getLiferay6StyleClass() {  
		return getProperties().getProperty("liferay6StyleClass", "org.openxava.web.style.Liferay6Style").trim(); 
	}

	/** @since 4.3 */
	public boolean isSummationInList() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"summationInList", "true").trim());
	}
	
		
}
