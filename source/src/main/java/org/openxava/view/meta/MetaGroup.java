package org.openxava.view.meta;



import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class MetaGroup extends MetaMember {
	
	private String membersNames;
	private boolean alignedByColumns = false;
	private MetaView metaView;
	private MetaView metaViewParent;
	
	
		
	public MetaGroup(MetaView parent) {
		this.metaViewParent = parent;
	}
	
	public MetaView getMetaView() throws XavaException {
		if (metaView == null) {
			try {
				metaView = (MetaView) metaViewParent.clone();
				metaView.setAlignedByColumns(isAlignedByColumns()); 
				metaView.setMembersNames(membersNames);				
			} 
			catch (CloneNotSupportedException e) {
				throw new XavaException("group_view_error_no_clone");			
			}			
		}
		return metaView;				
	}

	public void setMembersNames(String members) {
		this.membersNames = members;		
	}

	public boolean isAlignedByColumns() {
		return alignedByColumns;
	}

	public void setAlignedByColumns(boolean alignedByColumns) {
		this.alignedByColumns = alignedByColumns;
	}
		
}
