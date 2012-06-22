package org.openxava.test.model

import org.openxava.model.*
import javax.persistence.*
import org.openxava.annotations.*

@Entity
@View(members="value2; asEmbed3")
class AsEmbed2 extends Identifiable {

	@Column(length=40)
	String value2;
		
	@OneToOne(mappedBy = "asEmbed2")
	AsEmbed1 asEmbed1	
	
	@OneToOne
	@AsEmbedded
	AsEmbed3 asEmbed3

}
