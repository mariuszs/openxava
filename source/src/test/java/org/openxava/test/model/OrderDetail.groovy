package org.openxava.test.model
import org.openxava.model.*;

import javax.persistence.*
import org.openxava.annotations.*

@Entity
@View(members="product; quantity, amount")  
class OrderDetail extends Identifiable {
	
	@ManyToOne // Lazy fetching fails on removing a detail from parent
	Order parent
		
	int quantity;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@ReferenceView("SimpleWithFamily") 
	Product2 product;
	
	@Stereotype("MONEY")  
	@Depends("product.number, quantity") 
	BigDecimal getAmount() {
		return new BigDecimal(quantity).multiply(getProduct().getUnitPrice());
	}

}
