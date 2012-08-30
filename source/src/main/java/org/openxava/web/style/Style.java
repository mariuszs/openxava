package org.openxava.web.style;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.util.PropertiesReader;
import org.openxava.util.XavaPreferences;
import org.openxava.util.XavaResources;

/**
 * This class and its subclasses is used from JSP code to give
 * style to the web applications. <p>
 * 
 * The nomenclature is:
 * <ul>
 * <li>By default: CSS class name.
 * <li>..Style: A CSS inline style.
 * <li>..Image: URI of image
 * <li>..Events: code for javascript events
 * <li>..StartDecoration/EndDecoration: HTML code to put before and after.
 * <li>..Spacing: Table spacing
 * </ul>
 * 
 * @author Javier Paniza
 */ 

public class Style {
		 	
	private static Log log = LogFactory.getLog(Style.class);
	private static Style instance = null;
	private static Style portalInstance = null;
	@SuppressWarnings("rawtypes")
	private static Collection styleClasses; 
	private static Map<String, Style> stylesByBrowser = new HashMap<String, Style>(); 
	private Collection<String> additionalCssFiles; 
	private String cssFile; 
	private boolean insidePortal; 
	private String browser; 
	
	public Style() { 		
	}

	/**
	 * @since 4.2
	 */	
	public static Style getInstance(HttpServletRequest request) {
		if (portalInstance != null) return portalInstance;
		return getInstanceForBrowser(request);
	}
	
	/**
	 * @since 4.2
	 */
	private static Style getInstanceForBrowser(HttpServletRequest request) { 
		String browser = request.getHeader("user-agent"); 
		Style instance = stylesByBrowser.get(browser);
		if (instance == null) {
			try {
				for (Object styleClass: getStyleClasses()) {
					try {
						Style style = (Style) Class.forName((String) styleClass).newInstance();
						if (style.isForBrowse(browser)) {
							instance = style;							
							break;
						}
					}
					catch (Exception ex) {
						log.warn(XavaResources.getString("style_for_browser_warning", browser), ex);
					}
				}
				if (instance == null) instance = getInstance();
				instance.setBrowser(browser);
				stylesByBrowser.put(browser, instance);				
			}
			catch (Exception ex) {
				log.warn(XavaResources.getString("style_for_browser_warning", browser), ex); 					
				instance = getInstance();
				instance.setBrowser(browser);
			}			
		}		
		return instance; 
	}
	
	public static Style getInstance() { 
		if (instance == null) {
			try {
				instance = (Style) Class.forName(XavaPreferences.getInstance().getStyleClass()).newInstance();
				instance.cssFile = XavaPreferences.getInstance().getStyleCSS();
			}
			catch (Exception ex) {
				log.warn(XavaResources.getString("default_style_warning"), ex); 					
				instance = new Style();
				instance.cssFile = "default.css";
			}			
		}		
		return instance;
	}
	
	/**
	 * 
	 * @since 4.2
	 */	
	public static void setPotalInstance(Style style) { 
		portalInstance = style;
	}

	/**
	 * 
	 * @since 4.2
	 */
	public String getDefaultModeController() {
		return "Mode";
	}
	
	@SuppressWarnings("rawtypes")
	private static Collection getStyleClasses() throws Exception {
		if (styleClasses == null) {
			PropertiesReader reader = new PropertiesReader(Style.class, "styles.properties");
			styleClasses = reader.get().keySet();
		}
		return styleClasses;
	}
	

	/**
	 * If this style is specific for the indicated browser.
	 * 
	 * @since 4.2
	 */
	public boolean isForBrowse(String browser) {
		return false;
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public boolean isOnlyOneButtonForModeIfTwoModes() {
		return false;
	}
	
	public String [] getNoPortalModuleJsFiles() { 
		return null;
	}
	
	protected String getJQueryCss() { 
		return "/xava/style/ui-lightness/jquery-ui.css";
	}
	
	protected Collection<String> createAdditionalCssFiles() {
		return Arrays.asList( 
			"/xava/editors/calendar/skins/aqua/theme.css", 
			"/xava/style/yahoo-treeview/treeview.css",
			"/xava/style/custom.css", 
			getJQueryCss()
		);
	}

	
	/**
	 * These css files will be always added, inside and outside portal. <p>
	 * 
	 * To refine it overwrite the {@link #createAdditionalCssFiles} method.<br>
	 */
	final public Collection<String> getAdditionalCssFiles() {
		if (additionalCssFiles == null) {
			additionalCssFiles = createAdditionalCssFiles();
		}		
		return additionalCssFiles;
	}

	/** 
	 * The JavaScript function that assign the HTML token to a specific a element. 
	 * 
	 * @since 4.2
	 */
	public String getSetHtmlFunction() {
		return "function (id, content) { $('#' + id).html(content); };";
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public boolean allowsResizeColumns() { 
		return true;
	}
	
	public String getInitThemeScript() {
		return null;
	}
	
	public String getNoPortalModuleStartDecoration(String title) {
		return "";
	}
	
	public String getNoPortalModuleEndDecoration() {
		return "";
	}
	
	/**
	 *
	 * @since 4.2
	 */
	public String getCoreStartDecoration() { 
		return "";
	}

	/**
	 *
	 * @since 4.2
	 */	
	public String getCoreEndDecoration() { 
		return "";
	}	
	
	public String getCssFile() {
		return cssFile;
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public String getMetaTags() { 
		return "";
	}

	public String getEditorWrapper() { 
		return "";
	}
	
	/** 
	 * The folder with images used for actions. <p>
	 *  
	 * If it starts with / is absolute, otherwise starts from the application context path. 
	 * 
	 * @since 4.2
	 */
	public String getImagesFolder() { 
		return "xava/images";
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public String getPreviousPageDisableImage() { 
		return "previous_page_disable.gif";
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public String getNextPageDisableImage() { 
		return "next_page_disable.gif";
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public String getPageNavigationSelectedImage() { 
		return "page_navigation_selected.gif";
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public String getPageNavigationImage() { 
		return "page_navigation.gif";
	}
	
	public String getModule() {
		return "ox-module";		
	}
	
	
	
	/**
	 * 
	 * @since 4.2
	 */		
	public String getView() { 
		return "ox-view";
	}
	
	public String getDetail() {
		return "ox-detail";
	}


	public String getModuleSpacing() {
		return "style='padding: 4px;'";		
	}
	
	public String getActionLink() {
		return "ox-action-link";
	}
	
	public String getActionImage() {
		return "ox-image-link";
	}
		
	public String getButtonBar() {
		return "ox-button-bar"; 
	}
	
	public String getButtonBarButton() { 
		return "ox-button-bar-button";
	}	
	
	public boolean isSeveralActionsPerRow() {
		return true;
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public boolean isUseLinkForNoButtonBarAction() {  
		return false;
	}

	/**
	 * If true an image is shown using this value as class,
	 * otherwise the image would be shown as the background of a span 
	 * with the getButtonBarButton() class.
	 * 
	 * @since 4.2
	 */	
	public boolean isUseStandardImageActionForOnlyImageActionOnButtonBar() {
		return false;
	}
	
	/**
	 * 
	 * @since 4.2
	 */
	public boolean isSeparatorBeforeBottomButtons() {
		return true;
	}

	/**
	 * 
	 * @since 4.2
	 */	
	public String getButtonBarImage() { 
		return "ox-button-bar-image";
	}
	
	public String getButtonBarModeButton() {		
		return "ox-button-bar-mode-button"; 
	}
		
	/**
	 * 
	 * @since 4.2
	 */		
	public String getActive() { 
		return "ox-active";
	}

	/**
	 * 
	 * @since 4.2
	 */		
	public String getFirst() { 
		return "ox-first";
	}
	
	/**
	 * 
	 * @since 4.2
	 */		
	public String getLast() { 
		return "ox-last";
	}
	
	public String getLabel() { 
		return "ox-label";
	}
	
	/** 
	 * 
	 * @since 4.2
	 */
	public String getModuleDescription() { 
		return "ox-module-description"; 
	}
	
	protected String getFrameContent() { 
		return "ox-frame-content";
	}

	
	public String getList() {  
		return "ox-list";
	}
			
	public String getListCellSpacing() {
		return ""; 
	}
	
	public String getListStyle() { 
		return ""; 
	}	
	
	public String getListCellStyle() {
		return ""; 
	}
	
	public String getListHeader() { 
		return "ox-list-header";
	}
	
	public String getListHeaderCell() { 
		return getListHeader();
	}
	
	public String getListSubheader() {
		return "ox-list-subheader";
	}	

	public String getListSubheaderCell() { 
		return getListSubheader();
	}
	
	public String getListOrderBy() {
		return "";
	}
	
	public String getListPair() { 
		return "ox-list-pair";
	}

	
	/** 
	 * @param  Since v4m5 it has no parameters
	 */
	public String getListPairEvents() {  
		return "";
	}	
	
	public String getListPairCell() { 
		return getListPair();
	}
	
	public String getListOdd() { 
		return "ox-list-odd";
	}
	
	
	
	/** 
	 * @param  Since v4m5 it has no parameters
	 */
	public String getListOddEvents() {   
		return "";
	}		
		
	public String getListOddCell() { 
		return getListOdd();
	}
	
	public String getListPairSelected() { 
		return "list-pair-selected";
	}
	
	public String getListOddSelected() { 
		return "list-odd-selected";
	}
			
	public String getListInfo() {
		return "ox-list-info";
	}	
	
	public String getListInfoDetail() {
		return "ox-list-info-detail";
	}
	
	public String getListTitle() {
		return "ox-list-title";
	}
	
	/**
	 * 	
	 * @since 4.2
	 */	
	public String getHeaderListCount() { 
		return "ox-header-list-count";
	}

	
	public String getListTitleWrapper() {
		return "";
	}

	/** 
	 * @since 4.4 
	 */
	public String getFrameWidth() {
		return "width='100%'";
	}
	
	public String getFrameHeaderStartDecoration() {
		return getFrameHeaderStartDecoration(0); 
	}

	public String getFrameHeaderStartDecoration(int width) {  
		StringBuffer r = new StringBuffer();
		String widthAttribute = width == 0?"":"width=" + width+ "% ";
		r.append("<table ");
		r.append(widthAttribute);
		r.append(" class='");
		r.append(getFrame());
		r.append("' style='float:left; margin-right:4px'"); 
		r.append(getFrameSpacing());
		r.append(">");
		r.append("<tr class='");
		r.append(getFrameTitle());
		r.append("'>");		
		r.append("<th>\n");						
		return r.toString();
	}	
	public String getFrameHeaderEndDecoration() { 		
		return "</th></tr>";			
	}
	
	public String getFrameTitleStartDecoration() { 		
		StringBuffer r = new StringBuffer();
		r.append("<span style='float: left' ");
		r.append("class='");
		r.append(getFrameTitleLabel());
		r.append("'>\n");
		return r.toString();
	}	
	
	public String getFrameTitleEndDecoration() { 
		return "</span>";
	}
	
	/**
	 * 
	 * @since 4.2
	 */
	public String getFrameActions() {
		return "ox-frame-actions";
	}
	
	public String getFrameActionsStartDecoration() {		
		return "<span class='" + getFrameActions() + "' style='float: right'>"; 
	}	
	public String getFrameActionsEndDecoration() { 
		return "</span>";
	}		
	
	/**
	 * @since 4.4
	 */
	public String getFrameTotals() { 
		return "ox-frame-totals";
	}
	
	/**
	 * @since 4.4
	 */
	public String getFrameTotalsLabel() { 
		return "ox-frame-totals-label";
	}
	
	/**
	 * @since 4.4
	 */
	public String getFrameTotalsValue() { 
		return "ox-frame-totals-value";
	}
	
	public String getFrameContentStartDecoration() { 
		return getFrameContentStartDecoration(UUID.randomUUID().toString(), false);
	}
		
	public String getFrameContentStartDecoration(String id, boolean closed) { 
		StringBuffer r = new StringBuffer();		
		r.append("<tr id='");
		r.append(id);
		r.append("' ");
		if (closed) r.append("style='display: none;'");
		r.append("><td class='");
		r.append(getFrameContent());		
		r.append("'>\n");
		return r.toString();
	}
	
	
	public String getFrameContentEndDecoration() { 
		return "\n</td></tr></table>";
	}
	
	protected String getFrame() { 
		return "frame";
	}
	
	protected String getFrameTitle() {   
		return "ox-frame-title";
	}

	protected String getFrameTitleLabel() { 
		return "ox-frame-title-label";
	}

	protected String getFrameSpacing() { 
		return "";
	}
		
	public String getEditor() { 
		return "editor";
	}
	
	public String getSmallLabel() {
		return "small-label";
	}
		
	
	public String getErrors() { 
		return "ox-errors";
	}
	
	public String getMessages() { 
		return "ox-messages";
	}
	
	/**
	 * @since 4.3
	 */
	public String getInfos() { 
		return "ox-infos"; 
	}

	/**
	 * @since 4.3
	 */	
	public String getWarnings() {  
		return "ox-warnings"; 
	}	

	public String getErrorStartDecoration () { 
		return "";
	}
	
	public String getErrorEndDecoration () { 
		return "";
	}
	
	/**
	 * For messages and errors
	 */
	public String getMessagesWrapper() { 
		return "";
	}
	
	public String getMessageStartDecoration () { 
		return "";
	}
	
	public String getMessageEndDecoration () { 
		return "";
	}	
	
	public String getProcessing() { 
		return "processing";
	}
			
	public String getButton() {
		return "portlet-form-button";
	}
	
	public String getAscendingImage() {
		return "ascending.gif";
	}
	
	public String getDescendingImage() {
		return "descending.gif";
	}
	
	public String getAscending2Image() { 
		return "ascending2.gif";
	}
	
	public String getDescending2Image() { 
		return "descending2.gif";
	}	
	
	public String getSection() {
		return "ox-section";
	}

	/**
	 * 
	 * @since 4.2
	 */
	public String getSectionTab() {
		return "ox-section-tab";
	}
	
	public String getSectionTableAttributes() {
		return "border='0' cellpadding='0' cellspacing='0'";
	}

	
	/**
	 * 
	 * @since 4.2, renamed from getSectionActive()
	 */
	public String getActiveSection() { 
		return "ox-active-section";
	}
	
	
	public String getSectionLink() {
		return "ox-section-link";
	}

	
	public String getSectionLinkStyle() {
		return null;
	}
		
	public String getSectionBarStartDecoration() {
		return "";
	}
	
	public String getSectionBarEndDecoration() {
		return "";
	}
		
	public String getActiveSectionTabStartDecoration(boolean first, boolean last) { 
		return getSectionTabStartDecoration(first, last, true);
	}
	
	public String getSectionTabStartDecoration(boolean first, boolean last) { 
		return getSectionTabStartDecoration(first, last, false);
	}	
	
	protected String getSectionTabStartDecoration(boolean first, boolean last, boolean active) {
		StringBuffer r = new StringBuffer();		
		r.append("<span class='");
		if (active) {
			r.append(getActive());
			r.append(' ');
		}
		if (first) {
			r.append(getFirst());
			r.append(' ');
		}
		if (last) r.append(getLast());
		r.append("'>");		
		r.append("<span class='"); 
		r.append(getSectionTab());
		r.append("'>");
		return r.toString();
	}
	
	public String getActiveSectionTabEndDecoration() {		
		return "</span></span>";
	}
	
	public String getSectionTabEndDecoration() {
		return "</span></span>";
	}
	
	public String getCollectionListActions() { 
		return "ox-collection-list-actions"; 
	}
	
	/** 
	 * If it starts with '/' the URI is absolute, otherwise the context path is inserted before.
	 */
	public String getRestoreImage() {
		return getImagesFolder() +  "/restore.gif"; 
	}

	/** 
	 * If it starts with '/' the URI is absolute, otherwise the context path is inserted before.
	 */	
	public String getMaximizeImage() {
		return getImagesFolder() +  "/maximize.gif";
	}
	
	/** 
	 * If it starts with '/' the URI is absolute, otherwise the context path is inserted before.
	 */
	public String getMinimizeImage() {  
		return getImagesFolder() +  "/minimize.gif";
	}	

	/** 
	 * If it starts with '/' the URI is absolute, otherwise the context path is inserted before.
	 */	
	public String getRemoveImage() {
		return getImagesFolder() +  "/remove.gif";
	}
	
	public String getLoadingImage() {
		return getImagesFolder() +  "/loading.gif";
	}	
	
	/**
	 * If <code>true</code< the header in list is aligned as data displayed in its column. <p>
	 * 
	 * By default is <code>false</code> and it's used the portal default alignament for headers.
	 */
	public boolean isAlignHeaderAsData() {
		return false;
	}
	
	public boolean isFixedPositionSupported() {
		return true;
	}
	
	/**
	 * If <code>true</code> the style for selected row (or special style) in a list
	 * is applied to the row (tr) and to <b>also the cell</b> (td). <p>
	 * 
	 * If <code>false</code> the style is applied only to the row (tr).<p> 
	 *
     * By default is <code>true</code>.
	 */
	public boolean isApplySelectedStyleToCellInList() {
		return true;
	}
	
	public boolean isShowImageInButtonBarButton() {
		return true;
	}
	
	/**
	 * 
	 * @since 4.2
	 */
	public boolean isShowModuleDescription() {  
		return false;
	}
	
	/**
	 * 
	 * @since 4.2
	 */
	public boolean isShowPageNumber() { 
		return true;
	}	
		
	/**
	 * 
	 * @since 4.2
	 */	
	public boolean isRowLinkable() { 
		return true;
	}
	
		
	public String getBottomButtonsStyle() {
		return "";
	}
	
	public String getBottomButtons() {
		return "ox-bottom-buttons";
	}

	
	public boolean isNeededToIncludeCalendar() {
		return true;
	}

	/**
	 * 
	 * @since 4.2
	 */
	public boolean isChangingPageRowCountAllowed() { 
		return true;
	}

	/**
	 * 
	 * @since 4.2
	 */	
	public boolean isHideRowsAllowed() { 
		return true;
	}
	
	/**
	 * 
	 * @since 4.2
	 */	
	public boolean isShowRowCountOnTop() { 
		return false;
	}
	
	public boolean isInsidePortal() {
		return insidePortal;
	}

	public void setInsidePortal(boolean insidePortal) {
		this.insidePortal = insidePortal;
	}

	public String getSelectedRow(){
		return "selected-row";
	}
	
	public String getSelectedRowStyle(){
		return "";
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getBrowser() {
		return browser;
	}
	
	protected boolean isFirefox() { 		
		return browser == null?false:browser.contains("Firefox");
	}
	
	/** @since 4m5 */
	protected boolean isIE6() { 		
		return browser == null?false:browser.contains("MSIE 6");
	}
	
	/** @since 4m5 */
	protected boolean isIE7() { 		
		return browser == null?false:browser.contains("MSIE 7");
	}
	
		
	/**
	 * @since 4m5
	 */
	public String getCurrentRow() {
		return "current-row"; 
	}
	
	/**
	 * @since 4m5
	 */
	public String getCurrentRowCell() {
		return ""; 
	}
	
	
	/**
	 * @since 4m5
	 */
	public String getPageNavigationSelected() { 		
		return "ox-page-navigation-selected"; 
	}

	
	/**
	 * @since 4m5
	 */
	public String getPageNavigation() {
		return "ox-page-navigation"; 
	}

	/**
	 * 
	 * @since 4.2
	 */			
	public String getPageNavigationPages() {
		return "ox-page-navigation-pages";
	}
	
	/**
	 * @since 4m5
	 */
	public String getPageNavigationArrow() { 		
		return "ox-page-navigation-arrow";
	}
	
	/**
	 * 
	 * @since 4.2
	 */
	public String getNextPageNavigationEvents(String listId) {  		
		return "";
	}
	
	/**
	 * 
	 * @since 4.2
	 */
	public String getPreviousPageNavigationEvents(String listId) {  		
		return "";
	}
	


	/**
	 * @since 4m5
	 */		
	public String getPageNavigationArrowDisable() { 		
		return "ox-page-navigation-arrow-disable";
	}

	
	/**
	 * @since 4m5
	 */
	public String getRowsPerPage() { 		
		return "rows-per-page";
	}
	
	/**
	 * 
	 * @since 4.2
	 */		
	public boolean isHelpAvailable()  {
		return true;
	}

	/**
	 * @since 4m6
	 */
	public String getHelpImage() {
		return "images/help.png";
	}
			
	/**
	 * CSS class for the help icon, link or button. <p>
	 * 
	 * @since 4m6
	 */
	public String getHelp() {
		return "ox-help";
	}
	
	public String getTotalRow() { 
		return "ox-total-row";
	}
		
	public String getTotalCell() {
		return "ox-total-cell"; 
	}

	public String getTotalCapableCell() {
		return "ox-total-capable-cell"; 
	}

	
	public String getTotalCellStyle() {
		return "";
	}

	public String getTotalEmptyCellStyle() { 
		return ""; 
	}
	
	/**
	 * 
	 * @since 4.3
	 */
	public String getTotalLabelCellStyle() {  
		return "vertical-align: middle; text-align: right;" + getTotalEmptyCellStyle();
	}
	
	public String getTotalCapableCellStyle() { 
		return "";
	}
	
	/**
	 * 
	 * @since 4.2
	 */	
	public String getFilterCell() { 
		return "ox-filter-cell";
	}
	
	/**
	 * @since 4.5
	 */
	public String getLayoutLabelLeftSpacer() {
		return "ox-layout-label-left-spacer";
	}
	
	/**
	 * @since 4.5
	 */
	public String getLayoutLabelRightSpacer() {
		return "ox-layout-label-right-spacer";
	}

	/**
	 * @since 4.5
	 */
	public String getLayoutLabelRightSpacerStyle() {
		return "width:4px;";
	}
	
	/**
	 * The label cell contains the left spacer, label and right spacer.
	 * @since 4.5
	 */
	public String getLayoutLabelCell() {
		return "ox-layout-label-cell";
	}
	
	/**
	 * Since the label cell contains the left spacer, label and right spacer.
	 * this is the class for styling just the label.
	 * @since 4.5
	 */
	public String getLayoutLabel() {
		return "ox-layout-label;";
	}
	
	/**
	 * The data cell contains the data and might contain left spacer.
	 * 
	 */
	public String getLayoutDataCell() {
		return "ox-layout-data-cell";
	}
	
	/**
	 * The data cell contains the data and might contain left spacer.
	 * 
	 */
	public String getLayoutData() {
		return "ox-layout-data";
	}

	/**
	 * @since 4.5
	 */
	public String getLayoutRowSpacer() {
		return "ox-layout-row-spacer";
	}
}