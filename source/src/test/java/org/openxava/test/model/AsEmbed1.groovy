package org.openxava.test.model;
import org.openxava.model.*;

import javax.persistence.*;
import org.openxava.annotations.*;

@Entity
@View(members="value1; asEmbed2")
class AsEmbed1 extends Identifiable {

	@ManyToOne(fetch = FetchType.LAZY)
	AsEmbedContainer container;
		
	@Column(length=40)
	String value1;
	
	@OneToOne
	@AsEmbedded
	AsEmbed2 asEmbed2;
	
}
