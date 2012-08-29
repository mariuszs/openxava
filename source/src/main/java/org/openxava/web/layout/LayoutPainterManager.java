/**
 * 
 */
package org.openxava.web.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.openxava.view.View;

/**
 * Painter manager. Parses and paints views.
 * Views are painted from top to bottom, left to right.
 * @author Federico Alcantara
 *
 */
public class LayoutPainterManager {
	/** 
	 * Render the view.
	 * @param view Originating view.
	 * @param pageContext page context.
	 * @return True if a suitable parser / painter combination is found and used.
	 */
	public boolean renderView(View view, PageContext pageContext) {
		boolean returnValue = false;
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		ILayoutParser parser = 	LayoutFactory.getLayoutParserInstance(request);
		if (parser != null) {
			ILayoutPainter painter = LayoutFactory.getLayoutPainterInstance(
					(HttpServletRequest) pageContext.getRequest());
			if (painter != null) {
				returnValue = true;
				Collection<LayoutElement> elements = parser.parseView(view, pageContext);
				painter.initialize(view, pageContext);
				renderElements(painter, elements, view, pageContext);
				painter.finalize(view, pageContext);
			}
		}
		return returnValue;
	}

	/**
	 * Render a section.
	 * @param view Originating view.
	 * @param pageContext page context.
	 * @return True if a suitable parser / painter combination is found and used.
	 */
	public boolean renderSection(View view, PageContext pageContext) {
		boolean returnValue = false;
		ILayoutPainter painter = LayoutFactory.getLayoutPainterInstance(
				(HttpServletRequest) pageContext.getRequest());
		if (painter != null) {
			returnValue = true;
			painter.initialize(view, pageContext);
			LayoutElement element = new LayoutElement(view, 0);
			element.setElementType(LayoutElementType.SECTIONS_START);
			element.setSections(true);
			element.setView(view);
			Collection<LayoutElement> elements = new ArrayList<LayoutElement>();
			elements.add(element);
			renderElements(painter, elements, view, pageContext);
			painter.finalize(view, pageContext);
		}
		return returnValue;
	}
	
	/**
	 * Render each element.
	 * @param painter Painter which render the elements.
	 * @param elements Collection of layout elements.
	 */
	public void renderElements(ILayoutPainter painter, Collection<LayoutElement> elements, View view, PageContext pageContext) {
		painter.setPainterManager(this);
		Iterator<LayoutElement> it = elements.iterator();
		while(it.hasNext()) {
			LayoutElement element = (LayoutElement) it.next();
			switch(element.getElementType()) {
				case VIEW_START: painter.startView(element); break;
				case VIEW_END: painter.endView(element); break;
				case GROUP_START: painter.startGroup(element); break;
				case GROUP_END: painter.endGroup(element); break;
				case FRAME_START: painter.startFrame(element); break;
				case FRAME_END: painter.endFrame(element); break;
				case COLLECTION_START: painter.startCollection(element); break;
				case COLLECTION_END: painter.endCollection(element); break;
				case SECTIONS_START: painter.startSections(element); break;
				case SECTIONS_END: painter.endSections(element); break;
				case ROW_START: painter.startRow(element); break;
				case ROW_END: painter.endRow(element); break;
				case COLUMN_START: painter.startColumn(element); break;
				case COLUMN_END: painter.endColumn(element); break;
				case CELL_START: painter.startCell(element); break;
				case CELL_END: painter.endCell(element); break;
			}
		}
	}
	
}
