package org.openxava.test.model

import javax.persistence.*;
import org.openxava.annotations.*;

@Entity
@View(members="""
	main { id, name }
	assured { assured } 
""") 
@Tab(properties="id, name, assured.name") 
class Deal { 
	
	@Id
	Long id
	
	@Column(length=40)
	String name
	
	@OneToOne 
	@PrimaryKeyJoinColumn
	DealAssured assured 
	
} 