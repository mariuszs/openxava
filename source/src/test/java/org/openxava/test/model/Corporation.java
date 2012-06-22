package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Laurent Wibaux 
 */

@Entity
@Views({
	@View(members="name; employees {employees}"),
	@View(name="Simple", members="name")
})
public class Corporation extends Identifiable {

	@Required
	private String name;

	@OneToMany(mappedBy="corporation", cascade=CascadeType.ALL)
	private Collection<CorporationEmployee> employees;
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setEmployees(Collection<CorporationEmployee> employees) {
		this.employees = employees;
	}

	public Collection<CorporationEmployee> getEmployees() {
		return employees;
	}
	
}
