package org.openxava.test.model

import org.openxava.annotations.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

class Login {
	
	@Column(length=20)
	String user
	
	@Column(length=15) @Stereotype("PASSWORD")
	String password

}
