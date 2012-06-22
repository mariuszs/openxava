package org.openxava.tab.meta.xmlparse;




import org.openxava.filters.meta.*;
import org.openxava.filters.meta.xmlparse.*;
import org.openxava.model.meta.xmlparse.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.util.xmlparse.*;
import org.w3c.dom.*;

/**
 * 
 * @author: Javier Paniza
 */
public class TabParser extends XmlElementsNames {

	
	
	public static MetaTab parseTab(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaTab e = new MetaTab();
		e.setName(el.getAttribute(xname[lang]));
		String excludeByKey = el.getAttribute(xexclude_by_key[lang]);
		if (!Is.emptyString(excludeByKey)) {
			e.setExcludeByKey(Boolean.valueOf(excludeByKey).booleanValue());
		}		
		String excludeAll = el.getAttribute(xexclude_all[lang]);
		if (!Is.emptyString(excludeAll)) {
			e.setExcludeAll(Boolean.valueOf(excludeAll).booleanValue());
		}
		e.setDefaultPropertiesNames(ParserUtil.getString(el, xproperties[lang]));		
		e.setMetaFilter(createFilter(el, lang));
		fillRowStyles(el, e, lang);
		e.setBaseCondition(ParserUtil.getString(el, xbase_condition[lang]));		
		e.setDefaultOrder(ParserUtil.getString(el, xdefault_order[lang]));
		fillProperties(el, e, lang);
		fillConsults(el, e, lang);
		return e;
	}
	
	private static MetaConsult createConsult(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaConsult a = new MetaConsult();		
		a.setName(el.getAttribute(xname[lang]));
		a.setLabel(el.getAttribute(xlabel[lang]));
		a.setCondition(ParserUtil.getString(el, xcondition[lang]));
		a.setMetaFilter(createFilter(el, lang));
		fillParameters(el, a, lang);		
		return a;
	}
		
	private static MetaParameter createParameter(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaParameter p = new MetaParameter();
		p.setLabel(el.getAttribute(xlabel[lang]));
		p.setPropertyName(el.getAttribute(xproperty[lang]));
		p.setLike(Boolean.valueOf(el.getAttribute(xlike[lang])).booleanValue());
		p.setRange(Boolean.valueOf(el.getAttribute(xrange[lang])).booleanValue());
		p.setLabelId(el.getAttribute(xlabel_id[lang]));
		if (p.isLike() && p.isRange()) {
			throw new XavaException("like_range_incompatibles");
		}
		p.setMetaFilter(createFilter(el, lang));
		return p;
	}
	
	private static MetaFilter createFilter(Element el, int lang) throws XavaException {
		NodeList l = el.getChildNodes();				
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			Node n = l.item(i);			
			if (xfilter[lang].equals(n.getNodeName())) {
				return FilterParser.parseFilter(l.item(i), lang);
			} 
		}
		return null;		
	}
	
	private static void fillConsults(Element el, MetaTab container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xconsult[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container.addMetaConsult(createConsult(l.item(i), lang));
		}
	}
	
	private static void fillParameters(Element el, MetaConsult container, int lang)
		throws XavaException {
		NodeList l = el.getElementsByTagName(xparameter[lang]);
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			container.addMetaParameter(createParameter(l.item(i), lang));
		}
	}
	
	private static void fillProperties(Element el, MetaTab container, int lang)
		throws XavaException {
		NodeList l = el.getChildNodes();
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			if (!(l.item(i) instanceof Element)) continue;
			Element d = (Element) l.item(i);
			String type = d.getTagName();
			if (type.equals(xproperty[lang])) {
				container.addMetaProperty(ModelParser.createProperty(d, lang));
			}
		}
	}
	
	private static void fillRowStyles(Element el, MetaTab container, int lang)
		throws XavaException {
		NodeList l = el.getChildNodes();
		int c = l.getLength();
		for (int i = 0; i < c; i++) {
			if (!(l.item(i) instanceof Element)) continue;
			Element d = (Element) l.item(i);
			String type = d.getTagName();
			if (type.equals(xrow_style[lang])) {
				container.addMetaRowStyle(createRowStyle(d, lang));
			}
		}
	}
	
	public static MetaRowStyle createRowStyle(Node n, int lang) throws XavaException {
		Element el = (Element) n;
		MetaRowStyle style = new MetaRowStyle();
		style.setStyle(el.getAttribute(xstyle[lang]));
		style.setProperty(el.getAttribute(xproperty[lang]));
		style.setValue(el.getAttribute(xvalue[lang]));
		return style;
	}	
			
}