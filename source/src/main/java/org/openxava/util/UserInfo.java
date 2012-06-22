package org.openxava.util;

/**
 * Data about the current logged user, obtained via {@link Users}. <p>
 * 
 * This data is obtained from the portal where the OpenXava module
 * is executing.
 * 
 * @author Javier Paniza
 */

public class UserInfo implements java.io.Serializable {
	
	private String id;
	private String givenName;
	private String familyName;
	private String email;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGivenName() {
		return givenName==null?"":givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getFamilyName() {
		return familyName==null?"":familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getEmail() {
		return email==null?"":email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
