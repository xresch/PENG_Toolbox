# L-P-Toolbox
A collection of tools useful for load and performance testing

# License Note
The repositories MIT license applies only to the tools and components in the 
PENG_Toolbox, which do not have a license specified.

For all tools which specify their own license, please stick to the license 
terms of the tool.

# Overview 
An overview of the tools and a short description what they do and how to start.
* Bash Scripts
  * **AwkStatistics:** Creates statistical values from CSV formatted input. Check the docu in the script-File for examples.
  * **MetricCollector:** Collects various metrics from GNU/Linux machines and processes and creates graphs. Check the docu in the script-File for examples.
* Excel
  * **SilkPerformer_LoadManagement.xlsx:** Helps you creating load and analyze tests results. Chck the Docu-Sheet in the file for instructions.
  * **SilkPerformer_ResultAnalysis.xlsx:** Sheet for result analysis, check the Docu-Sheet for instructions.
  * **Timetracking.xlsx:** Sheet for tracking all your performance engineering activities. ;-)
* Java
  * **Java Agents:** Some examples of Java agents and how to write them.
  * **Page Analyzer:** A small jetty self-contained web application which analyzes .har-Files and shows the results.
    * **Quickstart:** 
	  * Get the distribution under \Java\PageAnalyzer_Workspace\PageAnalyzer\dist
	  * set config in ./config/pageanalyzer.properties
	  * execute start.bat(you might have to add a java path)
* Silk Performer
  * Scripts
    * **custom.bdh:** Some useful functions for Silk Performer scripting. 
    * Tools
	  * **Result Extractor:** Windows application which extracts values from Silk Performer .ovr-Files and converts them to CSV format.
	    * **Quickstart:** Get the .exe-File from SilkPerformer\Tools\ResultExtractor\XMLParser\bin\Release
      * **Silk Skript Styler:** Windows application which helps creating script structures based on //-comments.
	    * **Quickstart:** Get the .exe-File from \SilkPerformer\Tools\SilkScriptStyler\SilkScriptStyler\bin\Release
      * **True Log Reporter:** Windows application which creates reports from true log .xlg-Files.
	    * **Quickstart:** Get the .exe-File from SilkPerformer\Tools\ResultExtractor\XMLParser\bin\Release\SilkPerformer\Tools\TrueLogReporter\TrueLogReporter\bin\Release
		
