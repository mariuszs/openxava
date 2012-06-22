package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.hibernate.validator.*;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.test.calculators.*;
import org.openxava.test.validators.*;
import org.openxava.validators.*;

/**
 * As Product2 but uses property-based access. <p>
 * 
 * In this class the getters are annotated instead of fields.<br>
 * 
 * @author Javier Paniza
 */

@Entity
@Table(name="PRODUCT")
@View( members=
	"number;" +
	"description;" +
	"photos;" +
	"family;" +
	"subfamily;" +
	"warehouse, zoneOne;" +
	"unitPrice, unitPriceInPesetas;"		
)
@Tab(properties="number, description, family.description, subfamily.description")

public class Product4 {
		 
	private long number;	
	private String description;	
	private String photos;
	private Family2 family;		
	private Subfamily2 subfamily;
	private Warehouse warehouse;
	private BigDecimal unitPrice;	
	private Formula formula;	
	private int subfamilyNumber; 
	
	@PrePersist
	public void validate() throws ValidationException {
		if (getDescription().contains("OPENXAVA")) {
			throw new ValidationException("openxava_not_saleable"); 
		}
		if (getNumber() == 666) {
			throw new InvalidStateException(
				new InvalidValue [] {
					new InvalidValue(
						"number_of_man", getClass(), "number",
						getNumber(), this)
				}
			);
		}
	}
		
	@Id @Column(length=10)
	public long getNumber() {
		return number;
	}
	
	public void setNumber(long number) {
		this.number = number;
	}
	
	@Column(length=40) @Required
	@PropertyValidators ({
		@PropertyValidator(value=ExcludeStringValidator.class, properties=
			@PropertyValue(name="string", value="MOTO")
		),
		@PropertyValidator(value=ExcludeStringValidator.class, properties=
			@PropertyValue(name="string", value="COCHE")
		)		
	})			
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Stereotype("IMAGES_GALLERY")	
	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	@Stereotype("MONEY") @Required
	@DefaultValueCalculator(value=DefaultProductPriceCalculator.class, properties=
		@PropertyValue(name="familyNumber")
	)
	@PropertyValidator(UnitPriceValidator.class)	
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	@ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="FAMILY")
	@DefaultValueCalculator(value=IntegerCalculator.class, properties=
		@PropertyValue(name="value", value="2")
	)
	@DescriptionsList(orderByKey=true)	
	public Family2 getFamily() {
		return family;
	}

	public void setFamily(Family2 family) {
		this.family = family;
	}

	@ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="SUBFAMILY") @NoCreate
	@DescriptionsList(
		descriptionProperties="description", // In this case descriptionProperties can be omited
		depends="family",
		condition="${family.number} = ?"
	)	
	public Subfamily2 getSubfamily() {
		return subfamily;
	}

	public void setSubfamily(Subfamily2 subfamily) {
		this.subfamily = subfamily;
	}

	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumns({ 
		@JoinColumn(name="ZONE", referencedColumnName="ZONE"), 
		@JoinColumn(name="WAREHOUSE", referencedColumnName="NUMBER") 
	})
	@DefaultValueCalculator(DefaultWarehouseCalculator.class)
	@DescriptionsList
	@OnChange(org.openxava.test.actions.OnChangeWarehouseAction.class)	
	public Warehouse getWarehouse() {
		// In this way because the columns for warehouse can contain
		// 0 for no value
		try {
			if (warehouse != null) warehouse.toString(); // to force load
			return warehouse;
		}
		catch (EntityNotFoundException ex) {			
			return null;  
		}
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	// Only to show in view
	@Transient @Stereotype("SUBFAMILY_DEPENDS_REFERENCE")	
	public int getSubfamilyNumber() {
		return subfamilyNumber;
	}

	public void setSubfamilyNumber(int subfamilyNumber) {
		this.subfamilyNumber = subfamilyNumber;
	}

	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="FORMULA_OID")
	@ReferenceView("Simple") 
	@AsEmbedded(forViews="WithFormulaAsAggregate")	
	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}
	
	@Transient @Depends("unitPrice")  
	@Max(999999999999999999L) 	
	public BigDecimal getUnitPriceInPesetas() {
		if (unitPrice == null) return null;
		return unitPrice.multiply(new BigDecimal("166.386")).setScale(0, BigDecimal.ROUND_HALF_UP);
	}
	
	@Transient @Stereotype("LABEL") 
	public String getZoneOne() {
		return "In ZONE 1";
	}		

}
