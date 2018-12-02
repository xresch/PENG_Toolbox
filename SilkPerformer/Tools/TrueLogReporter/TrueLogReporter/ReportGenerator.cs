using Microsoft.Office.Interop.Word;
using System;
using System.Collections.Generic;
using System.IO.Compression;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;
using System.Xml;
using static TrueLogReporter.ImageUtils;

namespace TrueLogReporter
{
    class ReportGenerator
    {

        public static TextBox CONSOLE_AREA = null;

        private bool doResizeScreenshots = true;
        private bool doResizeIfSmaller = true;
        private int resizeSizePixel = 600;
        private Dimension resizeDimension = Dimension.Width;

        public ReportGenerator()
        {
        }

        public ReportGenerator setResizeScreenshots(bool doResize)
        {
            doResizeScreenshots = doResize;
            return this;
        }

        public ReportGenerator setResizeIfSmaller(bool doResize)
        {
            doResizeIfSmaller = doResize;
            return this;
        }

        public ReportGenerator setResizeDimension(string dimension)
        {
            if (dimension.ToLower().Equals(Dimension.Height.ToString().ToLower()))
            {
                resizeDimension = Dimension.Height;
            }
            else
            {
                resizeDimension = Dimension.Width;
            }

            return this;
        }

        public ReportGenerator setResizeSize(int sizePixel)
        {
            resizeSizePixel = sizePixel;
            return this;
        }

        public static void printConsole(int level, string text)
        {
            string prefix = "";
            for (int i = 0; i < level; i++) { prefix += "--"; }

            CONSOLE_AREA.AppendText(prefix + text + "\n");
            CONSOLE_AREA.ScrollToEnd();

            //force UI update to show console entries
            System.Windows.Forms.Application.DoEvents();
        }

        public void generateReport(string filePath)
        {

            //----------------------------------------
            // Load File
            //----------------------------------------
            System.Xml.XmlDocument doc = new XmlDocument();

            String parentFolder = filePath.Substring(0, filePath.LastIndexOf(@"\") + 1);
            String mediaFolderPath = parentFolder + @"media\";

            if (filePath.Contains(".tlz"))
            {
                int random = new Random().Next();
                String systemTempFolder = Environment.GetEnvironmentVariable("TEMP", EnvironmentVariableTarget.Machine);
                String tempPath = systemTempFolder + @"\truelogreporter-" + random;
                printConsole(0, "Extract .tlz-File to:" + tempPath);

                ZipFile.ExtractToDirectory(filePath, tempPath);

                filePath = tempPath + @"\truelog\document.xlg";
                mediaFolderPath = tempPath + @"\truelog\media\";
            }

            doc.Load(filePath);

            printConsole(0, "Load File:" + parentFolder);



            //----------------------------------------
            // Get Transaction Info
            //----------------------------------------

            XmlNodeList transactionNodes = doc.GetElementsByTagName("Transaction");

            string scriptPath = "";
            string scriptName = "";
            string timestamp = "";

            foreach (XmlNode transactionNode in transactionNodes)
            {
                XmlElement element = transactionNode as XmlElement;

                if (element.HasAttribute("file"))
                {
                    scriptPath = element.GetAttribute("file");
                    scriptName = scriptPath.Substring(scriptPath.LastIndexOf(@"\") + 1);
                    scriptName = scriptName.Replace(".bdf", "");
                }
                if (element.HasAttribute("time"))
                {
                    timestamp = element.GetAttribute("time");
                }

            }

            printConsole(1, "ScriptName:" + scriptName + ", Timestamp: " + timestamp);

            //----------------------------------------
            // Prepare Word
            //----------------------------------------

            Document word = WordUtils.createWordDocument();
            WordUtils.addParagraph(word, "Use Case Description", WordUtils.STYLE_DOC_TITLE);

            WordUtils.addParagraph(word, "Table Of Contents", WordUtils.STYLE_HEADING_TOC);
            TableOfContents toc = WordUtils.addTableOfContents(word);

            //----------------------------------------
            // Introduction
            //----------------------------------------
            WordUtils.addParagraph(word, "\fIntroduction", WordUtils.STYLE_HEADING_1);
            WordUtils.addParagraph(word, "This document was generated using result files from our testing tool Silk Performer. " +
                "In this section you find a description of how to read this documentation.", WordUtils.STYLE_BODY_TEXT);

            WordUtils.addBulletPointWithBoldStart(word, "Section Title", ":", "The section title is the name of the transaction timer.");
            WordUtils.addBulletPointWithBoldStart(word, "Measure Start", ":", "The location the response time measurement starts");
            WordUtils.addBulletPointWithBoldStart(word, "Measure Stop", ":", "The location the response time measurement stops.");
            WordUtils.addBulletPointWithBoldStart(word, "Method", ":", "The automation method executed by our testing tool. Tells you the action which is simulated.");
            WordUtils.addBulletPointWithBoldStart(word, "Locator", ":", "The XPath-Locator which references an element in the HTML document.");
            WordUtils.addBulletPointWithBoldStart(word, "Red Rectangle in Screenshots", ":", "The element which is referenced by the locator");
            WordUtils.addBulletPointWithBoldStart(word, "Green Square", ":", "The location of the mouse or a mouse click");


            //----------------------------------------
            // Use Case Header
            //----------------------------------------
            WordUtils.addParagraph(word, "\fUse Case " + scriptName, WordUtils.STYLE_HEADING_1);
            WordUtils.addParagraph(word, "Script: " + scriptPath, WordUtils.STYLE_BODY_TEXT);
            WordUtils.addParagraph(word, "Timestamp: " + timestamp, WordUtils.STYLE_BODY_TEXT);

            string pageBreak = "";
            //-------------------------------------
            // Iterate over nodes
            XmlNodeList nodeList = doc.SelectNodes("//SilkPerformerLog/*");
            int indentLevel = 2;
            foreach (XmlNode node in nodeList)
            {

                //-------------------------------
                // Skip Methods
                if (node.Name.Equals("BrowserApicall")
                    &&
                    (node.InnerText.Contains("BrowserStart")
                     || node.InnerText.Contains("BrowserSetReplayBehavior")
                     || node.InnerText.Contains("BrowserGetActiveWindow")
                     || node.InnerText.Contains("BrowserSetAuthentication")
                    ))
                {
                    continue;
                }

                //-------------------------------
                // MeasureStart Values
                if (node.Name.Equals("CommonApicall")
                    && node.InnerText.Contains("MeasureStart"))
                {
                    string transactionName = (node.Attributes["ApiHandle"] != null) ? node.Attributes["ApiHandle"].Value : "N/A";

                    printConsole(0, "\n\n");
                    printConsole(indentLevel, "===== Measure Start =====");
                    indentLevel += 1;

                    printConsole(indentLevel, "TransactionName: " + transactionName);
                    WordUtils.addParagraph(word, pageBreak + transactionName, WordUtils.STYLE_HEADING_2);
                    WordUtils.addParagraph(word, "=========== Measure Start ===========", WordUtils.STYLE_BODY_TEXT);

                    //don't do a page break for the first use case
                    pageBreak = "\f";
                }

                //-------------------------------
                // MeasureStop Values
                if (node.Name.Equals("CommonApicall")
                    && node.InnerText.Contains("MeasureStop"))
                {
                    WordUtils.addParagraph(word, "=========== Measure Stop ===========\n\n", WordUtils.STYLE_BODY_TEXT);
                    indentLevel -= 1;
                    printConsole(indentLevel, "===== Measure Stop =====");

                }



                //-------------------------------
                // Browser Actions
                if (node.Name.Equals("BrowserApicall")
                    && node.InnerText != null)
                {
                    String method = node.InnerText.Trim() + "()";
                    Microsoft.Office.Interop.Word.Paragraph paragraph = WordUtils.addParagraph(word, "Method:\t" + method, WordUtils.STYLE_BODY_TEXT);
                    printConsole(indentLevel, "Method - " + method);

                    //-------------------------------
                    // Get ApiCall Props
                    string id = node.Attributes["id"].Value;
                    XmlNodeList apiPropList = doc.SelectNodes(@"//ApiProp[ contains(@apicall,'[" + id + "]') ]");

                    foreach (XmlNode apinode in apiPropList)
                    {
                        string propName = apinode.Attributes["propName"].Value;

                        //-------------------------------
                        // Print Locator
                        if (propName.Equals("Locator"))
                        {
                            WordUtils.addParagraph(word, "Locator:\t" + apinode.InnerText.Trim(), WordUtils.STYLE_BODY_TEXT);
                            printConsole(indentLevel, "Locator - " + apinode.InnerText.Trim());
                        }

                        //-------------------------------
                        // Add Screenshot
                        if (propName.Equals("Bmp_0_0"))
                        {
                            System.Drawing.Image image = null;

                            if (apinode.Attributes["r:id"] != null)
                            {
                                //< ApiProp id = "149" apicall = "//*/*[147]" propId = "0" propName = "Bmp_0_0" propMode = "bin" r:id = "rId149" ></ ApiProp >
                                String rid = apinode.Attributes["r:id"].Value;
                                String screenshotPath = mediaFolderPath + rid + ".Bmp_0_0.png";

                                image = ImageUtils.getImageFromPath(screenshotPath);
                            }
                            else if (apinode.InnerText.Trim().Count() > 64) //only do if there is base 64 data inside
                            {
                                image = ImageUtils.getImageFromBase64String(apinode.InnerText.Trim());
                            }

                            if (image != null)
                            {
                                String rectCoordinates = "0,0,0,0";
                                String mouseCoordinates = "0,0,0,0";
                                foreach (XmlNode screenshotnode in apiPropList)
                                {
                                    string subPropName = screenshotnode.Attributes["propName"].Value;

                                    if (subPropName.Equals("Border"))
                                    {
                                        rectCoordinates = screenshotnode.InnerText;
                                        ImageUtils.addRectangle(image, rectCoordinates);
                                    }
                                    if (subPropName.Equals("MouseAction"))
                                    {
                                        mouseCoordinates = screenshotnode.InnerText;
                                        ImageUtils.addMouseCursor(image, mouseCoordinates);
                                    }
                                }

                                if (doResizeScreenshots)
                                {
                                   image = ImageUtils.resizeImageFitToSize(image, resizeDimension, resizeSizePixel, doResizeIfSmaller);
                                }
                                WordUtils.addWordImage(word, image);
                            }
                        }

                    }
                }
            }

            printConsole(0, "Update Table Of Contents");
            toc.Update();

            word.Application.Visible = true;
        }

    }
}
