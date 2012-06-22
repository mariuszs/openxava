package org.openxava.test.model

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@IdClass(EmployeeInfoKey.class)
class EmployeeInfo {
	
	@Id @Column(name="EMP_ID") 
	Integer id
	
	@Id @Column(name="EMP_NAME", length=40)
	String name

	@Column(name="EMP_POSITION", length=40)
	String position
	
	@Column(name="EMP_SENIORITY", length=20)
	String seniority

}
