/**
 * 
 */
package org.openxava.web.layout;

import java.util.EmptyStackException;
import java.util.Stack;

import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.view.View;

/**
 * Base class for painters.
 * @author Juan Mendoza and Federico Alcantara
 *
 */
public abstract class AbstractBasePainter implements ILayoutPainter {
	private static final Log LOG = LogFactory.getLog(AbstractBasePainter.class);
	private Stack<LayoutElement> containersStack;
	private Stack<LayoutElement> rowsStack;
	private View view;
	private PageContext pageContext;
	private LayoutPainterManager painterManager;

	/**
	 * @see org.openxava.web.layout.ILayoutPainter#initialize(org.openxava.view.View, javax.servlet.jsp.PageContext)
	 */
	public void initialize(View view, PageContext pageContext) {
		this.view = view;
		this.pageContext = pageContext;
		setContainersStack(null);
		setRowsStack(null);
	}
	
	public void finalize(View view, PageContext pageContext){
	}

	/**
	 * @see org.openxava.web.layout.ILayoutPainter#setPainterManager(org.openxava.web.layout.LayoutPainterManager)
	 */
	public void setPainterManager(LayoutPainterManager painterManager) {
		this.painterManager = painterManager;
	}

	public LayoutPainterManager getPainterManager() {
		return painterManager;
	}
	
	/**
	 * 
	 * @return Current container.
	 */
	protected LayoutElement getContainer() {
		LayoutElement returnValue = null;
		try {
			returnValue = getContainersStack().peek();
		} catch (EmptyStackException e) {
			LOG.error(e.getMessage(), e);
		}
		return returnValue;
	}
	
	/**
	 * @return Current row.
	 */
	protected LayoutElement getRow() {
		LayoutElement returnValue = null;
		try {
			returnValue = getRowsStack().peek();
		} catch (EmptyStackException e) {
			LOG.error(e.getMessage(), e);
		}
		return returnValue;
	}
	
	/**
	 * Sets element as the current container.
	 * @param layoutContainer.
	 */
	protected void setContainer(LayoutElement layoutElement) {
		getContainersStack().push(layoutElement);
	}
	
	/**
	 * Release current container and sets the previous one.
	 */
	protected void unsetContainer() {
		try {
			getContainersStack().pop();
		} catch (EmptyStackException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	/**
	 * Sets the element as the current row and resets its column count to 0.
	 * @param layoutElement
	 */
	protected void setRow(LayoutElement layoutElement) {
		layoutElement.setRowCurrentColumnsCount(0);
		getRowsStack().push(layoutElement);
	}

	/**
	 * Release current row and sets the previous one.
	 */
	protected void unsetRow() {
		try {
			getRowsStack().pop();
		} catch (EmptyStackException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * It is a stack of processed containers, the last element represents
	 * the current container.
	 * @return The current container stack;
	 */
	protected Stack<LayoutElement> getContainersStack() {
		if (containersStack == null) {
			containersStack = new Stack<LayoutElement>();
		}
		return containersStack;
	}

	/**
	 * Sets (or resets) the containers stack.
	 * @param containersStack New container stack to use.
	 */
	protected void setContainersStack(Stack<LayoutElement> containersStack) {
		this.containersStack = containersStack;
	}

	/**
	 * It is a stack of processed rows, where the last element (at the top of the stack)
	 * represents the current row.
	 * @return instance of row stack.
	 */
	protected Stack<LayoutElement> getRowsStack() {
		if (rowsStack == null) {
			rowsStack = new Stack<LayoutElement>();
		}
		return rowsStack;
	}

	/**
	 * Sets or (resets) the row stack.
	 * @param rowsStack New row stack to use.
	 */
	protected void setRowsStack(Stack<LayoutElement> rowsStack) {
		this.rowsStack = rowsStack;
	}
	
	/**
	 * 
	 * @return The current view.
	 */
	protected View getView(){
		return view;
	}
	/**
	 * @return The current page context.
	 */
	protected PageContext getPageContext() {
		return pageContext;
	}
}
