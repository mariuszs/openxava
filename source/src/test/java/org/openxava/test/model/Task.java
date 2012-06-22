package org.openxava.test.model;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.filters.*;

/** 
 * 
 * @author Javier Paniza
 */

@Entity
@Tab(filter=UserFilter.class, baseCondition="${user} = ?")
public class Task {
	
	@Id @GeneratedValue(generator="system-uuid") @Hidden 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name="ID")
	private String oid;
	
	@Stereotype("NO_FORMATING_STRING") 
	@Required @DefaultValueCalculator(CurrentUserCalculator.class)
	@Column(length=50, name="USERNAME")
	private String user;
	
	@Stereotype("NO_FORMATING_STRING") 
	@Required @DefaultValueCalculator(CurrentUserGivenNameCalculator.class)
	@Column(length=30)
	private String userGivenName;
	
	@Stereotype("NO_FORMATING_STRING") 
	@Required @DefaultValueCalculator(CurrentUserFamilyNameCalculator.class)
	@Column(length=30)
	private String userFamilyName;
	
	@Stereotype("EMAIL") 
	@Required @DefaultValueCalculator(CurrentUserEmailCalculator.class)
	@Column(length=50)
	private String userEMail;
		
	@Required @DefaultValueCalculator(CurrentDateCalculator.class)
	private java.util.Date date;
	
	@Required @Column(length=50)
	private String summary;
	
	@Stereotype("MEMO")
	private String comments;
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public java.util.Date getDate() {
		return date;
	}

	public void setDate(java.util.Date date) {
		this.date = date;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getUserGivenName() {
		return userGivenName;
	}

	public void setUserGivenName(String userGivenName) {
		this.userGivenName = userGivenName;
	}

	public String getUserFamilyName() {
		return userFamilyName;
	}

	public void setUserFamilyName(String userFamilyName) {
		this.userFamilyName = userFamilyName;
	}

	public String getUserEMail() {
		return userEMail;
	}

	public void setUserEMail(String userEMail) {
		this.userEMail = userEMail;
	}
		
}
