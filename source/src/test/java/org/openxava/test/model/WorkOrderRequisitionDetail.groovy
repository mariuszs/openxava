package org.openxava.test.model

import org.openxava.model.*;

import javax.persistence.*;

/**
 * 
 * @author JavierPaniza 
 */

@Entity
class WorkOrderRequisitionDetail extends Identifiable {

	@ManyToOne
	WorkOrderRequisition workOrderRequisition
	
	@Column(length=60)
	String description

}
