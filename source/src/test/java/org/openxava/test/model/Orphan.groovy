package org.openxava.test.model

import org.openxava.jpa.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
class Orphan extends Nameable {
	 
	@ManyToOne
	Orphanage orphanage
	
	static int count() {
		Query query = XPersistence.getManager().createQuery("select count(o) from Orphan o")
		return query.getSingleResult()
	}
	
}
