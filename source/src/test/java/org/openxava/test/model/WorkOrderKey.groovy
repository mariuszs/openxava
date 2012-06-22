package org.openxava.test.model

import javax.persistence.*

/**
 * 
 * @author Javier Paniza
 */
class WorkOrderKey implements java.io.Serializable {

	Integer year;
	Integer number;
	
	@Override
	boolean equals(Object obj) {
		if (obj == null) return false
		return obj.toString().equals(this.toString())
	}
	
	@Override
	int hashCode() {
		return toString().hashCode()
	}
	
	@Override
	String toString() {
		return "WorkOrderKey::" + year + ":" + number;
	}

}
