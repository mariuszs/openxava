package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
class InventDetail extends Identifiable {
	
	@ManyToOne
	Invent invent
	
	@Column(length=40) @Required
	String description

}
