
Build WAR File
==============
run maven with goals "clean package"

Webserver Config
================
The following configs can be used to configure the PageAnalyzer:

	# The host of the YSlow nodeJS server (Default: 'localhost')
	-Dpageanalyzer.nodejs.hostname=127.0.0.1
	 
	# The port of the YSlow nodeJS server (Default: '9999') 
	-Dpageanalyzer.nodejs.port=9999
	
	# The support details as semicolon separated string included on the docu page (Default: null)
	-Dpageanalyzer.support.details="Phone: +4100 000 00 00;Email: foo@bar.com"

Or for example in a web server config file:

<server>
	...
    <system-properties>
        <property name="pageanalyzer.nodejs.hostname" value="127.0.0.1"/>
        <property name="pageanalyzer.nodejs.port" value="9999"/>
        <property name="pageanalyzer.support.details" value="Phone: +4100 000 00 00;Email: foo@bar.com" />
    </system-properties>
    ...
    
Customized YSlow
================
- The file "./custom_yslow/yslow_with_ybear.js" is a customized yslow.js.
- To deploy the file it has to be copied to NodeJS in the following location: "<YSLOW_ROOT>\node_modules\yslow\lib\yslow.js

