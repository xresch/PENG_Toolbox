# Page Analyzer

Description
-----------
Page Analyzer is a web application used to analyze HTTP Archive Files(HAR) and store the results in a database. The project was created for performance engineering purposes, to have an easy way to track common performance issues of web applications over time. 
Therefore it also features a compare functionality and several views to analyze the results and the raw data of the HTTP Archive.

The analysis is based on a heavily improved version of the decommisioned YSlow Project, originally created by Yahoo. This provides the whole rule engine that was extended with several new rules.

Features
--------

- Upload .har-Files for analysis
- Analyze URL (Experimental)
- **Result View:** Analyze the results.
  - Summary
  - Rule Panels
  - Table View
  - Plain Text View
  - JIRA Format View
  - Statistics View
  - Gantt Chart View
  - Export CSV / JSON
- **History View:** List of all results created by the user.
  - Open Result or Gantt Chart
  - Open Application Link
  - Compare Results
  - Delete Results
  - Download result or HAR as .json-File
- **Gantt Chart View:** Gantt Chart based on the HAR file.
  - Analyze timings in a Gantt Chart
  - Browser Details for each Request
  - Analyze Cookies / Headers
- **Compare View:** Creates a table, each column contains one of the results selected on the History View.

