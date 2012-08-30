package org.openxava.tab.impl;

import java.rmi.*;

import javax.ejb.*;

/**
 * Interface to facilite a remote <b>impl</b>elmentation of
 * a {@link IEntityTab}. <p>
 *
 * Adds methods not intended to use with final user (a programmer)
 * of the component, but that they are utils to implement {@link IXTableModel}.<br>
 *
 * @author  Javier Paniza
 */

public interface IEntityTabImpl extends IEntityTab, ITabProvider
{

  /**
   * Search a concrete entity from a key. <p>
   * 
   * Ususally thie key is obtained from columns of table (IXTableModel).<br>
   * This method is used from IXTableModel.getObjectAt. It is not normal
   * that a application programmer call this method directly.<br>
   */
  Object findEntity(Object [] clave) throws FinderException, RemoteException;

  Number getSum(String property) throws RemoteException; 
  
}
