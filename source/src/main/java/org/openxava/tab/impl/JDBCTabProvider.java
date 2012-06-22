package org.openxava.tab.impl;

import java.rmi.*;
import java.sql.*;
import java.util.*;

import javax.ejb.*;



import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * An <code>ITabProvider</code> that obtain data via JDBC. <p>
 *
 * It is a JavaBean and allows set properties as table name, fields,
 * search condition, etc. <br>
 *
 * Before use this object is advisable call to {@link #invariant}.<br>
 *
 * @author  Javier Paniza
 */

public class JDBCTabProvider implements ITabProvider, java.io.Serializable {

	private static Log log = LogFactory.getLog(JDBCTabProvider.class);
	private static final int DEFAULT_CHUNK_SIZE = 50;	

	private String select; // Select ... from ...
	private String selectSize;
	private String table;
	private String[] fields;
	private String[] conditions;
	private Object[] key;
	private IConnectionProvider connectionProvider;
	private int chunkSize = DEFAULT_CHUNK_SIZE;
	private int current;  
	private boolean eof = true;
	private PreparedStatement ps;
	
	
	public void search(int index, Object key)
		throws FinderException, RemoteException {
		try {
			search(conditions[index], key);
		}
		catch (IndexOutOfBoundsException ex) {
			throw new IndexOutOfBoundsException(
					XavaResources.getString("tab_search_not_found", new Integer(index)));
		}
	}
	
	public void search(String condition, Object key) throws FinderException, RemoteException {		
		current = 0;
		eof = false;
		this.key = toArray(key);					
		condition = condition == null ? "" : condition.trim(); 
		if (condition.equals(""))
			select = generateSelect(); // for all
		else if (condition.toUpperCase().startsWith("SELECT")) 
			select = condition;
		else
			select = generateSelect() + " WHERE " + condition;					
		selectSize = createSizeSelect(select);
	}
	
	private String generateSelect() {
		if (table == null
			|| table.trim().equals("")
			|| fields == null
			|| fields.length == 0) {
			return null;
		}

		StringBuffer newSelect = new StringBuffer("SELECT ");
		int i;
		for (i = 0; i < fields.length - 1; i++) {
			newSelect.append(fields[i]);
			newSelect.append(", ");
		}
		newSelect.append(fields[i]); 
		newSelect.append(" FROM ");
		newSelect.append(table);
		return newSelect.toString();
	}
	
	public String[] getFields() {
		return fields;
	}
	
	/**
	 * List of codition in SQL format. <p>
	 * 
	 * The condition can be:
	 * <ul>
	 * <li> A complete SQL SELECT.
	 * <li> The SELECT sentence from WHERE (not included). In this case
	 * 			the complete sentece is formed form the values in {@link #getFields() fields} 
	 * 			and {@link @getTable table}.
	 * <li> Nothing. In this case a SELECT of all records is assumed.
	 * </ul>
	 *
	 * Although complete SELECTs are used in all cases is necessary to specify
	 * a correct value for {@link #getFields() fields} and {@link @getTable table}.<br>
	 * When you use complete SELECTs the table and fields used in SELECT must to
	 * match with {@link #getFields() fields} and {@link @getTable table}.<br>
	 */
	public String[] getConditions() {
		return conditions;
	}
	
	/** To obtaint JDBC connections.  */
	public IConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}
	
	/** Database table name.  */
	public String getTable() {
		return table;
	}
	
	/** Size of chunk returned by {@link #nextChunk}. */
	public int getChunkSize() {
		return chunkSize;
	}
	/**
	 * Verify invariant. <p>
	 * <b>Invariant:</b>
	 * <ul>
	 * <li> table != null
	 * <li> fields != null && fields.length > 0
	 * <li> conditions != null && conditions.length > 0
	 * <li> connectionProvider != null
	 * </ul>
	 *
	 * @exception IllegalStateException  If invariant is broken
	 */
	public void invariant() throws IllegalStateException {
		if (table == null)
			throw new IllegalStateException(XavaResources.getString("tabprovider_table_required"));
		if (fields == null)
			throw new IllegalStateException(XavaResources.getString("tabprovider_field_required"));
		if (conditions == null || conditions.length == 0)
			throw new IllegalStateException(XavaResources.getString("tabprovider_condition_required"));
		if (connectionProvider == null) {
			throw new IllegalStateException(XavaResources.getString("tabprovider_connection_provider_required"));
		}
	}
	
	/**
	 * Position the <code>ResultSet</code> in the appropiate part. <p>
	 *
	 * @param rs  <tt>!= null</tt>
	 */
	private void position(ResultSet rs) throws SQLException {
		//rs.absolute(current); // this only run with TYPE_SCROLL_INSENSITIVE, and this is very slow on executing query in some databases
		for (int i = 0; i < current; i++) {
			if (!rs.next())
				return;
		}
	}
	/** Table fields to include. */
	public void setFields(String[] fields) {
		this.fields = fields;
	}
	
	/**
	 * List of codition in SQL format. <p>
	 * 
	 * The condition can be:
	 * <ul>
	 * <li> A complete SQL SELECT.
	 * <li> The SELECT sentence from WHERE (not included). In this case
	 * 			the complete sentece is formed form the values in {@link #getFields() fields} 
	 * 			and {@link @getTable table}.
	 * <li> Nothing. In this case a SELECT of all records is assumed.
	 * </ul>
	 *
	 * Although complete SELECTs are used in all cases is necessary to specify
	 * a correct value for {@link #getFields() fields} and {@link @getTable table}.<br>
	 * When you use complete SELECTs the table and fields used in SELECT must to
	 * match with {@link #getFields() fields} and {@link @getTable table}.<br>
	 */
	public void setConditions(String[] conditions) {
		this.conditions = conditions;
	}
	
	/** To obtaint JDBC connections.  */
	public void setConnectionProvider(IConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	/** Database table name.  */
	public void setTable(String tabla) {
		this.table = tabla;
	}
	/** Size of chunk returned by {@link #nextChunk}. */
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	
	/**
	 * Creates a <code>ResultSet</code> with the next block data. <p>
	 *
	 * @param  con  <tt>!= null</tt>
	 */
	private ResultSet nextBlock(Connection con) throws SQLException {
		
		// assert(con)
		
		/* Not in this way because TYPE_SCROLL_INTENSIVE has a very poor performance
		   in some databases
		  PreparedStatement ps =
			con.prepareStatement(
				select,
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		*/
		
		if (keyHasNulls()) return null; // Because some databases (like Informix) have problems setting nulls
				
		ps = con.prepareStatement(select); 
		// Fill key values
		StringBuffer message =
			new StringBuffer("[JDBCTabProvider.nextBlock] ");
		message.append(XavaResources.getString("executing_select", select));		
		
		for (int i = 0; i < key.length; i++) {
			ps.setObject(i + 1, key[i]);
			message.append(key[i]);
			if (i < key.length - 1)
				message.append(", ");
		}
		log.debug(message);
		
		if ((current + chunkSize) < Integer.MAX_VALUE) { 
			ps.setMaxRows(current + chunkSize + 1); 
		}		
		ResultSet rs = ps.executeQuery();						
		position(rs);

		return rs;
	}

	private boolean keyHasNulls() {
		if (key == null) return true;
		for (int i=0; i < key.length; i++) {
			if (key[i] == null) return true;
		}
		return false;
	}

	// Implementa ITabProvider
	public DataChunk nextChunk() throws RemoteException {		
		if (select == null || eof) { // search not called yet
			return new DataChunk(new Vector(), true, current); // Empty
		}
		ResultSet resultSet = null;
		Connection con = null;
		try {
			con = connectionProvider.getConnection();
			resultSet = nextBlock(con);
			List data = new ArrayList();
			int f = 0;
			int nc = fields.length;
			while (resultSet != null && resultSet.next()) {
				if (++f > chunkSize) {
					current += chunkSize;
					return new DataChunk(data, false, current);
				}
				Object[] row = new Object[nc];
				for (int i = 0; i < nc; i++) {					
					row[i] = resultSet.getObject(i + 1);
				}
				data.add(row);
			}
			// No more
			eof = true;
			return new DataChunk(data, true, current);
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("select_error", select), ex);
			throw new RemoteException(XavaResources.getString("select_error", select));
		}
		finally {
			try {
				if (resultSet != null) resultSet.close();
				if (ps != null) {
					ps.close();
					ps = null;
				}
			}
			catch (Exception ex) {
			}
			try {
				con.close();
			}
			catch (Exception ex) {
			}
		}
	}
	
	/**
	 * Return an array from the sent object.
	 * Si obj == null return Object[0]
	 */
	private Object[] toArray(Object obj) {
		if (obj == null)
			return new Object[0];
		if (obj instanceof Object[]) {
			return (Object[]) obj;
		}
		else {
			Object[] rs = { obj };
			return rs;
		}
	}
	public int getCurrent() {
		return current;
	}

	public void setCurrent(int i) {
		current = i;
	}
	public int getResultSize() throws RemoteException { 
		return executeNumberSelect(this.selectSize, "tab_result_size_error").intValue();
	}
	
	public Number getSum(String column) throws RemoteException { 
		return executeNumberSelect(createSumSelect(column), "column_sum_error"); 		
	}
	
	private Number executeNumberSelect(String select, String errorId) throws RemoteException {
		if (select == null || keyHasNulls()) return 0;						
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			con = connectionProvider.getConnection();
			ps = con.prepareStatement(select);			
			for (int i = 0; i < key.length; i++) {
				ps.setObject(i + 1, key[i]);				
			}			
			rs = ps.executeQuery();
			rs.next();
			return (Number) rs.getObject(1);
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(XavaResources.getString(errorId));
		}
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				log.error(XavaResources.getString("close_resultset_warning"), ex);
			}			
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception ex) {
				log.error(XavaResources.getString("close_statement_warning"), ex);
			}
			try {
				con.close();
			}
			catch (Exception ex) {
				log.error(XavaResources.getString("close_connection_warning"), ex);
			}
		}						
	}
	
	private String createSizeSelect(String select) {
		if (select == null) return null;		
		String selectUpperCase = Strings.changeSeparatorsBySpaces(select.toUpperCase());
		int iniFrom = selectUpperCase.indexOf(" FROM ");
		int end = selectUpperCase.indexOf("ORDER BY ");
		StringBuffer sb = new StringBuffer("SELECT COUNT(*) ");
		if (end < 0) sb.append(select.substring(iniFrom));
		else sb.append(select.substring(iniFrom, end - 1));
		return sb.toString();
	}
	
	private String createSumSelect(String column) { 
		if (select == null) return null;		
		String selectUpperCase = Strings.changeSeparatorsBySpaces(select.toUpperCase());
		int iniFrom = selectUpperCase.indexOf(" FROM ");
		int end = selectUpperCase.indexOf("ORDER BY ");
		StringBuffer sb = new StringBuffer("SELECT SUM(");
		sb.append(column); 
		sb.append(") ");
		if (end < 0) sb.append(select.substring(iniFrom));
		else sb.append(select.substring(iniFrom, end - 1));
		return sb.toString();
	}
	
	public void reset() throws RemoteException {
		current = 0;
		eof = false;
	}

}
