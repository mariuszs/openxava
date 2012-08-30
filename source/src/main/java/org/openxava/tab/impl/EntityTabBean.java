package org.openxava.tab.impl;

import java.rmi.*;
import java.util.*;



import org.openxava.ejbx.*;

/**
 * 
 * @author Javier Paniza
 */
public class EntityTabBean extends SessionBase implements IEntityTabDataProvider {
	
	private EntityTabDataProvider dataProvider = new EntityTabDataProvider();
	
	
	
	public void ejbCreate() {		
	}
	
	public DataChunk nextChunk(ITabProvider tabProvider, String modelName, List propertiesNames, Collection tabCalculators, Map keyIndexs, Collection tabConverters) throws RemoteException {
		dataProvider.setConnectionProvider(getPortableContext());
		return dataProvider.nextChunk(tabProvider, modelName, propertiesNames, tabCalculators, keyIndexs, tabConverters);
	}

	public int getResultSize(ITabProvider tabProvider) {
		dataProvider.setConnectionProvider(getPortableContext());
		return dataProvider.getResultSize(tabProvider);		
	}
	
	public Number getSum(ITabProvider tabProvider, String property) { 
		dataProvider.setConnectionProvider(getPortableContext());
		return dataProvider.getSum(tabProvider, property);				
	}
		
}
