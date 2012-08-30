/**
 * 
 */
package org.openxava.web.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.controller.ModuleContext;
import org.openxava.model.meta.MetaCollection;
import org.openxava.model.meta.MetaMember;
import org.openxava.model.meta.MetaProperty;
import org.openxava.model.meta.MetaReference;
import org.openxava.util.Is;
import org.openxava.view.View;
import org.openxava.view.meta.MetaGroup;
import org.openxava.view.meta.PropertiesSeparator;
import org.openxava.web.Ids;

/**
 * Layout manager, class to prepare view presentation
 * 
 * @author Juan Mendoza and Federico Alcantara
 *
 */
public class DefaultLayoutParser implements ILayoutParser {

	private static Log LOG = LogFactory.getLog(DefaultLayoutParser.class);

	private String propertyInReferencePrefix = "";
	private String groupLabel;
	private Collection<LayoutElement> elements;
	private HttpServletRequest request;
	private ModuleContext context;
	private boolean beginGroup = false;
	private boolean endGroup = false;
	private boolean beginFrame = false;
	private boolean endFrame = false;
	private boolean editable = false;
	private int groupLevel = 0;
	private boolean columnStarted = false;
	private LayoutElement currentRow = null;
	private Stack<LayoutElement> containersStack = new Stack<LayoutElement>();
	
	public DefaultLayoutParser() {
	}
	
	/**
	 * Parses the layout in order to determine its size. No rendering
	 * occurs in this phase.
	 * @param view Originating view.
	 * @param pageContext pageContext.
	 * @return returnValue Integer value containing the count
	 */
	public Collection<LayoutElement> parseView(View view, PageContext pageContext) {
		request = (HttpServletRequest)pageContext.getRequest();
		context = (ModuleContext) request.getSession().getAttribute("context");
		parseLayout(view);
		if (view.hasSections()) {
			addLayoutElement(createSectionMarker(view));
			addLayoutElement(createMarker(view, LayoutElementType.SECTIONS_END));
		}
		for (LayoutElement readElement : elements) {
			System.out.println(StringUtils.repeat("    ", readElement.getGroupLevel()) 
					+ readElement.toString());
		}
		return elements;
	}

	/**
	 * Calculate cells by rows.
	 * 
	 * @param view. View to process it metamembers.
	 */
	private void parseLayout(View view) {
		groupLevel = 0;
		elements = new ArrayList<LayoutElement>();
		addLayoutElement(createBeginViewMarker(view));
		parseMetamembers(view.getMetaMembers(), view, false, true);
		addLayoutElement(createEndViewMarker(view));
	}

	/**
	 * Parses each meta member in order to get a hint of the view to display.
	 * 
	 * @param metaMembers. Metamembers to processed.
	 * @param view. View to be processed.
	 * @param descriptionsList. True if the meta property is a descriptionsList
	 * 
	 */
	@SuppressWarnings("rawtypes")
	private void parseMetamembers(Collection metaMembers, View view, boolean descriptionsList, boolean rowStarted) {
		boolean displayAsDescriptionsList = descriptionsList;
		boolean rowEnded = true;
		Iterator it = metaMembers.iterator();
		while (it.hasNext()) {
			MetaMember m = (MetaMember) it.next();
			if (rowStarted && rowEnded) {
				currentRow = createMarker(view, LayoutElementType.ROW_START);
				groupLevel++;
				addLayoutElement(currentRow);
				rowEnded = false;
			}
			if (!PropertiesSeparator.INSTANCE.equals(m)) {
				if (m instanceof MetaProperty) {
					MetaProperty p = (MetaProperty) m;
					setEditable(view.isEditable(p));
					boolean hasLabel = getCount(p, view) > 0;
					if (hasLabel) {
						if (columnStarted) {
							addLayoutElement(createEndColumn(view));
						} 
						addLayoutElement(createStartColumn(view));
						columnStarted = true;
					}
					addLayoutElement(createProperty(p, displayAsDescriptionsList, view));
					addLayoutElement(createMarker(view, LayoutElementType.CELL_END));
					this.groupLabel = "";
				}
			  	if (m instanceof MetaReference) {
					MetaReference ref = (MetaReference) m;
					try {
						String viewObject = getViewObject(view) + "_" + ref.getName();
						View subView = view.getSubview(ref.getName());
						propertyInReferencePrefix = view.getPropertyPrefix() + ref.getName() + ".";
						if (subView != null) {
							subView.setPropertyPrefix(propertyInReferencePrefix);
							subView.setViewObject(viewObject);
						}
						context.put(request, subView.getViewObject(), subView);
						if (subView.isFrame()) {
							if (columnStarted) {
								addLayoutElement(createEndColumn(view));
							} 
							addLayoutElement(createStartColumn(view));
					  		addLayoutElement(createBeginFrame(ref, view, ""));
						}
						parseMetamembers(subView.getMetaMembers(), subView, view.displayAsDescriptionsList(ref), subView.isFrame());
						if (subView.isFrame()) {
					  		addLayoutElement(createEndFrame(ref, view));
							addLayoutElement(createEndColumn(view));
							columnStarted = false;
						}
						propertyInReferencePrefix = "";
					} catch (Exception ex) {
						LOG.info("Sub-view not found: " + ref.getName());
					}
			  	}
				if (m instanceof MetaGroup) {
					MetaGroup group = (MetaGroup) m;
					View subView = view.getGroupView(group.getName());
					String viewObject = getViewObject(view) + "_" + group.getName();
					groupLabel = group.getLabel(request);
					if (subView != null) {
						subView.setPropertyPrefix(view.getPropertyPrefix());
						subView.setViewObject(viewObject);
					}
					context.put(request, subView.getViewObject(), subView);
			  		addLayoutElement(createBeginGroup(group, subView, groupLabel));
					parseMetamembers(group.getMetaView().getMetaMembers(), subView, false, true);
			  		addLayoutElement(createEndGroup(group, subView));
				}
				if (m instanceof MetaCollection) {
					addLayoutElement(createCollection(m, view));
					addLayoutElement(createMarker(view, LayoutElementType.COLLECTION_END));
				}
			} else {
				if (columnStarted) {
					addLayoutElement(createEndColumn(view));
					columnStarted = false;
				}
				if (rowStarted & !rowEnded) {
					addLayoutElement(createEndRowMarker(view));
					rowEnded = true;
				}
			}
		}
		if (columnStarted) {
			addLayoutElement(createEndColumn(view));
			columnStarted = false;
		}
  		if (rowStarted & !rowEnded) {
			addLayoutElement(createEndRowMarker(view));
			rowEnded = true;
  		}
	}

	/**
	 * Method to count special attributes
	 * 
	 * @param p. Metaproperty to process
	 * @param view. View to get special values.
	 * @return returnValue Integer value containing the count
	 */
	private int getCount(MetaProperty p, View view) {
		int returnValue = 0;
		int labelFormat = view.getLabelFormatForProperty(p);
		if (labelFormat != 2) { // No label required
			if (p.getLabel(view.getRequest()).length() > 0) {
				returnValue += 1;
			}
		}
		return returnValue;
	}

	/**
	 * Method to get the viewObject of a view.
	 * 
	 * @param view. View to process.
	 * @return returnValue. Value with viewobject.
	 */
	private String getViewObject(View view) {
		String returnValue = view.getViewObject();
		if (returnValue == null) {
			returnValue = LayoutKeys.LAYOUT_DEFAULT_VIEW_NAME;
		}
		return returnValue;
	}

	/**
	 * Creates a begin view marker.
	 * @param view Originating view.
	 * @return Created layout element.
	 */
	private LayoutElement createBeginViewMarker(View view) {
		LayoutElement returnValue = new LayoutElement(view, groupLevel, LayoutElementType.VIEW_START);
		groupLevel++;
		containersStack.push(returnValue);
		return returnValue;
	}
	
	/**
	 * Creates an end view marker.
	 * @param view Originating view.
	 * @return Created layout element.
	 */
	private LayoutElement createEndViewMarker(View view) {
		containersStack.pop();
		groupLevel--;
		return new LayoutElement(view, groupLevel, LayoutElementType.VIEW_END);
	}
	
	/**
	 * 
	 * @param m
	 * @param view
	 * @param label
	 * @return
	 */
	private LayoutElement createBeginGroup(MetaGroup metaGroup, View view, String label) {
		currentRow.setMaxRowColumnsCount(currentRow.getMaxRowColumnsCount() + 1);
		LayoutElement returnValue = createMetaMemberElement(metaGroup, view, LayoutElementType.GROUP_START);
		groupLevel++;
		containersStack.push(returnValue);
		return returnValue;
	}
	
	private LayoutElement createEndGroup(MetaGroup metaGroup, View view) {
		containersStack.pop();
		groupLevel--;
		LayoutElement returnValue = createMetaMemberElement(metaGroup, view, LayoutElementType.GROUP_END);
		return returnValue;
	}
	
	private LayoutElement createBeginFrame(MetaReference reference, View view, String label) {
		LayoutElement returnValue = createMetaMemberElement(reference, view, LayoutElementType.FRAME_START);
		groupLevel++;
		containersStack.push(returnValue);
		return returnValue;
	}
	
	private LayoutElement createEndFrame(MetaReference metaReference, View view) {
		containersStack.pop();
		groupLevel--;
		LayoutElement returnValue = createMetaMemberElement(metaReference, view, LayoutElementType.FRAME_END);;
		return returnValue;
	}

	/**
	 * Returns a marker element. It basically has its type and view properties set.
	 * @param view Originating view.
	 * @param elementType Element type.
	 * @return Newly created element. Never null.
	 */
	private LayoutElement createMetaMemberElement(MetaMember m, View view, LayoutElementType elementType) {
		LayoutElement returnValue = new LayoutElement(view, groupLevel, elementType);
		returnValue.setView(view);
		returnValue.setPropertyPrefix("");
		returnValue.setLabel(m.getLabel());
		returnValue.setName(m.getName());
		return returnValue;
	}
	
	/**
	 * Creates the end of row.
	 * @param view
	 * @param cellsCount
	 */
	private LayoutElement createEndRowMarker(View view) {
		groupLevel--;
		LayoutElement currentContainer = containersStack.peek();
		int maxRowCellsCount = currentRow.getMaxRowColumnsCount();
		if (maxRowCellsCount > currentContainer.getMaxContainerColumnsCount()) {
			currentContainer.setMaxContainerColumnsCount(maxRowCellsCount);
		}
		return createMarker(view, LayoutElementType.ROW_END);
	}

	/**
	 * Create element for section.
	 * 
	 * @param view. View object.
	 * @return returnValue. LayoutElement.
	 */
	private LayoutElement createSectionMarker(View view) {
		LayoutElement returnValue = new LayoutElement(view, groupLevel);
		returnValue.setSections(true);
		returnValue.setView(view);
		returnValue.setElementType(LayoutElementType.SECTIONS_START);
		return returnValue;
	}
	
	private LayoutElement createStartColumn(View view) {
		LayoutElement returnValue = createMarker(view, LayoutElementType.COLUMN_START);
		currentRow.setMaxRowColumnsCount(currentRow.getMaxRowColumnsCount() + 1);
		groupLevel++;
		return returnValue;
	}
	
	private LayoutElement createEndColumn(View view) {
		groupLevel--;
		return createMarker(view, LayoutElementType.COLUMN_END);
	}

	/**
	 * Returns a marker element. It basically has its type and view properties set.
	 * @param view Originating view.
	 * @param elementType Element type.
	 * @return Newly created element. Never null.
	 */
	private LayoutElement createMarker(View view, LayoutElementType elementType) {
		LayoutElement returnValue = new LayoutElement(view, groupLevel, elementType);
		returnValue.setView(view);
		return returnValue;
	}
	
	/**
	 * Method to create layout elements.
	 * 
	 * @param m. Metamember to process
	 * @param section. If view is a section
	 * @param frame. If view has frame.
	 * @param descriptionsList. If view must be display as description list.
	 * @param view. View with special meaning.
	 * @return returnValue. Layout element.
	 */
	private LayoutElement createProperty(MetaMember m, boolean descriptionsList, View view) {
		LayoutElement returnValue = new LayoutElement(view, groupLevel);
		returnValue.setFrame(false);
		returnValue.setSections(false);
		returnValue.setView(view);
		returnValue.setElementType(LayoutElementType.ROW_START);
		String referenceForDescriptionsList = view.getPropertyPrefix();
		MetaProperty p = (MetaProperty) m;
		String propertyPrefix = propertyInReferencePrefix;
		String propertyLabel = view.getLabelFor(p);
		if (propertyLabel == null) {
			propertyLabel = p.getLabel();
		}
		returnValue.setElementType(LayoutElementType.CELL_START);
		returnValue.setLabel(groupLabel);
		if (Is.empty(propertyPrefix)) {
			propertyPrefix = referenceForDescriptionsList;
		}
		String propertyKey= Ids.decorate(
				request.getParameter("application"),
				request.getParameter("module"),
				propertyPrefix + p.getName());
		returnValue.setMetaProperty(p);

		try {
			if (p.isKey() && view.isRepresentsEntityReference()) {
				returnValue.setSearch(view.isSearch());
				returnValue.setCreateNew(view.isCreateNew());
				returnValue.setModify(view.isModify());
			}
			if (view.getPropertyPrefix() == null) {
				view.setPropertyPrefix("");
			}
			returnValue.setActions(view.propertyHasActions(p));
			returnValue.setEditable(isEditable());//(view.isEditable(p)); // Must confirm this
			returnValue.setSearchAction(view.getSearchAction());
			returnValue.setLabel(propertyLabel);
			returnValue.setLabelFormat(view.getLabelFormatForProperty(p));
			returnValue.setThrowPropertyChanged(view.throwsPropertyChanged(p));
			returnValue.setPropertyKey(propertyKey);
			returnValue.setPropertyPrefix(view.getPropertyPrefix());
			returnValue.setLastSearchKey(view.isLastSearchKey(p));
			returnValue.setDisplayAsDescriptionsList(descriptionsList);
			if (returnValue.isEditable()) {
				returnValue.setActionsNameForReference(getActionsNameForReference(view, view.isLastSearchKey(p)));
			}
			returnValue.setActionsNameForProperty(getActionsNameForProperty(view, p, returnValue.isEditable()));
			if (referenceForDescriptionsList.length() > 1) {
				referenceForDescriptionsList = referenceForDescriptionsList.substring(0, referenceForDescriptionsList.length() - 1);
			}
			returnValue.setReferenceForDescriptionsList(referenceForDescriptionsList);
		} catch (Exception ex) {
			LOG.warn("Maybe this is a separator:" + p.getName());
		}
		returnValue.setGroupLevel(groupLevel);
		return returnValue;
	}
	
	/**
	 * Method to create collection layout elements.
	 * 
	 * @param m. Metamember to process
	 * @param section. If view is a section
	 * @param frame. If view has frame.
	 * @param descriptionsList. If view must be display as description list.
	 * @param view. View with special meaning.
	 * @return returnValue. Layout element.
	 */
	private LayoutElement createCollection(MetaMember m, View view) {
		LayoutElement returnValue = new LayoutElement(view, groupLevel);
		returnValue.setFrame(false);
		returnValue.setSections(false);
		returnValue.setView(view);
		if (Is.empty(view.getMemberName())) {
			view.setMemberName("");
		}
		if (Is.empty(view.getPropertyPrefix())) {
			view.setPropertyPrefix("");
		}
		MetaCollection collection = (MetaCollection) m;
		returnValue.setElementType(LayoutElementType.COLLECTION_START);
		returnValue.setMetaCollection(collection);
		returnValue.setLabel(collection.getLabel(request));
		returnValue.setView(view);
		returnValue.setFrame(!view.isSection() || view.getMetaMembers().size() > 1);
		return returnValue;
	}
	

	@SuppressWarnings("rawtypes")
	private Collection getActionsNameForProperty(View view, MetaProperty p,
			boolean editable) {
		Collection<String> returnValues = new ArrayList<String>();
		for (java.util.Iterator itActions = view.getActionsNamesForProperty(p, editable).iterator(); itActions.hasNext();) {
			returnValues.add((String) itActions.next());
		}
		return returnValues;
	}

	@SuppressWarnings("rawtypes")
	private Collection<String> getActionsNameForReference(View view, boolean lastSearchKey) {
		Collection<String> returnValues = new ArrayList<String>();
		for (java.util.Iterator itActions = view.getActionsNamesForReference(lastSearchKey).iterator(); itActions.hasNext();) {
			returnValues.add((String) itActions.next());
		}
		return returnValues;
	}

	private void addLayoutElement(LayoutElement e) {
		if (elements == null) {
			elements = new ArrayList<LayoutElement>();
		}
		elements.add(e);
	}

	/**
	 * @return the elements
	 */
	public Collection<LayoutElement> getElements() {
		return elements;
	}

	/**
	 * @param elements the elements to set
	 */
	public void setElements(Collection<LayoutElement> elements) {
		this.elements = elements;
	}

	/**
	 * @param beginGroup the beginGroup to set
	 */
	public void setBeginGroup(boolean beginGroup) {
		this.beginGroup = beginGroup;
	}

	/**
	 * @return the beginGroup
	 */
	public boolean isBeginGroup() {
		return beginGroup;
	}

	/**
	 * @param endGroup the endGroup to set
	 */
	public void setEndGroup(boolean endGroup) {
		this.endGroup = endGroup;
	}

	/**
	 * @return the endGroup
	 */
	public boolean isEndGroup() {
		return endGroup;
	}

	/**
	 * @param beginFrame the beginFrame to set
	 */
	public void setBeginFrame(boolean beginFrame) {
		this.beginFrame = beginFrame;
	}

	/**
	 * @return the beginFrame
	 */
	public boolean isBeginFrame() {
		return beginFrame;
	}

	/**
	 * @param endFrame the endFrame to set
	 */
	public void setEndFrame(boolean endFrame) {
		this.endFrame = endFrame;
	}

	/**
	 * @return the endFrame
	 */
	public boolean isEndFrame() {
		return endFrame;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @param editable the editable to set
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * @return the request
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

}
