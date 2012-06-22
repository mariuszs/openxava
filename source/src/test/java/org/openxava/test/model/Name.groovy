package org.openxava.test.model

import javax.persistence.*
import org.openxava.annotations.*

/**
 * 
 * @author Javier Paniza 
 */
class Name {
	
	@Column(length=40) @Required
	String name

}
