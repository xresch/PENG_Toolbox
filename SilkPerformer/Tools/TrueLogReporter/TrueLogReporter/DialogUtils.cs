using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TrueLogReporter
{
    class DialogUtils
    {

        public static void showExceptionDialog(String message, Exception e)
        {
            String text = "Message: " + message +
                          "\n\nException Message: " + e.Message +
                          "\n\nException Class: " + e.GetType().Name +
                          "\n\nStackTrace:\n" + e.StackTrace;

            System.Windows.Forms.MessageBox.Show(text, "Error Occured");
        }
    }
}
