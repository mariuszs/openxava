/**
 * 
 */
package org.openxava.web.layout;

import static org.openxava.web.layout.LayoutJspKeys.ATTRVAL_STYLE_WIDTH_100P;
import static org.openxava.web.layout.LayoutJspKeys.ATTR_CLASS;
import static org.openxava.web.layout.LayoutJspKeys.ATTR_COLSPAN;
import static org.openxava.web.layout.LayoutJspKeys.ATTR_ID;
import static org.openxava.web.layout.LayoutJspKeys.ATTR_LIST;
import static org.openxava.web.layout.LayoutJspKeys.ATTR_SRC;
import static org.openxava.web.layout.LayoutJspKeys.ATTR_STYLE;
import static org.openxava.web.layout.LayoutJspKeys.TAG_DIV;
import static org.openxava.web.layout.LayoutJspKeys.TAG_IMG;
import static org.openxava.web.layout.LayoutJspKeys.TAG_SPAN;
import static org.openxava.web.layout.LayoutJspKeys.TAG_TABLE;
import static org.openxava.web.layout.LayoutJspKeys.TAG_TD;
import static org.openxava.web.layout.LayoutJspKeys.TAG_TR;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.controller.ModuleContext;
import org.openxava.util.Is;
import org.openxava.view.meta.MetaView;
import org.openxava.web.Ids;
import org.openxava.web.taglib.ActionTag;
import org.openxava.web.taglib.DescriptionsListTag;
import org.openxava.web.taglib.EditorTag;
import org.openxava.web.taglib.LinkTag;

/**
 * Implements a basic Painter.
 * @author Federico Alcantara
 *
 */
public class DefaultLayoutPainter extends AbstractJspPainter {
	private static final Log LOG = LogFactory.getLog(DefaultLayoutPainter.class);
	private boolean firstCellPainted = false;
	private Integer lastColumnSpan = 0;
	private int tdPerColumn = 2; // One TD for the label and another for Data and other cells.
	
	/**
	 * @see org.openxava.web.layout.ILayoutPainter#startView(org.openxava.web.layout.LayoutElement)
	 */
	public void startView(LayoutElement element) {
		setContainer(element);
		attributes.clear();
		write(LayoutJspUtils.INSTANCE.startTag(TAG_TABLE, attributes));
	}

	/**
	 * @see org.openxava.web.layout.ILayoutPainter#endView(org.openxava.web.layout.LayoutElement)
	 */
	public void endView(LayoutElement element) {
		write(LayoutJspUtils.INSTANCE.endTag(TAG_TABLE));
	}

	/**
	 * This has the same behavior as the startFrame method.
	 * @see org.openxava.web.layout.ILayoutPainter#startGroup(org.openxava.web.layout.LayoutElement)
	 */
	public void startGroup(LayoutElement element) {
		startFrame(element);
	}

	/**
	 * This has the same behavior as the endFrame method.
	 * @see org.openxava.web.layout.ILayoutPainter#endGroup(org.openxava.web.layout.LayoutElement)
	 */
	public void endGroup(LayoutElement element) {
		endFrame(element);
	}

	/**
	 * Creates the frame. This implementation uses the same style as the original OX design.
	 * @see org.openxava.web.layout.ILayoutPainter#startFrame(org.openxava.web.layout.LayoutElement)
	 */
	public void startFrame(LayoutElement element) {
		attributes.clear();
		// Frame should occupy as many columns as needed. 
		// In this design each column is 3 TD wide.
		// However if this frame is the only one in the row
		// Takes the full size of the view.
		Integer columnSpan = element.getMaxContainerColumnsCount() * tdPerColumn;
		if (getRow().getMaxRowColumnsCount() == 1) {
			columnSpan = getContainer().getMaxContainerColumnsCount() * tdPerColumn;
		}
		attributes.put(ATTR_COLSPAN, columnSpan.toString());
		write(LayoutJspUtils.INSTANCE.startTag(TAG_TD, attributes));
		
		write(getStyle().getFrameHeaderStartDecoration(100));
			write(getStyle().getFrameTitleStartDecoration());
				write(element.getLabel());
			write(getStyle().getFrameTitleEndDecoration());
			write(getStyle().getFrameActionsStartDecoration());
				String frameId = Ids.decorate(getRequest(), "frame_group_" + getView().getPropertyPrefix() + element.getName());
				String frameActionsURL = "frameActions.jsp?frameId=" + frameId + 
					"&closed=" + getView().isFrameClosed(frameId);
				includeJspPage(frameActionsURL);
			write(getStyle().getFrameActionsEndDecoration());
		write(getStyle().getFrameHeaderEndDecoration());
		write(getStyle().getFrameContentStartDecoration(frameId + "content", getView().isFrameClosed(frameId)));
		write(LayoutJspUtils.INSTANCE.startTag(TAG_TABLE));
		setContainer(element);
	}

	/**
	 * @see org.openxava.web.layout.ILayoutPainter#endFrame(org.openxava.web.layout.LayoutElement)
	 */
	public void endFrame(LayoutElement element) {
		write(LayoutJspUtils.INSTANCE.endTag(TAG_TABLE));
		write(getStyle().getFrameContentEndDecoration());
		write(LayoutJspUtils.INSTANCE.endTag(TAG_TD));
		unsetContainer();
	}

	/**
	 * Actually starts a row where all the columns are painted.
	 * @see org.openxava.web.layout.ILayoutPainter#startRow(org.openxava.web.layout.LayoutElement)
	 */
	public void startRow(LayoutElement element) {
		setRow(element);
		attributes.clear();
		write(LayoutJspUtils.INSTANCE.startTag(TAG_TR, attributes));
	}

	/**
	 * @see org.openxava.web.layout.ILayoutPainter#endRow(org.openxava.web.layout.LayoutElement)
	 */
	public void endRow(LayoutElement element) {
		// Separation line
		attributes.clear();
		attributes.put(ATTR_CLASS, getStyle().getLayoutRowSpacer());
		write(LayoutJspUtils.INSTANCE.startTag(TAG_TR, attributes));
		write(LayoutJspUtils.INSTANCE.endTag(TAG_TR));
		unsetRow();
	}

	/**
	 * Each column does not open a table element (TD), this is done by the 
	 * startCell method. However, each column can contain more than one cell,
	 * but only three TD elements are allowed in the column, so the first cell
	 * creates one TD for the left spacer, another TD for the label and 
	 * a final TD for the data and any other remaining cell of the column.
	 * By parsing contiguous cells without labels are considered as part of the column.<br />
	 * In this implementation columns are composed of three TD.
	 * @see org.openxava.web.layout.ILayoutPainter#startColumn(org.openxava.web.layout.LayoutElement)
	 */
	public void startColumn(LayoutElement element) {
		int count = getRow().getRowCurrentColumnsCount() + 1;
		lastColumnSpan = count == getRow().getMaxRowColumnsCount() ? getContainer().getMaxContainerColumnsCount() - count + 1: 0;
		lastColumnSpan = lastColumnSpan * tdPerColumn; // Each column has 3 TD elements.
		getRow().setRowCurrentColumnsCount(count);
		firstCellPainted = false; // to indicate to the cell renderer that the TD pair is about to start.
	}

	/**
	 * In this painter implementation the column does end the last TD. So the cell
	 * implementation must start the first TD but NOT close the last one
	 * @see org.openxava.web.layout.ILayoutPainter#endColumn(org.openxava.web.layout.LayoutElement)
	 */
	public void endColumn(LayoutElement element) {
		write(LayoutJspUtils.INSTANCE.endTag(TAG_TD));
	}

	/**
	 * @see org.openxava.web.layout.ILayoutPainter#startCell(org.openxava.web.layout.LayoutElement)
	 */
	public void startCell(LayoutElement element) {
		if (!firstCellPainted) {
			attributes.clear();
			attributes.put(ATTR_CLASS, getStyle().getLabel() + " " + getStyle().getLayoutLabelCell());
			write(LayoutJspUtils.INSTANCE.startTag(TAG_TD, attributes));
		}
		
		// Left spacer
		startCellSpacer(element, getStyle().getLayoutLabelLeftSpacer());
		
		// Label
		startCellLabel(element);
		
		// Left spacer
		startCellSpacer(element, getStyle().getLayoutLabelRightSpacer());
		
		if (!firstCellPainted) {
			write(LayoutJspUtils.INSTANCE.endTag(TAG_TD));
		}

		// Data. There is no end TD tag this one is closed by the end column method.
		if (!firstCellPainted) {
			attributes.clear();
			attributes.put(ATTR_CLASS, getStyle().getLayoutDataCell());
			if (lastColumnSpan > 0) {
				attributes.put(ATTR_COLSPAN, lastColumnSpan.toString());
			}
			write(LayoutJspUtils.INSTANCE.startTag(TAG_TD, attributes));
		}
		if (element.isDisplayAsDescriptionsList()) {
			startCellDescriptionList(element);
		} else {
			startCellData(element);
		}
		
		// Mark first cell painted
		firstCellPainted = true;
	}

	/**
	 * Paints the cell left spacer.
	 * @param element Representing cell element.
	 */
	private void startCellSpacer(LayoutElement element, String classType) {
		attributes.clear();
		attributes.put(ATTR_CLASS, classType);
		attributes.put(ATTR_SRC, getRequest().getContextPath() + "/xava/images/spacer.gif");
		write(LayoutJspUtils.INSTANCE.startTag(TAG_IMG, attributes));
		write(LayoutJspUtils.INSTANCE.endTag(TAG_IMG));
	}
	
	/**
	 * Paints the cell label.
	 * @param element Representing cell element.
	 */
	private void startCellLabel(LayoutElement element) {
		attributes.clear();
		attributes.put(ATTR_CLASS, getStyle().getLayoutLabel());
		write(LayoutJspUtils.INSTANCE.startTag(TAG_SPAN));
			String label = element.getLabelFormat() != 2 ? element.getLabel() + LayoutJspKeys.CHAR_SPACE : LayoutJspKeys.CHAR_SPACE;
			label = label.replaceAll(" ", LayoutJspKeys.CHAR_SPACE);
			write(label);
			String img = "";
			if (!element.isDisplayAsDescriptionsList()) {
				if (element.getMetaProperty().isKey()) {
					img = "key.gif";
				} else if (element.getMetaProperty().isRequired()) {
					if (element.isEditable()) { // No need to mark it as required, since the user can not change it anyway
						img = "required.gif";
					}
				}
			} else if (element.getMetaProperty().isRequired()) {
				img = "required.gif";
			}
			if (!Is.emptyString(img)) {
				attributes.clear();
				attributes.put(ATTR_SRC, getRequest().getContextPath() + "/xava/images/" + img);
				write(LayoutJspUtils.INSTANCE.startTag(TAG_IMG, attributes));
				write(LayoutJspUtils.INSTANCE.endTag(TAG_IMG));
			}
		write(LayoutJspUtils.INSTANCE.endTag(TAG_SPAN));
		attributes.clear();
		attributes.put(ATTR_ID, Ids.decorate(getRequest(), "error_image_" + element.getMetaProperty().getQualifiedName()));
		write(LayoutJspUtils.INSTANCE.startTag(TAG_SPAN, attributes));
			if (getErrors().memberHas(element.getMetaProperty())) {
				attributes.clear();
				attributes.put(ATTR_SRC, getRequest().getContextPath() + "/xava/images/error.gif");
				write(LayoutJspUtils.INSTANCE.startTag(TAG_IMG, attributes));
				write(LayoutJspUtils.INSTANCE.endTag(TAG_IMG));
			}
		write(LayoutJspUtils.INSTANCE.endTag(TAG_SPAN));
	}
	
	/**
	 * Paints the input controls.
	 * @param element Element to be painted.
	 */
	@SuppressWarnings("rawtypes")
	private void startCellData(LayoutElement element) {
		attributes.clear();
		attributes.put(ATTR_CLASS, getStyle().getLayoutData());
		attributes.put(ATTR_ID, Ids.decorate(getRequest(), "editor_" + element.getPropertyPrefix() + element.getMetaProperty().getName()));
		write(LayoutJspUtils.INSTANCE.startTag(TAG_SPAN, attributes));
		EditorTag editorTag = new EditorTag();
		editorTag.setProperty(element.getPropertyPrefix() + element.getMetaProperty().getName());
		editorTag.setEditable(element.isEditable());
		if (element.isEditable()) {
			if (element.getMetaProperty().isKey()) {
				editorTag.setEditable(element.getView().isKeyEditable());
			}
			if (element.isLastSearchKey() && element.isSearch()) {
				editorTag.setEditable(element.isEditable());
			}
		}
		editorTag.setPageContext(getPageContext());
		editorTag.setThrowPropertyChanged(element.isThrowPropertyChanged());
		try {
			editorTag.doStartTag();
		} catch (JspException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}

		write(LayoutJspUtils.INSTANCE.endTag(TAG_SPAN));
		String propertyPrefix = element.getPropertyPrefix() == null ? "" : element.getPropertyPrefix();
		ActionTag actionTag;
		try {
			if (element.hasActions()) {
				attributes.clear();
				attributes.put("id", Ids.decorate(getRequest(), "property_actions_" + propertyPrefix + element.getMetaProperty().getName()));
				write(LayoutJspUtils.INSTANCE.startTag(TAG_SPAN, attributes));
				if (element.isLastSearchKey()) {
					if (element.isSearch() && element.isEditable()) {
						actionTag = new ActionTag();
						actionTag.setAction(element.getSearchAction());
						actionTag.setArgv("keyProperty="+Ids.undecorate(element.getPropertyKey()));
						actionTag.setPageContext(getPageContext());
						actionTag.doStartTag();
					}
					if (element.isCreateNew() && element.isEditable()) {
						actionTag = new ActionTag();
						actionTag.setAction("Reference.createNew");
						actionTag.setArgv("model=" + 
							element.getMetaProperty().getMetaModel().getName() + ",keyProperty="+Ids.undecorate(element.getPropertyKey()));
						actionTag.setPageContext(getPageContext());
						actionTag.doStartTag();
					}
					if (element.isModify() && element.isEditable()) {
						actionTag = new ActionTag();
						actionTag.setAction("Reference.modify");
						actionTag.setArgv("model=" + 
								element.getMetaProperty().getMetaModel().getName() + ",keyProperty="+Ids.undecorate(element.getPropertyKey()));
						actionTag.setPageContext(getPageContext());
						actionTag.doStartTag();
					}
				}
				if (element.isEditable() && element.getActionsNameForReference().size() > 0) {
					Iterator it = element.getActionsNameForReference().iterator();
					while(it.hasNext()) {
						String action = (String) it.next();
						actionTag = new ActionTag();
						actionTag.setAction(action);
						actionTag.setPageContext(getPageContext());
						actionTag.doStartTag();
					}
				}
				if (element.getActionsNameForProperty().size() > 0) {
					Iterator it = element.getActionsNameForProperty().iterator();
					while(it.hasNext()) {
						String action = (String) it.next();
						actionTag = new ActionTag();
						actionTag.setAction(action);
						actionTag.setArgv("xava.keyProperty=" + Ids.undecorate(element.getPropertyKey()));
						actionTag.setPageContext(getPageContext());
						actionTag.doStartTag();
					}
				}
				write(LayoutJspUtils.INSTANCE.endTag(TAG_SPAN));
			}
		} catch (JspException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Display element as description list.
	 * @param element Element to be displayed.
	 */
	private void startCellDescriptionList(LayoutElement element) {
		DescriptionsListTag descriptionsListTag = new DescriptionsListTag();
		descriptionsListTag.setReference(element.getReferenceForDescriptionsList());
		descriptionsListTag.setPageContext(getPageContext());
		try {
			descriptionsListTag.doStartTag();
		} catch (JspException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * In this implementation nothing is done at cell end.
	 * @see org.openxava.web.layout.ILayoutPainter#endCell(org.openxava.web.layout.LayoutElement)
	 */
	public void endCell(LayoutElement element) {

	}

	/**
	 * Besides the frame handling, it lets the collection.jsp to take care of the collection rendering.
	 * @see org.openxava.web.layout.ILayoutPainter#startCollection(org.openxava.web.layout.LayoutElement)
	 */
	public void startCollection(LayoutElement element) {
		write(LayoutJspUtils.INSTANCE.startTag(TAG_DIV));
		if (element.hasFrame()) {
			write(getStyle().getFrameHeaderStartDecoration(5));
			write(getStyle().getFrameTitleStartDecoration());
			write(getStyle().getFrameTitleEndDecoration());
			write(getStyle().getFrameActionsStartDecoration());
			String frameId = Ids.decorate(getRequest(), "frame_" + element.getView().getPropertyPrefix() + element.getMetaCollection().getName());
			String frameActionsURL = "frameActions.jsp?frameId=" + frameId +
					"&closed=" + element.getView().isFrameClosed(frameId);
			includeJspPage(frameActionsURL);
			write(getStyle().getFrameActionsEndDecoration());
			write(getStyle().getFrameHeaderEndDecoration());
			write(getStyle().getFrameContentStartDecoration(frameId + "content", element.getView().isFrameClosed(frameId)));
		}
		includeJspPage("collection.jsp?collectionName=" + 
				element.getMetaCollection().getName() + 
				"&viewObject=" + 
				element.getView().getViewObject());
		if (element.hasFrame()) {
			write(getStyle().getFrameContentEndDecoration());
		}
		write(LayoutJspUtils.INSTANCE.endTag(TAG_DIV));
	}

	/**
	 * Actually all code is performed in the startCollection method.
	 * However for future implementations this might be useful for adding
	 * features to the collection that might be painted at end of the collection
	 * renderization.
	 * @see org.openxava.web.layout.ILayoutPainter#endCollection(org.openxava.web.layout.LayoutElement)
	 */
	public void endCollection(LayoutElement element) {

	}

	/**
	 * Each section behave as a marker. This is called upon section change or page reload. 
	 * @see org.openxava.web.layout.ILayoutPainter#startSections(org.openxava.web.layout.LayoutElement)
	 */
	@SuppressWarnings("rawtypes")
	public void startSections(LayoutElement element) {
		Collection sections = getView().getSections();
		int activeSection = getView().getActiveSection();

		
		attributes.clear();
		attributes.put(ATTR_ID, Ids.decorate(getRequest(), "sections_" + getView().getViewObject()));
		write(LayoutJspUtils.INSTANCE.startTag(TAG_DIV, attributes));
			attributes.clear();
			attributes.put(ATTR_STYLE, ATTRVAL_STYLE_WIDTH_100P);
			write(LayoutJspUtils.INSTANCE.startTag(TAG_TABLE, attributes));
				write(LayoutJspUtils.INSTANCE.startTag(TAG_TR));
					write(LayoutJspUtils.INSTANCE.startTag(TAG_TD));
						
						attributes.clear();
						attributes.put(ATTR_CLASS, getStyle().getSection());
						write(LayoutJspUtils.INSTANCE.startTag(TAG_DIV, attributes));
							
							attributes.clear();
							attributes.put(ATTR_LIST, getStyle().getSectionTableAttributes());
							write(LayoutJspUtils.INSTANCE.startTag(TAG_TABLE, attributes));
								write(LayoutJspUtils.INSTANCE.startTag(TAG_TR));
									
									write(getStyle().getSectionBarStartDecoration());
									// Loop to paint section(s)
									Iterator itSections = sections.iterator();
									int i = 0;
									while(itSections.hasNext()) {
										MetaView section = (MetaView) itSections.next();
										if (activeSection == i) {
											write(getStyle().getActiveSectionTabStartDecoration(i == 0, !itSections.hasNext()));
											write(section.getLabel(getRequest()));
											write(getStyle().getActiveSectionTabEndDecoration());
										} else {
											try {
												write(getStyle().getSectionTabStartDecoration(i == 0, !itSections.hasNext()));
												String viewObjectArgv = "xava_view".equals(getView().getViewObject())?"":",viewObject=" + getView().getViewObject();
												LinkTag linkTag = new LinkTag();
												linkTag.setAction("Sections.change");
												linkTag.setArgv("activeSection=" + i + viewObjectArgv);
												linkTag.setCssClass(getStyle().getSectionLink());
												linkTag.setCssStyle(getStyle().getSectionLinkStyle());
												linkTag.setPageContext(getPageContext());
												linkTag.doStartTag();
												write(section.getLabel(getRequest()));
												linkTag.doAfterBody();
												linkTag.doEndTag();
												write(getStyle().getSectionTabEndDecoration());
											} catch (JspException e) {
												LOG.error(e.getMessage(), e);
												throw new RuntimeException(e);
											}
										}
										String viewName = getView().getViewObject() + "_section" + i;
										ModuleContext context = (ModuleContext) getPageContext().getSession().getAttribute("context");
										context.put(getRequest(), viewName, getView().getSectionView(i));
										getView().getSectionView(i).setViewObject(viewName);
										getView().getSectionView(i).setPropertyPrefix("");
										// TODO; Verify with Fede why Javier just use parent without care of null on view.getRequest()
										//if (view.getRequest() == null) {
											getView().setRequest(getRequest());
										//}
										i++;
									}
									write(getStyle().getSectionBarEndDecoration());
								write(LayoutJspUtils.INSTANCE.endTag(TAG_TR));
							write(LayoutJspUtils.INSTANCE.endTag(TAG_TABLE));
						write(LayoutJspUtils.INSTANCE.endTag(TAG_DIV));
					write(LayoutJspUtils.INSTANCE.endTag(TAG_TD));
				write(LayoutJspUtils.INSTANCE.endTag(TAG_TR));
				write(LayoutJspUtils.INSTANCE.startTag(TAG_TR));
					attributes.clear();
					attributes.put(ATTR_CLASS, getStyle().getActiveSection());
					write(LayoutJspUtils.INSTANCE.startTag(TAG_TD, attributes));
				
						String viewName = getView().getViewObject() + "_section" + activeSection;
						ModuleContext context = (ModuleContext) getPageContext().getSession().getAttribute("context");
						context.put(getRequest(), viewName, getView().getSectionView(activeSection));
						getView().getSectionView(activeSection).setViewObject(viewName);
						if (getView().getSectionView(activeSection).getPropertyPrefix() == null) {
							getView().getSectionView(activeSection).setPropertyPrefix("");
						}
						attributes.clear();
						attributes.put(ATTR_STYLE, ATTRVAL_STYLE_WIDTH_100P);
						write(LayoutJspUtils.INSTANCE.startTag(TAG_TABLE, attributes));
							write(LayoutJspUtils.INSTANCE.startTag(TAG_TR));
								write(LayoutJspUtils.INSTANCE.startTag(TAG_TD));
									// This actually prepares the section content by calling the layout manager with 
									// the current section marker.
									
									Collection<LayoutElement> elementsSection = 
											LayoutFactory.getLayoutParserInstance(getRequest()).parseView(getView().getSectionView(activeSection), getPageContext());
									getPainterManager().renderElements(this, elementsSection, getView().getSectionView(activeSection), getPageContext());
								write(LayoutJspUtils.INSTANCE.endTag(TAG_TD));
							write(LayoutJspUtils.INSTANCE.endTag(TAG_TR));
						write(LayoutJspUtils.INSTANCE.endTag(TAG_TABLE));
					write(LayoutJspUtils.INSTANCE.endTag(TAG_TD));
				write(LayoutJspUtils.INSTANCE.endTag(TAG_TR));
			write(LayoutJspUtils.INSTANCE.endTag(TAG_TABLE));
		write(LayoutJspUtils.INSTANCE.endTag(TAG_DIV));
	}

	/**
	 * @see org.openxava.web.layout.ILayoutPainter#endSections(org.openxava.web.layout.LayoutElement)
	 */
	public void endSections(LayoutElement element) {
		
	}
	
}
