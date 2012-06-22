package org.openxava.test.model

import org.openxava.annotations.*;
import javax.persistence.*;

@Entity
@IdClass(EmployeeKey.class)
@Tab(properties="id, name, info.position, info.seniority")
class Employee {

	@Id 
	Integer id
	
	@Id @Column(length=40)	
	String name

	@OneToOne
	@PrimaryKeyJoinColumns([
		@PrimaryKeyJoinColumn(name="ID",
			referencedColumnName="EMP_ID"),
		@PrimaryKeyJoinColumn(name="NAME",
			referencedColumnName="EMP_NAME")
	])
	EmployeeInfo info

}
