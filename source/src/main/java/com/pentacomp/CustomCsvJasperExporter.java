package com.pentacomp;

import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRStyledTextAttributeSelector;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.util.JRStyledText;

/**
 * 
 * Zmiania sposób dodawania znakow " w stosunku do oryginalu. Teraz wszystkie pola String
 * otoczone znakiem ". Znak " w tekście jest zamieniany na ""
 * 
 */
public class CustomCsvJasperExporter extends JRCsvExporter {

    private static final char QUOTE_CHAR = '"';

    @Override
    protected JRStyledText getStyledText(JRPrintText textElement)
    {
        JRStyledText oryg = textElement.getFullStyledText(JRStyledTextAttributeSelector.NONE);
        if (textElement.getValueClassName() == null) {
            JRStyledText result = new JRStyledText(oryg.getLocale());
            result.append(addQuotas(replaceQuotas(oryg.getText())));
            return result;
        } else {
            return oryg;
        }
    }

    public String addQuotas(String source) {
        String result = source;
        if (source != null && source.length() > 0) {
            // already has quotes so don't bother
            if (result.startsWith("" + QUOTE_CHAR) && result.endsWith("" + QUOTE_CHAR)) {
                return result;
            } else {
                StringBuffer sb = new StringBuffer(result);
                sb.insert(0, QUOTE_CHAR);
                sb.append(QUOTE_CHAR);

                return sb.toString();
            }
        } else {
            return result;
        }

    }

    private String replaceQuotas(String source) {
        return source.replaceAll("\"", "\"\"");
    }

    @Override
    protected String prepareText(String source)
    {
        return source.toString();
    }

}
