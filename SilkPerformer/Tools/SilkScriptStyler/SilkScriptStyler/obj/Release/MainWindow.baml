   M S B A M L   `   `   `  ���� M  ISilk Script Styler, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null3clr-namespace:SilkScriptStylerSilkScriptStyler  !    SilkScriptStyler.MainWindow   \ XPresentationFramework, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35R NWindowsBase, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35W SPresentationCore, Version=4.0.0.0, Culture=neutral, PublicKeyToken=31bf3856ad364e35D 9http://schemas.microsoft.com/winfx/2006/xaml/presentation    R NSystem.Xaml, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089:x,http://schemas.microsoft.com/winfx/2006/xaml     @d2http://schemas.microsoft.com/expression/blend/2008     Jmc;http://schemas.openxmlformats.org/markup-compatibility/2006     0localclr-namespace:SilkScriptStyler     -     �Name$  Silk_Script_Styler�� � Title$ Silk Script Styler��$��556.929��$��958.909��.���  � RowDefinitions �� $
��195* ��� $
��331* � � ColumnDefinitions �� $
��260* ��� $
��257* �.+��� $��Silk Script Formatter��$
��Left=�$��32��$��	10,10,0,0q�$	��Top=�$	��200��$
��Bold"�$��16(��� $
��Auto=�$��Stretch=�$��Stretch=�$��10,10,10,10q�$��1��.���� -    ��Name$ 	inputText�� �� TextWrapping$ NoWrap=� �� AcceptsReturn .  �� 
AcceptsTab . �� $
��Auto=�$��Stretch=�$��Stretch=�$��10,10,10,10q�$��1��$��1��.���� -   $ 
outputText��$ NoWrap=� .  . �� -    ��Name$ browserClickCheckbox��$6��0Replace comments on BrowserClick() with variable��$
��Left=�$��
10,175,0,0q�$	��Top=�	 d� 	IsChecked$
	 TrueY�$��15��$	��284���� -   $ replaceThinkTimesCheckbox��$��Replace Think Times��$
��Left=�$��
10,155,0,0q�$	��Top=�$
	 TrueY�$��15��$	��127���� -   $ removeBlankLinesCheckbox��$��Remove Blank Lines��$
��Left=�$��
10,135,0,0q�$	��Top=�$
	 TrueY�$��15��$	��124���� $��Start Method:��$
��Left=�$��23��$��	10,46,0,0q�$	��Top=�$��90���� $��Stop Method:��$
��Left=�$��23��$��	10,70,0,0q�$	��Top=�$��90���� -   $ startMethodTextBox��$
��Left=�$��22��$��
100,46,0,0q�$
 Wrap=�$��CustomStart��$	��Top=�$	��189���� -   $ stopMethodTextBox��$
��Left=�$��22��$��
100,70,0,0q�$
 Wrap=�$��
CustomStop��$	��Top=�$	��189���� $��	Variable:��$
��Left=�$��23��$��	10,93,0,0q�$	��Top=�$��90���� -	   $ variableTextBox��$
��Left=�$��22��$��
100,93,0,0q�$
 Wrap=�$��sMeasure��$	��Top=�$	��189��