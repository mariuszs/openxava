package org.openxava.ejbx;

import java.lang.reflect.*;
import java.rmi.*;
import java.util.*;
import javax.ejb.*;
import javax.rmi.*;



import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * Utility class for create EJBs from home. <p>
 *
 * We can create with argument (typical in EntityBeans case) or
 * without argument (typical in SessionBeans case).
 *
 * @author  Javier Paniza
 */

public class EJBFactory {

  private static Class [] detailsClass0 = { };
  private static Class [] detailsClass1 = { java.lang.Object.class };
  private static Class [] detailsClass2 = { java.util.Map.class };
  

  /**
   * Create a EJB from a <i>home</i>. <p> 
   *
   * The <i>home</i> must to have a <code>create()</code> method.<br>
   *
   * @param home  The <i>home</i> against call the create() method
   * @exception CreateException The original from create() method
   * @exception RemoteException The original from create() method
   * @exception NoSuchMethodException If the <i>home</i> does not have <code>create()</code> method
   */
  public static Object create(EJBHome home) throws CreateException, RemoteException, NoSuchMethodException {
  	try {
  		Class cl = home.getEJBMetaData().getHomeInterfaceClass();
  		if (cl == null) {
  			throw new RemoteException(XavaResources.getString("ejbhome_error"));
  		}
  		Method m = cl.getDeclaredMethod("create", detailsClass0);
  		Object narrowHome = PortableRemoteObject.narrow(home, cl);
  		return m.invoke(narrowHome, null);
  	}
  	catch (InvocationTargetException ex) {
  		Throwable th = ex.getTargetException();
  		try {
  			throw th;
  		}
  		catch (CreateException ex2) {
  			throw ex2;
  		}
  		catch (RemoteException ex2) {
  			throw ex2;
  		}
  		catch (Throwable ex2) {
  			throw new RemoteException(ex2.getLocalizedMessage(), ex2);
  		}
  	}
  	catch (NoSuchMethodException ex) {
  		throw ex;
  	}
  	catch (Exception ex) {
  		throw new RemoteException(ex.getLocalizedMessage(), ex);
  	}
  }
  
  /**
   * Creates a EJB from a <i>home</i> and its class. <p> 
   *
   * The <i>home</i> must to have a <code>create()</code> method.<br>
   *
   * @param home <i>home</i> on which the creation method is invoked.
   * @param className <i>home</i> class on which the creation method is invoked.
   * @exception CreateException From original create
   * @exception RemoteException From original create
   * @exception NoSuchMethodException If the <i>home</i> does not have a <code>create()</code> method
   */
  public static Object create(Object home, Class className) throws CreateException, RemoteException, NoSuchMethodException {
  	try {		
  		Method m = className.getDeclaredMethod("create", detailsClass0);	  
  		Object narrowHome = PortableRemoteObject.narrow(home, className);	  
  		return m.invoke(narrowHome, null);
  	}
		catch (InvocationTargetException ex) {
		  Throwable th = ex.getTargetException();
		  try {
			throw th;
		  }
		  catch (CreateException ex2) {
			throw ex2;
		  }
		  catch (RemoteException ex2) {
			throw ex2;
		  }
		  catch (Throwable ex2) {
			throw new RemoteException(ex2.getLocalizedMessage(), ex2);
		  }
		}
		catch (NoSuchMethodException ex) {
		  throw ex;
		}
		catch (Exception ex) {
		  throw new RemoteException(ex.getLocalizedMessage(), ex);
		}
  }
  
  /**
   * Create a EJB form <i>home</i> and the create arguments. <p>
   *
   * The <i>home</i> must to have a <code>create(Object )</code> method.<br> 
   *
   * @param home  The <i>home</i> on which create method is invoked.
   * @param details  Argument to send to <code>create</code> method
   * @exception ValidationException From create method
   * @exception CreateException From create method
   * @exception RemoteException From create method
   * @exception NoSuchMethodException If the <i>home</i> does not have a method <code>create(Object )</code>
   */
  public static Object create(EJBHome home, Object details) throws ValidationException, CreateException, RemoteException, NoSuchMethodException  {
		try {
		  Class cl = home.getEJBMetaData().getHomeInterfaceClass();
		  if (cl == null) {
		  	throw new RemoteException(XavaResources.getString("ejbhome_error"));
		  }	  
		  Method m = cl.getDeclaredMethod("create", detailsClass1);
		  Object [] args = { details };
		  return m.invoke(home, args);
		}
		catch (InvocationTargetException ex) {
		  Throwable th = ex.getTargetException();
		  try {
		  	throw th;
		  }
		  catch (CreateException ex2) {
		  	throw ex2;
		  }
		  catch (ValidationException ex2) {
		  	throw ex2;
		  }
		  catch (RemoteException ex2) {
		  	throw ex2;
		  }
		  catch (Throwable ex2) {
		  	throw new RemoteException(ex2.getLocalizedMessage(), ex2);
		  }
		}
		catch (NoSuchMethodException ex) {
		  throw ex;
		}
		catch (Exception ex) {
		  throw new RemoteException(ex.getLocalizedMessage(), ex);
		}
  }
  
  /**
   * Create a EJB from a <i>home</i> and the create argument. <p>
   *
   * The <i>home</i> must to have a <code>create(Map )</code> method.<br>
   *
   * @param home  The <i>home</i> on which the create method is invoked.
   * @param map  Argument sent to <code>create</code> method
   * @exception ValidationException From create method
   * @exception CreateException From create method
   * @exception RemoteException From create method
   * @exception NoSuchMethodException If the <i>home</i> does not have a <code>create(Object ) method</code>
   */
  public static Object create(EJBHome home, Map map) throws ValidationException, CreateException, RemoteException, NoSuchMethodException  {
		try {
		  Class cl = home.getEJBMetaData().getHomeInterfaceClass();
		  if (cl == null) {
		  	throw new RemoteException(XavaResources.getString("ejbhome_error"));
		  }	  
		  Method m = cl.getDeclaredMethod("create", detailsClass2);
		  Object [] args = { map };
		  return m.invoke(home, args);
		}
		catch (InvocationTargetException ex) {
		  Throwable th = ex.getTargetException();
		  try {
		  	throw th;
		  }
		  catch (CreateException ex2) {
		  	throw ex2;
		  }
		  catch (ValidationException ex2) {
		  	throw ex2;
		  }
		  catch (RemoteException ex2) {
		  	throw ex2;
		  }
		  catch (Throwable ex2) {
		  	throw new RemoteException(ex2.getLocalizedMessage(), ex2);
		  }
		}
		catch (NoSuchMethodException ex) {
		  throw ex;
		}
		catch (Exception ex) {
		  throw new RemoteException(ex.getLocalizedMessage(), ex);
		}
  }
  
  
  /**
   * Create a EJB from a <i>home</i>, its class and a <tt>java.util.Map</tt> as
   * create argument. <p>
   * 
   * The <i>home</i> sent must to have a <code>create(Map )</code> method.<br>
   *
   * @param home  The <i>home</i> on which create method is invoked
   * @param homeClass Class of the home
   * @param map  Argument sent to <code>create</code> method
   * @exception CreateException From create method
   * @exception ValidationException From create method
   * @exception RemoteException From create method
   * @exception NoSuchMethodException If the <i>home</i> does not have a <code>create(Map )</code> method
   */
  public static Object create(Object home, Class homeClass, Map map) throws CreateException, ValidationException, RemoteException, NoSuchMethodException  {
		try {	  
		  Method m = homeClass.getDeclaredMethod("create", detailsClass2);
		  Object [] args = { map };
		  return m.invoke(home, args);
		}
		catch (InvocationTargetException ex) {
		  Throwable th = ex.getTargetException();
		  try {
		  	throw th;
		  }
		  catch (CreateException ex2) {
		  	throw ex2;
		  }
		  catch (ValidationException ex2) {
		  	throw ex2;
		  }
		  catch (RemoteException ex2) {
		  	throw ex2;
		  }
		  catch (Throwable ex2) {
		  	throw new RemoteException(ex2.getLocalizedMessage(), ex2);
		  }
		}
		catch (NoSuchMethodException ex) {
		  throw ex;
		}
		catch (Exception ex) {
		  throw new RemoteException(ex.getLocalizedMessage(), ex);
		}
  }
  
}
