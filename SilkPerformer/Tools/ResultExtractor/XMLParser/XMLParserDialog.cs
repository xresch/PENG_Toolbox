using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Diagnostics;
using System.Xml;

namespace com.peng.toolbox.resultextractor
{
    public partial class XMLParserDialog : Form
    {
        private string inputfile;
        private string outputfile;
        private string percentile;
        private string timerfilter;
        private string separator;
        private string metricType;

        public XMLParserDialog()
        {
            InitializeComponent();
        }
        

        private void inputSearch_Click(object sender, EventArgs e)
        {
            openFileDialog.Filter = "XML files (*.xml)|*.xml";

            DialogResult result = openFileDialog.ShowDialog(); 
            if (result == DialogResult.OK) 
            {
                InputFile = openFileDialog.FileName;
                inputFileName.Text = InputFile;
            }

        }

        private void outputSearch_Click(object sender, EventArgs e)
        {
            saveFileDialog.Filter = "CSV Files (*.csv)|*.csv";

            DialogResult result = saveFileDialog.ShowDialog();
            if (result == DialogResult.OK)
            {
                OutputFile = saveFileDialog.FileName;
                outputFileName.Text = OutputFile;
            }
        }

        private void startParsing_Click(object sender, EventArgs e)
        {
            // get latest values from GUI
            Percentile = percentileComboBox.Text;

            if(Percentile.Equals("All Available Percentiles"))
            {
                Percentile = availablePercentilesBox.Text;
            }

            TimerFilter = timerFilter.Text;

            InputFile = inputFileName.Text;
            OutputFile = outputFileName.Text;
            Separator = separatorTextBox.Text;
            metricType = metricCombo.Text;

            XMLParser xmlParser = new XMLParser();
            xmlParser.buildResultFile(InputFile, OutputFile, Percentile, metricType, TimerFilter, Separator);

            DialogResult dialogResult = MessageBox.Show("All done. Do you wan't to open the result file?", "Parsing Done!!!", MessageBoxButtons.YesNo);
            if (dialogResult == DialogResult.Yes)
            {
                Process.Start("notepad.exe", OutputFile);
            }
            else if (dialogResult == DialogResult.No)
            {
                //do nothing
            }

            
        }


        private void cancel_Click(object sender, EventArgs e)
        {
            Dispose();
        }


        /*
         * Below all used properties
         */
        public string InputFile
        {
            get
            {
                return inputfile;
            }
            set
            {
                inputfile = value;
            }

        }

        public string OutputFile
        {
            get
            {
                return outputfile;
            }
            set
            {
                outputfile = value;
            }

        }

        public string Percentile
        {
            get
            {
                return percentile;
            }
            set
            {
                percentile = value;
            }

        }

        public string TimerFilter
        {
            get
            {
                return timerfilter;
            }
            set
            {
                timerfilter = value;
            }

        }

        public string Separator
        {
            get
            {
                return separator;
            }
            set
            {
                separator = value;
            }

        }

        private void label4_Click(object sender, EventArgs e)
        {

        }

        private void percentileValue_TextChanged(object sender, EventArgs e)
        {

        }

        private void label2_Click(object sender, EventArgs e)
        {

        }

        private void label1_Click(object sender, EventArgs e)
        {

        }

        private void pictureBox3_Click(object sender, EventArgs e)
        {

        }

        private void pictureBox2_Click(object sender, EventArgs e)
        {

        }

        private void label6_Click(object sender, EventArgs e)
        {

        }

        private void inputFileName_TextChanged(object sender, EventArgs e)
        {
            InputFile = inputFileName.Text;

            System.Xml.XmlDocument doc = new XmlDocument();
            doc.Load(InputFile);

            // load node with name "Percentiles"
            XmlNode percentileNode = doc.SelectSingleNode(".//Percentiles");

            String percentageString = "";
            if (percentileNode != null)
            {
                XmlNodeList percentageNodes = percentileNode.SelectNodes("./Values/Value/Percent");

                // go through all nodes
                foreach (XmlNode node in percentageNodes)
                {
                    percentageString += node.InnerText + ",";
                }
            }

            availablePercentilesBox.Text = percentageString;
        }

        private void percentileComboBox_SelectedIndexChanged(object sender, EventArgs e)
        {

        }

        private void pictureBox1_Click(object sender, EventArgs e)
        {

        }

        private void label1_Click_1(object sender, EventArgs e)
        {

        }

        private void pictureBox7_Click(object sender, EventArgs e)
        {

        }

        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {

        }
    }
}
