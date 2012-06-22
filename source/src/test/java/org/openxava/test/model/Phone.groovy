package org.openxava.test.model

import org.openxava.test.validators.*;
import org.openxava.provaox.validators.*;
import org.openxava.annotations.*;
import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity 
@EntityValidators ([ 
	@EntityValidator(value=PhoneNumberValidator.class, 
		properties=[  
			@PropertyValue(name="phoneCountry"), 
			@PropertyValue(name="phone") 
		]
	) 
])   
public class Phone {
	 
	@Id @Hidden @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(length = 8) 
	int phoneId 
	
	@ManyToOne(optional = false) @NoCreate @NoModify 
	@DescriptionsList 
	Country phoneCountry 
	
	@Column(length = 20) @Required 
	String phone // This must name like the entity in order to test a case
	 	
	int phoneExtension
	
} 