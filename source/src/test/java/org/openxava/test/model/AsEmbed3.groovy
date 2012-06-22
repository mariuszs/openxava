package org.openxava.test.model
import org.openxava.model.*;

import javax.persistence.*
import org.openxava.annotations.*

@Entity
@View(members="value3")
public class AsEmbed3 extends Identifiable {

	@Column(length=40)
	String value3;
		
	@OneToOne(mappedBy = "asEmbed3")
	AsEmbed2 asEmbed2;	

}
