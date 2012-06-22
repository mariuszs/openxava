package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class Experience extends Nameable {
	
	@ManyToOne
	private Programmer programmer;

	@Stereotype("MEMO")
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Programmer getProgrammer() {
		return programmer;
	}

	public void setProgrammer(Programmer programmer) {
		this.programmer = programmer;
	}
	
}
