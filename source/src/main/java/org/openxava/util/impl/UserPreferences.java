package org.openxava.util.impl;

import java.io.*;
import java.util.*;
import java.util.prefs.*;

import org.openxava.util.*;

/**
 * Implementation of Java Preferences for OX applications. <p>
 * 
 * It's not intended for use at global preferences for the JVM
 * (that is as value for system property <code>
 * java.util.prefs.PreferencesFactory</code>), but for obtaining
 * it from {@link org.openxava.util.Users#getCurrentPreferences}.<br> 
 * 
 * @author Javier Paniza
 */

public class UserPreferences extends AbstractPreferences {
	
	private final static String ANONIMOUS = "__ANONIMOUS__";
	private static Map preferencesByUser; 
	private String userName;
	private String name;
	private Map children;
	private Properties properties;
	
	protected UserPreferences(AbstractPreferences parent, String name, String userName) {
		super(parent, name);
		this.userName = userName;
		this.name = name;
	}
	
	public static Preferences getForUser(String userName) throws BackingStoreException {
		if (userName == null) userName = ANONIMOUS;
		if (preferencesByUser == null) preferencesByUser = new HashMap();
		UserPreferences preferences = (UserPreferences) preferencesByUser.get(userName);
		if (preferences == null) {			
			preferences = new UserPreferences(null, "", userName);						
			preferences.syncSpi();						
			preferencesByUser.put(userName, preferences);
		}
		return preferences;
	}
		

	protected AbstractPreferences childSpi(String name) {
		if (children == null) children = new HashMap();
		UserPreferences child = (UserPreferences) children.get(name);
		if (child == null) {
			child = new UserPreferences(this, name, userName);
			try {
				child.syncSpi();
			}
			catch (BackingStoreException ex) {
				throw new RuntimeException(ex);
			}
			children.put(name, child);
		}
		return child;
	}

	protected String[] childrenNamesSpi() throws BackingStoreException {
		if (children == null) return new String[0];		
		return XCollections.toStringArray(children.keySet());
	}

	protected void flushSpi() throws BackingStoreException {
		if (properties == null) return;
		try {			
			createFileIfNotExist();					
			properties.store(new FileOutputStream(getFileName()), "OpenXava preferences. User: " + userName + ". Node: " + name); 
		} 
		catch (Exception ex) {
			throw new BackingStoreException(ex);
		}
	}

	private void createFileIfNotExist() throws Exception {
		File f = new File(getFileName());
		if (!f.exists()) {
			File dir = new File(getBaseDir());
			if (!dir.exists()) dir.mkdirs();
			f.createNewFile();
		}
	}

	private String getFileName() {		
		return getBaseDir() + userName + "__" + name + ".properties";
	}

	protected String getSpi(String key) {
		if (properties == null) return null;
		return properties.getProperty(key);
	}

	protected String[] keysSpi() throws BackingStoreException {
		if (properties == null) return new String[0];
		return XCollections.toStringArray(properties.keySet());		
	}

	protected void putSpi(String key, String value) {
		if (properties == null) properties = new Properties();
		properties.put(key, value);
	}

	protected void removeNodeSpi() throws BackingStoreException {		
		if (properties != null) properties.clear();
	}

	protected void removeSpi(String key) {
		if (properties != null) properties.remove(key);
	}

	protected void syncSpi() throws BackingStoreException {		
		try {
			Properties newProperties = new Properties();
			newProperties.load(new FileInputStream(getFileName()));
			if (properties == null) properties = newProperties;
			else properties.putAll(newProperties);
		} 
		catch (FileNotFoundException ex) {
			// If file does not exist just we don't load it
		}
		catch (Exception ex) {
			throw new BackingStoreException(ex);
		}		
	}
		
	private String getBaseDir() {		
		return System.getProperty("user.home") + "/.openxava/";
	}	

}
