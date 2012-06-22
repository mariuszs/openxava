package org.openxava.test.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;
import org.openxava.annotations.AsEmbedded;
import org.openxava.annotations.CollectionView;
import org.openxava.annotations.CollectionViews;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.OnChange;
import org.openxava.annotations.Required;
import org.openxava.annotations.RowStyle;
import org.openxava.annotations.SearchListCondition;
import org.openxava.annotations.SearchListConditions;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.Tab;
import org.openxava.annotations.Tabs;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Views ({	
	@View(members="number; name"),			
	@View(name="Complete",	members="number; name; level; regions; customers"),	
	@View(name="DecorateName", members="number; name"),	
	@View(name="ForCustomJSP", members="number; name; level"),
	@View(name="CannotCreateCustomer", members=
		"number; name; " +
		"customers { customers }"
	),
	@View(name="CustomersAsAggregate", members="number; name; level; customers"),
	@View(name="LevelNoDescriptionsList", members="number; name; level"),
	@View(name="SearchListCondition", members="number; name;level;customers"),
	@View(name="SearchListConditionOff", members="number; name;level;customers"),
	@View(name="SearchListConditionBlank", members="number; name;level;customers")
})
@Tabs({
	@Tab(filter=org.openxava.test.filters.NumbersToLettersFilter.class),
	@Tab(name="Other", filter=org.openxava.test.filters.NumbersToLettersFilter.class)
})

public class Seller {
	
	@Id @Column(length=3)
	@OnChange(forViews="DecorateName", value=org.openxava.test.actions.DecorateNameAction.class)
	private int number;
	
	@Column(length=40) @Required	
	private String name;
		
	@DescriptionsList(notForViews="ForCustomJSP, LevelNoDescriptionsList, SearchListCondition, SearchListConditionOff, SearchListConditionBlank")
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="LEVEL")
	@OnChange(org.openxava.test.actions.OnChangeVoidAction.class)
	@SearchListConditions({
		@SearchListCondition(value="${id}<'C'", forViews="SearchListCondition"),
		@SearchListCondition(value="", forViews="SearchListConditionBlank")
	})
	private SellerLevel level;

	@AsEmbedded(forViews="CustomersAsAggregate")
	@ListProperties("number, name, remarks, relationWithSeller, seller.level.description, type") 
	@OneToMany(mappedBy="seller")
	@CollectionViews ({
		@CollectionView("Simple"),
		@CollectionView(forViews="Complete", value="SimpleWithDeliveryPlaces")
	})
	@NoCreate(forViews="CannotCreateCustomer")
	@RowStyle(style="row-highlight", property="type", value="steady")
	@SearchListCondition(value="${number} < 5", forViews="SearchListCondition, SearchListConditionBlank")
	private Collection<Customer> customers;
		
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="BOSS")
	private Seller boss;	
	
	@Stereotype("REGIONS") @Type(type="org.openxava.test.types.RegionsType")
	private String [] regions;
	
 	public static Seller findByNumber(int number) throws NoResultException { 			
 		javax.persistence.Query query = org.openxava.jpa.XPersistence.getManager().createQuery("from Seller as o where o.number = :number"); 
		query.setParameter("number", new Integer(number)); 
 		return (Seller) query.getSingleResult();		  		
 	} 


	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SellerLevel getLevel() {
		// In this way in order to allow assume space (and not null) in the column 
		// as an case of level == null. A tip to work with legate databases with EJB3
		try { 
			if (level == null) return null;
			level.toString(); 
			return level;
		}
		catch (Exception ex) {
			return null;
		}
	}

	public void setLevel(SellerLevel level) {
		this.level = level;
	}

	public Collection<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(Collection<Customer> customers) {
		this.customers = customers;
	}

	public String [] getRegions() {
		return regions;
	}

	public void setRegions(String [] regions) {
		this.regions = regions;	
	}
	
	public Seller getBoss() {
		return boss;
	}

	public void setBoss(Seller boss) {
		this.boss = boss;
	}	
	   
}
