package org.openxava.model.impl;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * For obtaining a instance of the persistence provider configured in the system. <p>
 * 
 * @author Javier Paniza
 */

public class PersistenceProviderFactory {
	
	private static Log log = LogFactory.getLog(PersistenceProviderFactory.class);
	private static IPersistenceProvider instance;
	
	public static IPersistenceProvider getInstance() {
		if (instance == null) {
			try {
				instance = (IPersistenceProvider) Class.forName(XavaPreferences.getInstance().getPersistenceProviderClass()).newInstance();
			}
			catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				throw new PersistenceProviderException(XavaResources.getString("persistence_provider_creation_error"));
			}
		}		
		return instance;
	}

}
