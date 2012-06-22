package org.openxava.test.model


import org.openxava.annotations.*;
import org.openxava.model.*;
import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity 
class Studio extends Identifiable { 
	
	@Column(length= 40) @Required 
	String name
	 
	@OneToMany(mappedBy="artistStudio") @AsEmbedded 
	Collection<Artist> artists
	
} 