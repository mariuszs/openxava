package org.openxava.test.calculators;

import org.openxava.calculators.*;

/**
 * @author Javier Paniza
 */
public class CarrierRemarksCalculator implements ICalculator {

	private String drivingLicenceType;

	public Object calculate() throws Exception {		
		if (drivingLicenceType == null) return "";
		if (drivingLicenceType.toUpperCase().startsWith("C")) {
			return "He can drive trucks";
		}
		return "";
	}
	public String getDrivingLicenceType() {
		return drivingLicenceType;
	}
	public void setDrivingLicenceType(String tipoDrivingLicence) {
		this.drivingLicenceType = tipoDrivingLicence;
	}
}
