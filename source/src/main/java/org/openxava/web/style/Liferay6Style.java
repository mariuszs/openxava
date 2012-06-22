package org.openxava.web.style;

import java.util.*;

/**
 * 
 * @author Stephan Rusch
 * @author Javier Paniza
 */

public class Liferay6Style extends Liferay51Style {
	
	private static Liferay6Style instance = null;

	protected Liferay6Style() {
	}
	
	public static Style getInstance() {
		if (instance == null) {
			instance = new Liferay6Style();
		}
		return instance;
	}
	
	public String getInitThemeScript() {
		return "";
	}
	
	public String getLoadingImage() { 
		return getLiferayImagesFolder() + "application/loading_indicator.gif";
	}
	
	public String getNoPortalModuleStartDecoration(String title) {   
		return "<div id='wrapper' class='portlet' style='margin: 4px'><div class='portlet-topper'><span class='portlet-title'>"
			+ title + "</span></div><div id='content' class='portlet-content'>"; 
	}
		
	protected String getJQueryCss() {  
		return "/xava/style/smoothness/jquery-ui.css";
	}	
		
	protected Collection<String> createAdditionalCssFiles() { 
		Collection<String> files = new ArrayList<String>(super.createAdditionalCssFiles());		
		files.add("/xava/style/smoothness/liferay6-patch.css");
		return files;
	}
		
	public String getSectionBarStartDecoration() { 
		return "<td><ul class='aui-tabview-list'>"; 
	}
	
	public String getSectionBarEndDecoration() { 
		return "</ul></td>"; 
	} 
	
	protected String getActiveSectionFirstTabStartDecoration() {  	
		return "<li class='aui-tab aui-state-default current aui-tab-active aui-state-active first aui-selected'><span class='aui-tab-content'> <span class='aui-tab-label'>"; // position: static needed for ie7 
	} 
	
	protected String getActiveSectionLastTabStartDecoration() { 
		return "<li class='aui-tab aui-state-default current aui-tab-active aui-state-active last aui-selected'><span class='aui-tab-content'> <span class='aui-tab-label'>"; // position: static needed for ie7 
	} 
	
	protected String getActiveSectionTabStartDecoration() { 
		return "<li class='aui-tab aui-state-default current aui-tab-active aui-state-active aui-selected'><span class='aui-tab-content'> <span class='aui-tab-label'>"; // position: static needed for ie7 
	} 
	
	public String getActiveSectionTabEndDecoration() { 
		return "</span></span></li>"; 
	} 
	
	protected String getSectionTabStartDecoration() { 
		return "<li class='aui-tab aui-state-default'><span class='aui-tab-content'> <span class='aui-tab-label'>"; // position: static needed for ie7 
	} 
	
	protected String getSectionFirstTabStartDecoration() { 
		return "<li class='aui-tab aui-state-default first'><span class='aui-tab-content'> <span class='aui-tab-label'>"; // position: static needed for ie7 		
	} 
	
	protected String getSectionLastTabStartDecoration() { 
		return "<li class='aui-tab aui-state-default last'><span class='aui-tab-content'> <span class='aui-tab-label'>"; // position: static needed for ie7 
	} 
	
	public String getSectionTabEndDecoration() { 
		return "</span></span></li>"; 
	}
	
	
	public String getTotalEmptyCellStyle() {  
		return "border-bottom-style: hidden;";
	}
	
	public String getTotalCell() { 
		return "";
	}
	
	public String getTotalCellStyle() {  
		return getTotalCellAlignStyle() + ";border-bottom-style: hidden;";
	}	
	
}
