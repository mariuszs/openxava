package org.openxava.util;

import java.text.DateFormat;
import java.util.*;




/**
 * Utilities to working with dates (<tt>java.util.Date</tt>). <p>  
 * 
 * @author Javier Paniza
 * @author Peter Smith
 */
public class Dates {
	
	/**
	 * With hour to 0.
	 * If day, month and year are 0 return null.
	 */	
	public static Date create(int day, int month, int year) {
		return create(day, month, year, 0, 0, 0);
	}
	
	/**
	 * If day, month and year are 0 return null.
	 */	
	public static Date create(int day, int month, int year, int hourofday, int minute, int second) {
		if (day == 0 && month == 0 && year == 0) return null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day, hourofday, minute, second);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();			
	}

		
	/**
	 * Current date without time. 
	 */
	public static Date createCurrent() {
		return removeTime(new java.util.Date());
	}
	
	/**
	 * Returns the day of date. <p>
	 * 
	 * If date is null return 0.
	 */
	public static int getDay(Date date) {
		if (date == null) return 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * Returns the year (4 digits) of date. <o> 
	 * 
	 * If date is null returns 0.
	 */
	public static int getYear(Date date) {
		if (date == null) return 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
	
	/**
	 * Returns the month (1 to 12) of date. <p>
	 * 
	 * If date is null returns 0.
	 */
	public static int getMonth(Date date) {
		if (date == null) return 0;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}	
	
	/**
	 * Put the day to the date. <p>
	 * 
	 * If date is null it has no effect (but no exception is thrown)
	 */
	public static void setDay(Date date, int day) {
		if (date == null) return;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, day);
	}
	
	/**
	 * Put the month (1 to 12) to the date. <p>
	 * 
	 * If date is null it has no effect (but no exception is thrown)
	 */
	public static void setMonth(Date date, int month) {
		if (date == null) return;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MONTH, month - 1);
	}
	
	/**
	 * Put the year to the date. <p>
	 * 
	 * If date is null it has no effect (but no exception is thrown)
	 */
	public static void setYear(Date date, int year) {
		if (date == null) return;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.YEAR, year);
	}
	
	
	/**
	 * Puts hours, minutes, seconds and milliseconds to zero. <p>
	 * 
	 * @return The same date sent as argument (a new date is not created). If null
	 * 			if sent a null is returned.
	 */
	public static Date removeTime(Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0); 
		cal.set(Calendar.MINUTE, 0);		
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		date.setTime(cal.getTime().getTime());
		return date;
	}
	
	/**
	 * Returns a clone but without hours, minutes, seconds and milliseconds. <p>
	 * 
	 * @return If null if sent a null is returned.
	 */
	public static Date cloneWithoutTime(Date date) { 
		if (date == null) return null;
		Date result = (Date) date.clone();
		removeTime(result);
		return result;
	}
	
	/**
	 * Returns a clone but with 23:59:59:999 for hours, minutes, seconds and milliseconds. <p>
	 * 
	 * @return The same date sent as argument (a new date is not created). If null
	 * 			if sent a null is returned.
	 */
	public static Date cloneWith2359(Date date) { 
		if (date == null) return null;
		Date result = (Date) date.clone();		
		Calendar cal = Calendar.getInstance();
		cal.setTime(result);
		cal.set(Calendar.HOUR_OF_DAY, 23); 
		cal.set(Calendar.MINUTE, 59);		
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		result.setTime(cal.getTime().getTime());
		return result;
	}
	
		
	/**
	 * Creates a java.sql.Date from a java.util.Date. <p>
	 * 
	 * @param date If null returns null
	 */
	public static java.sql.Date toSQL(java.util.Date date) {
		if (date == null) return null;
		return new java.sql.Date(date.getTime());		
	}
	
	/**
	 * String with date in short format according current <i>locale</i>. <p>
	 * 
	 * Current locale is from {@link Locales#getCurrent}. <br>
	 * 
	 * @param date  If null returns empty string
	 * @return Not null
	 */
	public static String toString(java.util.Date date) {
		return DateFormat.getDateInstance(DateFormat.SHORT, Locales.getCurrent()).format(date);			
	}
	
	/**
	 * Creates a date with day, month and year of original,
	 * but with current time. <p>
	 *  
	 * @param date  It is not changed
	 * @return If arguments is null then is null
	 */
	public static java.util.Date withTime(java.util.Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(new java.util.Date());
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();
	}
	
	/**
	 * Compares if 2 dates are equals at day, month and year
	 * level, ignoring time in comparing. 
	 * 
	 * @param f1 Can be null
	 * @param f2 Can be null
	 */
	public static boolean isDifferentDay(java.util.Date f1, java.util.Date f2) {
		if (f1 == null && f2 == null) return false;
		if (f1 == null || f2 == null) return true;
		Calendar cal = Calendar.getInstance();
		cal.setTime(f1);
		int dd1 = cal.get(Calendar.DAY_OF_MONTH);
		int mm1 = cal.get(Calendar.MONTH);
		int aa1 = cal.get(Calendar.YEAR);
		cal.setTime(f2);
		int dd2 = cal.get(Calendar.DAY_OF_MONTH);
		int mm2 = cal.get(Calendar.MONTH);
		int aa2 = cal.get(Calendar.YEAR);
		return !(aa1==aa2 && mm1==mm2 && dd1==dd2);
	}
	
	/**
	 * Difference of 2 dates in years, months and days. <p>  
	 *  
	 * @param f1  If null returns null
	 * @param f2  If null returns null 
	 */	
	public static DateDistance dateDistance(java.util.Date f1, java.util.Date f2, boolean includeStartDate ) {
		DateDistance df = new DateDistance();
		if (null == f1 || null == f2)
			return null;
		Calendar fmax = Calendar.getInstance(), fmin = Calendar.getInstance();

		f1 = Dates.removeTime(f1);
		f2 = Dates.removeTime(f2);

		if (f1.after(f2)) {
			fmax.setTime(f1);
			fmin.setTime(f2);
		} 
		else {
			fmin.setTime(f1);
			fmax.setTime(f2);
		}
        
		int initDay = fmin.get(Calendar.DATE);
		int initMonth = fmin.get(Calendar.MONTH);
		int initYear = fmin.get(Calendar.YEAR);
		int endMonth = fmax.get(Calendar.MONTH);
		int endYear = fmax.get(Calendar.YEAR);
		int finalLimit = fmax.getActualMaximum(Calendar.DATE);
		int initPeak = 0;
		int finalPeak = 0;
        
		if (initMonth == endMonth && initYear == endYear) {
			while ( fmin.getTime().before(fmax.getTime()) ) {
				fmin.add(Calendar.DATE, 1);
				df.days++;
			}

			if (includeStartDate) {
				df.days++;
			}
			if (df.days >= finalLimit) {
				df.months++;
				df.days = 0;
			}
			return df;
		}
        
		if (initDay != 1) {
			while (fmin.get(Calendar.DATE) != 1) {
				fmin.add(Calendar.DATE, 1);
				initPeak++;
			}
		}
        
		while (fmin.get(Calendar.MONTH) != endMonth
				|| fmin.get(Calendar.YEAR) != endYear) {
			fmin.add(Calendar.MONTH, 1);
			df.months++;
			if (df.months == 12) {
				df.years++;
				df.months = 0;
			}
		}
        
		while ( fmin.getTime().before(fmax.getTime()) ) {
			fmin.add(Calendar.DATE, 1);
			finalPeak++;
		}
        
		int peak = initPeak + finalPeak;
		if (includeStartDate) {
			peak++;
		}
        
		if (peak >= finalLimit) {
			peak = peak - finalLimit;
			df.months++;
			if (df.months == 12) {
				df.years++;
				df.months = 0;
			}
		}
		df.days = peak;
		return df;        
	}

	/**
	 * Difference of 2 dates in years, months and days. <p>  
	 *  
	 * @param f1  If null returns null
	 * @param f2  If null returns null 
	 */	
	public static DateDistance dateDistance(java.util.Date f1, java.util.Date f2) {
		return dateDistance(f1, f2, false);		
	}
		
	public static DateDistance addDateDistances(DateDistance dis1, DateDistance dis2) {
		DateDistance df=new DateDistance();
		if ( null == dis1 || null == dis2 ) return null;
				
		int years, months, days;
		days = dis1.days + dis2.days;
		months = days / 30;
		days = days % 30;
		
		months = months + dis1.months + dis2.months ;
		years =  months / 12 ;
		months = months % 12;		
				
		years = years + ( dis1.years + dis2.years );
		df.years = years;
		df.months=months;
		df.days=days;
				
		return df;
	}
	
	
	public static DateDistance subtractDateDistances(DateDistance dis1, DateDistance dis2) {
		DateDistance df = new DateDistance();
		if ( null == dis1 || null == dis2 ) return null;
				
		int years=0;
		int months=0;
		int days=0;
		days = dis1.days - dis2.days;
		months = dis1.months - dis2.months;
		years =dis1.years - dis2.years;
			
		if (days<0) {
			days=days+30;
			months=months-1;
		}
		if (months<0){
			months=months+12;
			years=years-1;
		}
			
		df.years = years;
		df.months = months;
		df.days = days;
				
		return df;
	}
	
	public static String dateFormatForJSCalendar(Locale locale) {		
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		String date = df.format(create(1, 2, 1971)); // d, m, y
		boolean always4InYear= "es".equals(locale.getLanguage()) || "pl".equals(locale.getLanguage());
		String result = date.
			replaceAll("01", "%d").
			replaceAll("02", "%m").
			replaceAll("1971", "%Y").
			replaceAll("71", always4InYear?"%Y":"%y"). 			
			replaceAll("1", "%d").
			replaceAll("2", "%m");
		return result;
	}	
	
	public static String dateTimeFormatForJSCalendar(Locale locale) {		
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
		String datetime = df.format(create(1, 2, 1971, 15, 59, 0)); // d, m, y, hr, min, sec 
		boolean always4InYear= "es".equals(locale.getLanguage()) || "pl".equals(locale.getLanguage());
		String result = datetime.
		
			// time part
			replaceAll("15", "%H").	// 24hr format 
			replaceAll("03", "%I").	// 12hr format - double digit 
			replaceAll("3", "%l").	// 12hr format - single digit
			replaceAll("59","%M").	// minute
			replaceAll("PM", "%p").	// AM/PM - uppercase
			replaceAll("pm", "%P").	// am/pm - lowercase

			// date part
			replaceAll("01", "%d").	// day - double digit
			replaceAll("02", "%m").	// month - double digit
			replaceAll("1971", "%Y").	// year - 4 digit
			replaceAll("71", always4InYear?"%Y":"%y"). 	// year - 2 digit 		
			replaceAll("1", "%e"). 	// day - single digit
			replaceAll("2", "%m")	// month - ??? seems only double digit is supported by calendar
			;
					
		return result;
	}	
	

	/** 
	 * Returns number of days between startDate and endDate<p> 
	 *  
	 * @param java.util.Date startDate
	 * @param java.util.Date endDate
	 * @param boolean includeStartDate<p>
	 *  
	 */	
	  public static int daysInterval (Date startDate, Date endDate,
	  		boolean includeStartDate ) {
		
		startDate = Dates.removeTime(startDate);
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);		

		endDate = Dates.removeTime(endDate);		
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		
		if (includeStartDate) {
			start.add(Calendar.DATE, -1);
		}
		
		int days = 0;
		while (start.before(end)) {
			days++;
			start.add(Calendar.DATE,1);
		}
		return days;
	}
	  
	  
	 
	
	public static class DateDistance {
		public  int days;
		public  int months;
		public  int years;
	}
	
}


