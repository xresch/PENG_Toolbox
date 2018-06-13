using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace SilkScriptStyler
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private bool isLoaded = false;
        public MainWindow()
        {
            InitializeComponent();

            isLoaded = true;
        }

        private void textBox_TextChanged(object sender, TextChangedEventArgs e)
        {
            updateResults();
        }

        private void inputText_TextChanged(object sender, TextChangedEventArgs e)
        {
            updateResults();
        }

        private void CheckBox_Changed(object sender, RoutedEventArgs e)
        {
            updateResults();
        }

        private void updateResults()
        {

            if (isLoaded)
            {
                String text = inputText.Text;

                //---------------------------------------
                //Remove all Think Times
                String thinkTimeInsertion = "";
                if (replaceThinkTimesCheckbox != null && replaceThinkTimesCheckbox.IsChecked == true)
                {
                    text = Regex.Replace(text, "^.*ThinkTime.*?$", "", RegexOptions.Multiline | RegexOptions.IgnoreCase);
                    thinkTimeInsertion = "ThinkTime(1.0);\r\n\r\n";
                }

                //---------------------------------------
                //Remove all Blank Lines
                if (removeBlankLinesCheckbox != null && removeBlankLinesCheckbox.IsChecked == true)
                {
                    text = Regex.Replace(text, @"^\s*$[\r\n]*", "", RegexOptions.Multiline | RegexOptions.IgnoreCase);
                }

                //---------------------------------------
                //Remove all Blanks from the Start
                text = Regex.Replace(text, @"^[\t ]+", "", RegexOptions.Multiline | RegexOptions.IgnoreCase);

                //---------------------------------------
                //Insert Code Block
                String startMethod = startMethodTextBox.Text;
                String stopMethod = stopMethodTextBox.Text;
                String variableName = variableTextBox.Text;
                
                String pattern = "^//(.*?)[\r\n]*$";
                String replacement = stopMethod + "("+ variableName + ");\r\n\r\n" + thinkTimeInsertion +
                    "//---------------------------------\r\n" +
                    "//$1\r\n" +
                    "//---------------------------------\r\n" +
                    variableName + " := sUC+\" 000 $1\";\r\n" +
                    startMethod + "("+ variableName + ");";

                text = Regex.Replace(text, pattern, replacement, RegexOptions.Multiline | RegexOptions.IgnoreCase);

                //---------------------------------------
                // Replace spaces with underscore from sMeasure definitions
                text = Regex.Replace(text, "(?<="+ variableName + " := sUC.*?) +", "_", RegexOptions.Multiline | RegexOptions.IgnoreCase);

                //---------------------------------------
                // Replace BrowserClick names with sMaesure
                if (browserClickCheckbox != null && browserClickCheckbox.IsChecked == true)
                {
                    text = Regex.Replace(text, "BUTTON_Left,.*\\);", "BUTTON_Left, "+ variableName + ");", RegexOptions.Multiline | RegexOptions.IgnoreCase);
                }



                //---------------------------------------
                // remove to much stuff
                int index = text.IndexOf("//");

                if (index > 0)
                {
                    text = text.Substring(index);
                }

                //---------------------------------------
                // add missing stuff
                text = text + "\r\n" + stopMethod + "("+ variableName + ");\r\n\r\nThinkTime(1.0);\r\n\r\n";

                //---------------------------------------
                // Show Result
                outputText.Text = text;
            }
        }
    }
}
