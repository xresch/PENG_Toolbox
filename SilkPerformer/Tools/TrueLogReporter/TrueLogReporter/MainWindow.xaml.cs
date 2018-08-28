using System;
using System.IO;
using System.IO.Compression;
using System.Linq;
using System.Windows;
using System.Windows.Forms;
using System.Xml;
using Microsoft.Office.Interop.Word;

namespace TrueLogReporter
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : System.Windows.Window
    {

        public MainWindow()
        {
            InitializeComponent();
            ReportGenerator.CONSOLE_AREA = consoleArea;
        }

        private void inputSearch_Click(object sender, RoutedEventArgs e)
        {
            OpenFileDialog openFileDialog = new OpenFileDialog();

            openFileDialog.Filter = "True Log Files (*.xlg, *.tlz)|*.xlg;*.tlz";

            DialogResult result = openFileDialog.ShowDialog();

            if (result == System.Windows.Forms.DialogResult.OK)
            {
                inputPath.Text = openFileDialog.FileName;
            }


        }

        private void inputSearchFolder_Click(object sender, RoutedEventArgs e)
        {
            using (var fbd = new FolderBrowserDialog())
            {
                DialogResult result = fbd.ShowDialog();

                if (result == System.Windows.Forms.DialogResult.OK && !string.IsNullOrWhiteSpace(fbd.SelectedPath))
                {
                    inputPath.Text = fbd.SelectedPath;
                }
            }
        }

        private void generateClick(object sender, RoutedEventArgs e)
        {
            String filepath = inputPath.Text;
            
            if(File.Exists(filepath)){
                try
                {
                   ReportGenerator generator = new ReportGenerator();

                    generator.setResizeDimension(dimensionCombobox.Text)
                             .setResizeIfSmaller(resizeSmallerCheckbox.IsChecked.Value)
                             .setResizeScreenshots(resizeCheckbox.IsChecked.Value)
                             .setResizeSize(Int32.Parse(resizeTextbox.Text))
                             .generateReport(filepath);
                }
                    catch (Exception ex)
                {
                    DialogUtils.showExceptionDialog("Exception occured while generating the Report", ex);
                }
            }
            else
            {
                System.Windows.Forms.MessageBox.Show("Please select a valid File.");
            }

        }



        private void resizeCheckBox_Checked(object sender, RoutedEventArgs e)
        {
            if (resizeTextbox != null)
            {
                resizeTextbox.IsEnabled = true;
                dimensionCombobox.IsEnabled = true;
                resizeSmallerCheckbox.IsEnabled = true;
            }
        }

        private void resizeCheckBox_Unchecked(object sender, RoutedEventArgs e)
        {
            if (resizeTextbox != null)
            {
                resizeTextbox.IsEnabled = false;
                dimensionCombobox.IsEnabled = false;
                resizeSmallerCheckbox.IsEnabled = false;
            }
        }


        private void dimensionComboboxChanged(object sender, System.Windows.Controls.SelectionChangedEventArgs e)
        {

        }
    }
}
