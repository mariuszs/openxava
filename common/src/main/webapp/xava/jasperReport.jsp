<%="<?xml version='1.0' encoding='" + org.openxava.util.XSystem.getEncoding() + "' ?>"%>

<!DOCTYPE jasperReport PUBLIC "-//JasperReports//DTD Report Design//EN" 
"http://jasperreports.sourceforge.net/dtds/jasperreport.dtd">

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.openxava.util.XavaResources" %>
<%@ page import="org.openxava.util.Primitives" %>
<%@ page import="org.openxava.util.Strings" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.tab.meta.MetaTab" %>
<%@ page import="org.openxava.component.MetaComponent" %>
<%@ page import="org.openxava.model.meta.MetaModel" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.util.XSystem"%>
<%@ page import="org.openxava.util.XavaPreferences"%>


<%!

private static int EXTRA_WIDTH = 5; 

private void tightenWidths(int [] widths) {	
	int max = 190;
	int littleOnesTotal = 0;
	int littleOnesCount = 0;
	for (int i=0; i<widths.length; i++) {
		if (widths[i] <= 20) {
			littleOnesTotal+=widths[i];
			littleOnesCount++;
		}
	}	
	int spaceForBigOnes = max - littleOnesTotal;
	int bigOnesCount = widths.length - littleOnesCount; 
	int widthForBig = bigOnesCount==0?20:spaceForBigOnes / bigOnesCount; 
	if (widthForBig < 20) widthForBig = 20;
	for (int i=0; i<widths.length; i++) {
		if (widths[i] > 20 && widths[i] > widthForBig) widths[i] = widths[i] = widthForBig;
	}		
}

private String getType(MetaProperty p) throws Exception {
	if (p.hasValidValues() || 
		p.getType().equals(boolean.class) ||
		p.getType().equals(java.lang.Object.class)) return "java.lang.String";
	if (java.sql.Timestamp.class.equals(p.getType())) return "java.lang.String";
	if (java.util.Date.class.equals(p.getType())) return "java.lang.String";
	if (java.math.BigDecimal.class.equals(p.getType())) return "java.lang.String";
	if (java.sql.Time.class.equals(p.getType())) return "java.lang.String";
	if (java.lang.Number.class.isAssignableFrom(Primitives.toWrapperClass(p.getType()))) return "java.lang.Number";
	return Primitives.toWrapperClass(p.getType()).getName();
}

private String getAlign(MetaProperty p) throws Exception {
	String align = "Left";
	if (p.isNumber() && !p.hasValidValues()) align = "Right";
	else if (p.getType().equals(boolean.class)) align = "Center";
	return align;
}

%>

<%
String modelName = request.getParameter("model");
String reportName = Strings.change(modelName, ".", "_");
MetaModel metaModel = MetaModel.get(modelName);
String tabName = request.getParameter("tab");
MetaTab tab = null;
if (tabName.startsWith(org.openxava.tab.Tab.COLLECTION_PREFIX)) {
	tab = MetaTab.createDefault(metaModel);
}
else {
	MetaComponent component = metaModel.getMetaComponent();
	tab = component.getMetaTab(tabName);
}
String propertiesNames = request.getParameter("properties");
if (!Is.emptyString(propertiesNames)) {
	tab = tab.cloneMetaTab();
	tab.setPropertiesNames(propertiesNames);
}
java.util.Set totalProperties = Strings.toSet(request.getParameter("totalProperties"));
String language = request.getParameter("language");
if (language == null) language = org.openxava.util.Locales.getCurrent().getDisplayLanguage();
language = language == null?request.getLocale().getDisplayLanguage():language;
java.util.Locale locale = new java.util.Locale(language, "");

int columnsSeparation = 10; 
Iterator it = tab.getMetaProperties().iterator();
int [] widths = new int[tab.getMetaProperties().size()];
int totalWidth = 0;
int i=0;
while (it.hasNext()) {
	MetaProperty p = (MetaProperty) it.next();
	String label = p.getLabel(locale);	
	widths[i]=Math.max(p.getSize(), label.length());
	totalWidth+=widths[i];
	i++;
}
int letterWidth;
int letterSize;
int detailHeight;
int pageWidth;
int pageHeight;
int columnWidth;
String orientation = null;

if (totalWidth > 120) {
	tightenWidths(widths);
	orientation="Landscape";
	letterWidth = 4;
	letterSize = 7;
	detailHeight = 10;
	pageWidth=842;
	pageHeight=595;
	columnWidth=780;	
}
else if (totalWidth > 90) {
	orientation="Landscape";
	letterWidth = 6;
	letterSize=8;
	detailHeight = 10;
	pageWidth=842;
	pageHeight=595;
	columnWidth=780;	
} 
else if (totalWidth > 53) {
	orientation="Portrait";
	letterWidth = 6;
	letterSize=8;
	detailHeight = 10;
	pageWidth=595;
	pageHeight=842;
	columnWidth=535;
}
else {
	orientation="Portrait";
	letterWidth = 10;
	letterSize = 12;
	detailHeight = 15;
	pageWidth=595;
	pageHeight=842;
	columnWidth=535;
}

%>

<jasperReport
		 name="<%=reportName%>"
		 columnCount="1"
		 printOrder="Vertical"
		 orientation="<%=orientation%>"
		 pageWidth="<%=pageWidth%>"
		 pageHeight="<%=pageHeight%>"
		 columnWidth="<%=columnWidth%>"
		 columnSpacing="0"
		 leftMargin="30"
		 rightMargin="30"
		 topMargin="20"
		 bottomMargin="20"
		 whenNoDataType="NoPages"
		 isTitleNewPage="false"
		 isSummaryNewPage="false">		 
	<%
	String fontPath=request.getSession().getServletContext().getRealPath("/WEB-INF/fonts/").concat(System.getProperty("file.separator"));
	String fontName="DejaVu Sans";
	String fontNameExt="DejaVuSans.ttf";
	String boldFontNameExt="DejaVuSans-Bold.ttf";	
	String pdfEncoding="Identity-H";
	%>	
	<reportFont name="Arial_Normal" isDefault="true" fontName="<%=fontName%>" size="8" pdfFontName="<%=fontPath.concat(fontNameExt)%>" pdfEncoding="<%=pdfEncoding%>" isPdfEmbedded="true"/>
	<reportFont name="Arial_Bold" isDefault="false" fontName="<%=fontName%>" size="8" isBold="true" pdfFontName="<%=fontPath.concat(boldFontNameExt)%>" pdfEncoding="<%=pdfEncoding%>" isPdfEmbedded="true"/>
	<reportFont name="Arial_Italic" isDefault="false" fontName="<%=fontName%>" size="8" isItalic="true" pdfFontName="<%=fontPath.concat(fontNameExt)%>" pdfEncoding="<%=pdfEncoding%>" isPdfEmbedded="true"/>

	<parameter name="Title" class="java.lang.String"/>	
	<parameter name="Organization" class="java.lang.String"/>
	<%
	it = tab.getMetaProperties().iterator();
	while (it.hasNext()) {
		MetaProperty p = (MetaProperty) it.next();				
		if (totalProperties.contains(p.getQualifiedName())) {				 
	%>
	<parameter name="<%=p.getQualifiedName()%>__TOTAL__" class="java.lang.String"/> 	
	<%
		}
	}
	%>	
		
	<%
	it = tab.getMetaProperties().iterator();
	while (it.hasNext()) {
		MetaProperty p = (MetaProperty) it.next();
		String type=getType(p); 
	%>
	<field name="<%=Strings.change(p.getQualifiedName(), ".", "_")%>" class="<%=type%>"/> 	
	<%
	}
	%>	
		<background>
			<band height="0"  isSplitAllowed="true" >
			</band>
		</background>
		<title>
			<band height="25"  isSplitAllowed="true" >

				<textField>
					<reportElement
						mode="Transparent"
						x="0"
						y="0"
						width="200"
						height="25"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="Left" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Normal" size="8"/>
					</textElement>
					<textFieldExpression class="java.lang.String">$P{Organization}</textFieldExpression>					
				</textField>
						
				<textField>
					<reportElement
						mode="Transparent"
						x="5"
						y="5"
						width="<%=columnWidth%>"
						height="20"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="Center" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Normal" size="16"/>
					</textElement>
					<textFieldExpression class="java.lang.String">$P{Title}</textFieldExpression>					
				</textField>
				

				<line direction="TopDown">
					<reportElement
						mode="Opaque"
						x="0"
						y="23"
						width="<%=columnWidth%>"
						height="0"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<graphicElement stretchType="NoStretch" pen="1Point" fill="Solid" />
				</line>

				<!-- Top line
				<line direction="TopDown">
					<reportElement
						mode="Opaque"
						x="0"
						y="3"
						width="<%=columnWidth%>"
						height="0"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<graphicElement stretchType="NoStretch" pen="1Point" fill="Solid" />
				</line>
				-->
			</band>
		</title>
		<pageHeader>
			<band height="9"  isSplitAllowed="true" >
			</band>
		</pageHeader>
		<columnHeader>
			<band height="15"  isSplitAllowed="true" >
				<rectangle radius="0" >
					<reportElement
						mode="Opaque"
						x="0"
						y="0"
						width="<%=columnWidth%>"
						height="15"
						forecolor="#000000"
						backcolor="#808080"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<graphicElement stretchType="NoStretch" pen="None" fill="Solid" />
				</rectangle>
				<line direction="BottomUp">
					<reportElement
						mode="Opaque"
						x="0"
						y="0"
						width="<%=columnWidth%>"
						height="0"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<graphicElement stretchType="NoStretch" pen="Thin" fill="Solid" />
				</line>
				<line direction="BottomUp">
					<reportElement
						mode="Opaque"
						x="0"
						y="14"
						width="<%=columnWidth%>"
						height="0"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<graphicElement stretchType="NoStretch" pen="Thin" fill="Solid" />
				</line>
<%
it = tab.getMetaProperties().iterator();
int x = 0;
i=0;
while (it.hasNext()) {			
	MetaProperty p = (MetaProperty) it.next();
	int width=widths[i++]*letterWidth + EXTRA_WIDTH; 		
%>								
				<staticText>
					<reportElement
						mode="Transparent"
						x="<%=x%>"
						y="2"
						width="<%=width%>"
						height="13"
						forecolor="#FFFFFF"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="<%=getAlign(p)%>" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Normal" size="10"/>
					</textElement>
					<% String label = "<![CDATA[" + p.getLabel(locale) + "]]>"; %>
					<text><%=label%></text>
				</staticText>
<%
	x+=(width+columnsSeparation);
}
%>				
			</band>
		</columnHeader>
		
		<detail>
			<band height="19"  isSplitAllowed="true" >
				<line direction="TopDown">
					<reportElement
						mode="Opaque"
						x="0"
						y="0" 
						width="<%=columnWidth%>"
						height="0"
						forecolor="#808080"
						backcolor="#FFFFFF"
						positionType="Float"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="true"/>					
					<graphicElement stretchType="NoStretch" pen="Thin" fill="Solid" />
				</line>
<%
it = tab.getMetaProperties().iterator();
x = 0;
i=0;
while (it.hasNext()) {			
	MetaProperty p = (MetaProperty) it.next();	
	int width=widths[i++]*letterWidth + + EXTRA_WIDTH; 
%>								
				<textField isStretchWithOverflow="true" pattern="" isBlankWhenNull="true" evaluationTime="Now" hyperlinkType="None" >					<reportElement
						mode="Transparent"
						x="<%=x%>"
						y="2"
						width="<%=width%>"
						height="<%=detailHeight%>"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="<%=getAlign(p)%>" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Normal" size="<%=letterSize%>"/>
					</textElement>
					<%		
					String type=getType(p);
					%>
					<textFieldExpression   class="<%=type%>">$F{<%=Strings.change(p.getQualifiedName(), ".", "_")%>}</textFieldExpression>
				</textField>
<%
	x+=(width+columnsSeparation);
}
%>				
			</band>
		</detail>
		<pageFooter>
			<band height="27"  isSplitAllowed="true" >
				<textField isStretchWithOverflow="false" pattern="" isBlankWhenNull="false" evaluationTime="Now" hyperlinkType="None" >					<reportElement
						mode="Transparent"
						x="<%=columnWidth - 210%>"
						y="4"
						width="174"
						height="19"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="Right" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Normal" size="10"/>
					</textElement>
				<%
				String iniPageLabel = "<![CDATA[\"" + XavaResources.getString(request, "page") + " \"";
				String endPageLabel = " \" " + XavaResources.getString("of") + " \"]]>";
				%>
				<textFieldExpression class="java.lang.String"><%=iniPageLabel%> + $V{PAGE_NUMBER} + <%=endPageLabel%></textFieldExpression>
				</textField>
				<textField isStretchWithOverflow="false" pattern="" isBlankWhenNull="false" evaluationTime="Report" hyperlinkType="None" >					<reportElement
						mode="Transparent"
						x="<%=columnWidth - 36%>"
						y="4"
						width="36"
						height="19"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="Left" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Normal" size="10" />
					</textElement>
				<textFieldExpression   class="java.lang.String"><![CDATA[" " + $V{PAGE_NUMBER}]]></textFieldExpression>
				</textField>
				<line direction="TopDown">
					<reportElement
						mode="Opaque"
						x="0"
						y="1"
						width="<%=columnWidth%>"
						height="0"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<graphicElement stretchType="NoStretch" pen="2Point" fill="Solid" />
				</line>
				<textField isStretchWithOverflow="false" pattern="" isBlankWhenNull="false" evaluationTime="Now" hyperlinkType="None" >					<reportElement
						mode="Transparent"
						x="1"
						y="6"
						width="209"
						height="19"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="Left" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Normal" size="10"/>
					</textElement>
				<textFieldExpression   class="java.lang.String">
					<![CDATA[java.text.DateFormat.getDateInstance().format(new java.util.Date())]]>
				</textFieldExpression>
				</textField>
			</band>
		</pageFooter>
		<summary>
			<band height="19"  isSplitAllowed="true" >
				<line direction="TopDown">
					<reportElement
						mode="Opaque"
						x="0"
						y="0" 
						width="<%=columnWidth%>"
						height="0"
						forecolor="#808080"
						backcolor="#FFFFFF"
						positionType="Float"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="true"/>					
					<graphicElement stretchType="NoStretch" pen="Thin" fill="Solid" />
				</line>
<%
it = tab.getMetaProperties().iterator();
x = 0;
i=0;
while (it.hasNext()) {			
	MetaProperty p = (MetaProperty) it.next();	
	int width=widths[i++]*letterWidth + + EXTRA_WIDTH;
	if (totalProperties.contains(p.getQualifiedName())) { 
%>								
				<textField isStretchWithOverflow="true" pattern="" isBlankWhenNull="true" evaluationTime="Now" hyperlinkType="None" >					<reportElement
						mode="Transparent"
						x="<%=x%>"
						y="2"
						width="<%=width%>"
						height="<%=detailHeight%>"
						forecolor="#000000"
						backcolor="#FFFFFF"
						positionType="FixRelativeToTop"
						isPrintRepeatedValues="true"
						isRemoveLineWhenBlank="false"
						isPrintInFirstWholeBand="false"
						isPrintWhenDetailOverflows="false"/>
					<textElement textAlignment="<%=getAlign(p)%>" verticalAlignment="Top" lineSpacing="Single">
						<font reportFont="Arial_Bold" size="<%=letterSize%>"/>
					</textElement>
					<textFieldExpression class="java.lang.String">$P{<%=p.getQualifiedName()%>__TOTAL__}</textFieldExpression>
				</textField>
<%
	} 			
	x+=(width+columnsSeparation);
}
%>					
			</band>
		</summary>
</jasperReport>

