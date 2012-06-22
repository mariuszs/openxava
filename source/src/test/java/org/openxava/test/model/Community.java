package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@View(name="EditableMembers") 
public class Community extends Nameable {
	
	@ManyToMany
	@ListAction("ManyToMany.new")
	@EditAction(forViews="EditableMembers", value="ManyToMany.edit") 
	@OrderBy("id asc") 	
	private Collection<Human> members;

	public void setMembers(Collection<Human> members) {
		this.members = members;
	}

	public Collection<Human> getMembers() {
		return members;
	}
	
}
