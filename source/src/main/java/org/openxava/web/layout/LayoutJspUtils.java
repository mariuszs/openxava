/**
 * 
 */
package org.openxava.web.layout;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Helper for html / jsp rendering.
 * @author Federico Alcantara
 *
 */
public enum LayoutJspUtils {
	INSTANCE;
	
	/**
	 * Creates a html tag.
	 * @param tagName Tag name to create.
	 * @param attributes List of attributes.
	 * @return A string properly formed.
	 */
	public String startTag(String tagName, Map<String, String> attributes) {
		StringBuffer buffer = new StringBuffer("");
		buffer.append('<')
			.append(tagName)
			.append(' ');
		if (attributes != null) {
			for (Entry<String, String> entry : attributes.entrySet()) {
				if (!entry.getKey().equals(LayoutJspKeys.ATTR_LIST)) {
					buffer.append(entry.getKey())
							.append('=')
							.append('"');
				}
				buffer.append(entry.getValue());
				if (!entry.getKey().equals(LayoutJspKeys.ATTR_LIST)) {
					buffer.append('"');
				}
				buffer.append(' ');
			}
		}
		buffer.append(' ')
			.append('>');
		return buffer.toString();
	}

	/**
	 * Creates a html tag.
	 * @param tagName Tag name to create.
	 * @return A string properly formed.
	 */
	public String startTag(String tagName) {
		return startTag(tagName, null);
	}
	
	/**
	 * Creates a end tag for the given tag name
	 * @param tagName Tag name to create
	 * @return
	 */
	public String endTag(String tagName) {
		StringBuffer buffer = new StringBuffer("");
		buffer.append('<')
			.append('/')
			.append(tagName)
			.append('>');
		return buffer.toString();
	}
}
