package org.openxava.test.model


import org.openxava.model.*;

import javax.persistence.*;

@Entity
class Country extends Identifiable {

	@Column(length=20)
	String name
	
}
