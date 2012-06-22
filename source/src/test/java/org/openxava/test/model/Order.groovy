package org.openxava.test.model
import org.openxava.model.*;

import javax.persistence.*

import org.openxava.annotations.*
import org.openxava.calculators.*
import org.openxava.jpa.*

@Entity
@Table(name="TOrder")
@View(members="""
	year, number, date;
	customer;
	details;
	amount;
	remarks
	"""
)
class Order extends Identifiable {
	
	@Column(length=4) 
	@DefaultValueCalculator(CurrentYearCalculator.class)
	@SearchKey 
	int year
	
	
	@Column(length=6)
	@SearchKey @ReadOnly 
	int number
	
	@Required
	@DefaultValueCalculator(CurrentDateCalculator.class)
	Date date	
	
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@ReferenceView("Simplest")
	Customer customer
	
	
	@OneToMany(mappedBy="parent", cascade=CascadeType.ALL)	
	@ListProperties("product.number, product.description, quantity, product.unitPrice, amount")
	Collection<OrderDetail> details = new ArrayList<OrderDetail>()  
	
	@Stereotype("MEMO") 
	String remarks
	
	
	@Stereotype("MONEY")
	BigDecimal getAmount() {
		BigDecimal result = 0
		details.each { OrderDetail detail ->
			result += detail.amount
		}
		return result
	}
		
	@PrePersist
	void calculateNumber() throws Exception { 		
		Query query = XPersistence.getManager()
			.createQuery("select max(o.number) from Order o " + 
					"where o.year = :year")
		query.setParameter("year", year)		
		Integer lastNumber = (Integer) query.getSingleResult()
		this.number = lastNumber == null?1:lastNumber + 1
	}
		
}
