#########################################################################
#
# This File contains some useful commands for performnace engineering
# activities
#
#########################################################################

#########################################################################
# One Liners for Creating Java Thread Dumps in a loop
#########################################################################

# all dumps in single file
while sleep 1; do jstack {processID} >> threaddump.txt ; echo -n ">>> " ; done

# dumps into multiple files with timestamp
while sleep 1; do time=$(date +"%Y-%m-%d_%H.%M.%S"); jstack 37672 >> "./dumps/threaddump_${time}.txt" ; echo -n ">>> " ; done

#########################################################################
# One Liner for Analyzing thread dumps
#########################################################################
cat threaddump_* | grep "at " | sort | uniq -c | sort


#########################################################################
# One Liner for Creating Thread Dumps
#########################################################################

#########################################################################
# vmstat: vmstat prints information regarding processes, 
# memory, paging, block IO and CPU activity.
#########################################################################
vmstat -a -SM

#########################################################################
# iostat: Prints statistics for CPU, devices and network.
#########################################################################
iostat -x -d

#########################################################################
# netstat: Get network related statistic. For example, get the count of 
# established tcp connections:
#########################################################################
netstat | grep -c "${FILTER}.*ESTABLISHED"

#########################################################################
# top: Prints information about the processes running on the machine. Updates based on an interval.
#########################################################################
top -n1

#########################################################################
# ps: Prints a snapshot of the current processes.
#########################################################################
ps -aux

#########################################################################
# df: Prints information regarding the file system usage.
#########################################################################
df -Pk

#########################################################################
# /proc/fd: Not a command, but can be used to monitor the number of open 
# files used by a process. The following gets the count of file 
# descriptors for a process
#########################################################################
ls -la /proc/{PROCESSID}/fd | wc -l
