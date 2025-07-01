import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend } from 'k6/metrics';

var UC = 'UC10_Homepage';

//#####################################################
// Default
//#####################################################
export default function() {
	group(UC, function () {
		
		simpleResponseCheck();
		loadHomepage();
		
		group(UC + '_Custom', function () {
			customMetric();
		});
	});
}


//#####################################################
// 
//#####################################################
function simpleResponseCheck() {
    let res = http.get('https://test.k6.io', { tags: { name: UC + '_000_Login' } } );
    check(res, {
        'status is 200': (r) => r.status === 200,
    });
    sleep(1);
}

//#####################################################
// 
//#####################################################
function loadHomepage() {
    let res = http.get('https://test.k6.io', { tags: { name: UC + '_010_LoadHompage' } } );
    sleep(1);
}

//#####################################################
// Default
//#####################################################
var myCustomMetric = new Trend(UC + '_myCustomMetric');
var myCustomTime = new Trend(UC + '_myCustomTime');
function customMetric() {

	let start = Date.now();
		let res = http.get('https://test.k6.io', { tags: { name: UC + '_999_Logout' } } );
	let end = Date.now();
   
	myCustomMetric.add(res.timings.duration);
	myCustomTime.add(end-start);
	
    sleep(1);
}
