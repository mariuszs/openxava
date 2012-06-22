package org.openxava.test.model;

import javax.persistence.*;

import org.hibernate.validator.*;
import org.openxava.annotations.*;

/**
 * Family2 not have oid,
 * like the typical number/description table. <p>
 * 
 * In this class we use Hibernate Validator annotations
 * for defining the size of the properties (@Max and @Length)
 * instead of the JPA one (@Column(length=)).<br>
 * 
 * @author Javier Paniza
 */

@Entity
@View(name="OneLine", members="number, description") 
public class Family2 {

	@Id @Max(999)
	private int number;
	
	@Length(max=40) @Required
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
}
