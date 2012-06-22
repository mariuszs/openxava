package org.openxava.test.domain

import javax.persistence.*;
import org.openxava.model.*;
import org.openxava.test.model.*;

/**
 * To test a model class inside a package with a name other than 'model' or 'modelo',
 * 
 * @author Javier Paniza 
 */

@Entity
class Moto extends Identifiable {
	
	String make
	String model
	

}
