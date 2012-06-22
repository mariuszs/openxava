package org.openxava.util;

import static org.fest.assertions.Assertions.assertThat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.testng.annotations.Test;

public class DatesTest {
	@Test
	public void format() {
		Locale locale = Locale.getDefault();
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		String date = df.format(Dates.create(1, 2, 1971)); // d, m, y
		System.out.println("date " + date + " for " + locale + " (default)");

		SimpleDateFormat sf = (SimpleDateFormat) df;
		String p1 = sf.toPattern();
		String p2 = sf.toLocalizedPattern();

		System.out.println("          pattern " + p1);
		System.out.println("localized pattern " + p2);
	}

	//
	@Test
	public void formatPL() {
		Locale locale = new Locale("pl", "PL");
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);

		String date = df.format(Dates.create(1, 2, 1971)); // d, m, y
		System.out.println("date " + date + " for " + locale);

		SimpleDateFormat sf = (SimpleDateFormat) df;
		String p1 = sf.toPattern();
		String p2 = sf.toLocalizedPattern();

		System.out.println("  MEDIUM        pattern " + p1);
		System.out.println("localized pattern " + p2);
	}

	@Test
	public void polishLocale() {
		System.out.println("polishLocale");
		Locale locale = new Locale("pl","PL");
		String format = Dates.dateFormatForJSCalendar(locale);
		System.out.println("Locale " + locale);
		System.out.println("Language " + locale.getLanguage());
		System.out.println("Date format " + format);
		assertThat(format).isEqualTo("%Y-%m-%d");
	}

	@Test
	public void defaultLocale() {
		System.out.println("defaultLocale");
		Locale locale = Locale.getDefault();
		String format = Dates.dateFormatForJSCalendar(locale);
		System.out.println("Locale " + locale);
		System.out.println("Language " + locale.getLanguage());
		System.out.println("Date format " + format);
		assertThat(format).isEqualTo("%Y-%m-%d");
	}
}
