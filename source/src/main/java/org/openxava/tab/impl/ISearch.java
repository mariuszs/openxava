package org.openxava.tab.impl;

import java.rmi.*;

import javax.ejb.*;

/**
 * Allows search along some predefined searches and specifying a
 * concrete condition too. <p>
 * 
 * It uses EJB exceptions to facilitate a remote implementation.<br>
 * 
 * @author  Javier Paniza
 */

public interface ISearch {

  /**
   * Execute the search. <p>
   * 
   * If there are no object then generate a empty result, but does not
   * throw a exception. <br>
   * 
   * @param index  Index of serach to execute
   * @param key  Key to send to search 
   * @exception FinderException  Any logic problem on search
   * @exception RemoteException  Any system problem on search
   * @exception IndexOutOfBoundsException  If index of consult does not exist
   */
  void search(int index, Object key) throws FinderException, RemoteException;

  /**
   * Execute search. <p>
   * 
   * If there are no object then generate a empty result, but does not
   * throw a exception. <br>
   * 
   * @param condition  Condition to use in search.
   * @param key  Key to send to search 
   * @exception FinderException  Any logic problem on search
   * @exception RemoteException  Any system problem on search
   */  
  void search(String condition, Object key) throws FinderException, RemoteException;
  
}
