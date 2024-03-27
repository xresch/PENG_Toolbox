import http from 'k6/http';
import { sleep, check, fail, regexp2 } from 'k6';

/************************************************************
* K6 Options
*************************************************************/
//export const options = {
//  vus: 1,
  //duration: '30s',
//};

/************************************************************
* Exense STEP Schema
*************************************************************/
/*
{
  "required": [
    "url"
  ],
  "properties": {
    "url": {
      "type": "string"
    },
    "trace": {
      "type": "boolean",
      "default": true
    },
    "contains": {
      "type": "string"
    },
    "cookieKey": {
      "type": "string"
    },
    "headerKey": {
      "type": "string"
    },
    "authMethod": {
      "enum": [
        "basic",
        "ntlm",
        "digest"
      ]
    },
    "httpStatus": {
      "type": "number"
    },
    "containsNot": {
      "type": "string"
    },
    "regexMatches": {
      "type": "string"
    },
    "headerContains": {
      "type": "string"
    },
    "authParamNamePW": {
      "type": "string"
    },
    "regexMatchesNot": {
      "type": "string"
    },
    "authParamNameUser": {
      "type": "string"
    }
  }
}
*/

/************************************************************
* Step Inputs
*************************************************************/
var TRACE 				= getStepInput("trace", false);
var URL 				= getStepInput("url", null);			// the URL that should be monitored
var HTTP_STATUS 		= getStepInput("httpStatus", null);		// the expected http status
var CONTAINS 			= getStepInput("contains", null);		// string to verify if it is contained in the reponse body
var CONTAINS_NOT 		= getStepInput("containsNot", null);	// string to verify if it is not contained in the response body
var REGEX_MATCHES 		= getStepInput("regexMatches", null);	// regex to verify if it matches the reponse body
var REGEX_MATCHES_NOT	= getStepInput("regexMatchesNot", null);// regex to verify if it does not match the reponse body
var HEADER_KEY			= getStepInput("headerKey", null);		// a name of a header which should be present
var HEADER_CONTAINS 	= getStepInput("headerContains", null);	// string to verify if the header specified by "headerKey" contains the string in it's value
var COOKIE_KEY			= getStepInput("cookieKey", null);		// a name of a cookie which should be present
var AUTH_METHOD			= getStepInput("authMethod", null);		// the type of authentication that should be used basic / digest / ntlm
var AUTH_PARAMNAME_USER = getStepInput("authParamNameUser", null);	// the name of the step parameter that contains the username
var AUTH_PARAMNAME_PW	= getStepInput("authParamNamePW", null);	// the name of the step parameter that contains the password

/************************************************************
* Step Parameters
*************************************************************/
var AUTH_USER			= getStepParameter(AUTH_PARAMNAME_USER, null); // the user for authentication
var AUTH_PASSWORD		= getStepParameter(AUTH_PARAMNAME_PW, null); // the password for authentication


/************************************************************
* testdata to check general functions
*************************************************************/
function testdata(){
	TRACE 			= true;
	URL 			= 'https://test.acme.com/acm/admin/healthcheck';
	HTTP_STATUS 	= 200;
	CONTAINS 		= "healthy";
	CONTAINS_NOT 	= '"healthy":false';
	HEADER_KEY 		= 'Content-Type';
	HEADER_CONTAINS = 'application/json';
	REGEX_MATCHES 	= "healthy.*tenant";
}

/************************************************************
* testdata to check regex
*************************************************************/
function testdata2(){
	TRACE 				= true;
	URL 				= 'https://emp.acme.com/metrics';
	HTTP_STATUS 		= 200;
	CONTAINS 			= "jetty_requests_total";
	CONTAINS_NOT 		= null;
	HEADER_KEY 			= 'Content-Type';
	HEADER_CONTAINS 	= 'text/plain';
	REGEX_MATCHES 		= "jetty_requests_total.*jetty_dispatched_total"
	REGEX_MATCHES_NOT 	= ".*jboss_requests_total.*"
	COOKIE_KEY			= "CFWSESSIONID";
}

/************************************************************
* testdata to check regex
*************************************************************/
function testdata22(){
	TRACE 				= true;
	URL 				= 'https://test.acme.com/tblogin/';
	HTTP_STATUS 		= 200;
	//REGEX_MATCHES 		= '<td>Database connections available<\/td>\s*?<td class="true">true'
	REGEX_MATCHES 		= '<td>Database connections available<\\/td>\\s*?<td class="true">true'
	//REGEX_MATCHES_NOT 	= '<td>Database connections available<\/td>\s*?<td class="false">false'
}

/************************************************************
* testdata to check login
*************************************************************/
function testdata3(){
	TRACE 				= true;
	URL 				= 'https://intranet.acme.com';
	HTTP_STATUS 		= 200;
	CONTAINS 			= "Intranet - Home";
	CONTAINS_NOT 		= null;
	REGEX_MATCHES 		= null;
	REGEX_MATCHES_NOT	= null;
	AUTH_METHOD			= 'ntlm';
	AUTH_USER			= 'acmeUser';
	AUTH_PASSWORD		= 'xxx';
}

/************************************************************
* testdata to check follow redirects
*************************************************************/
function testdata4(){
	TRACE 				= true;
	URL 				= 'https://emp.acme.com/app/dashboard/list';
	HTTP_STATUS 		= 200;
	CONTAINS 			= "Sign In";
}


/************************************************************
* K6 Main Function
*************************************************************/
export default function () {
  
  var success = true;
  //testdata22();
  
  initialTrace();

  //-----------------------------------
  // Call URL
  //-----------------------------------
  if(URL != null){
	  
	var response;
	if(AUTH_METHOD == null){
		response = http.get(URL);
	}else{
		response = callURLWithAuth(URL);
	}

	printResponse(response);
	
  }else{
	  fail('The URL cannot be null');
  }
  
  //-----------------------------------
  // Check HTTP Status
  //-----------------------------------
  if(HTTP_STATUS != null){
	  success = check(response, {
		'is status 200': (r) => r.status == HTTP_STATUS,
	  });
	  
	  if(!ess){
		  fail("The http succstatus was not '"+HTTP_STATUS+"' but was "+response.status);
	  }
  }
  
  //-----------------------------------
  // Check Body Contains string
  //-----------------------------------
  if(CONTAINS != null){
	  success = check(response, {
		'verify response contains': (r) =>
		  r.body.includes(CONTAINS),
	  });
	  	  
	  if(!success){
		  fail("The http response body did not contain the expected text: '"+CONTAINS+"'");
	  }
  }
  
  //-----------------------------------
  // Check Body not contains string
   //-----------------------------------
  if(CONTAINS_NOT != null){
	  success = check(response, {
		'verify response does not contain': (r) =>
		  !r.body.includes(CONTAINS_NOT),
	  });
	  
	  if(!success){
		  fail("The http response body did contain a text it should not have contained: '"+CONTAINS_NOT+"'");
	  }
  }
  
  //-----------------------------------
  // Check Body Matches Regex
  //-----------------------------------
  
  // workaround because flag "s" to make  dot match newlines is not supported
  var bodyNoNewlines = 
			response.body
					.replace(/(?:\r\n|\r|\n)/g, ' ');
					;

  if(REGEX_MATCHES != null){
	  
	  var regexObject = new RegExp(REGEX_MATCHES, "gi"); 

	  success = check(response, {
		'verify response matches regex': (r) =>
			regexObject.test(bodyNoNewlines),
	  });
	  
	  if(!success){
		  fail("The http response body did not match the regex: '"+REGEX_MATCHES+"'");
	  }
  }
  
  //-----------------------------------
  // Check Body Not Matches Regex
  //-----------------------------------
  if(REGEX_MATCHES_NOT != null){
	  
	  var regexObject2 = new RegExp(REGEX_MATCHES_NOT, "gi"); 

	  success = check(response, {
		'verify response not matches regex': (r) =>
			!regexObject2.test(bodyNoNewlines),
	  });
	  
	  if(!success){
		  fail("The http response body did match the regex but it shouldn't: '"+REGEX_MATCHES_NOT+"'");
	  }
  }
  
  
  
  //-----------------------------------
  // Check Response contains header
  //-----------------------------------
  if(HEADER_KEY != null){
	  success = check(response, {
		'verify response has header': (r) =>
		  (r.headers[HEADER_KEY] != undefined),
	  });
	  
	  if(!success){
		  fail("The http response did not contain the header: '"+HEADER_KEY+"'");
	  }
  }
  
  //-----------------------------------
  // Check Heade value contains
  //-----------------------------------
  if(HEADER_CONTAINS != null){
	  success = check(response, {
		'verify response header contains string': (r) =>
			r.headers[HEADER_KEY].includes(HEADER_CONTAINS),
	  });
	  
	  if(!success){
		  fail("The value of the http response header '"+HEADER_KEY+"' did not contain the string: '"+HEADER_CONTAINS+"'");
	  }
  }
  
    //-----------------------------------
  // Check Response not contains header
  //-----------------------------------
  if(COOKIE_KEY != null){
	  success = check(response, {
		'verify response has cookie': (r) =>
		  (r.cookies[COOKIE_KEY] != undefined),
	  });
	  
	  if(!success){
		  fail("The http response did not contain the cookie: '"+COOKIE_KEY+"'");
	  }
  }
}

/************************************************************
* Function to retrive inputs from Step.
* @returns value for inputName if defined, defaultValue if null or empty string
*************************************************************/
function getStepParameter(paramName, defaultValue){
	
	if(paramName == null){ return defaultValue; }
	
	return getStepInput("PROPERTIES_"+paramName, defaultValue);

}
	
/************************************************************
* Function to retrive inputs from Step.
* @returns value for inputName if defined, defaultValue if null or empty string
*************************************************************/
function getStepInput(inputName, defaultValue){

	if(inputName == null){ return defaultValue; }
	
	if( __ENV[inputName] !== undefined ){
		
		var value = decodeURIComponent(__ENV[inputName]);
		
		if (typeof value === "string" 
		&& value.length === 0){
			return defaultValue;
		}
		
		return value;
	}
	
	return defaultValue;
}

/************************************************************
* Calls a URL with credentials, returns the response.
*************************************************************/
function callURLWithAuth(urlToCall){
	
	var credentials = `${AUTH_USER}:${AUTH_PASSWORD}@`;
	var credentializedURL = "";
	
	//----------------------------------
	// Create Credentials URL
	if(urlToCall.startsWith("https://")){
		credentializedURL = urlToCall.replace('https://', 'https://'+credentials);
	}else if(urlToCall.startsWith("http://")){
		credentializedURL = urlToCall.replace('http://', 'http://'+credentials);
	}else{
		credentializedURL = 'http://'+credentials+urlToCall;
	}
		
	//----------------------------------
	// Make Options

	var options = {};
	
	switch(AUTH_METHOD.trim().toLowerCase()){
		case 'ntlm': 	options = { auth: 'ntlm' }; 	break;
		case 'digest': 	options = { auth: 'digest' }; 	break;	
		
		case 'basic': 
		default:
		break;
	}
	
	//----------------------------------
	// Call URL
	console.log('credentializedURL:'+credentializedURL);
	
	return http.get(credentializedURL, options);
}

/************************************************************
* 
*************************************************************/
function initialTrace(){
	
	if(!TRACE){	return; }
	
	console.log("##################################################");
	console.log("SCRIPT PARAMATERS");
	console.log("##################################################");
	console.log("url: "+URL);
	console.log("httpstatus: "+HTTP_STATUS);
	console.log("contains: "+CONTAINS);
	console.log("containsNot: "+CONTAINS_NOT);
	console.log("regexMatches: "+REGEX_MATCHES);
	console.log("regexMatchesNot: "+REGEX_MATCHES_NOT);
	console.log("headerKey: "+HEADER_KEY);
	console.log("headerContains: "+HEADER_CONTAINS);
	console.log("cookieKey: "+COOKIE_KEY);
	
	
	console.log("##################################################");
	console.log("__ENV ");
	console.log("##################################################");
	for(var i in __ENV){
		console.log(i+": "+__ENV[i]);
	}

}

/************************************************************
* 
*************************************************************/
function printResponse(response){
	
	if(TRACE){
		console.log("========= HTTP RESPONSE BODY - START ======");
		console.log( JSON.stringify(response, null, 2) );
		console.log("========= HTTP RESPONSE BODY - END ======");
	}
	
}