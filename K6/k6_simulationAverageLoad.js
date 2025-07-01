import { default as scenarioDashboard } from './k6_scenarioDashboard.js';
import { default as scenarioHomepage } from './k6_scenarioHomepage.js';

export const options = {
  thresholds: {
    http_req_duration: [`p(99)<500`],
    checks: ['rate>0.80'],
  },
  scenarios: {
	scenarioHomepage: {
      exec: 'execScenarioHomepage',  // Refers to the method to be executed
      executor: 'ramping-vus',
      stages: [
          { duration: '5s', target: 5 }
        , { duration: '5s', target: 10 }
      ],
    }
	, scenarioDashboard: {
	  exec: 'execScenarioDashboard',  // Refers to the method to be executed
      executor: 'constant-arrival-rate',
      duration: '10s', 		// How long the test lasts
      rate: 100,			// How many iterations per timeUnit
      timeUnit: '1m', 		// Start `rate` iterations per second
      preAllocatedVUs: 2,	// Pre-allocate 2 VUs before starting the test
      maxVUs: 10,			// Spin up a maximum of 10 VUs to sustain the defined rate
    }
	/*scriptAuthenticatedScenario: {
      exec: 'execScenarioDashboard',  // Refers to the method to be executed
      executor: 'constant-vus',
      vus: 10,
      duration: '10s',
    }*/

  }
};

export function execScenarioDashboard() {
  scenarioDashboard(); // imported from other file
}
export function execScenarioHomepage() {
  scenarioHomepage(); // imported from other file
}