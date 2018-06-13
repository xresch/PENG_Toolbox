using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;



namespace com.peng.toolbox.resultextractor
{

    /*
     * Parses Usecase-, Usergroup-, Timer-Name and 
     * Percentile values out of Silk XML outputfile
     */
    public class XMLParserMain
    {
        

        [STAThread]
        public static void Main(string[] args)
        {

            // Start forms
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new XMLParserDialog());

        }

 
        

    }

   
}





