package org.openxava.test.model

import org.openxava.model.*;
import org.openxava.annotations.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.openxava.test.annotations.*;
import org.openxava.test.validators.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
class Artist extends Identifiable {
	
	@ManyToOne
	Studio artistStudio // Not the same name of parent entity, to test a case
	
	@Column(length=40) @Required
	String name
	
	@Max(90l)	
	Integer age

}
