package org.openxava.util;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.regex.*;

import org.apache.commons.logging.*;

/**
 * Utilities to work with <code>String</code>. <p>
 * 
 * @author  Javier Paniza
 */

public class Strings {
	
	private static Log log = LogFactory.getLog(Strings.class);
	private final static String XSS_REGEXP_PATTERN = "(?i)<[\\s]*/?script.*?>|<[\\s]*/?embed.*?>|<[\\s]*/?object.*?>|<[\\s]*a[\\s]*href[^>]*javascript[\\s]*:[^(^)^>]*[(][^)]*[)][^>]*>[^<]*(<[\\s]*/[\\s]*a[^>]*>)*";
	private final static Pattern XSS_PATTERN = Pattern.compile(XSS_REGEXP_PATTERN);
	private static Map separatorsBySpaces;
	
	/**
	 * Changes \n, \r and \t by space. <p>
	 * 
	 * @param string Not null
	 */
	public static String changeSeparatorsBySpaces(String string) {
		return change(string, getSeparatorsBySpaces());
	}
	
	private static Map getSeparatorsBySpaces() {
		if (separatorsBySpaces == null) {
			separatorsBySpaces = new HashMap();
			separatorsBySpaces.put("\n", " ");
			separatorsBySpaces.put("\t", " ");
			separatorsBySpaces.put("\r", " ");
		}
		return separatorsBySpaces;
	}

	/**
	 * Translate to the charset specified. <p>
	 * 
	 * @param original  Original string
	 * @param charSet   Charset to traslate to, for example, UTF-8, or ISO-8859-1
	 * @return The string translated
	 * @throws UnsupportedEncodingException  If charset is not supported 
	 */
	public static String toCharSet(String original, String charSet) throws UnsupportedEncodingException {
		return new String(original.getBytes(charSet));
	}

	/**
	 * Cut to specified length. <p> 
	 *
	 * Returns a string with the sent string but with specified length. <br>
	 * Cut the string according align. <br>
	 *
	 * WARNING!!! IF THIS method GO TO PUBLIC REVIEW THIS PRECONDITION!!!
	 * 
	 * <b>Precondition:</b>
	 * <ul>
	 * <li><tt>string.length() > length</tt>
	 * </ul>
	 * 
	 * @return Not null
	 * @param string Not null. WARNING!!! This can change if method is maked public
	 * @param length Number the characters to cut.
	 * @param align Not null. WARNING!!! This can change if method is maked public
	 */
	private static String cut(String string, int length, Align align) {
		int c = length - string.length();
		StringBuffer result = createSB(c, ' ');
		if (align.isLeft()) {
			return string.substring(0, length);
		}
		else if (align.isRight()) {
			int t = string.length();
			return string.substring(t - length , t);
	
		}
		else if (align.isCenter()) {
			int le = (string.length() - length)/2;		
			return string.substring(le, le + length);
		}
		else {
			Assert.fail(XavaResources.getString("align_not_supported", align.getDescription()));
		}
		return result.toString();	
	}
	
	/**
	 * Creates a string with the specified blank spaces. <p> 
	 * 
	 * @param count  Quantity of spaces
	 * @return Not null.
	 */
	public static String spaces(int count) {
		return createSB(count, ' ').toString();
	}
	
	/**
	 * Creates a <tt>StringBuffer</tt> with character speciefied repeated the count specified.
	 * 
	 * @param count  Times to repeat
	 * @param character  Character to repeat
	 * @return  Not null
	 */
	private static StringBuffer createSB(int count, char character) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < count; i++){
			result.append(character);
		}
		return result;
	}
	
	/**
	 * Fix the length of the string filling with spaces if needed. <p> 
	 *
	 * Returns the sent string but with the specified length. <br>
	 * It fills or cuts as it needs. 
	 * 
	 * @param string  Can be null, in which case empty string is assumed 
	 * @param length  Character count of result string
	 * @param align Not null
	 * @return Not null
	 */
	public static String fix(String string, int length, Align align) {
		return fix(string, length, align, ' ');
	}

	/**
	 * Fix the length of the string filling with the specified character if needed. <p> 
	 *
	 * Returns the sent string but with the specified length. <br>
	 * It fills or cuts as it needs. 
	 * 
	 * @param string  Can be null, in which case empty string is assumed 
	 * @param length  Character count of result string
	 * @param align Not null
	 * @param fillCharacter  Character used to fill
	 * @return Not null
	 */
	public static String fix(String string, int length, Align align, char fillCharacter) {
		if (length < 0) 
			throw new IllegalArgumentException(XavaResources.getString("size_in_Strings_fix_not_negative"));
		string = string == null?"":string.trim();
		int t = string.length();
		if (t < length) return fill(string, length, align, fillCharacter);    
		if (t == length) return string;
		return cut(string, length, align); // if (t > length)
	}

	/**
	 * Fills to specefied length. <p> 
	 * 
	 * Returns the sent string but with the specified length. <br>
	 * Fills with the specified charactare as needed but it does not cut the string. <p> 
	 *
	 * WARNING!!! IF THIS method GO TO PUBLIC REVIEW THIS PRECONDITION!!! 
	 *  
	 * <b>Precondition:</b>
	 * <ul>
	 * <li><tt>length > string.length()</tt>
	 * </ul>
	 * 
	 * @return  Not null
	 * @param string  Cannot be null. WARNING!!! This can change if the method is maked public
	 * @param length  Length of result string
	 * @param align  Not null
	 * @param fillCharacter  Character used to fill
	 */
	private static String fill(String string, int length, Align align, char fillCharacter) {	
		int c = length - string.length();
		StringBuffer result = createSB(c, fillCharacter);
		if (align.isLeft()) {
			result.insert(0, string);		
		}
		else if (align.isRight()) {		
			result.append(string);
	
		}
		else if (align.isCenter()) {
			int iz = c/2;		
			result.insert(iz, string);
		}
		else {
			Assert.fail(XavaResources.getString("align_not_supported", align.getDescription()));
		}
		return result.toString();	
	}

	/**
	 * Creates a string from repeating another string. <p> 
	 * 
	 * @param count  Times to repeat
	 * @param string  String to repeat
	 * @return Not null
	 */
	public static String repeat(int count, String string) {	
		return repeatSB(count, string).toString();
	}

	/**
	 * Create a <code>StringBuffer</tt> repeating a string. <p> 
	 * 
	 * @param count  Times to repeat
	 * @param string  String to repeat
	 * @return Not null
	 */
	private static StringBuffer repeatSB(int count, String string) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < count; i++){
			result.append(string);
		}
		return result;
	}
	
  /**
   * Converts a list of comma separated elements in a string array. <p> 
   *
   * For example, the list <i>Angel, Manolo, Antonia</i> is converted to
   * a array of 3 elements with this 3 names without comman nor spaces.<br>
   *
   * @param list  String with the list. If null return a empty string
   * @return Not null, including the case <tt>list == null</tt>.
   */
  public final static String [] toArray(String list) {
		Collection c = toCollection(list);
		String [] rs = new String[c.size()];
		c.toArray(rs);
		return rs;
  }
  
  /**
   * Converts a list of elements separated by a arbitrary character 
   * in a string array. <p> 
   *
   * For example, the list <i>Angel : Manolo : Antonia</i> is converted to
   * a array of 3 elements with this 3 names without colon (for example) nor spaces.<br>
   *
   * @param list  String with the list. If null return a empty string
   * @param separator  The character used as separator.
   * @return Not null, including the case <tt>list == null</tt>.
   */
  public final static String [] toArray(String list, String separator) {
		Collection c = toCollection(list, separator);
		String [] rs = new String[c.size()];
		c.toArray(rs);
		return rs;
  }
  
  /**
   * Converts a list of comma separated elements in a string collection. <p> 
   *
   * For example, the list <i>Angel, Manolo, Antonia</i> is converted to
   * a collection of 3 elements with this 3 names without comman nor spaces.<br>
   *
   * @param list  String with the list. If null return a empty string
   * @return Not null, including the case <tt>list == null</tt>.
   */
  public final static Collection<String> toCollection(String list) {
  	return toCollection(list, ",");
  }
  
  /**
   * Converts a list of elements separated by a arbitrary character 
   * in a string collection. <p> 
   *
   * For example, the list <i>Angel : Manolo : Antonia</i> is converted to
   * a collection of 3 elements with this 3 names without colon (for example) nor spaces.<br>
   *
   * @param list  String with the list. If null return a empty string
   * @param separator  The character used as separator.
   * @return Not null, including the case <tt>list == null</tt>.
   */
  public final static Collection<String> toCollection(String list, String separator) {
	  return toList(list, separator); 
  }
  
  /**
   * Converts a list of comma separated elements in a string List. <p> 
   *
   * For example, the list <i>Angel, Manolo, Antonia</i> is converted to
   * a List of 3 elements with this 3 names without comma nor spaces.<br>
   *
   * @since 4.3
   * @param list  String with the list. If null return a empty string
   * @return Not null, including the case <tt>list == null</tt>.
   */
  public final static List<String> toList(String list) { 
	  return toList(list, ",");
  }
  
  /**
   * Converts a list of elements separated by a arbitrary character 
   * in a string List. <p> 
   *
   * For example, the list <i>Angel : Manolo : Antonia</i> is converted to
   * a List of 3 elements with this 3 names without colon (for example) nor spaces.<br>
   *
   * @since 4.3
   * @param list  String with the list. If null return a empty string
   * @param separator  The character used as separator.
   * @return Not null, including the case <tt>list == null</tt>.
   */
  public final static List<String> toList(String list, String separator) { 		
	  List<String> rs = new ArrayList<String>();
	  if (list == null) return rs;
	  fillCollection(rs, list, separator);
	  return rs;
  }
  
  
  private final static void fillCollection(Collection rs, String list, String separator) {
		Assert.arg(separator);
		StringTokenizer st = new StringTokenizer(list, separator);
		while (st.hasMoreTokens()) {
		  rs.add(st.nextToken().trim());
		}
  }

  
  /**
   * Converts a list of comma separated elements in a string set. <p> 
   *
   * For example, the list <i>Angel, Manolo, Antonia</i> is converted to
   * a set of 3 elements with this 3 names without comman nor spaces.<br>
   *
   * @param list  String with the list. If null return a empty string
   * @return Not null, including the case <tt>list == null</tt>.
   * @since 4.1
   */
  public final static Set<String> toSet(String list) { 
  	return toSet(list, ",");
  }

  /**
   * Converts a list of comma separated elements in a string set. <p> 
   *
   * For example, the list <i>Angel, Manolo, Antonia</i> is converted to
   * a set of 3 elements with this 3 names without comman nor spaces.<br>
   *
   * @param list  String with the list. If null return a empty string
   * @return Null in the case <tt>list == null</tt>.
   * @since 4.1
   */  
  public final static Set<String> toSetNullByPass(String list) { 
	if (list == null) return null;
    return toSet(list, ",");
  }
  
  /**
   * Converts a list of elements separated by a arbitrary character 
   * in a string set. <p> 
   *
   * For example, the list <i>Angel : Manolo : Antonia</i> is converted to
   * a set of 3 elements with this 3 names without colon (for example) nor spaces.<br>
   *
   * @param list  String with the list. If null return a empty string
   * @param separator  The character used as separator.
   * @return Not null, including the case <tt>list == null</tt>.
   * @since 4.1
   */
  public final static Set<String> toSet(String list, String separator) {   
		Set<String> rs = new HashSet<String>();
		if (list == null) return rs;
		fillCollection(rs, list, separator);
		return rs;
  }
    
 
  /**
   * Converts a collection of objects in a string of comma separated elements. <p> 
   *
   * For example, a collection of 3 elements with 3 names 
   * is converted to the string <i>Angel, Manolo, Antonia</i> <br>
   *
   * @param collection  Collection with the elements. If null return a empty string
   * @return Not null, including the case <tt>list == null</tt>.
   */
  public final static String toString(Collection collection) {
  	return toString(collection, ",");
  }
  
  /**
   * Converts a collection of objects in string of elements separated by a 
   * arbitrary character . <p> 
   *
   * For example, a collection of 3 elements with this 3 names
   * is converted to a string of 3 elements with this 3 names and colon (for example).<br>
   *
   * @param collection  A collection of objects. If null return a empty string
   * @param separator  The character used as separator.
   * @return Not null, including the case <tt>list == null</tt>.
   */
  public final static String toString(Collection collection, String separator) {
		Assert.arg(separator);
		StringBuffer cad = new StringBuffer();
		if (collection == null) return "";
		Iterator itCollections = collection.iterator();	
		while (itCollections.hasNext()) {	
			cad.append(itCollections.next());
			if (itCollections.hasNext()) {
				cad.append(separator);
			}	
		}	
		return cad.toString();
  }
    
 
   
  /**
   * Converts a string a object of the specified type. <p>
   * 
   * Supports all primitive type plus its wrappers except char and void.<br>
   * Support <code>String</code> and <code>BigDecimal</code> too. <p>
   * 
   * If there is conversion error or is a type not supporte
   * return null or the default value for the type (zero for numeric). <p>
   * 
   * @param type  The type of returned object (can be a primitive type
   * 							in this case return its wrapper). Not null
   * @param string  String with data to convert. Can be null, in this case
   * 		return a default value.
   */
  public final static Object toObject(Class type, String string) {
  	try {
	  	if (type.equals(String.class)) return string;
	  	
	  	if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
	  		try {
		  		if (Is.emptyString(string)) return new Integer(0);
		  		return new Integer(string.trim());
	  		}
	  		catch (NumberFormatException ex) {	  			
	  			log.warn(XavaResources.getString("string_convesion_zero_assumed_warning", string, type),ex);				  			
	  			return new Integer(0);
	  		}
	  	}
	  	
	  	if (type.equals(java.math.BigDecimal.class)) {
	  		try {	  		
		  		if (Is.emptyString(string)) return new BigDecimal("0.00");
		  		return new BigDecimal(string.trim());
	  		}
	  		catch (NumberFormatException ex) {
	  			log.warn(XavaResources.getString("string_convesion_zero_assumed_warning", string, type),ex);				  			
	  			return new BigDecimal("0.00");
	  		}		  		
	  	}
	  	
	  	if (type.equals(Double.TYPE) || type.equals(Double.class)) {
	  		try {	  		
		  		if (Is.emptyString(string)) return new Double(0);
		  		return new Double(string.trim());
	  		}
	  		catch (NumberFormatException ex) {
	  			log.warn(XavaResources.getString("string_convesion_zero_assumed_warning", string, type),ex);				  			
	  			return new Double(0);
	  		}		  				  		
	  	}
	  	
	  	if (type.equals(Long.TYPE) || type.equals(Long.class)) {
	  		try {	  		
		  		if (Is.emptyString(string)) return new Long(0);
		  		return new Long(string.trim());
	  		}
	  		catch (NumberFormatException ex) {
	  			log.warn(XavaResources.getString("string_convesion_zero_assumed_warning", string, type),ex);				  			
	  			return new Long(0);
	  		}		  				  		
	  	}
	  	
	  	if (type.equals(Float.TYPE) || type.equals(Float.class)) {
	  		try {	  		
		  		if (Is.emptyString(string)) return new Float(0);
		  		return new Float(string.trim());
	  		}
	  		catch (NumberFormatException ex) {
	  			log.warn(XavaResources.getString("string_convesion_zero_assumed_warning", string, type),ex);				  			
	  			return new Float(0);
	  		}		  				  		
	  	}
	  	
	  	if (type.equals(Short.TYPE) || type.equals(Short.class)) {
	  		try {	  		
		  		if (Is.emptyString(string)) return new Short((short)0);
		  		return new Short(string.trim());
	  		}
	  		catch (NumberFormatException ex) {
	  			log.warn(XavaResources.getString("string_convesion_zero_assumed_warning", string, type),ex);				  			
	  			return new Short((short)0);
	  		}		  				  		
	  	}
	  	
	  	if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
	  		try {	  		
		  		if (Is.emptyString(string)) return new Byte((byte)0);
		  		return new Byte(string.trim());
	  		}
	  		catch (NumberFormatException ex) {
	  			log.warn(XavaResources.getString("string_convesion_zero_assumed_warning", string, type),ex);				  			
	  			return new Byte((byte)0);
	  		}		  				  		
	  	}
	  	
	  	if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {	  			  		
		  	if (Is.emptyString(string)) return Boolean.FALSE;
		  	return Boolean.valueOf(string.trim());	  				  				  		
	  	}
        
        if (type.equals(Object.class)) {
            return string;
        }
        
  	}
  	catch (Exception ex) {
  		log.error(XavaResources.getString("string_convesion_warning", string, type), ex);
  	}  	
  	return null;
  }
  
	/**
	 * Returns a string like the sent one but with the first letter in uppercase. <p> 
	 * 
	 * If null is sent null is returned.
	 */
	public static String firstUpper(String s) {
		if (s==null) return null;
		if (s.length() == 0) return "";
		return s.substring(0, 1).toUpperCase() + s.substring(1);		
	}

	/**
	 * Returns a string like the sent one but with the first letter in lowercase. <p> 
	 * 
	 * If null is sent null is returned.
	 */
	public static String firstLower(String s) {
		if (s==null) return null;
		if (s.length() == 0) return "";
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}
	
	/**
	 * In the sent string changes the strings in toChange map
	 * for its values. <p>
	 * 
	 * @param string Not null
	 * @param toChagne Not null 
	 */
	public static String change(String string, Map toChange) {
		Iterator it = toChange.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry en = (Map.Entry) it.next();
			string = change(string, (String) en.getKey(), (String)en.getValue());
		}
		return string;				
	}
	
	/**
	 * Change in <code>string</tt> <code>original</code> by <code>newString</code>. <p>
	 * 
	 * @param string  String in which we make the changes. Can be null
	 * @param original  String to search. Not null
	 * @param newString  New value to put in place of original. Not null
	 * @return The changed string, if the sent string is null a null is returned 
	 */
	public static String change(String string, String original, String newString) {
		if (string == null) return null; 
		int i = string.indexOf(original);
		if (i < 0) return string; 
		StringBuffer sb = new StringBuffer(string);		
		while (i >= 0) {
			int f = i + original.length();			
			sb.replace(i, f, newString);			
			i = sb.toString().indexOf(original, i + newString.length());
		}		
		return sb.toString();
	} 

	/** 
	 * @return If string if null or have no tokens returns empty string.
	 */	
	public static String lastToken(String string) {
		if (string == null) return "";
		return lastToken(new StringTokenizer(string));
	}

	/** 
	 * @return If string if null or have no tokens returns empty string.
	 */		
	public static String lastToken(String string, String delim) {
		if (string == null) return "";
		if (delim.length() == 1 && string.indexOf(delim) < 0) return string; // Only one token
		return lastToken(new StringTokenizer(string, delim));
	}
		
	private static String lastToken(StringTokenizer st) {
		String r = "";
		while (st.hasMoreTokens()) r = st.nextToken();
		return r;
	}

	
	/**
	 * All string but without last token. <p>
	 * 
	 * A trim is applied to the result.
	 *  
	 * @return If string if null or have no tokens returns empty string.
	 */		
	public static String noLastToken(String string) {
		if (string == null) return "";
		return noLastToken(new StringTokenizer(string, " \t\n\r\f", true), false).trim();
	}	
	

	/**
	 * All string but without last token. <p>
	 * 
	 * Includes the last delim.
	 *  
	 * @return If string if null or have no tokens returns empty string.
	 */		
	public static String noLastToken(String string, String delim) {
		if (string == null) return "";
		return noLastToken(new StringTokenizer(string, delim, true), false);
	}
	
	/**
	 * All string but without first token. <p>
	 * 
	 * Includes the delim.
	 *  
	 * @return If string if null or have no tokens returns empty string.
	 */		
	public static String noFirstToken(String string, String delim) {
		if (string == null) return "";
		int idx = string.indexOf(delim);
		if (idx < 0) return "";
		return string.substring(idx);
	}

	
	/**
	 * All string but without last token. <p>
	 * 
	 * It does not include the last delim.
	 *  
	 * @return If string if null or have no tokens returns empty string.
	 */		
	public static String noLastTokenWithoutLastDelim(String string, String delim) {
		if (string == null) return "";
		return noLastToken(new StringTokenizer(string, delim, true), true);
	}	
	
	
	private static String noLastToken(StringTokenizer st, boolean withoutDelim) {
		StringBuffer r = new StringBuffer();
		int nt = withoutDelim?st.countTokens() - 2:st.countTokens() - 1;
		for (int i = 0; i < nt; i++) {
			r.append(st.nextToken());
		}		
		return r.toString();
	}	
		
	/**
	 * 
	 * @return If string if null or have no tokens returns empty string.
	 */
	public static String firstToken(String string, String delim) {
		if (string == null) return "";
		if (delim.length() == 1 && string.indexOf(delim) < 0) return string; // Only one token  
		StringTokenizer st = new StringTokenizer(string, delim);		
		if (st.hasMoreTokens()) return st.nextToken().trim();
		return "";
	}
	
	/** 
	* A key function of any application filtering process will be  
	* the removal of possible dangerous special characters.   
	*  
	* @author Sami AlSayyed 
	* @return new safe string 
	*/ 
	public static String removeXSS(String notSafeValue) {
		if (Is.emptyString(notSafeValue)) { 
			return notSafeValue;
		} 
			 		 
		CharSequence sequence = notSafeValue.subSequence(0, notSafeValue.length()); 
		Matcher matcher = XSS_PATTERN.matcher(sequence); 
			 
		return matcher.replaceAll(""); 
	} 	
	
	/** 
	* @param notSafeValue 
	* @return Safe Object 
	*/ 
	public static Object removeXSS(Object notSafeValue) { 
		if (notSafeValue != null && notSafeValue instanceof String) { 
			return removeXSS(notSafeValue.toString()); 
		} 
		return notSafeValue; 
	}	
	
	/**
	 * Convert a string with a Java identifier in label natural for a human. <p>
	 * 
	 * If you send "firstName" it returns "First name". <br>
	 * If you send  "CustomerOrder" it returns "Customer order". <br>
	 */
	public static String javaIdentifierToNaturalLabel(String name) { 
		if (Is.emptyString(name)) return "";
		if (name.toUpperCase().equals(name)) return change(name, "_", " "); 
		StringBuffer result = new StringBuffer();
		result.append(Character.toUpperCase(name.charAt(0)));
        boolean acronym = false;
        for (int i=1; i<name.length(); i++) {
            char letter = name.charAt(i);
            boolean isUpperCase = Character.isUpperCase(letter);
            if (!acronym && (isUpperCase || Character.isDigit(letter))) result.append(' ');
            if (isUpperCase) {
                if ((i < name.length()-1) && Character.isUpperCase(name.charAt(i+1))) {
                    acronym = true;
                }
                if (acronym) {
                    result.append(letter);
                } else {
                    result.append(Character.toLowerCase(letter));
                }
            } else {
                if (acronym) {
                    result.append(' ');
                    acronym = false;
                }
                result.append(letter);
            }
        }        		
		return result.toString();
	}
	
	/**
	 * Change from a vowel with an accent, to vowel with no accent
	 * 
	 * If you send "Camión" it returns "Camion"
	 * 
	 * @since v4m6
	 */
	public static String removeAccents(String value){ 
		return value.
			replace("á", "a").replace("Á", "A").
			replace("é", "e").replace("É", "E").
			replace("í", "i").replace("Í", "I").
			replace("ó", "o").replace("Ó", "O").
			replace("ú", "u").replace("Ú", "U");
	}
	
	/**
	 * Determines if the string is a valid model name.
	 * 
	 * @since 4.5
	 */
	public static boolean isModelName(String string) {
		return Character.isUpperCase(string.charAt(0));
	}

	
}
