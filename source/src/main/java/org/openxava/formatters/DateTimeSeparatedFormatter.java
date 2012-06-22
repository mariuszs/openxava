package org.openxava.formatters;

import java.text.*;

import javax.servlet.http.*;

import org.openxava.util.*;

/**
 * Date and time formatter with multilocale support. <p>
 * 
 * Although it does some refinement in Spanish case, it support formatting
 * on locale basis.<br>
 * 
 * @author Jos� Luis Santiago
 * @author Javier Paniza
 */

public class DateTimeSeparatedFormatter implements IMultipleValuesFormatter {
	
	private static DateFormat spanishDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	private static DateFormat [] spanishDateTimeFormats = {
		new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"),
		new SimpleDateFormat("dd/MM/yyyy HH:mm"),
		new SimpleDateFormat("ddMMyyyy HH:mm"),
		new SimpleDateFormat("ddMMyyyy HH:mm:ss"),
		new SimpleDateFormat("dd.MM.yyyy HH:mm"),		
		new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"),		
		new SimpleDateFormat("dd/MM/yyyy"),		
		new SimpleDateFormat("ddMMyyyy"),		
		new SimpleDateFormat("dd.MM.yyyy")
	};	
	
	public String [] format(HttpServletRequest request, Object date) throws Exception {	
        String[] result = new String[2];
        result[0] = "";
        result[1] = "";
		if (date == null) return result;		
		result[0] = getDateFormat().format(date);		
		result[1] = DateFormat.getTimeInstance(DateFormat.SHORT, Locales.getCurrent()).format(date);
		return result;
	}

	public Object parse(HttpServletRequest request, String [] strings) throws Exception {		
		if( strings == null || strings.length < 2 ) return null;
		if( Is.emptyString(strings[0])) return null;
		if( Is.emptyString(strings[1])) return null;
	
		String fDate = strings[0];
		String fTime = strings[1];
		String dateTime = fDate + " " + fTime;
		
        // SimpleDateFormat does not work well with -
		if (dateTime.indexOf('-') >= 0) { 
			dateTime = Strings.change(dateTime, "-", "/");
		}
		
		DateFormat [] dateFormats = getDateTimeFormats();
		for (int i=0; i < dateFormats.length; i++) {
			try {				
				java.util.Date result =  (java.util.Date) dateFormats[i].parseObject(dateTime);				
				return new java.sql.Timestamp( result.getTime() );
			}
			catch (ParseException ex) {				
			}						
		}
		throw new ParseException(XavaResources.getString("bad_date_format",dateTime),-1);
	}
	
	private DateFormat getDateFormat() {
		if ("es".equals(Locales.getCurrent().getLanguage()) || 
				"pl".equals(Locales.getCurrent().getLanguage())) return spanishDateFormat;
		return DateFormat.getDateInstance(DateFormat.SHORT, Locales.getCurrent());		
	}
	
	private DateFormat[] getDateTimeFormats() {
		if ("es".equals(Locales.getCurrent().getLanguage()) || 
				"pl".equals(Locales.getCurrent().getLanguage())) return spanishDateTimeFormats;				
		return new DateFormat [] { DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locales.getCurrent() ) };
	}
	
}
