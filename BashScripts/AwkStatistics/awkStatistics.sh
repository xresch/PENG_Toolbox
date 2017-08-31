####################################################################################
# Author:    Reto Scheiwiller, 2017
# License:   MIT License
#
# AWK Statistics
# --------------
# awkStatistics is a script which is used to generate statistical data for csv data
# by using awk. It takes two columns from the input  file, one is used to identify
# the row(option -i) and the other one is the column where the values are 
# taken from (option -v).
#
# The script was designed for performance engineers to be able to analyze CSV-Data
# on the fly and have a good possibility to automate the analysis process.
#
# Syntax: awkStatistics.sh -f <file> [-mfdivroph]
#
# Options:  -m 
#              Prints a small description with the list of options.
#
#           -f <file>
#              Defines the csv file to proceed. If this option is not specified, the script reads from standard input.
#  
#           -d <delimiter>
#              The Delimiter of the file (Default: one ore more blanks or tabs --> '[ \t]+').
#              Please be aware that the delimiter if you change it can only be a single character.
#
#           -i <column-number>
#              The column which is used to identify the record (Default: 1)
#  
#           -v <column-number>
#              The column which contains the values used for the statistics (Default: 2)
#  
#           -r <result file>
#              The file were the results should be stored (Default: standard output)
#  
#           -o <output_delimiter>
#              The string which should be used as output delimiter (Default: ';')
#  
#           -p <percentiles>
#              Percentiles which should be calculated. (Default: '80,90,95')
#  
#           -h 
#             Use this flag if the input doesn't contain a header row.
#
####################################################################################

########################################
# Set Default Values
########################################
INPUT_FILE=""
DELIMITER="[ \t]+"
OUT_DELIMITER=";"
IDENTIFIER_COLUMN="1"
VALUE_COLUMN="2"
RESULT_FILE='/dev/tty'
PERCENTILES="80,90,95"
HAS_HEADLINE="TRUE"
TEMP_FILE="awkStatisticsSortedInput.tmp"

PRINT_HELP="FALSE"
HAS_DELIMITER_CHANGED="FALSE"

########################################
# Load Options
########################################
while getopts "f:d:i:v:r:p:o:hm?" option
do
	case $option in
		f)	INPUT_FILE="$OPTARG";;
		d)	DELIMITER="$OPTARG"
			HAS_DELIMITER_CHANGED="TRUE";;
		i)	IDENTIFIER_COLUMN="$OPTARG";;
		v)	VALUE_COLUMN="$OPTARG";;
		r)	RESULT_FILE="$OPTARG";;
		p)	PERCENTILES="$OPTARG";;
		o)	OUT_DELIMITER="$OPTARG";;
		h)	HAS_HEADLINE="FALSE";;
		m)  PRINT_HELP="TRUE";;
		?)  echo "unkown argument: $option";
	esac
done

if [ "$PRINT_HELP" == "TRUE" ]
then
	########################################
	# Print Help
	########################################
	echo " AWK Statistics"
	echo " --------------"
	echo " awkStatistics is a script which is used to generate statistical data for csv data"
	echo " by using awk. It takes two columns from the input  file, one is used to identify"
	echo " the row(option -i) and the other one is the column were the values are "
	echo " taken from (option -v)."
	echo ""
	echo " Syntax: awkStatistics.sh -f <file> [-divrph]"
	echo ""
	echo " Options:  -m "
	echo "              Prints a small description with the list of options."
	echo ""
	echo "           -f <file>"
	echo "              Defines the csv file to proceed. If this option is not specified, the script reads from standard input."
	echo "  "
	echo "           -d <delimiter>"
	echo "              The Delimiter of the file (Default: one ore more blanks or tabs --> '[ \t]+')."
	echo "              Please be aware that the delimiter if you change it can only be a single character."
	echo ""
	echo "           -i <column-number>"
	echo "              The column which is used to identify the record (Default: 1)"
	echo ""
	echo "           -v <column-number>"
	echo "              The column which contains the values used for the statistics (Default: 2)"
	echo ""
	echo "           -r <result file>"
	echo "              The file were the results should be stored (Default: standard output)"
	echo "  "
	echo "           -o <output_delimiter>"
	echo "              The string which should be used as output delimiter (Default: ';')"
	echo "  "
	echo "           -p <percentiles>"
	echo "              Percentiles which should be calculated. (Default: '80,90,95')"
	echo "  "
	echo "           -h "
	echo "             Use this flag if the input doesn't contain a header row."
	echo ""

else

	########################################
	# Prepare the Input
	# -----------------
	# Sort 
	########################################
	if [ $HAS_DELIMITER_CHANGED == "TRUE" ] 
	then
		cat $INPUT_FILE |
		awk '{if(HEADLINE == "TRUE" && NR == 1){next} else{ print}} ' HEADLINE=$HAS_HEADLINE |
		sort -n -t"$DELIMITER" -k $VALUE_COLUMN > $TEMP_FILE
	else
		cat $INPUT_FILE |
		awk '{if(HEADLINE == "TRUE" && NR == 1){next} else{ print}} ' HEADLINE=$HAS_HEADLINE |
		sort -n -k $VALUE_COLUMN > $TEMP_FILE
	fi

	
	########################################
	# Create Statistics
	########################################
	
	cat $TEMP_FILE |
	awk '
		{
			###############################################
			# Skip headline if there is one
			###############################################
			#if(HEADLINE == "TRUE" && NR == 1){
			#	next
			#}
			
			###############################################
			# Read data into Arrays by considering 
			# the identifier.
			###############################################
			if($IDENTIFIER in identifiersCount){
				identifiersCount[$IDENTIFIER] += 1;
				values[$IDENTIFIER, identifiersCount[$IDENTIFIER]] = $VALUE;
				values[$IDENTIFIER, "sum"] += $VALUE;
			} else{
				identifiersCount[$IDENTIFIER]= 1;
				values[$IDENTIFIER, identifiersCount[$IDENTIFIER]] = $VALUE
				values[$IDENTIFIER, "sum"] = $VALUE;
			}
		}
		END{
			###############################################
			# Skip headline if there is one
			###############################################
			perc_count = split(PERCENTILES, percArr, ",");
			percHeaders="";
			for(j=1; j <= perc_count; j++){
				percHeaders=percHeaders OFS percArr[j] "pc";
			}
			
			###############################################
			# print the result headline
			###############################################
			print "Identifier", "Count", "Sum", "Avg", "Min", "Median", "Max" percHeaders > RESULT_FILE
			
			###############################################
			# Iterate over each identifier and print a
			# result line with statistical values.
			###############################################
			for(i in identifiersCount){
				
				count = identifiersCount[i];
				sum = values[i, "sum"];
				avg = sum / count;
				min = values[i, 1];
				max = values[i, count];
				median = values[i, int(count/2)];
				
				percValues="";
				#for(ind in percArr){
				#	percValues = percValues OFS values[i, int(count * percArr[ind] / 100)+1];
				#}
				for(j=1; j <= perc_count; j++){
					percValues = percValues OFS values[i, int(count * percArr[j] / 100)+1];
				}
				print i, count, sum, avg, min, median, max percValues > RESULT_FILE
			}
		}' IDENTIFIER=$IDENTIFIER_COLUMN\
		   FS="$DELIMITER"\
		   OFS="$OUT_DELIMITER"\
		   VALUE=$VALUE_COLUMN\
		   PERCENTILES=$PERCENTILES\
		   HEADLINE=$HAS_HEADLINE\
		   RESULT_FILE=$RESULT_FILE
		   
	rm $TEMP_FILE
fi


		
		
		
		
		
		
		