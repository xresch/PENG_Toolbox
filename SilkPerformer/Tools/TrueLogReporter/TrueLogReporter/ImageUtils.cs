using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TrueLogReporter
{
    class ImageUtils
    {
        public enum Dimension { Width, Height };
        /***********************************************************************************
         * 
         ***********************************************************************************/
        public static Image getImageFromPath(string imagePath)
        {
            Image image = Image.FromFile(imagePath);

            return image;
        }

        /***********************************************************************************
         * 
         ***********************************************************************************/
        public static Image getImageFromBase64String(string base64)
        {
            Byte[] bitmapData = Convert.FromBase64String(FixBase64ForImage(base64));
            System.IO.MemoryStream streamBitmap = new System.IO.MemoryStream(bitmapData);
            Image bitImage = Image.FromStream(streamBitmap);

            return bitImage;
        }

        /***********************************************************************************
         * 
         ***********************************************************************************/
        public static string FixBase64ForImage(string Image)
        {
            System.Text.StringBuilder sbText = new System.Text.StringBuilder(Image, Image.Length);
            sbText.Replace("\r\n", String.Empty); sbText.Replace(" ", String.Empty);
            return sbText.ToString();
        }

        /***********************************************************************************
         * 
         ***********************************************************************************/
        public static void addRectangle(Image image, string trueLogCoordinates)
        {

            using (Graphics g = Graphics.FromImage(image))
            {

                //----------------------
                // Fill Rectangle
                Color customColor = Color.FromArgb(50, Color.Red);
                SolidBrush shadowBrush = new SolidBrush(customColor);
                g.FillRectangle(shadowBrush, getRectangle(trueLogCoordinates));

                //----------------------
                // Stroke Rectangle
                int[] coords = getCoordinatesInt(trueLogCoordinates);

                Point topLeft = new Point(coords[0], coords[1]);
                Point topRight = new Point(coords[0]+coords[2], coords[1]);
                Point bottomLeft = new Point(coords[0], coords[1] + coords[3]);
                Point bottomRight = new Point(coords[0] + coords[2], coords[1] + coords[3]);

                Pen pen = new Pen(Color.Red, 2.0F);

                g.DrawLines(pen, new Point[] {topLeft, topRight, bottomRight, bottomLeft, topLeft });
                //----------------------
                // Dispose
                g.Dispose();
            }

        }

        /***********************************************************************************
         * 
         ***********************************************************************************/
        public static void addMouseCursor(Image image, string trueLogCoordinates)
        {

            using (Graphics g = Graphics.FromImage(image))
            {

                //----------------------
                // Stroke Rectangle
                Point mouseLocation = getMousePoint(trueLogCoordinates);
                int offset = 5;
                Point top = new Point(mouseLocation.X, mouseLocation.Y+ offset);
                Point bottom = new Point(mouseLocation.X, mouseLocation.Y- offset);
                Point left = new Point(mouseLocation.X- offset, mouseLocation.Y);
                Point right = new Point(mouseLocation.X+ offset, mouseLocation.Y);

                Pen pen = new Pen(Color.FromArgb(180,0,255,0) , 2.0F);


                //g.DrawLines(pen, new Point[] { top, bottom });
                //g.DrawLines(pen, new Point[] { left, right });
                g.DrawLines(pen, new Point[] { left, top, right, bottom, left });

                //----------------------
                // Dispose
                g.Dispose();
            }

        }

        /***********************************************************************************
         * 
         ***********************************************************************************/
        private static RectangleF getRectangle(string trueLogCoordinates)
        {

            float[] coordinates = getCoordinates(trueLogCoordinates); 
            RectangleF rectangle = new RectangleF(coordinates[0], coordinates[1], coordinates[2], coordinates[3]) ;

            return rectangle;
        }

        /***********************************************************************************
         * 
         ***********************************************************************************/
        private static Point getMousePoint(string trueLogCoordinates)
        {
            
            String[] splitted = trueLogCoordinates.Split(',');
            Point point = new Point(int.Parse(splitted[0]), int.Parse(splitted[1]));
            
            Console.WriteLine("Mouse: "+point.X +", "+ point.Y);

            return point;
        }

        /***********************************************************************************
         * 
         ***********************************************************************************/
        private static float[] getCoordinates(string trueLogCoordinates)
        {

            String[] splitted = trueLogCoordinates.Split(',');

            float[] floats = new float[8];
            for(int i = 0; i < splitted.Length && i < 8 ; i++)
            {
                
                floats[i] = float.Parse(splitted[i]);
            }

            return floats;
        }
        /***********************************************************************************
         * 
         ***********************************************************************************/
        private static int[] getCoordinatesInt(string trueLogCoordinates)
        {

            String[] splitted = trueLogCoordinates.Split(',');

            int[] intArray = new int[8];
            for (int i = 0; i < splitted.Length && i < 8; i++)
            {

                intArray[i] = int.Parse(splitted[i]);
            }

            return intArray;
        }

        /***********************************************************************************
         * 
         ***********************************************************************************/
        public static Image resizeImageFitToSize(Image image, Dimension dimension, int targetSizePixel, bool growIfSmaller)
        {
            //--------------------------------
            // Evaluate Size
            int currentSize = 0;
            float percentage = 0;
            if (dimension == Dimension.Width)
            {
                currentSize = image.Width;
            }
            else{
                currentSize = image.Height;
            }
            
            percentage = (float)targetSizePixel / currentSize;
            Console.WriteLine("percentage:" + percentage);
            //--------------------------------
            // Evaluate Size
            if (currentSize > targetSizePixel
            || (currentSize < targetSizePixel && growIfSmaller) )
            { 
                return resizeImage(image, percentage);
            }

            return image;
        }

        /***********************************************************************************
         * 
         ***********************************************************************************/
        public static Image resizeImage(Image image, float percentage)
        {
            int newWidth =  (int)(image.Width * percentage);
            int newHeight = (int)(image.Height * percentage);

            ReportGenerator.printConsole(1, "Resize Image - From: "+ image.Width +"/"+ image.Height+
                                                           ", To: " + newWidth + "/" + newHeight+
                                                           ", Percentage: "+percentage+"%");

            var destRect = new Rectangle(0, 0, newWidth, newHeight);
            var destImage = new Bitmap(newWidth, newHeight);

            destImage.SetResolution(image.HorizontalResolution, image.VerticalResolution);

            using (var graphics = Graphics.FromImage(destImage))
            {
                graphics.CompositingMode = CompositingMode.SourceCopy;
                graphics.CompositingQuality = CompositingQuality.HighQuality;
                graphics.InterpolationMode = InterpolationMode.HighQualityBicubic;
                graphics.SmoothingMode = SmoothingMode.HighQuality;
                graphics.PixelOffsetMode = PixelOffsetMode.HighQuality;

                using (var wrapMode = new ImageAttributes())
                {
                    wrapMode.SetWrapMode(WrapMode.TileFlipXY);
                    graphics.DrawImage(image, destRect, 0, 0, image.Width, image.Height, GraphicsUnit.Pixel, wrapMode);
                }
            }

            return (Image)destImage;
        }

    }
}
