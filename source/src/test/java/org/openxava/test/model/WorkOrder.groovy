package org.openxava.test.model

import org.openxava.annotations.*;
import org.openxava.provaox.model.*;
import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@IdClass(WorkOrderKey.class)
class WorkOrder {
	
	@Id @Column(length=4)
	Integer year
	
	@Id @Column(length=6)
	Integer number

	@OneToMany(cascade=CascadeType.REMOVE,mappedBy="workOrder")	
	Collection<WorkOrderRequisition> requisitions

}
