
/**************************************************************************************
 * Global VARS
 *************************************************************************************/

var injected = {};

/**************************************************************************************
 * Set Cookie
 *************************************************************************************/
injected.setCookie = function(cname, cvalue, exdays) {
     var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
} 

/**************************************************************************************
 * Get Cookie
 *************************************************************************************/
injected.getCookie = function (cname) {
     var name = cname + "=";
     var decodedCookie = decodeURIComponent(document.cookie);
     var ca = decodedCookie.split(';');
     for(var i = 0; i <ca.length; i++) {
         var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
         if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
     }
     return "";
 } 
 
 /**************************************************************************************
 * 
 *************************************************************************************/
injected.renderInfoView = function(){
	
	$("#pa-content").html(
	    '<h3>Injection Information</h3>'
	   +'<p class="injected-css">This content was created by injected javascript, and the blue color comes from injected css styles.</p>'
	);
}


/**************************************************************************************
 * 
 *************************************************************************************/
injected.addAgentMenu = function(){

	var menuHTML = 
	 '<li class="dropdown">'
	+	'<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">'
	+		'Injected Menu<span class="caret"></span></a>'
	+	'<ul class="dropdown-menu">'
	+		'<li><a href="#" onclick="injected.draw({view: \'info\'})">Info</a></li>'
	+	'</ul>'
	+'</li>';
	
	$('.navbar-nav').append(menuHTML);
	
}

/**************************************************************************************
 * 
 *************************************************************************************/
injected.draw = function(args){
	
	switch(args.view){
		case 'info':
			injected.renderInfoView();
			break;
			
		default: break;
	}

}
/**************************************************************************************
 * Main Entry Method
 *************************************************************************************/
injected.main = function(){
	
	injected.addAgentMenu();

}

window.onload = injected.main;
