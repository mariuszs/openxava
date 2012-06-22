package org.openxava.test.model

import org.openxava.model.*
import javax.persistence.*
import org.openxava.annotations.*

@Entity
@View(members="value; children")
class AsEmbedContainer extends Identifiable {
	
	@Column(length=40)
	String value
		
	@OneToMany(mappedBy = "container")
	@AsEmbedded
	Collection<AsEmbed1> children

}
