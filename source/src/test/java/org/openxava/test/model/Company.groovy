package org.openxava.test.model

import org.openxava.annotations.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
@Tab(properties="name,mainBuilding.name") // name
class Company extends Nameable {
		
	@OneToMany(mappedBy="company", cascade=CascadeType.REMOVE)
	@DetailAction("Company.saveBuildingFailing") 
	Collection<Building> buildings
	
	@ReferenceView("Simple")
	@ManyToOne(fetch=FetchType.LAZY)
	Building mainBuilding // Must be here, below building collections, in order to test a bug

}
