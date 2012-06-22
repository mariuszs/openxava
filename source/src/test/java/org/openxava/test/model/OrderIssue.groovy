package org.openxava.test.model

import javax.persistence.*;
import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
class OrderIssue extends Identifiable {
	
	Date date
	
	@Stereotype("MEMO")
	String description
	
	@ManyToOne(fetch=FetchType.LAZY)
	@SearchKey 
	Order order

}
