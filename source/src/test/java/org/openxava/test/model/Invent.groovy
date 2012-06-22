package org.openxava.test.model

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
class Invent extends Identifiable {
	
	@Column(length=40) @Required
	String description
	
	
	int value
	
	@OneToMany(mappedBy="invent", cascade=CascadeType.REMOVE)
	Collection<InventDetail> details

}
