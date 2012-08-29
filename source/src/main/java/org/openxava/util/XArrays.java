package org.openxava.util;

import java.util.*;

import org.apache.commons.lang.*;





/**
 * Utilities to work with arrays. <p> 
 * 
 * @since 4.5
 * @author Javier Paniza
 */
public class XArrays {

	/** 
	 * To know if the two arrays has the same elements. <p>
	 * 
	 * The order does not matter, a null arrays has the same elements than an arrays with 0 elements.
	 * 
	 * @param array1 Can be null
	 * @param array2 Can be null
	 * @since 4.5
	 */
	public static boolean haveSameElements(int[] array1, int[] array2) { 
		if (array1 == null && array2 == null) return true;		
		if (array1 == null) array1 = ArrayUtils.EMPTY_INT_ARRAY;
		if (array2 == null) array2 = ArrayUtils.EMPTY_INT_ARRAY;
		Arrays.sort(array1);
		Arrays.sort(array2);
		return Arrays.equals(array1, array2);
	}
	
}
