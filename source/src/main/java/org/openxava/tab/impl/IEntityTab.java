package org.openxava.tab.impl;

/**
 * Provides bussines component data in tabular format. <p>
 * 
 * The data is handle via <@link IXTableModel>,
 * that is obtained from <@link IWithXTableModel#getTable>
 * This table can maintain a connection to the source <code>IEntityTab</code>
 * in order to obtaining on demand data from it. <p>
 * 
 * <h4>Example:</h4>
 * This is a example of use, but not implementation.
 * <pre>
 * IEntityTab tab = obtaineEntityTab(); 
 * tableModel = tab.getTable(); 
 * jtable.setModel(tableModel); // In this case we assign to a swing table
 *
 * tab.search(0, null); // We execute the 0 consult
 * tableModel.refresh(); // The data is loaded in table model and then displayed in jtable
 * </pre>
 *
 * @author  Javier Paniza
 */

public interface IEntityTab extends IWithXTableModel, ISearch {

}
