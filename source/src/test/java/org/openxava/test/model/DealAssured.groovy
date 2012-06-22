package org.openxava.test.model

import javax.persistence.*
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter


@Entity 
public class DealAssured { 

	@Id
	Long id
	
	@Column(length=40)
	String name
	
	@OneToOne(mappedBy="assured", fetch=FetchType.LAZY)
	Deal deal  
	
} 