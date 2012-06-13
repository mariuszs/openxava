if (openxava == null) var openxava = {};

openxava.init = function(application, module) {
	document.onkeydown = openxava.processKey;
	openxava.initUI(application, module);	
	openxava.editorsInitFunctionsClosed = true; 
	if (openxava.staticInit == null) {
		openxava.staticInit = function() {			  
			  openxava.initEditors();
		}
		$(openxava.staticInit);
	}
	openxava.initStrokeActions(application, module);
}

openxava.ajaxRequest = function(application, module, firstRequest) {
	if (openxava.isRequesting(application, module)) return;
	openxava.setRequesting(application, module);
	document.throwPropertyChange = false; 
	openxava.getElementById(application, module, "loading").value=true;    
	document.body.style.cursor='wait';
	$('#xava_loading').show(); 	
	$('#xava_loading2').show(); 
	Module.request(
			application, module, document.additionalParameters,			
			openxava.getFormValues(openxava.getForm(application, module)), 
			openxava.getMultipleValues(application, module), 
			openxava.getSelectedValues(application, module),
			firstRequest,
			openxava.refreshPage);	
	$(window).unbind('resize');
}

openxava.refreshPage = function(result) {	
	var changed = "";	
	if (result.error != null) {		
		openxava.systemError(result);
		changed = " ERROR";
		return;
	}
	if (result.reload) {
		window.location.reload();
		return;
	}		
	if (result.forwardURL != null) {
		if (result.forwardInNewWindow) { 
			window.open(result.forwardURL);
			var form = openxava.getForm(result.application, result.module);
			if (form != null) { 
				form[openxava.decorateId(result.application, result.module, "xava_action")].value="";	
				form[openxava.decorateId(result.application, result.module, "xava_action_argv")].value="";
				form[openxava.decorateId(result.application, result.module, "xava_changed_property")].value="";
				form[openxava.decorateId(result.application, result.module, "xava_action_range")].value="";
			}
			window.location.reload();	
			return; 			
		}
		else {
			location.href=result.forwardURL;			
		}
	}
	else if (result.forwardURLs != null) {
		for (var i=0; i<result.forwardURLs.length; i++) {
			window.open(result.forwardURLs[i]);
			var form = openxava.getForm(result.application, result.module);
			if (form != null) { 
				form[openxava.decorateId(result.application, result.module, "xava_action")].value="";	
				form[openxava.decorateId(result.application, result.module, "xava_action_argv")].value="";
				form[openxava.decorateId(result.application, result.module, "xava_changed_property")].value="";
				form[openxava.decorateId(result.application, result.module, "xava_action_range")].value="";
			}
		}
		window.location.reload(); 
		return; 				
	}	
	else if (result.nextModule != null) {	
		openxava.updateRootIds(result.application, result.module, result.nextModule);
		document.getElementById("xava_last_module_change").value=result.module + "::" + result.nextModule;		
		if (openxava.dialogLevel > 0) openxava.closeDialog(result);							
		openxava.resetRequesting(result); 
		openxava.ajaxRequest(result.application, result.nextModule); 
		return;
	}	
	else {		
		if (result.showDialog){	
			openxava.disableElements(result);
		}
		else if (result.hideDialog) {
			openxava.closeDialog(result); 
		}
		openxava.dialogLevel = result.dialogLevel;
		var dialog;
		if (result.showDialog) {
			dialog = openxava.getDialog(result.application, result.module);
		}
		openxava.strokeActions = result.strokeActions;
		var changedParts = result.changedParts;
		for (var id in changedParts) {			
			changed = changed + id + ", ";  			
			try {
				openxava.setHtml(id, changedParts[id]);
			}
			catch (ex) {
				changed = changed + " ERROR";
				alert("Error refreshing part: " + id);
				errors = true;
				break;
			}			
		}  
		if (result.showDialog){	
			dialog.attr("application", result.application); 
			dialog.attr("module", result.module); 
			dialog.dialog('option', 'title', result.dialogTitle);
			dialog.dialog('option', 'width', 'auto');
			dialog.dialog('option', 'width', dialog.parent().width());
			dialog.dialog('option', 'height', 'auto');
			dialog.dialog('option', 'position', 'center' );
			dialog.dialog('option', 'zIndex', 99999 );
			dialog.dialog('open');
		}
		if (result.focusPropertyId != null) { 
			openxava.getElementById(result.application, result.module, "xava_focus_property_id").value = result.focusPropertyId;
			openxava.setFocus(result.application, result.module);		
		}		
		openxava.initUI(result.application, result.module, result.currentRow); 	
	}		
	document.getElementById('xava_processing_layer').style.display='none';
	var form = openxava.getForm(result.application, result.module);	
	if (form != null) {  
		form[openxava.decorateId(result.application, result.module, "xava_action")].value=""; 
		form[openxava.decorateId(result.application, result.module, "xava_action_argv")].value="";
		form[openxava.decorateId(result.application, result.module, "xava_changed_property")].value="";
		form[openxava.decorateId(result.application, result.module, "xava_action_range")].value="";		
	}	
	openxava.getElementById(result.application, result.module, "loaded_parts").value=changed;
	openxava.getElementById(result.application, result.module, "loading").value=false;
	openxava.getElementById(result.application, result.module, "view_member").value=result.viewMember; 
	openxava.lastApplication=result.application;
	openxava.lastModule=result.module;
	openxava.hasOnSelectAll(result.application, result.module);
	openxava.showMessages(result); 
	openxava.resetRequesting(result);
	$('#xava_loading').hide(); 
	$('#xava_loading2').hide();
	document.body.style.cursor='auto';
}

openxava.initUI = function(application, module, currentRow) {
	if (openxava.initTheme != null) openxava.initTheme();
	openxava.clearLists(application, module); 
	openxava.initLists(application, module);
	if (typeof currentRow != "undefined") {
		openxava.initEditors(application, module);
	}
	openxava.initSelectedRows();
	openxava.initCurrentRow(application, module, currentRow);	
}

openxava.initStrokeActions = function(application, module) { 
	Module.getStrokeActions(application, module, openxava.setStrokeActions);
}

openxava.setStrokeActions = function(strokeActions) { 
	openxava.strokeActions = strokeActions;
}

openxava.showMessages = function(result) { 
	var messagesIsEmpty = openxava.getElementById(result.application, result.module, "messages_table") == null;
	var errorsIsEmpty = openxava.getElementById(result.application, result.module, "errors_table") == null;
	if (!messagesIsEmpty) openxava.effectShow(result.application, result.module, "messages");
	if (!errorsIsEmpty) openxava.effectShow(result.application, result.module, "errors");
}

openxava.initSelectedRows = function() { 
	$("._XAVA_SELECTED_ROW_").addClass(openxava.selectedRowClass);
}

openxava.initCurrentRow = function(application, module, currentRow) {
	$("._XAVA_CURRENT_ROW_")
		.removeClass("_XAVA_CURRENT_ROW_")
		.removeClass(openxava.currentRowClass)
		.children()
			.removeClass(openxava.currentRowCellClass);
	if (currentRow == null) return;
	var id = openxava.decorateId(application, module, "" + currentRow);		
	$("#" + id).addClass("_XAVA_CURRENT_ROW_").addClass(openxava.currentRowClass).
		children().addClass(openxava.currentRowCellClass);
}

openxava.markRows = function() { 	
	$("._XAVA_CURRENT_ROW_").addClass(openxava.currentRowClass)
		.children().addClass(openxava.currentRowCellClass); 
	openxava.initSelectedRows();
}

openxava.disableElements = function(result) {	
	var rawRootId = openxava.dialogLevel > 0?"dialog" + openxava.dialogLevel:"core"; 
	var rootId = openxava.decorateId(result.application, result.module, rawRootId); 
	var prefixId = openxava.decorateId(result.application, result.module, "");
	$("#" + rootId).find("[id^=" + prefixId + "]").each(
		function () {			
			this.id = this.id + "__DISABLED__";  
		}	
	);
	$("#" + rootId).find("[name^=" + prefixId + "]").each(
		function () {			
			this.name = this.name + "__DISABLED__"; 
		}	
	);		
}

openxava.closeDialog = function(result) { 
	var dialog = openxava.getDialog(result.application, result.module); 
	dialog.attr("application", ""); 
	dialog.attr("module", ""); 
	dialog.dialog('close');
}

openxava.onCloseDialog = function(event) {  
	var dialog = $(event.target); 
	var application = dialog.attr("application");	
	if (application != "") {
		var module = dialog.attr("module");
		openxava.executeAction(application, module, false, false, "Dialog.cancel");
		return;
	}	
	$("[id$=__DISABLED__]", dialog).each(
		function () {
			this.id = this.id.substring(0, this.id.length - 12);
		}	
	);
	$("[name$=__DISABLED__]", dialog).each(
		function () {
			this.name = this.name.substring(0, this.name.length - 12);
		}	
	);
	openxava.clearLists(); 
	dialog.empty();
}


openxava.updateRootIds = function(application, moduleFrom, moduleTo) { 
	openxava.getElementById(
		application, moduleFrom, "loading").id =	
			openxava.decorateId(application, moduleTo, "loading");
	openxava.getElementById(
		application, moduleFrom, "core").id =	
			openxava.decorateId(application, moduleTo, "core");
	openxava.getElementById(
		application, moduleFrom, "loaded_parts").id =	
			openxava.decorateId(application, moduleTo, "loaded_parts");
	openxava.getElementById( 
			application, moduleFrom, "view_member").id =	
				openxava.decorateId(application, moduleTo, "view_member");	
}

openxava.initLists = function(application, module) {  
	$(".xava_resizable").resizable({
		handles: 'e',
		resize: function(event, ui) { 
			var newWidth = $(event.target).width() - 1;
			$(event.target).parent().width(newWidth);
			$("." + event.target.id).width(newWidth);
		},
		stop: function(event, ui) {			
			Tab.setColumnWidth(event.target.id, $(event.target).width());
		}
	});				
	openxava.setListsSize(application, module, "list", 1);	
	openxava.setListsSize(application, module, "collection", 0.95);
}

openxava.setListsSize = function(application, module, type, percentage) {
	var buttonBar = $('#' + openxava.decorateId(application, module, "bottom_buttons"));
	var scrollId = '.' + openxava.decorateId(application, module, type + "_scroll");
	$(scrollId).width(50); 	  
	$(scrollId).width(buttonBar.width() * percentage);  
	$(window).resize(function() {
		$(scrollId).width(50); 
		$(scrollId).width(buttonBar.width() * percentage);
	});		
}

openxava.addEditorInitFunction = function(initFunction) {
	if (openxava.editorsInitFunctionsClosed) return; 
	if (openxava.editorsInitFunctions == null) {
		openxava.editorsInitFunctions = new Array();	
	}
	openxava.editorsInitFunctions.push(initFunction);	
}

openxava.initEditors = function(application, module) { 
	if (openxava.editorsInitFunctions == null) return;	
	for (var i in openxava.editorsInitFunctions) {
		openxava.editorsInitFunctions[i]();
	}
}

openxava.clearLists = function(application, module) { 
	$('.qtip').hide();
}

openxava.getDialog = function(application, module) {  
	if (openxava.dialogs == null) openxava.dialogs = { };
	var dialogId = openxava.decorateId(application, module, "dialog" + openxava.dialogLevel);
	var dialog = openxava.dialogs[dialogId];
	if (dialog == null) {
		$("body").append("<div id='" + openxava.decorateId(application, module , 
				"dialog" + openxava.dialogLevel) + "'></div>");
		dialog = $('#' + dialogId).dialog({
			autoOpen: false,
			modal: true,
			resizable: true,
			width: 'auto',
			height: 'auto',
			bgiframe: true,
			close: openxava.onCloseDialog,
			closeOnEscape: openxava.closeDialogOnEscape 
		});
		openxava.dialogs[dialogId] = dialog;		
	}
	return dialog;
}

openxava.setRequesting = function(application, module) {
	if (openxava.requesting == null) openxava.requesting = { };
	openxava.requesting[application + "::" + module] = true;
}

openxava.isRequesting = function(application, module) {
	if (openxava.requesting == null) return false;
	return openxava.requesting[application + "::" + module];
}

openxava.resetRequesting = function(result) {
	if (openxava.requesting == null) return;
	openxava.requesting[result.application + "::" + result.module] = false;
}

openxava.getElementById = function(application, module, id) {
	return $("#" + openxava.decorateId(application, module, id))[0]; 
}

openxava.getForm = function(application, module) {
	return openxava.getElementById(application, module, "form");
}

// The logic is the same of org.openxava.web.Ids.decorateId 
openxava.decorateId = function(application, module, simpleName) { 	
	return "ox_" + application + "_" + module + "__" + simpleName.replace(/\./g, "___"); 
}

openxava.systemError = function(result) { 
	document.body.style.cursor='auto';	
	openxava.getElementById(result.application, result.module, "core").innerHTML="<big id='xava_system_error'><big style='padding: 5px;font-weight: bold; color: rgb(255, 0, 0);'>ERROR: " + result.error + "</big></big>";
}

openxava.processKey = function(event) {	
	if (!event) event = window.event;
	
	if ( !(event.keyCode >= 112 && event.keyCode <= 123 ||
			event.ctrlKey || event.altKey || event.shiftKey) ) return;
	
	if (event.keyCode >= 49 && event.keyCode <= 57 && event.ctrlKey && !event.altKey) {				
		event.returnValue = false;
		event.preventDefault();
		openxava.executeAction(openxava.lastApplication, openxava.lastModule,
			"", false, "Sections.change", "activeSection=" + (event.keyCode - 49)); 
		return;
	}	
	
	var key = event.keyCode + "," + event.ctrlKey + "," + event.altKey + "," + event.shiftKey;	
	var action = openxava.strokeActions[key];
	if (action != null) {				
		event.returnValue = false;
		event.preventDefault();
		openxava.executeAction(openxava.lastApplication, openxava.lastModule,
			action.confirmMessage, action.takesLong, action.name);  
		return;
	}
		
}

openxava.getSelectedValues = function(application, module) {  	  		
	var result = new Array();	  		
	var selected = document.getElementsByName(openxava.decorateId(application, module, "xava_selected"));  
	var j=0;
	for (var i=0; i<selected.length; i++) {
  		if (selected[i].checked) {
	  		result[j++] = selected[i].value;
  		}
	}	  		
	return result;
}

openxava.getMultipleValues = function(application, module) { 
	var result = new Object();
	var multiple = document.getElementsByName(openxava.decorateId(application, module, "xava_multiple"));  
	for (var i=0; i<multiple.length; i++) {
  		var propertyName = multiple[i].value; 
  		var elements = document.getElementsByName(propertyName);
  		if (elements.length == 1) {
  			result[propertyName] = dwr.util.toDescriptiveString(dwr.util.getValue(propertyName), 2);
  		}
  		else {  	  			
  			for (var j=0; j<elements.length; j++) {
  				var indexedName = elements[j].name + "::" + j;
  				var originalName = elements[j].name;
  				var element = elements[j]; 
  				element.name = indexedName;
  				result[indexedName] = dwr.util.getValue(indexedName);
  				element.name = originalName;  				  				
  			}
  		}
	}	  		
	return result;
}

// JavaScript for text area maxsize
openxava.limitLength = function(ev, max) { 
	var target = window.event ? window.event.srcElement : ev.target;			
	if ( target.value.length > max ) {
		target.value = target.value.substring(0, max);		
	}	
}

// JavaScript for collections and list
openxava.manageFilterRow = function(application, module, id, tabObject) { 
    var img = openxava.getElementById(application, module, "filter_image_" + id);
    var elem = openxava.getElementById(application, module, "tr_list_filter_" + id);
    var link = openxava.getElementById(application, module, "filter_link_" + id);
    var prefix = openxava.getForm(application, module)["xava_image_filter_prefix"].value;
	if (elem.style.display == ''){
		elem.style.display = 'none';
		img.src=prefix + 'show-filter.gif';
		link.title=openxava.showFiltersMessage;		
		Tab.setFilterVisible(application, module, false, tabObject);
	}
	else {
		elem.style.display = '';
		img.src=prefix + 'hide-filter.gif';
		link.title=this.hideFiltersMessage;
		Tab.setFilterVisible(application, module, true, tabObject);
	}    
}

openxava.setPageRowCount = function(application, module, collection, select) {	
	openxava.executeAction(application, module, '', false, "List.setPageRowCount", "rowCount=" + select.value + ",collection=" + collection)
}

openxava.executeAction = function(application, module, confirmMessage, takesLong, action, argv, range, alreadyProcessed) {
	if (confirmMessage != "" && !confirm(confirmMessage)) return;
	if (takesLong) { 
		document.getElementById('xava_processing_layer').style.display='block';
	}
	var form = openxava.getForm(application, module);
	form[openxava.decorateId(application, module, "xava_focus_forward")].value = "false";
	form[openxava.decorateId(application, module, "xava_action")].value=action;	
	form[openxava.decorateId(application, module, "xava_action_argv")].value=argv;	
	form[openxava.decorateId(application, module, "xava_action_range")].value=range;
	form[openxava.decorateId(application, module, "xava_action_already_processed")].value=alreadyProcessed + "_";
	if (openxava.isSubmitNeeded(form)) { 
		form.submit();
	} 
	else {					
		openxava.ajaxRequest(application, module);
	}				
}

openxava.getFormValues = function(ele) { // A refinement of dwr.util.getFormValues
	if (ele != null) {
		if (ele.elements == null) {
			alert("getFormValues() requires an object or reference to a form element.");
			return null;
		}
		var reply = {};
		var name;
		var value;
		for (var i = 0; i < ele.elements.length; i++) {
			if (ele[i].type in {button:0,submit:0,reset:0,image:0,file:0}) continue;
			if (ele[i].name) {
				name = ele[i].name;
				value = openxava.getFormValue(ele[i]);
			}
			else {
				if (ele[i].id) name = ele[i].id;
				else name = "element" + i;
				value = openxava.getFormValue(ele[i]);
			}			
			if (value != null) { 
				reply[name] = value;
			}
		}
		return reply;
	}
}

openxava.getFormValue = function(ele) { // A refinement of dwr.util.getValue
	
	if (dwr.util._isHTMLElement(ele, "select")) {
	    // Using "type" property instead of "multiple" as "type" is an official 
	    // client-side property since JS 1.1
	    if (ele.type == "select-multiple") {
	      return new Array();
	    }
	    else {
	      var sel = ele.selectedIndex;
	      if (sel != -1) {
	        var item = ele.options[sel];
	        var valueAttr = item.getAttributeNode("value");
	        if (valueAttr && valueAttr.specified) {
	          return item.value;
	        }
	        return item.text;
	      }
	      else {
	        return "";
	      }
	    }
	}

	if (dwr.util._isHTMLElement(ele, "input")) {
	    if (ele.type == "radio") {
	      return ele.checked?ele.value:null; 
	    }
	    if (ele.type == "checkbox") {
	      return ele.checked;
	    }
	    return ele.value;
	}

	return $(ele).val(); 
};


openxava.isSubmitNeeded = function(form) {		
	return form.enctype=="multipart/form-data";
}

openxava.onBlur = function(application, module, property) {
	openxava.getElementById(application, module, "xava_previous_focus").value = property;
	openxava.getElementById(application, module, "xava_current_focus").value = "";
}

openxava.onFocus = function(application, module, property) {
	openxava.getElementById(application, module, "xava_previous_focus").value = "";
	openxava.getElementById(application, module, "xava_current_focus").value = property;	
}

openxava.throwPropertyChanged = function(application, module, property) {
	if (openxava.isRequesting(application, module)) return;	
	document.throwPropertyChange = true;
	var form = openxava.getForm(application, module);
	form[openxava.decorateId(application, module, "xava_focus_forward")].value = "true";	
	form[openxava.decorateId(application, module, "xava_previous_focus")].value=property;
	form[openxava.decorateId(application, module, "xava_changed_property")].value=property;
	setTimeout ('openxava.requestOnChange("' + application + '", "' + module + '")', 100);	
}

openxava.requestOnChange = function(application, module) {
	if (document.throwPropertyChange)  {		
		openxava.ajaxRequest(application, module); 
	}			
}

openxava.setFocus = function(application, module) {	
	var form = openxava.getForm(application, module);	
	var elementName = form.elements[openxava.decorateId(application, module, "xava_focus_property_id")].value;
	var elementDecoratedName =  openxava.decorateId(application, module, elementName);
	var element = form.elements[elementDecoratedName];
	
	if (element != null && typeof element.disabled != "undefined" && !element.disabled) {
		if (!$(element).is(':visible')) return; 
		if (element.type != "hidden") {
			element.focus();
		}
		if (typeof element.select != "undefined") {
			element.select();
		}
	}	
}

openxava.clearCondition = function(application, module, prefix) { 
	openxava.clearConditionValues(application, module, prefix);
	openxava.clearConditionComparators(application, module, prefix);
	openxava.clearConditionValuesTo(application, module, prefix);
}

openxava.clearConditionValues = function(application, module, prefix) { 
	var form = openxava.getForm(application, module);
	var elementName = openxava.decorateId(application, module, prefix + "conditionValue.");
	var element = form.elements[elementName + "0"];
	var i=0;
	while (typeof element != "undefined") {
		element.value = '';
		element.placeholder = "";
		element = form.elements[elementName + (++i)];
	}
}

openxava.clearConditionComparators = function(application, module, prefix) {  
	var form = openxava.getForm(application, module);
	var elementName = openxava.decorateId(application, module, prefix + "conditionComparator.");
	var element = form.elements[elementName + "0"];
	var i=0;
	while (typeof element != "undefined") {
		if (element.type == "select-one") { 
			element[0].selected = 'selected';
		}
		element = form.elements[elementName + (++i)];
	}
}

openxava.clearConditionValuesTo = function(application, module, prefix) { 
	var form = openxava.getForm(application, module);
	var elementName = openxava.decorateId(application, module, prefix + "conditionValueTo.");
	var element = form.elements[elementName + "0"];
	var i=0;
	while (typeof element != "undefined") {
		element.value = '';
		element.style.display='none';
		element = form.elements[elementName + (++i)];
	}
}

openxava.onSelectElement = function(application, module, action, argv, checkValue, idRow, hasOnSelectAction, selectedRowStyle, rowStyle, confirmMessage, takesLong, selectingAll) { 	
	var row = $("#" + idRow)[0];
	if (checkValue) {
		$(row).addClass("_XAVA_SELECTED_ROW_").addClass(openxava.selectedRowClass);
		row.style.cssText = rowStyle + selectedRowStyle;
	}
	else {
		$(row).removeClass("_XAVA_SELECTED_ROW_").removeClass(openxava.selectedRowClass);
		row.style.cssText = rowStyle;
	}	
	if (hasOnSelectAction){
		argv = argv + ",selected=" + checkValue;
		openxava.executeAction(application, module, confirmMessage, takesLong, action, argv);	
	}	
	if (!selectingAll) openxava.hasOnSelectAll(application, module);
}

openxava.clearLog = function(message) { 
	$('#xava_console').empty();
}

openxava.log = function(message) { 	
	$('#xava_console').append(message).append("<br/>");
}

openxava.onSelectAll = function(application, module, action, argv, checkValue, hasOnSelectAction, prefix, selectedRowStyle, rowStyle){
	var selected = document.getElementsByName(openxava.decorateId(application, module, "xava_selected"));
	var first = -1;
	var last = 0;
	var alreadyProcessed = "";
	var selectedPrefix = prefix == ""?"selected:":prefix; 
	for (var i=0; i<selected.length; i++) {
		if (selected[i].value.indexOf(selectedPrefix) == 0){ 
			var row = selected[i].value.replace(prefix + "selected:", "");
			if (selected[i].value.search("__SELECTED__") != -1) row = selected[i].value.replace(prefix + "__SELECTED__:", "");	// calculatedCollections
			if (first < 0) first = row;
			if (selected[i].checked==checkValue) alreadyProcessed = alreadyProcessed + "_" + i;
			else{
				var idRow = openxava.decorateId(application, module, prefix) + row;
				openxava.onSelectElement(application, module, action, argv, checkValue, idRow, null, selectedRowStyle, rowStyle, null, null, true);  
				last = row;
			}
			selected[i].checked=checkValue?1:0;
		}
	}
	
	if (first < 0) return;	// no items in the collection
	if (hasOnSelectAction){ 
		argv = argv + ",selected=" + checkValue;
		var range = first + "_" + last;
		openxava.executeAction(application, module, "", "", action, argv, range, alreadyProcessed);
	}
}

openxava.hasOnSelectAll = function(application, module){
	var selectedAll = document.getElementsByName(openxava.decorateId(application, module, "xava_selected_all"));
	for (var i=0; i<selectedAll.length; i++){
		var selected = document.getElementsByName(openxava.decorateId(application, module, "xava_selected"));
		var all = selected.length > 0;
		var e = 0;	// when there are several collections: it checks that the collection has at least one element
		
		var value = selectedAll[i].value.replace("selected_all", ""); 
		var selectedPrefix = value == ""?"selected:":value; 
		for (var j=0; j<selected.length && all; j++){
			if (selected[j].value.indexOf(selectedPrefix) == 0){ 
				e++;
				all = selected[j].checked;
			}
		}
		selectedAll[i].checked=e == 0 ? false : all;
	}
}

openxava.effectShow = function(application, module, id) {
	$("#"+openxava.decorateId(application, module, id)).show("clip", null, "slow", null);
}

openxava.showFrame = function(id) { 
	$("#"+id+"content").slideDown(); 
	$("#"+id+"header").children().fadeOut(2000); 
	$("#"+id+"hide").show();
	$("#"+id+"show").hide();
	View.setFrameClosed(id, false);
}

openxava.hideFrame = function(id) { 
	$("#"+id+"content").slideUp(); 
	$("#"+id+"header").children().fadeIn(2000); 
	$("#"+id+"hide").hide();
	$("#"+id+"show").show();
	View.setFrameClosed(id, true);
}

openxava.onChangeComparator = function(id,idConditionValue,idConditionValueTo,labelFrom) {
	var comparator = openxava.getFormValue(document.getElementById(id));
	if ("range_comparator" == comparator){
		document.getElementById(idConditionValueTo).style.display='inline';
		document.getElementById(idConditionValue).placeholder = labelFrom;
	}
	else{
		document.getElementById(idConditionValueTo).style.display='none';
		document.getElementById(idConditionValue).placeholder = "";
	}
}