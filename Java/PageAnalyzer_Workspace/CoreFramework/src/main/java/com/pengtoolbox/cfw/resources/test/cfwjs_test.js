
/********************************************************************
 * CFW.JS Tests
 * ============
 * Test javascript for testing.
 *
 *@author Reto Scheiwiller, 2019
 ********************************************************************/

/********************************************************************
 * Executes the tests cases.
 * 
 ********************************************************************/
function cfw_test_createTable(parent){
	
	//---------------------------------
	// Create Table
	var cfwTable = CFW.ui.createTable();
	cfwTable.addHeaders(["FirstCol","2ndCol","III Column"]);
	cfwTable.addHeader("AnotherCol");
	
	cfwTable.addRow('<tr>'
			+'<td>A</td>'
			+'<td>B</td>'
			+'<td>C</td>'
			+'<td>D</td>'
			+'</tr>');
	
	cfwTable.addRows('<tr>'
			+'<td>1</td>'
			+'<td>2</td>'
			+'<td>3</td>'
			+'<td>4</td>'
			+'</tr>'
			+'<tr>'
			+'<td>E</td>'
			+'<td>F</td>'
			+'<td>G</td>'
			+'<td>H</td>'
			+'</tr>');
	
	//---------------------------------
	// Add to Page
	var resultDiv = $('<div id="createTableTest">');
	resultDiv.html("<h1> Test CFW.ui.createTable()</h1>");
	cfwTable.appendTo(resultDiv);
	parent.append(resultDiv);
	
	//---------------------------------
	// Verify

	if((thCount = $('#createTableTest th').length) != 4){
		CFW.ui.addAlert("error", "Table header count expected 4 but was "+thCount);
	}
	
	if((tdCount = $('#createTableTest td').length) != 12){
		CFW.ui.addAlert("error", "Table cell count expected 12 but was "+tdCount);
	}
	
}

/********************************************************************
 * Executes the tests cases.
 * 
 ********************************************************************/
function run(){
	
	CFW.ui.addAlert("info", "================ TEST ISSUES BELOW THIS LINE =================");
	parent = $("#cfw-content");
	
	cfw_test_createTable(parent);
	
}

run();