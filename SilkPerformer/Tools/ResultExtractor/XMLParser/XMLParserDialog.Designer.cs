namespace com.peng.toolbox.resultextractor
{
    partial class XMLParserDialog
    {
        /// <summary>
        /// Erforderliche Designervariable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Verwendete Ressourcen bereinigen.
        /// </summary>
        /// <param name="disposing">True, wenn verwaltete Ressourcen gelöscht werden sollen; andernfalls False.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Vom Windows Form-Designer generierter Code

        /// <summary>
        /// Erforderliche Methode für die Designerunterstützung.
        /// Der Inhalt der Methode darf nicht mit dem Code-Editor geändert werden.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(XMLParserDialog));
            this.openFileDialog = new System.Windows.Forms.OpenFileDialog();
            this.inputFileName = new System.Windows.Forms.TextBox();
            this.inputSearch = new System.Windows.Forms.Button();
            this.outputSearch = new System.Windows.Forms.Button();
            this.outputFileName = new System.Windows.Forms.TextBox();
            this.timerFilter = new System.Windows.Forms.TextBox();
            this.startParsing = new System.Windows.Forms.Button();
            this.cancel = new System.Windows.Forms.Button();
            this.ovrFileLabel = new System.Windows.Forms.Label();
            this.resultFileLabel = new System.Windows.Forms.Label();
            this.percentileLabel = new System.Windows.Forms.Label();
            this.TimerFilterLabel = new System.Windows.Forms.Label();
            this.saveFileDialog = new System.Windows.Forms.SaveFileDialog();
            this.pictureBox1 = new System.Windows.Forms.PictureBox();
            this.pictureBox2 = new System.Windows.Forms.PictureBox();
            this.toolTipTimeFilter = new System.Windows.Forms.ToolTip(this.components);
            this.pictureBox5 = new System.Windows.Forms.PictureBox();
            this.toolTipPercentile = new System.Windows.Forms.ToolTip(this.components);
            this.pictureBox3 = new System.Windows.Forms.PictureBox();
            this.pictureBox4 = new System.Windows.Forms.PictureBox();
            this.pictureBox6 = new System.Windows.Forms.PictureBox();
            this.label5 = new System.Windows.Forms.Label();
            this.label6 = new System.Windows.Forms.Label();
            this.SeparatorLabel = new System.Windows.Forms.Label();
            this.separatorTextBox = new System.Windows.Forms.TextBox();
            this.availablePercentilesBox = new System.Windows.Forms.TextBox();
            this.percentileComboBox = new System.Windows.Forms.ComboBox();
            this.metricCombo = new System.Windows.Forms.ComboBox();
            this.pictureBox7 = new System.Windows.Forms.PictureBox();
            this.label1 = new System.Windows.Forms.Label();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox2)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox5)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox3)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox4)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox6)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox7)).BeginInit();
            this.SuspendLayout();
            // 
            // openFileDialog
            // 
            this.openFileDialog.FileName = "openFileDialog";
            // 
            // inputFileName
            // 
            this.inputFileName.Location = new System.Drawing.Point(105, 95);
            this.inputFileName.Name = "inputFileName";
            this.inputFileName.Size = new System.Drawing.Size(336, 20);
            this.inputFileName.TabIndex = 0;
            this.inputFileName.TextChanged += new System.EventHandler(this.inputFileName_TextChanged);
            // 
            // inputSearch
            // 
            this.inputSearch.Location = new System.Drawing.Point(450, 95);
            this.inputSearch.Name = "inputSearch";
            this.inputSearch.Size = new System.Drawing.Size(30, 20);
            this.inputSearch.TabIndex = 1;
            this.inputSearch.Text = ".....";
            this.inputSearch.UseVisualStyleBackColor = true;
            this.inputSearch.Click += new System.EventHandler(this.inputSearch_Click);
            // 
            // outputSearch
            // 
            this.outputSearch.Location = new System.Drawing.Point(450, 125);
            this.outputSearch.Name = "outputSearch";
            this.outputSearch.Size = new System.Drawing.Size(30, 20);
            this.outputSearch.TabIndex = 3;
            this.outputSearch.Text = ".....";
            this.outputSearch.UseVisualStyleBackColor = true;
            this.outputSearch.Click += new System.EventHandler(this.outputSearch_Click);
            // 
            // outputFileName
            // 
            this.outputFileName.Location = new System.Drawing.Point(105, 125);
            this.outputFileName.Name = "outputFileName";
            this.outputFileName.Size = new System.Drawing.Size(336, 20);
            this.outputFileName.TabIndex = 2;
            // 
            // timerFilter
            // 
            this.timerFilter.Location = new System.Drawing.Point(105, 321);
            this.timerFilter.Name = "timerFilter";
            this.timerFilter.Size = new System.Drawing.Size(336, 20);
            this.timerFilter.TabIndex = 5;
            this.timerFilter.Text = ".*";
            // 
            // startParsing
            // 
            this.startParsing.Location = new System.Drawing.Point(308, 400);
            this.startParsing.Name = "startParsing";
            this.startParsing.Size = new System.Drawing.Size(82, 23);
            this.startParsing.TabIndex = 6;
            this.startParsing.Text = "Parse";
            this.startParsing.UseVisualStyleBackColor = true;
            this.startParsing.Click += new System.EventHandler(this.startParsing_Click);
            // 
            // cancel
            // 
            this.cancel.Location = new System.Drawing.Point(404, 400);
            this.cancel.Name = "cancel";
            this.cancel.Size = new System.Drawing.Size(75, 23);
            this.cancel.TabIndex = 7;
            this.cancel.Text = "Close";
            this.cancel.UseVisualStyleBackColor = true;
            this.cancel.Click += new System.EventHandler(this.cancel_Click);
            // 
            // ovrFileLabel
            // 
            this.ovrFileLabel.AutoSize = true;
            this.ovrFileLabel.Location = new System.Drawing.Point(35, 95);
            this.ovrFileLabel.Name = "ovrFileLabel";
            this.ovrFileLabel.Size = new System.Drawing.Size(52, 13);
            this.ovrFileLabel.TabIndex = 8;
            this.ovrFileLabel.Text = "OVR File:";
            this.ovrFileLabel.Click += new System.EventHandler(this.label1_Click);
            // 
            // resultFileLabel
            // 
            this.resultFileLabel.AutoSize = true;
            this.resultFileLabel.Location = new System.Drawing.Point(35, 125);
            this.resultFileLabel.Name = "resultFileLabel";
            this.resultFileLabel.Size = new System.Drawing.Size(59, 13);
            this.resultFileLabel.TabIndex = 9;
            this.resultFileLabel.Text = "Result File:";
            this.resultFileLabel.Click += new System.EventHandler(this.label2_Click);
            // 
            // percentileLabel
            // 
            this.percentileLabel.AutoSize = true;
            this.percentileLabel.Location = new System.Drawing.Point(35, 184);
            this.percentileLabel.Name = "percentileLabel";
            this.percentileLabel.Size = new System.Drawing.Size(57, 13);
            this.percentileLabel.TabIndex = 10;
            this.percentileLabel.Text = "Percentile:";
            // 
            // TimerFilterLabel
            // 
            this.TimerFilterLabel.AutoSize = true;
            this.TimerFilterLabel.Location = new System.Drawing.Point(35, 321);
            this.TimerFilterLabel.Name = "TimerFilterLabel";
            this.TimerFilterLabel.Size = new System.Drawing.Size(61, 13);
            this.TimerFilterLabel.TabIndex = 11;
            this.TimerFilterLabel.Text = "Timer Filter:";
            this.TimerFilterLabel.Click += new System.EventHandler(this.label4_Click);
            // 
            // pictureBox1
            // 
            this.pictureBox1.Image = global::com.peng.toolbox.resultextractor.Properties.Resources.icon_question_mark;
            this.pictureBox1.Location = new System.Drawing.Point(10, 321);
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.Size = new System.Drawing.Size(17, 13);
            this.pictureBox1.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox1.TabIndex = 12;
            this.pictureBox1.TabStop = false;
            this.toolTipTimeFilter.SetToolTip(this.pictureBox1, "Regular expression filter results by timer names. (e.g. UC_, SV_ ...)");
            this.pictureBox1.Click += new System.EventHandler(this.pictureBox1_Click);
            // 
            // pictureBox2
            // 
            this.pictureBox2.Image = global::com.peng.toolbox.resultextractor.Properties.Resources.icon_question_mark;
            this.pictureBox2.Location = new System.Drawing.Point(10, 184);
            this.pictureBox2.Name = "pictureBox2";
            this.pictureBox2.Size = new System.Drawing.Size(17, 13);
            this.pictureBox2.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox2.TabIndex = 13;
            this.pictureBox2.TabStop = false;
            this.toolTipPercentile.SetToolTip(this.pictureBox2, "Comma separated percentile values, \r\ne.g. 50, 90, 95 or 99");
            this.pictureBox2.Click += new System.EventHandler(this.pictureBox2_Click);
            // 
            // pictureBox5
            // 
            this.pictureBox5.Image = global::com.peng.toolbox.resultextractor.Properties.Resources.icon_question_mark;
            this.pictureBox5.Location = new System.Drawing.Point(10, 351);
            this.pictureBox5.Name = "pictureBox5";
            this.pictureBox5.Size = new System.Drawing.Size(17, 13);
            this.pictureBox5.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox5.TabIndex = 18;
            this.pictureBox5.TabStop = false;
            this.toolTipTimeFilter.SetToolTip(this.pictureBox5, "The separator used in the result CSV file.");
            // 
            // pictureBox3
            // 
            this.pictureBox3.Image = global::com.peng.toolbox.resultextractor.Properties.Resources.icon_question_mark;
            this.pictureBox3.Location = new System.Drawing.Point(10, 95);
            this.pictureBox3.Name = "pictureBox3";
            this.pictureBox3.Size = new System.Drawing.Size(17, 13);
            this.pictureBox3.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox3.TabIndex = 14;
            this.pictureBox3.TabStop = false;
            this.toolTipPercentile.SetToolTip(this.pictureBox3, "The OVR-File found in the test result folder. \r\nTo generate this file you have to" +
        " open the rest report in Silk performer at least once.");
            this.pictureBox3.Click += new System.EventHandler(this.pictureBox3_Click);
            // 
            // pictureBox4
            // 
            this.pictureBox4.Image = global::com.peng.toolbox.resultextractor.Properties.Resources.icon_question_mark;
            this.pictureBox4.Location = new System.Drawing.Point(10, 125);
            this.pictureBox4.Name = "pictureBox4";
            this.pictureBox4.Size = new System.Drawing.Size(17, 13);
            this.pictureBox4.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox4.TabIndex = 15;
            this.pictureBox4.TabStop = false;
            this.toolTipPercentile.SetToolTip(this.pictureBox4, "Choose a folder and file name were the parsing results will be stored in CSV form" +
        "at.");
            // 
            // pictureBox6
            // 
            this.pictureBox6.Image = global::com.peng.toolbox.resultextractor.Properties.Resources.icon_question_mark;
            this.pictureBox6.Location = new System.Drawing.Point(83, 211);
            this.pictureBox6.Name = "pictureBox6";
            this.pictureBox6.Size = new System.Drawing.Size(17, 13);
            this.pictureBox6.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox6.TabIndex = 22;
            this.pictureBox6.TabStop = false;
            this.toolTipPercentile.SetToolTip(this.pictureBox6, "Select an OVR file and this box will show you the available percentiles.");
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Font = new System.Drawing.Font("Verdana", 14.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label5.Location = new System.Drawing.Point(8, 9);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(185, 23);
            this.label5.TabIndex = 16;
            this.label5.Text = "Result Extractor";
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(9, 49);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(415, 26);
            this.label6.TabIndex = 17;
            this.label6.Text = "This tool is used to parse results (especially 90% values) from Silk Performer re" +
    "sult files. \r\nHover over the blue question mark icons to get more help.";
            this.label6.Click += new System.EventHandler(this.label6_Click);
            // 
            // SeparatorLabel
            // 
            this.SeparatorLabel.AutoSize = true;
            this.SeparatorLabel.Location = new System.Drawing.Point(35, 351);
            this.SeparatorLabel.Name = "SeparatorLabel";
            this.SeparatorLabel.Size = new System.Drawing.Size(56, 13);
            this.SeparatorLabel.TabIndex = 19;
            this.SeparatorLabel.Text = "Separator:";
            // 
            // separatorTextBox
            // 
            this.separatorTextBox.Location = new System.Drawing.Point(105, 351);
            this.separatorTextBox.Name = "separatorTextBox";
            this.separatorTextBox.Size = new System.Drawing.Size(336, 20);
            this.separatorTextBox.TabIndex = 20;
            this.separatorTextBox.Text = ";";
            // 
            // availablePercentilesBox
            // 
            this.availablePercentilesBox.BackColor = System.Drawing.SystemColors.GradientInactiveCaption;
            this.availablePercentilesBox.Location = new System.Drawing.Point(106, 211);
            this.availablePercentilesBox.Multiline = true;
            this.availablePercentilesBox.Name = "availablePercentilesBox";
            this.availablePercentilesBox.Size = new System.Drawing.Size(335, 101);
            this.availablePercentilesBox.TabIndex = 21;
            // 
            // percentileComboBox
            // 
            this.percentileComboBox.FormattingEnabled = true;
            this.percentileComboBox.Items.AddRange(new object[] {
            "90",
            "95",
            "99",
            "90, 95, 99",
            "25, 50, 75, 100",
            "50, 75, 80, 90, 95, 99",
            "10, 20, 30, 40, 50, 60, 70, 80, 90, 100",
            "All Available Percentiles"});
            this.percentileComboBox.Location = new System.Drawing.Point(106, 184);
            this.percentileComboBox.Name = "percentileComboBox";
            this.percentileComboBox.Size = new System.Drawing.Size(335, 21);
            this.percentileComboBox.TabIndex = 23;
            this.percentileComboBox.Text = "90";
            this.percentileComboBox.SelectedIndexChanged += new System.EventHandler(this.percentileComboBox_SelectedIndexChanged);
            // 
            // metricCombo
            // 
            this.metricCombo.FormattingEnabled = true;
            this.metricCombo.Items.AddRange(new object[] {
            "Response time[s]",
            "Action time[s]",
            "DOM interactive[s]",
            "DOM complete[s]",
            "First paint[s]",
            "Load end[s]",
            "Request data sent[kB]",
            "Requests sent",
            "Responses received",
            "Http cache hits",
            "Http cookies received",
            "Http cookies sent",
            "Http hits",
            "Transactions",
            "Trans. ok[s]",
            "Trans.(busy) ok[s]"});
            this.metricCombo.Location = new System.Drawing.Point(106, 154);
            this.metricCombo.Name = "metricCombo";
            this.metricCombo.Size = new System.Drawing.Size(335, 21);
            this.metricCombo.TabIndex = 26;
            this.metricCombo.Text = "Response time[s]";
            this.metricCombo.SelectedIndexChanged += new System.EventHandler(this.comboBox1_SelectedIndexChanged);
            // 
            // pictureBox7
            // 
            this.pictureBox7.Image = global::com.peng.toolbox.resultextractor.Properties.Resources.icon_question_mark;
            this.pictureBox7.Location = new System.Drawing.Point(10, 154);
            this.pictureBox7.Name = "pictureBox7";
            this.pictureBox7.Size = new System.Drawing.Size(17, 13);
            this.pictureBox7.SizeMode = System.Windows.Forms.PictureBoxSizeMode.Zoom;
            this.pictureBox7.TabIndex = 25;
            this.pictureBox7.TabStop = false;
            this.toolTipPercentile.SetToolTip(this.pictureBox7, "The metric which should be extrated.");
            this.pictureBox7.Click += new System.EventHandler(this.pictureBox7_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(35, 154);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(66, 13);
            this.label1.TabIndex = 24;
            this.label1.Text = "Metric Type:";
            this.label1.Click += new System.EventHandler(this.label1_Click_1);
            // 
            // XMLParserDialog
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(491, 436);
            this.Controls.Add(this.metricCombo);
            this.Controls.Add(this.pictureBox7);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.percentileComboBox);
            this.Controls.Add(this.pictureBox6);
            this.Controls.Add(this.availablePercentilesBox);
            this.Controls.Add(this.separatorTextBox);
            this.Controls.Add(this.SeparatorLabel);
            this.Controls.Add(this.pictureBox5);
            this.Controls.Add(this.label6);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.pictureBox4);
            this.Controls.Add(this.pictureBox3);
            this.Controls.Add(this.pictureBox2);
            this.Controls.Add(this.pictureBox1);
            this.Controls.Add(this.TimerFilterLabel);
            this.Controls.Add(this.percentileLabel);
            this.Controls.Add(this.resultFileLabel);
            this.Controls.Add(this.ovrFileLabel);
            this.Controls.Add(this.cancel);
            this.Controls.Add(this.startParsing);
            this.Controls.Add(this.timerFilter);
            this.Controls.Add(this.outputSearch);
            this.Controls.Add(this.outputFileName);
            this.Controls.Add(this.inputSearch);
            this.Controls.Add(this.inputFileName);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "XMLParserDialog";
            this.Text = "Result Extractor";
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox2)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox5)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox3)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox4)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox6)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox7)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.OpenFileDialog openFileDialog;
        private System.Windows.Forms.TextBox inputFileName;
        private System.Windows.Forms.Button inputSearch;
        private System.Windows.Forms.Button outputSearch;
        private System.Windows.Forms.TextBox outputFileName;
        private System.Windows.Forms.TextBox timerFilter;
        private System.Windows.Forms.Button startParsing;
        private System.Windows.Forms.Button cancel;
        private System.Windows.Forms.Label ovrFileLabel;
        private System.Windows.Forms.Label resultFileLabel;
        private System.Windows.Forms.Label percentileLabel;
        private System.Windows.Forms.Label TimerFilterLabel;
        private System.Windows.Forms.SaveFileDialog saveFileDialog;
        private System.Windows.Forms.PictureBox pictureBox1;
        private System.Windows.Forms.ToolTip toolTipTimeFilter;
        private System.Windows.Forms.PictureBox pictureBox2;
        private System.Windows.Forms.ToolTip toolTipPercentile;
        private System.Windows.Forms.PictureBox pictureBox3;
        private System.Windows.Forms.PictureBox pictureBox4;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.PictureBox pictureBox5;
        private System.Windows.Forms.Label SeparatorLabel;
        private System.Windows.Forms.TextBox separatorTextBox;
        private System.Windows.Forms.TextBox availablePercentilesBox;
        private System.Windows.Forms.PictureBox pictureBox6;
        private System.Windows.Forms.ComboBox percentileComboBox;
        private System.Windows.Forms.ComboBox metricCombo;
        private System.Windows.Forms.PictureBox pictureBox7;
        private System.Windows.Forms.Label label1;
    }
}