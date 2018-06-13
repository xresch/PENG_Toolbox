using Microsoft.Office.Interop.Word;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms
    ;

namespace TrueLogReporter
{


    class WordUtils
    {
        //Create a missing variable for missing value
        private static object MISSING = System.Reflection.Missing.Value;

        public static object STYLE_DOC_TITLE = WdBuiltinStyle.wdStyleTitle;
        public static object STYLE_HEADING_TOC = WdBuiltinStyle.wdStyleTocHeading;
        public static object STYLE_HEADING_1 = WdBuiltinStyle.wdStyleHeading1;
        public static object STYLE_HEADING_2 = WdBuiltinStyle.wdStyleHeading2;
        public static object STYLE_BODY_TEXT = WdBuiltinStyle.wdStyleBodyText;
        public static object STYLE_LIST_BULLET = WdBuiltinStyle.wdStyleListBullet;

        public static WdOutlineLevel OUTLINE_LEVEL_1 = Microsoft.Office.Interop.Word.WdOutlineLevel.wdOutlineLevel1;
        public static WdOutlineLevel OUTLINE_LEVEL_2 = Microsoft.Office.Interop.Word.WdOutlineLevel.wdOutlineLevel2;
        public static WdOutlineLevel OUTLINE_LEVEL_TEXT = Microsoft.Office.Interop.Word.WdOutlineLevel.wdOutlineLevelBodyText;

        public static Document createWordDocument()
        {
            Document document = null;
            try
            {
                //Create an instance for word app
                Microsoft.Office.Interop.Word.Application winword = new Microsoft.Office.Interop.Word.Application();

                //Set status for word application is to be visible or not.
                winword.Visible = false;

                //Create a new document
                document = winword.Documents.Add(ref MISSING, ref MISSING, ref MISSING, ref MISSING);
               
            }
            catch (Exception ex)
            {
                System.Windows.Forms.MessageBox.Show(ex.Message);
            }

            return document;

        }

        public static TableOfContents addTableOfContents(Document doc)
        {
            Paragraph p = WordUtils.addParagraph(doc, "     ", STYLE_BODY_TEXT);
            Range insertRange = doc.Range(doc.Content.End - 3, doc.Content.End - 2);

            TableOfContents toc = doc.TablesOfContents.Add(insertRange);

            toc.Update();

            return toc;
        }
        


        public static Paragraph addParagraph(Document document, string text, object style)
        {
            Microsoft.Office.Interop.Word.Paragraph paragraph = document.Content.Paragraphs.Add(ref MISSING);

            //Microsoft.Office.Interop.Word.WdBuiltinStyle.wdStyleHeading1;
            paragraph.Range.Text = text;
            paragraph.set_Style(style);
            paragraph.OutlineLevel = getOutlineLevelForStyle(style);
           
            paragraph.Range.InsertParagraphAfter();

            return paragraph;
        }

        public static Paragraph addBulletPointWithBoldStart(Document document, string startText, string delimiter, string endText)
        {
            String text = startText + delimiter + " " + endText; 
            Paragraph para = addParagraph(document, text, STYLE_LIST_BULLET);

            int objStart = para.Range.Start - text.Count() -1;
            int objEnd = objStart + text.IndexOf(delimiter)+1;

            Console.WriteLine("addBulletPointWithBoldStart - start:" + objStart + ", end: " + objEnd);
            Console.WriteLine("addBulletPointWithBoldStart - doc start:" + document.Content.Start + ", doc end: " + document.Content.End);

            Range rngBold = document.Range(objStart, objEnd);
            rngBold.Bold = 1;

            return para;

        }

        public static WdOutlineLevel getOutlineLevelForStyle(object style)
        {
            if      ( style.Equals(STYLE_BODY_TEXT) )         { return OUTLINE_LEVEL_TEXT; }
            else if ( style.Equals(STYLE_HEADING_1) )         { return OUTLINE_LEVEL_1;    }
            else if (style.Equals(STYLE_HEADING_2))           { return OUTLINE_LEVEL_2;  }

            return OUTLINE_LEVEL_TEXT;
        }



        public static void addWordImage(Document doc, Image image)
        {
            Paragraph p =  WordUtils.addParagraph(doc, "    ", STYLE_BODY_TEXT);
            Range insertRange = doc.Range(doc.Content.End - 3, doc.Content.End - 2);

            Clipboard.SetImage(image);
            insertRange.Paste();

            Paragraph p2 = WordUtils.addParagraph(doc, " ", STYLE_BODY_TEXT);

        }

        
    }
}
