package org.openxava.test.model

import org.openxava.annotations.*;
import org.openxava.model.*;
import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
class Journal extends Identifiable {
	
	@Required
	Date date;
	
	@Column(length=40) @Required
	String description

	@OneToMany(mappedBy="theJournal",cascade=CascadeType.ALL)	
	Collection<JournalEntry> entries
	
}
