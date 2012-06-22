package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * Create on 16/01/2012 (09:54:11)
 * @author Ana Andres
 */
@IdClass(CityKey.class)
@Entity
@Tab(properties="code, name, state.fullNameWithFormula")
public class City {
	
	@Id 
	@ManyToOne(fetch=FetchType.LAZY) 
	@DescriptionsList
	@JoinColumn(name="STATE", referencedColumnName="ID")
	private State state;
	
	@Id
	private int code;
	
	@Stereotype("CITY_NAME")
	private String name;

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
