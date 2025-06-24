import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend } from 'k6/metrics';

//#####################################################
// Options
//#####################################################
export let options = {
    stages: [
        { duration: '10s', target: 20 }, // Ramp-up to 20 VUs
        { duration: '10s', target: 20 },  // Stay at 20 VUs for 1 minute
        { duration: '10s', target: 0 },  // Ramp-down to 0 VUs
    ],
	thresholds: {
        'http_req_duration': ['p(95)<500'], // 95% of requests must complete below 500ms
        'myCustomMetric': ['avg<200'], // Custom threshold for the custom metric
    },
};

//#####################################################
// Default
//#####################################################
export default function () {
	group('MyTestCase', function () {
			simpleResponseCheck();
			customMetric();
		}
	);
}


//#####################################################
// Default
//#####################################################
function simpleResponseCheck() {
    let res = http.get('https://test.k6.io');
    check(res, {
        'status is 200': (r) => r.status === 200,
    });
    sleep(1);
}

//#####################################################
// Default
//#####################################################
let myTrend = new Trend('myCustomMetric');
function customMetric() {
    
	
	let res = http.get('https://test.k6.io');
	
    myTrend.add(res.timings.duration);
	
    sleep(1);
}