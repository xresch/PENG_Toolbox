REM toggle k6 built-in dashboard
REM Note: Not that useful, will be inaccessible as soon as the test execution is over
set K6_WEB_DASHBOARD=false

REM open dashboard in default browser
REM start http://localhost:5665

REM execute test
k6 run --summary-mode=full --out csv=k6_script_results.csv k6_script.js

REM pause to not close execution window
pause
