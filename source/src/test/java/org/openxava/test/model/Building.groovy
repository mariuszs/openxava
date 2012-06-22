package org.openxava.test.model

import org.openxava.test.actions.*;
import org.openxava.annotations.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
@Views([
	@View(members="building [name, function; address]"), // All data in a group for a test
	@View(name="Simple", members="name")
])
@Tab( properties= "name, address.street, address.zipCode, address.city" )
class Building extends Nameable {
	
	@ManyToOne
	Company company
	
	@Column(length=40)
	@OnChange(OnChangeVoidAction) 
	String function 
	
	@AttributeOverrides([
		@AttributeOverride(name="street",
			column=@Column(name="BSTREET")),
		@AttributeOverride(name="zipCode",
			column=@Column(name="BZIPCODE"))
	])
	Address address
	
}
