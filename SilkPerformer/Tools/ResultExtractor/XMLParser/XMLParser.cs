using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.IO;
using System.Text.RegularExpressions;

namespace com.peng.toolbox.resultextractor
{
    public class XMLParser
    {

        /*
        * Goes through xml file and searches for this nodes:
        * 
        * XML-Node Name                            Silk Useage
        * -----------------------------------------------------------
        * Group-Name (first part of string)        Usecase
        * Group-Name (second part of string)       Usergroup
        * Measure-Name                             Timername
        * Percentiles                              Percentile Value
        * 
        * Input Parameters: 
        * inputFile    XML filename, incl. path
        * outputFile   CSV output filename, incl. path
        * percentile   Percentile value (e.g. 95)
        * timerFilter  Timername filter pattern 
        */
        public void buildResultFile(string inputFile, string outputFile, string percentiles, string metricTypeFilter, string timerFilter, string separator)
        {

            System.IO.StreamWriter outputText = new System.IO.StreamWriter(outputFile);
            System.Xml.XmlDocument doc = new XmlDocument();
            doc.Load(inputFile);

            // load all nodes with name "Group"
            XmlNodeList nodeList = doc.SelectNodes(".//Group");


            //--------------------------------------
            // Get Percentiles

            String percentileHeaders = "";

            string[] percentilesArray = percentiles.Split(',');

            for(int i = 0; i < percentilesArray.Length;i++)
            {
                percentilesArray[i] = percentilesArray[i].Trim();
                percentileHeaders += separator + percentilesArray[i]+"%";
            }

            //--------------------------------------
            // Write Header
            outputText.WriteLine(
                "\nUsecase"+ separator +
                "User" + separator +
                "Timer" + separator +
                "Count" + separator +
                "MeasuredCount" + separator +
                "Min" + separator +
                "Avg" + separator +
                "Max" + separator +
                "StdDev" + percentileHeaders);

            // go through all nodes
            foreach (XmlNode node in nodeList)
            {

                if (node.SelectSingleNode(".//SumSum") != null)
                {

                    // search Name and Percentiles values within Measure node
                    XmlNodeList measure = node.SelectNodes(".//Measure");
                    foreach (XmlNode mNode in measure)
                    {
                        //only grab nodes which match the filter
                        if ( mNode.SelectSingleNode(".//Type").InnerText.Trim().Equals(metricTypeFilter)
                          && Regex.IsMatch(mNode.SelectSingleNode(".//Name").InnerText.Trim(), timerFilter) )
                        {


                            string[] ucAndUsrGrp = node.SelectSingleNode(".//Name").InnerText.Trim().Split('/');
                            string[] usrGrp = ucAndUsrGrp[1].Split('-');

                            // write Usecase-, Timer- and Usergroup-Name to outputfile
                            outputText.Write(ucAndUsrGrp[0]);
                            outputText.Write(separator + usrGrp[0]);
                            outputText.Write(separator + mNode.SelectSingleNode(".//Name").InnerText.Trim());
                            outputText.Write(separator + mNode.SelectSingleNode(".//SumCount1").InnerText);
                            outputText.Write(separator + mNode.SelectSingleNode(".//SumCount2").InnerText);
                            outputText.Write(separator + mNode.SelectSingleNode(".//MinMin").InnerText);
                            outputText.Write(separator + mNode.SelectSingleNode(".//Avg").InnerText);
                            outputText.Write(separator + mNode.SelectSingleNode(".//MaxMax").InnerText);
                            outputText.Write(separator + mNode.SelectSingleNode(".//Stdd").InnerText);

                            // display only Percentiles value
                            if (mNode.SelectSingleNode(".//Percentiles") != null)
                            {
                                //-------------------------------------------------------
                                // search for percentile values and print them

                                String percentileValues = "";
                                foreach( string percentile in percentilesArray)
                                {

                                    XmlNodeList mValueNode = mNode.SelectSingleNode(".//Percentiles").SelectNodes("./Values/Value");
                                    foreach (XmlNode vNode in mValueNode)
                                    {
                                        if (vNode.SelectSingleNode(".//Percent").InnerText == percentile)
                                        {
                                            outputText.Write(separator + vNode.SelectSingleNode(".//Value").InnerText);
                                        }
                                    }
                                }
                            }

                            outputText.Write(Environment.NewLine);

                        }
                    }
                }

            }

            outputText.Close();
        }



        /*
         * Goes through xml file and searches for this nodes:
         * 
         * XML-Node Name                            Silk Useage
         * -----------------------------------------------------------
         * Group-Name (first part of string)        Usecase
         * Group-Name (second part of string)       Usergroup
         * Measure-Name                             Timername
         * Percentiles                              Percentile Value
         * 
         * Input Parameters: 
         * inputFile    XML filename, incl. path
         * outputFile   CSV output filename, incl. path
         * percentile   Percentile value (e.g. 95)
         * timerFilter  Timername filter pattern
         */
        public void buildResultFileExt(string inputFile, string outputFile, string percentile, string timerFilter)
        {

            System.IO.StreamWriter outputText = new System.IO.StreamWriter(outputFile);
            System.Xml.XmlDocument doc = new XmlDocument();
            doc.Load(inputFile);

            // return all nodes with pattern "Percentiles" 
            XmlNodeList nodeList = doc.SelectNodes(".//Measure/Name[contains(text(),\"" + timerFilter + "\")]/../Percentiles");

            outputText.WriteLine("\nUsecase;User;Timer;Count;Measured;" + percentile + "-Percentile");

            // go through all "Percentile" nodes
            foreach (XmlNode percentiles in nodeList)
            {
                // returns usecase name, first part (split "/") contains usecase name
                outputText.Write(percentiles.SelectSingleNode("../../../Name").InnerText.Trim().Split('/')[0]);
                // returns user group name, second part (split "/") contains group name
                outputText.Write(";" + percentiles.SelectSingleNode("../../../Name").InnerText.Trim().Split('/')[1].Split('-')[0]);

                // returns timer name
                XmlNode percentValue = percentiles.SelectSingleNode("./Values/Value/Percent[contains(text(),\"" + percentile + "\")]/../Value");
                outputText.Write(";" + percentValue.SelectSingleNode("../../../../Name").InnerText.Trim());
                outputText.Write(";" + percentValue.SelectSingleNode("../../../../SumCount1").InnerText);
                outputText.Write(";" + percentValue.SelectSingleNode("../../../../SumCount2").InnerText);
                outputText.WriteLine(";" + percentValue.InnerText);


            }

            outputText.Close();
        }

    }
}
