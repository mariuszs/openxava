package org.openxava.test.model

import javax.persistence.*;
import org.openxava.annotations.*;

@View(members="subfamily, year")
class FilterBySubfamilyAndYear extends FilterBySubfamily {
	
	@Column(length=4)
	int year

}
