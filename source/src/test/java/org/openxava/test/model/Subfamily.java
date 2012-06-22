package org.openxava.test.model;

import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.annotations.*;
import org.hibernate.validator.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@View(members=
	"number;" +
	"data {" +
	"	familyNumber;" +
	"	family;" +
	"	description;" +
	"	remarks" +
	"}"	
)
@Tab(name="CompleteSelect",
	properties="number, description, family",
	/* For Hypersonic */ 	
	baseCondition = 
		"select ${number}, ${description}, FAMILY.DESCRIPTION " +
		"from   XAVATEST.SUBFAMILY, XAVATEST.FAMILY " +
		"where  SUBFAMILY.FAMILY = FAMILY.NUMBER"								
	/* For AS/400 	    	
	baseCondition = 
		"select ${number}, ${description}, XAVATEST.FAMILY.DESCRIPTION " +
		"from   XAVATEST.SUBFAMILY, XAVATEST.FAMILY " +
		"where  XAVATEST.SUBFAMILY.FAMILY = XAVATEST.FAMILY.NUMBER"
	*/		
)
public class Subfamily {
	
	@Id @GeneratedValue(generator="system-uuid") @Hidden 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String oid;
	
	@Column(length=3) @Required @Stereotype("ZEROS_FILLED")
	private int number;
	
	@Required @Stereotype("FAMILY") @Column(name="FAMILY")
	private int familyNumber;
	
	@Column(length=40) @Required 
	private String description;
	
	@Column(length=400) @Stereotype("MEMO") 
	@org.hibernate.annotations.Type(type="org.openxava.types.NotNullStringType")
	private String remarks;
	
	@Column(length=40) @Hidden
	public String getFamily() {
		return ""; /* Only for column description in tab */
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFamilyNumber() {
		return familyNumber;
	}

	public void setFamilyNumber(int familyNumber) {
		this.familyNumber = familyNumber;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}
