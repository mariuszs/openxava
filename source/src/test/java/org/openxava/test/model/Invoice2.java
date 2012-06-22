package org.openxava.test.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.hibernate.validator.*;
import org.openxava.annotations.*;
import org.openxava.calculators.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Table(name="INVOICE")
@IdClass(InvoiceKey.class) // We reuse the key class for Invoice
@Views({
	@View( members =
		"year, number, date;" + 
		"vatPercentage, amountsSum;" +
		"customer;" +
		"details;"
	),
	@View( name="NoModifyDetails", members =
		"year, number, date;" + 
		"details;"
	)
})

public class Invoice2 {
	
	@Transient
	private boolean removing = false;
	
	@Id @Column(length=4) @Required
	@DefaultValueCalculator(CurrentYearCalculator.class)
	private int year;
	
	@Id @Column(length=6) @Required
	private int number;
		
	@Required
	@DefaultValueCalculator(CurrentDateCalculator.class)
	private java.util.Date date;
	
	@Digits(integerDigits=2, fractionalDigits=1) 
	@Required
	private BigDecimal vatPercentage;
	
	@Stereotype("MONEY") @ReadOnly
	private BigDecimal amountsSum; 
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@ReferenceView("Simplest")
	private Customer customer;
	
	@OneToMany (mappedBy="invoice", cascade=CascadeType.REMOVE)
	@org.hibernate.validator.Size(min=1) 
	@ListProperties("product.description, quantity, unitPrice, amount")
	@XOrderBy("product.description desc")
	@NoModify(forViews="NoModifyDetails")
	@NewAction("InvoiceDetail2.new") 
	private Collection<InvoiceDetail2> details;
	
	boolean isRemoving() {
		return removing;
	}
	
	@PreRemove
	private void markRemoving() {
		this.removing = true;
	}
	
	@PostRemove
	private void unmarkRemoving() {
		this.removing = false;
	}
	
	public void recalculateAmountsSum() { 
		BigDecimal result = new BigDecimal("0.00");
		for (InvoiceDetail2 detail: getDetails()) {
			result = result.add(detail.getAmount());
		}
		setAmountsSum(result);
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public java.util.Date getDate() {
		return date;
	}

	public void setDate(java.util.Date date) {
		this.date = date;
	}

	public BigDecimal getVatPercentage() {
		return vatPercentage;
	}

	public void setVatPercentage(BigDecimal vatPercentage) {
		this.vatPercentage = vatPercentage;
	}
	
	public BigDecimal getAmountsSum() {
		return amountsSum;
	}

	public void setAmountsSum(BigDecimal amountsSum) {
		this.amountsSum = amountsSum;
	}	

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Collection<InvoiceDetail2> getDetails() {
		if (details == null) details = new ArrayList(); 
		return details;
	}

	public void setDetails(Collection<InvoiceDetail2> details) {
		this.details = details;
	}


}
