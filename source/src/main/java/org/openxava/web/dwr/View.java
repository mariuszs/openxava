package org.openxava.web.dwr;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * For accessing to the View from DWR. <p>
 * 
 * @author Javier Paniza
 */

public class View extends DWRBase {
	
	private static Log log = LogFactory.getLog(View.class);

	
	public static void setFrameClosed(HttpServletRequest request, String frameId, boolean closed) { 
		try {
			String [] id = frameId.split("_");
			if (!"ox".equals(id[0])) {
				// Bad format. This method relies in the id format by Ids class
				log.warn(XavaResources.getString("impossible_store_frame_status")); 
				return;
			}
			String application = id[1];
			String module = id[2];
			checkSecurity(request, application, module);
			Users.setCurrent(request);

			org.openxava.view.View view = (org.openxava.view.View) 
				getContext(request).get(application, module, "xava_view");
			view.setFrameClosed(frameId, closed);
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("impossible_store_frame_status"), ex);
		}
	}

	
	
}
