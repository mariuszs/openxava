package org.openxava.test.model

import org.openxava.model.*;
import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
class WorkOrderRequisition extends Identifiable {

	@ManyToOne
	WorkOrder workOrder
	
	@Column(length=60)
	String description
	
	@OneToMany(mappedBy="workOrderRequisition",cascade = CascadeType.REMOVE)
	Collection<WorkOrderRequisitionDetail> details

		
}
