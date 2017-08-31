#!/bin/bash
###########################################################################################
# Author: Reto Scheiwiller, 2016
#
# Metric Collector
# -----------------
# Collects server and process information from various tools and creates charts with the 
# data. The script was tested on GNU/Linux with gnuplot 4.4.
# For the script to work you have to set the variable "JAVA_HOME" to a valid location.
# All arguments are optional, if "-p" is not used no collections from a process will be done.
# 
# The following information gets collected:
#   >> output from vmstat
#   >> netstat tcp states and established connections
#   >> information from /proc/fd
#   >> jstat -gc output
#   >> jstat -class output
#   >> CPU%, MEM% and ThreadCount from top and ps-command for specific ProcessID
#   >> disk usage for each mounted device from df-command
#
# Syntax:   metricCollector.sh [-c <numberCollections> -s <sleepTime> -f <Filter> -p <processID>] [ -o -g <directory>] 
# 
# Examples:  >> metricCollector.sh -c 480 -s 60 -f "ws-backplane" -p 11111
#               480 collections each 1 minute (ca. 8 hours) for process 11111 and netstat output
#               filtered by "ws-backplane". Create graphs after all collections are done.
#
#            >> metricCollector.sh -c 480 -s 60 -o
#               Only collect but do not create graphs.
#
#            >> metricCollector.sh -d 3600
#               Delay 3600 seconds before the collection starts with default values.
#
#            >> metricCollector.sh -g "./MetricCollector_1970-01-01T12.59.59"
#               Only create graphs for the given result directory.
#
# Options:  -c <numberCollections> 
#              Number of collections that should be taken (Default: 1000000)
#
#           -s <sleepTime>
#              Sleep time between two collections in seconds (Default: 30)
#
#           -d Delay before the collection starts in seconds (Default: 0) 
#
#           -f <Filter>
#              Filter for grepping the netstat output for specific lines (Default: "")
#
#           -p <processID>
#              ProcessID to collect information from (Optional)
#
#           -o 
#              only collect data, do not create graphs(usefull if gnuplot is not available)
#
#           -g <directory> 
#              do not collect data, only create the graphs for the given result directory
#
#           -u 
#              enables "Unstoppable-Mode", will collect even if the ProcessID cannot be found
#
#           -h 
#              Prints this documentation.
#
# Known and potential issues:
#  >> Older versions of gnuplot (<4.4) might not support the -e option or will fail with 
#     "Cannot open load file '-e'".
#  >> VMStat gets not collected or not at each collection for an unknown reason.
#  >> NetStat TCP states were not tested thoroughly, the states were taken from a website,
#     with the assumption they will match the strings in the output of netstat.
#  >> The collection itself will create around 2% User CPU% and 3% Software Interrupts CPU%.
#     This will reflect in the collected data as well.
#
# Change Log
# ----------
# >> 2016-11-08: 
#     - When script is executed without arguments it will print the manual.
#     - Script will not execute collection for ProcessID when the process is not present,
#       print an error and exit. Added the -u option that will not stop the script.
#     - Changed default value for -c <numberCollections> from 480 to 1000000, to make 
#       sure it will run quite long when it is not set manually.
# 
# >> 2016-10-21: 
#     - Added the option -d to delay the script
# 
###########################################################################################

########################################
# Set Globals
########################################

JAVA_HOME="/bin/java/jdk160_105-64b"

#------------------------------
# Defaults for Arguments
NUMBER_COLLECTIONS=1000000
SLEEP_SECS=60
STARTDELAY_SECS=0
FILTER=""
PROCESSID="NOTSET"
FLAG_ONLY_COLLECT="FALSE"
FLAG_UNSTOPPABLE="FALSE"

#------------------------------
# Files and Folders
TIMESTAMP=$(date +"%Y-%m-%dT%H.%M.%S")

RESULT_FOLDER="MetricCollector_${TIMESTAMP}"
FILE_TCPSTATES="${RESULT_FOLDER}/data_netstat_TCPStates.csv"
FILE_TCPESTABLISHED="${RESULT_FOLDER}/data_netstat_TCPestablished.dat"
FILE_VMSTAT="${RESULT_FOLDER}/data_vmstat.csv"
FILE_PROC_FILEDESCRIPTORS="${RESULT_FOLDER}/data_proc_filedescriptors.csv"
FILE_JSTAT_GC="${RESULT_FOLDER}/data_jstat_gc.csv"
FILE_JSTAT_CLASS="${RESULT_FOLDER}/data_jstat_classloading.csv"
FILE_PS_TOP="${RESULT_FOLDER}/data_process_cpu_mem_threads.csv"
FILE_DF_USAGE="${RESULT_FOLDER}/data_disk_usage.csv"
FILE_MACHINE_CPU="${RESULT_FOLDER}/data_machine_cpu_usage.csv"

#------------------------------
# Other variables
PROCESSOR_COUNT=$(cat /proc/cpuinfo | grep "^processor" | wc -l)

############################################################
# Print documentation written in the bash file.
############################################################
print_docu(){

	SOURCE_PATH="${BASH_SOURCE[0]}"

	cat "${SOURCE_PATH}" | gawk ' 
		BEGIN{ 
			isLineInDocumentHead="before";
			print " ";
		}
		
		{
			if ( match($0,"^#############") ){
				
				if ( isLineInDocumentHead == "before" ){
					isLineInDocumentHead="between";
					next;
				}else {
					if ( isLineInDocumentHead == "between"){
						isLineInDocumentHead="end";
					}
				}
				
			}
			
			if ( isLineInDocumentHead == "between"){
				sub("^#","", $0)
				print ;
			}
		}
	'
	
	exit 1
}

########################################
# Function: Collect NetStat TCP States
########################################
function collect_tcpstates(){

	COUNT_SYN_SEND=$(netstat | grep -c "${FILTER}.*SYN_SEND")
	COUNT_SYN_RECEIVED=$(netstat | grep -c "${FILTER}.*SYN_RECEIVED")
	COUNT_ESTABLISHED=$(netstat | grep -c "${FILTER}.*ESTABLISHED")
	COUNT_LISTEN=$(netstat | grep -c "${FILTER}.*LISTEN")
	COUNT_FIN_WAIT_1=$(netstat | grep -c "${FILTER}.*FIN_WAIT_1")
	COUNT_CLOSE_WAIT=$(netstat | grep -c "${FILTER}.*CLOSE_WAIT")
	COUNT_FIN_WAIT_2=$(netstat | grep -c "${FILTER}.*FIN_WAIT_2")
	COUNT_LAST_ACK=$(netstat | grep -c "${FILTER}.*LAST_ACK")
	COUNT_TIMED_WAIT=$(netstat | grep -c "${FILTER}.*TIMED_WAIT")
	COUNT_CLOSING=$(netstat | grep -c "${FILTER}.*CLOSING")
	COUNT_CLOSED=$(netstat | grep -c "${FILTER}.*CLOSED")

	echo "${TIMESTAMP};${COUNT_SYN_SEND};${COUNT_SYN_RECEIVED};${COUNT_ESTABLISHED};${COUNT_LISTEN};${COUNT_FIN_WAIT_1};${COUNT_CLOSE_WAIT};${COUNT_FIN_WAIT_2};${COUNT_LAST_ACK};${COUNT_TIMED_WAIT};${COUNT_CLOSING};${COUNT_CLOSED}" >> ${FILE_TCPSTATES}
		
}

########################################
# Function: Collect NetStat Established
########################################
function collect_tcpestablished(){

	echo "================================= ${TIMESTAMP} =================================" >> ${FILE_TCPESTABLISHED}
	netstat -a | grep "${FILTER}.*ESTABLISHED" >> ${FILE_TCPESTABLISHED}
	
}

########################################
# Function: Collect VMStat
########################################
function collect_vmstat(){

	vmstat -a -SM | egrep "^ *[0-9]" | sed -r -e 's/[ ]+/;/g' -e 's/^;//' | gawk "{print \"${TIMESTAMP};\" \$0}" >> ${FILE_VMSTAT}

}

########################################
# Function: Collect jstat -gc
########################################
function collect_jstat_gc(){

	${JAVA_HOME}/bin/jstat -gc ${PROCESSID} 1 1 | egrep "^ *[0-9]" | sed -r -e 's/[ ]+/;/g' -e 's/^;//'  | gawk "{print \"${TIMESTAMP};\" \$0}" >> ${FILE_JSTAT_GC}

}

########################################
# Function: Collect jstat -class
########################################
function collect_jstat_class(){

	${JAVA_HOME}/bin/jstat -class ${PROCESSID} 1 1 | egrep "^ *[0-9]" | sed -r -e 's/[ ]+/;/g' -e 's/^;//'  | gawk "{print \"${TIMESTAMP};\" \$0}" >> ${FILE_JSTAT_CLASS}

}


########################################
# Function: Collect /proc/<pid>/fd
########################################
function collect_proc_fd(){

	COUNT_TOTAL=$(ls -la /proc/${PROCESSID}/fd | wc -l) 
	COUNT_SOCKETS=$(ls -la /proc/${PROCESSID}/fd | egrep "socket:\[" | wc -l) 
	COUNT_JARS=$(ls -la /proc/${PROCESSID}/fd | egrep ".jar$" | wc -l) 
	COUNT_OTHER=$(ls -la /proc/${PROCESSID}/fd | egrep -v "socket:\[|.jar$"  | wc -l) 
	echo "${TIMESTAMP};${COUNT_TOTAL};${COUNT_SOCKETS};${COUNT_JARS};${COUNT_OTHER}" >> ${FILE_PROC_FILEDESCRIPTORS}
	
}

########################################
# Function: Collect ps and top
########################################
function collect_ps_top(){

	PROCESS_THREAD_COUNT=$( ps huH -p ${PROCESSID} | wc -l) 

	# $9 >> CPU Usage % on processor basis
	# $10 Memory Usage %
	# $9 / ${PROCESSOR_COUNT}  >> CPU Usage based on 100%
	top -bn2 -p ${PROCESSID} | egrep -v "^\s*$" | tail -1 | gawk "{print \"${TIMESTAMP};\" \$9 \";\" \$9 / ${PROCESSOR_COUNT} \";\" \$10 \";${PROCESS_THREAD_COUNT}\"}" >> ${FILE_PS_TOP}
	
}

########################################
# Function: Collect machine CPU Usage
########################################
function collect_machine_cpu_usage(){
	
	# 100 - $4 >> Total CPU Usage (100 - Idle CPU) 
	# $1 >> User CPU%
	# $2 >> System CPU%
	# $3 >> Nice CPU% (time spent on low priority processes)
	# $4 >> Idle CPU%
	# $5 >> IO/Wait CPU%
	# $6 >> Hardware Interrupts CPU%
	# $7 >> Software Interrupts CPU%
	top -bn2 | 
	grep "Cpu(s)" | 
	tail -1 | 
	sed -r -e "s/Cpu\(s\): +([0-9.]+?)%us, +([0-9.]+?)%sy, +([0-9.]+?)%ni, +([0-9.]+?)%id, +([0-9.]+?)%wa, +([0-9.]+?)%hi, +([0-9.]+?)%si.*/\1 \2 \3 \4 \5 \6 \7/" |
	gawk "{print \"${TIMESTAMP};\" 100 - \$4 \";\" \$1 \";\" \$2 \";\" \$3 \";\" \$4  \";\" \$5 \";\" \$6 \";\" \$7 }" >> ${FILE_MACHINE_CPU}
	
	#top -bn2 | grep "Cpu(s)" | tail -1 | sed -r -e "s/.*, ([0-9.]+?)%id,.*/\1/" | gawk "{print \"${TIMESTAMP};\" 100 - \$1}" >> ${FILE_MACHINE_CPU}
}

########################################
# Function: Collect df disk usage
########################################
function collect_df(){

	df -Pk | gawk '
				BEGIN{
					used="'${TIMESTAMP}'";
				} 
					
				NR > 1 {
					used=used ";" $5;
				} 
				
				END{
					print used >> FILE_DF_USAGE;
				}
				' FILE_DF_USAGE=${FILE_DF_USAGE} 

}

########################################
# Function: Plot netstat States
########################################
function plot_netstat_states(){
	
	gnuplot -e "set datafile separator ';';
	
		  set xdata time;
		  set timefmt '%Y-%m-%dT%H:%M:%S'; 
		  set format x \"%d-%m\n%H:%M\"; 

		  set terminal png font arial 10 size 1000,1500 xffffff x000000 x404040 x0000ff;

		  set output '${RESULT_FOLDER}/graphs_netstat_TCPStates.png'; 
		  
		  set size 1,1;
		  set origin 0,0;
		  
		  set multiplot;
		  
		  set size 0.5,0.16;
		  set rmargin 5;
		  set lmargin 8;
		  set bmargin 5;
		  set key off;
		  
		  set yrange [0:];
		  
		  set origin 0,0.80; set title 'SYN_SEND Count'; 		plot '${FILE_TCPSTATES}'  using 1:2 with filledcurves x1  title 'SYN_SEND Count'; 
		  set origin 0,0.64; set title 'SYN_RECEIVED Count'; 	plot '${FILE_TCPSTATES}'  using 1:3 with filledcurves x1 title 'SYN_RECEIVED Count';
		  set origin 0,0.48; set title 'ESTABLISHED Count';  	plot '${FILE_TCPSTATES}'  using 1:4 with filledcurves x1 title 'ESTABLISHED Count';
		  set origin 0,0.32; set title 'LISTEN Count'; 			plot '${FILE_TCPSTATES}'  using 1:5 with filledcurves x1 title 'LISTEN Count';
		  set origin 0,0.16; set title 'FIN_WAIT_1 Count'; 		plot '${FILE_TCPSTATES}'  using 1:6 with filledcurves x1 title 'FIN_WAIT_1 Count';
		  set origin 0,0.00; set title 'CLOSE_WAIT Count'; 		plot '${FILE_TCPSTATES}'  using 1:7 with filledcurves x1 title 'CLOSE_WAIT Count';
		  
		  set origin 0.5,0.80; set title 'FIN_WAIT_2 Count'; 	plot '${FILE_TCPSTATES}'  using 1:8 with filledcurves x1 title 'FIN_WAIT_2 Count';
		  set origin 0.5,0.64; set title 'LAST_ACK Count'; 		plot '${FILE_TCPSTATES}'  using 1:9 with filledcurves x1 title 'LAST_ACK Count';
		  set origin 0.5,0.48; set title 'TIMED_WAIT Count'; 	plot '${FILE_TCPSTATES}'  using 1:10 with filledcurves x1 title 'TIMED_WAIT Count';
		  set origin 0.5,0.32; set title 'CLOSING Count'; 		plot '${FILE_TCPSTATES}'  using 1:11 with filledcurves x1 title 'CLOSING Count';
		  set origin 0.5,0.16; set title 'CLOSED Count'; 		plot '${FILE_TCPSTATES}'  using 1:12 with filledcurves x1 title 'CLOSED Count';
		  
		  unset multiplot; "

}

########################################
# Function: Plot disk usage
########################################
function plot_df(){
	
	
	GNUPLOT_SETTINGS="set datafile separator ';';
	
		  set xdata time;
		  set timefmt '%Y-%m-%dT%H:%M:%S'; 
		  set format x \"%d-%m\n%H:%M\"; 

		  set terminal png font arial 10 size 800,1600 xffffff x000000 x404040 x0000ff;

		  set output '${RESULT_FOLDER}/graphs_disk_usage.png'; 
		  
		  set size 1,1;
		  set origin 0,0;
		  
		  set multiplot;
		  
		  set rmargin 5;
		  set lmargin 8;
		  set bmargin 4;
		  set key off;
		  
		  set yrange [0:100];";
	
	# Escape '\' properly in case a windows path was provided
	FILE_DF_USAGE_GAWK=$( echo "${FILE_DF_USAGE} " | sed -r -e 's:\\:\\\\:g' )
	FILE_DF_USAGE_GNUPLOT=$( echo "${FILE_DF_USAGE_GAWK} " | sed -r -e 's:\\:\\\\:g' )
	
	PLOT_DEVICES=$(
		cat ${FILE_DF_USAGE} | gawk ' 
		
		NR == 1 {
		
			graphHeight = 1 / (NF-1);
			
			print "set size 1," graphHeight ";";
			
			for(j=2; j <= NF; j++){
				yorigin = 1 - (j-1)*graphHeight;
				
				print "set origin 0," yorigin "; set title \"Disk Usage (%) for " $j "\"; plot \"" FILE_DF_USAGE_GNUPLOT "\"  using 1:" j " with filledcurves x1; " ;
			}
		}
		
		' FS=";" FILE_DF_USAGE_GAWK="${FILE_DF_USAGE_GAWK}" FILE_DF_USAGE_GNUPLOT="${FILE_DF_USAGE_GNUPLOT}"
	)
	
	gnuplot -e "${GNUPLOT_SETTINGS} ${PLOT_DEVICES}"
	

}

########################################
# Function: Plot /proc/fd
########################################
function plot_proc_fd(){
	
	gnuplot -e "set datafile separator ';';
	
		  set xdata time;
		  set timefmt '%Y-%m-%dT%H:%M:%S'; 
		  set format x \"%d-%m\n%H:%M\"; 

		  set terminal png font arial 10 size 1000,1500 xffffff x000000 x404040 x0000ff;

		  set output '${RESULT_FOLDER}/graphs_proc_fd.png'; 
		  
		  set size 1,1;
		  set origin 0,0;
		  
		  set multiplot;
		  
		  set size 1,0.25;
		  set rmargin 5;
		  set lmargin 8;
		  set bmargin 5;
		  set key off;
		  
		  set yrange [0:];
		  
		  set origin 0,0.75; set title 'Total File Descriptors'; 	plot '${FILE_PROC_FILEDESCRIPTORS}'  using 1:2 with filledcurves x1; 
		  set origin 0,0.50; set title 'Socket File Descriptors'; 	plot '${FILE_PROC_FILEDESCRIPTORS}'  using 1:3 with filledcurves x1;
		  set origin 0,0.25; set title '.jar-File Descriptors';  	plot '${FILE_PROC_FILEDESCRIPTORS}'  using 1:4 with filledcurves x1;
		  set origin 0,0.00; set title 'Other File Descriptors'; 	plot '${FILE_PROC_FILEDESCRIPTORS}'  using 1:5 with filledcurves x1;
		  
		  unset multiplot; "

}

########################################
# Function: Plot jstat GC
########################################
function plot_jstat_gc(){
	
	#------------------------------
	# Plot Heap Usage
	gnuplot -e "set datafile separator ';';
	
		  set xdata time;
		  set timefmt '%Y-%m-%dT%H:%M:%S'; 
		  set format x \"%d-%m\n%H:%M\"; 
		
		  set terminal png font arial 10 size 1500,1500 xffffff x000000 x404040 x00ff00 x0000ff;

		  set output '${RESULT_FOLDER}/graphs_jstat_gc_heapusage.png'; 
		  
		  set size 1,1;
		  set origin 0,0;
		  
		  set multiplot;
		  
		  set yrange [0:];
		  
		  set rmargin 5;
		  set lmargin 8;
		  set bmargin 4;
		  set size 0.5,0.25;
		  set key off;
		  
		  set origin 0.0,0.75; set title 'S0C/S0U: Survivor Space 0 Capacity & Utilization(MB)'; 	plot '${FILE_JSTAT_GC}' using 1:(\$2/1000) with filledcurves x1 title 'Capacity', '${FILE_JSTAT_GC}' using 1:(\$4/1000) with filledcurves x1 title 'Utilization'; 
		  set origin 0.5,0.75; set title 'S1C/S1U: Survivor Space 1 Capacity & Utilization(MB)'; 	plot '${FILE_JSTAT_GC}' using 1:(\$3/1000) with filledcurves x1 title 'Capacity', '${FILE_JSTAT_GC}' using 1:(\$5/1000) with filledcurves x1 title 'Utilization'; 
		  
		  set size 1,0.25;
		  set origin 0,0.50; set title 'EC/EU: Eden Generation Capacity & Utilization(MB)'; 	plot '${FILE_JSTAT_GC}' using 1:(\$6/1000) with filledcurves x1 title 'Capacity', '${FILE_JSTAT_GC}' using 1:(\$7/1000) with filledcurves x1 title 'Utilization'; 
		  set origin 0,0.25; set title 'OC/OU: Old Generation Capacity & Utilization(MB)'; 		plot '${FILE_JSTAT_GC}' using 1:(\$8/1000) with filledcurves x1 title 'Capacity', '${FILE_JSTAT_GC}' using 1:(\$9/1000) with filledcurves x1 title 'Utilization'; 
		  
		  set key below;
		  set origin 0,0.00; set title 'PC/PU: Perm Generation Capacity & Utilization(MB)'; 	plot '${FILE_JSTAT_GC}' using 1:(\$10/1000) with filledcurves x1 title 'Capacity', '${FILE_JSTAT_GC}' using 1:(\$11/1000) with filledcurves x1 title 'Utilization'; 

		  unset multiplot; "
	
	#------------------------------
	# GC Avtivity
	gnuplot -e "set datafile separator ';';
	
		  set xdata time;
		  set timefmt '%Y-%m-%dT%H:%M:%S'; 
		  set format x \"%d-%m\n%H:%M\"; 
		
		  set terminal png font arial 10 size 1500,1500 xffffff x000000 x404040 x0000ff;

		  set output '${RESULT_FOLDER}/graphs_jstat_gc_activity.png'; 
		  
		  set size 1,1;
		  set origin 0,0;
		  
		  set multiplot;
		  
		  set yrange [0:];
		  
		  set size 1,0.20;
		  set rmargin 5;
		  set lmargin 8;
		  set bmargin 4;
		  set key off;

		  set origin 0,0.8; set title 'YGC:	Number of young generation GC Events'; 		plot '${FILE_JSTAT_GC}' using 1:12 with filledcurves x1 ; 
		  set origin 0,0.6; set title 'YGCT: Young generation garbage collection time'; plot '${FILE_JSTAT_GC}' using 1:13 with filledcurves x1 ; 
		  set origin 0,0.4; set title 'FGC:	Number of full GC events'; 					plot '${FILE_JSTAT_GC}' using 1:14 with filledcurves x1 ; 
		  set origin 0,0.2; set title 'FGCT: Full garbage collection time'; 			plot '${FILE_JSTAT_GC}' using 1:15 with filledcurves x1 ; 
		  set origin 0,0.0; set title 'GCT: Total garbage collection time'; 			plot '${FILE_JSTAT_GC}' using 1:16 with filledcurves x1 ; 


		  unset multiplot; "

}

########################################
# Function: Plot jstat Class
########################################
function plot_jstat_class(){

	gnuplot -e "set datafile separator ';';
	
		  set xdata time;
		  set timefmt '%Y-%m-%dT%H:%M:%S'; 
		  set format x \"%d-%m\n%H:%M\"; 
		
		  set terminal png font arial 10 size 1500,1500 xffffff x000000 x404040 x0000ff;

		  set output '${RESULT_FOLDER}/graphs_jstat_classloading.png'; 
		  
		  set size 1,1;
		  set origin 0,0;
		  
		  set multiplot;
		  
		  set yrange [0:];
		  
		  set size 1,0.20;
		  set rmargin 5;
		  set lmargin 8;
		  set bmargin 4;
		  set key off;

		  set origin 0,0.8; set title 'Loaded: Number of classes loaded'; 			plot '${FILE_JSTAT_CLASS}' using 1:2 with filledcurves x1 ; 
		  set origin 0,0.6; set title 'Bytes: Number of Kbytes loaded'; 			plot '${FILE_JSTAT_CLASS}' using 1:3 with filledcurves x1 ; 
		  set origin 0,0.4; set title 'Unloaded: Number of classes unloaded'; 		plot '${FILE_JSTAT_CLASS}' using 1:4 with filledcurves x1 ; 
		  set origin 0,0.2; set title 'Bytes: Number of Kbytes unloaded.'; 			plot '${FILE_JSTAT_CLASS}' using 1:5 with filledcurves x1 ; 
		  set origin 0,0.0; set title 'Time: Time spent performing class load and unload operations.'; 		plot '${FILE_JSTAT_CLASS}' using 1:6 with filledcurves x1 ; 


		  unset multiplot; "

}

########################################
# Function: Plot netstat States
########################################
function plot_vmstat(){
	
	#------------------------------
	# Plot Memory & I/O
	gnuplot -e "set datafile separator ';';
	
		  set xdata time;
		  set timefmt '%Y-%m-%dT%H:%M:%S'; 
		  set format x \"%d-%m\n%H:%M\"; 

		  set terminal png font arial 10 size 1000,1000 xffffff x000000 x404040 x0000ff;

		  set output '${RESULT_FOLDER}/graphs_vmstat_memory_io.png'; 
		  
		  set size 1,1;
		  set origin 0,0;
		  
		  set multiplot;
		  
		  set size 0.5,0.25;
		  set rmargin 5;
		  set lmargin 8;
		  set bmargin 5;
		  set key off;
		  
		  set yrange [0:];
		  
		  set origin 0,0.75; set title 'swpd: Amount of virtual memory used(MB)';  		plot '${FILE_VMSTAT}'  using 1:4 with filledcurves x1 title 'notset';
		  set origin 0,0.50; set title 'free: Amount of idle memory(MB)'; 				plot '${FILE_VMSTAT}'  using 1:5 with filledcurves x1 title 'notset';
		  set origin 0,0.25; set title 'buff: Amount of memory used as buffers(MB)'; 	plot '${FILE_VMSTAT}'  using 1:6 with filledcurves x1 title 'notset';
		  set origin 0,0.00; set title 'cache: Amount of memory used as cache(MB)'; 	plot '${FILE_VMSTAT}'  using 1:7 with filledcurves x1 title 'notset';
		 
		  set origin 0.5,0.75; set title 'si: Memory swapped in from disk per second.'; 		plot '${FILE_VMSTAT}'  using 1:8 with filledcurves x1 title 'notset';
		  set origin 0.5,0.50; set title 'so: Memory swapped to disk per second).'; 			plot '${FILE_VMSTAT}'  using 1:9 with filledcurves x1 title 'notset';
		  set origin 0.5,0.25; set title 'bi: Blocks received from a block device (blocks/s).'; plot '${FILE_VMSTAT}'  using 1:10 with filledcurves x1 title 'notset';
		  set origin 0.5,0.00; set title 'bo: Blocks sent to a block device (blocks/s).'; 		plot '${FILE_VMSTAT}'  using 1:11 with filledcurves x1 title 'notset';
		  
		  unset multiplot; " 
	
	#------------------------------
	# Plot Processes & CPU
	gnuplot -e "set datafile separator ';';
	
		  set xdata time;
		  set timefmt '%Y-%m-%dT%H:%M:%S'; 
		  set format x \"%d-%m\n%H:%M\"; 

		  set terminal png font arial 10 size 1000,1500 xffffff x000000 x404040 x0000ff;

		  set output '${RESULT_FOLDER}/graphs_vmstat_procs_cpu.png'; 
		  
		  set size 1,1;
		  set origin 0,0;
		  
		  set multiplot;
		  
		  set size 0.5,0.2;
		  set rmargin 5;
		  set lmargin 8;
		  set bmargin 5;
		  set key off;
		  
		  set yrange [0:];
		  
		  set origin 0,0.8; set title 'us: Time spent running non-kernel code. (user time, CPU%)'; 	plot '${FILE_VMSTAT}'  using 1:14 with filledcurves x1;
		  set origin 0,0.6; set title 'sy: Time spent running kernel code. (system time, CPU%)'; 	plot '${FILE_VMSTAT}'  using 1:15 with filledcurves x1;
		  set origin 0,0.4; set title 'id: Time spent idle (CPU%)'; 								plot '${FILE_VMSTAT}'  using 1:16 with filledcurves x1;
		  set origin 0,0.2; set title 'wa: Time spent waiting for IO (CPU%)'; 						plot '${FILE_VMSTAT}'  using 1:17 with filledcurves x1;
		  set origin 0,0.0; set title 'st: Time stolen from a virtual machine.'; 					plot '${FILE_VMSTAT}'  using 1:18 with filledcurves x1;
		  	  
		  set origin 0.5,0.8; set title 'r: Processes waiting for run time'; 				plot '${FILE_VMSTAT}'  using 1:2 with filledcurves x1; 
		  set origin 0.5,0.6; set title 'b: Processes in uninterruptible sleep'; 			plot '${FILE_VMSTAT}'  using 1:3 with filledcurves x1;
		  set origin 0.5,0.4; set title 'in: Interrupts per second, including the clock.'; 	plot '${FILE_VMSTAT}'  using 1:12 with filledcurves x1;
		  set origin 0.5,0.2; set title 'cs: Context switches per second.'; 				plot '${FILE_VMSTAT}'  using 1:13 with filledcurves x1;

		  
		  unset multiplot; " 
}

########################################
# Function: Plot netstat States
########################################
function plot_ps_top(){
	
	gnuplot -e "set datafile separator ';';
	
		  set xdata time;
		  set timefmt '%Y-%m-%dT%H:%M:%S'; 
		  set format x \"%d-%m\n%H:%M\"; 

		  set terminal png font arial 10 size 1000,1500 xffffff x000000 x404040 x0000ff;

		  set output '${RESULT_FOLDER}/graphs_process_cpu_mem_threads.png'; 
		  
		  set size 1,1;
		  set origin 0,0;
		  
		  set multiplot;
		  
		  set size 1,0.25;
		  set rmargin 5;
		  set lmargin 8;
		  set bmargin 5;
		  set key off;
		  
		  set yrange [0:];
		  
		  set origin 0,0.75; set title 'Process CPU% (100% per Core)'; 			plot '${FILE_PS_TOP}'  using 1:2 with filledcurves x1;
		  set origin 0,0.50; set title 'Process CPU% (calculated to 100%)'; 	plot '${FILE_PS_TOP}'  using 1:3 with filledcurves x1;
		  set origin 0,0.25; set title 'Process MEM%'; 							plot '${FILE_PS_TOP}'  using 1:4 with filledcurves x1;
		  set origin 0,0.0; set title 'Total Threads'; 							plot '${FILE_PS_TOP}'  using 1:5 with filledcurves x1;

		  unset multiplot; "

}

########################################
# Function: Plot machine CPU usage
########################################
function plot_machine_cpu_usage(){
	
	gnuplot -e "set datafile separator ';';
	
		  set xdata time;
		  set timefmt '%Y-%m-%dT%H:%M:%S'; 
		  set format x \"%d-%m\n%H:%M\"; 

		  set terminal png font arial 10 size 1500,1000 xffffff x000000 x404040 x0000ff;

		  set output '${RESULT_FOLDER}/graphs_machine_cpu_usage.png'; 
		  
		  set size 0.5,0.25;
		  set origin 0,0;
		  
		  set multiplot;
		  
		  set rmargin 5;
		  set lmargin 8;
		  set bmargin 3;
		  set key off;
		  
		  set yrange [0:];
		  
		  set origin 0.0, 0.75; set title 'Total CPU Usage %'; 			plot '${FILE_MACHINE_CPU}'  using 1:2 with filledcurves x1;
		  set origin 0.0, 0.50; set title 'us: User CPU Usage %'; 		plot '${FILE_MACHINE_CPU}'  using 1:3 with filledcurves x1;
		  set origin 0.0, 0.25; set title 'sy: System CPU Usage %'; 	plot '${FILE_MACHINE_CPU}'  using 1:4 with filledcurves x1;
		  set origin 0.0, 0.00; set title 'ni: Nice CPU Usage %'; 		plot '${FILE_MACHINE_CPU}'  using 1:5 with filledcurves x1;
		  
		  set origin 0.5, 0.75; set title 'id: Idle CPU %'; 			plot '${FILE_MACHINE_CPU}'  using 1:6 with filledcurves x1;
		  set origin 0.5, 0.50; set title 'wa: CPU % waiting for I/O'; 	plot '${FILE_MACHINE_CPU}'  using 1:7 with filledcurves x1;
		  set origin 0.5, 0.25; set title 'Hardware Interrupts CPU%'; 	plot '${FILE_MACHINE_CPU}'  using 1:8 with filledcurves x1;
		  set origin 0.5, 0.00; set title 'Software Interrupts CPU%'; 	plot '${FILE_MACHINE_CPU}'  using 1:9 with filledcurves x1;

		  unset multiplot; "
		  

}

#######################################################################################
# Function: Create Plots and Exit
# -------------------------------
# This is set to be executed when the user hits Ctrl+C.
#######################################################################################
createPlotsAndExit(){
	
	if [ "${FLAG_ONLY_COLLECT}" != "TRUE" ]
	then
		#------------------------------
		# Create Graphs
		echo "================== Create Graphs ======================"
		
		echo "----- Machine CPU Usage Graphs -----"
		plot_machine_cpu_usage
		
		echo "----- Netstat State Graphs -----"
		plot_netstat_states
		
		echo "----- VMStat Graphs -----"
		plot_vmstat
		
		echo "----- Disk Usage Graphs -----"
		plot_df
		
		if [ "${PROCESSID}" != "NOTSET" ]
		then
			echo "----- /proc/fd Graphs -----"
			plot_proc_fd
			
			echo "----- jstat -gc Graphs -----"
			plot_jstat_gc
			
			echo "----- jstat -class Graphs -----"
			plot_jstat_class
			
			echo "----- ps/top Graphs -----"
			plot_ps_top
			
		fi
		
		echo "======================================================="
		wait
	fi
	
	#------------------------------
	# Permissions & Exit
	chmod -R 777 "${RESULT_FOLDER}"
	
	exit 130;
}

#-----------------------------------
# Set trap for Ctrl+C
#-----------------------------------
trap createPlotsAndExit SIGINT SIGTERM 

##############################################################################
# Main Process
##############################################################################

########################################
# Load Options
########################################

# Check if there is at least one argument present, else print help
if [ "$#" -eq 0 ]
then
	print_docu
fi

while getopts "c:s:d:f:p:g:ouh?" option
do
	case $option in
		c)	NUMBER_COLLECTIONS="$OPTARG";;
		s)	SLEEP_SECS="$OPTARG";;
		d)	STARTDELAY_SECS="$OPTARG";;
		f)	FILTER="$OPTARG";;
		p)  PROCESSID="$OPTARG";;
		o)  FLAG_ONLY_COLLECT="TRUE";;
		u)  FLAG_UNSTOPPABLE="TRUE";;
		
		g)  RESULT_FOLDER="$OPTARG"
			FILE_TCPSTATES="${RESULT_FOLDER}/data_netstat_TCPStates.csv"
			FILE_TCPESTABLISHED="${RESULT_FOLDER}/data_netstat_TCPestablished.dat"
			FILE_VMSTAT="${RESULT_FOLDER}/data_vmstat.csv"
			FILE_PROC_FILEDESCRIPTORS="${RESULT_FOLDER}/data_proc_filedescriptors.csv"
			FILE_JSTAT_GC="${RESULT_FOLDER}/data_jstat_gc.csv"
			FILE_JSTAT_CLASS="${RESULT_FOLDER}/data_jstat_classloading.csv"
			FILE_PS_TOP="${RESULT_FOLDER}/data_process_cpu_mem_threads.csv"
			FILE_DF_USAGE="${RESULT_FOLDER}/data_disk_usage.csv"
			FILE_MACHINE_CPU="${RESULT_FOLDER}/data_machine_cpu_usage.csv"
			PROCESSID="UnknownPID"
			createPlotsAndExit
			;;
		
		h) print_docu;;
		
		?)  echo "unkown argument: $option";
	esac
done
shift $(expr $OPTIND - 1)

########################################
# Create/Reset Files
########################################

#------------------------------
# Create Result Dir
if ! [ -d "${RESULT_FOLDER}" ]
then
		mkdir -p "${RESULT_FOLDER}"
fi

#------------------------------
# Create Files(with Headers were needed)
echo "TIMESTAMP;COUNT_SYN_SEND;COUNT_SYN_RECEIVED;COUNT_ESTABLISHED;COUNT_LISTEN;COUNT_FIN_WAIT_1;COUNT_CLOSE_WAIT;COUNT_FIN_WAIT_2;COUNT_LAST_ACK;COUNT_TIMED_WAIT;COUNT_CLOSING;COUNT_CLOSED;" > ${FILE_TCPSTATES}
echo "" > ${FILE_TCPESTABLISHED}
echo "" > ${FILE_VMSTAT}
echo "TIMESTAMP;TotalCPUUsage%;UserCPUUsage%;SystemCPUUsage%;NiceCPUUsage%;IdleCPUUsage%;IOWaitCPUUsage%;HardwareInterruptsCPUUsage%;SoftwareInterruptsCPUUsage%" > ${FILE_MACHINE_CPU}

vmstat -a | egrep "^ +r" | sed -r -e 's/[ ]+/;/g' -e 's/^;//'  | gawk '{print "timestamp;" $0}' > ${FILE_VMSTAT}
df -Pk | gawk 'BEGIN{device="timestamp";} NR > 1 {device=device ";" $6; } END{print device; }' > ${FILE_DF_USAGE}

#------------------------------
# Create Files for Process collection
if [ "${PROCESSID}" != "NOTSET" ]
then
	echo "Timestamp;TotalFileDescriptors;Sockets;JarFiles;Other" > ${FILE_PROC_FILEDESCRIPTORS}
	${JAVA_HOME}/bin/jstat -gc ${PROCESSID} 1 1 | head -1 | sed -r -e 's/[ ]+/;/g' -e 's/^;//'  | gawk "{print \"TIMESTAMP;\" \$0}" > ${FILE_JSTAT_GC}
	${JAVA_HOME}/bin/jstat -class ${PROCESSID} 1 1 | head -1 | sed -r -e 's/[ ]+/;/g' -e 's/^;//'  | gawk "{print \"TIMESTAMP;\" \$0}" > ${FILE_JSTAT_CLASS}
	echo "TIMESTAMP;ProcessCPUCore%;ProcessCPUUsage%;ProcessMEM%;ProcessThreadCount" >> ${FILE_PS_TOP}
fi


########################################
# Collection Loops
########################################

# Delayed start
sleep ${STARTDELAY_SECS}

for (( i=1; i<=${NUMBER_COLLECTIONS}; i++ )) ; do

	TIMESTAMP=$(date +"%Y-%m-%dT%H:%M:%S")
	
	echo "${TIMESTAMP} [INFO] collection-${i}"
	
	collect_vmstat &
	collect_tcpstates &
	collect_tcpestablished &
	collect_df &
	collect_machine_cpu_usage &
	
	#check if PID was set
	if [ "${PROCESSID}" != "NOTSET" ]
	then
	
		#check if PID exists
		if ps -p ${PROCESSID} > /dev/null
		then
			collect_proc_fd &
			collect_jstat_gc &
			collect_jstat_class &
			collect_ps_top &
		else
			echo "${TIMESTAMP} [ERROR] Cannot collect from ProcessID '${PROCESSID}' as it doesn't exist."
			
			if [ "${FLAG_UNSTOPPABLE}" != "TRUE" ]
			then
				exit 0;
			fi
		fi

	fi

	sleep ${SLEEP_SECS}
	wait
done

########################################
# Terminate
########################################

createPlotsAndExit 



