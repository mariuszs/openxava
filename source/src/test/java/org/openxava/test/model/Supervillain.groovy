package org.openxava.test.model

import org.openxava.model.*;
import org.openxava.annotations.*;
import javax.persistence.*;

@Entity
@Table(name="SUPERS")
@Tab(properties="name, superheroe.name")
class Supervillain extends Identifiable {
	
	@Column(length=40) @Required
	String name
	
	@ManyToOne(fetch=FetchType.LAZY)
	Superheroe superheroe

}
