package org.openxava.test.model

import org.hibernate.validator.constraints.*;
import org.openxava.annotations.*;
import org.openxava.model.*;
import org.openxava.test.validators.*;

import javax.persistence.*;

/**
 *
 * @author Javier Paniza
 */

@Entity
@EntityValidator(value=
	JournalEntryValidator.class, 
	properties=@PropertyValue(name="theJournal")
) 
class JournalEntry extends Identifiable {

	@ManyToOne 
	Journal theJournal
	
	@Column(length=40) @Required
	String description
	
	@Required
	BigDecimal cantidad
	
}
