package blob;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.event.*;

/**
 * A GUI for the Colour Tracker. The live camera feed is displayed allowing the user to select a region of colour for tracking.
 * Six sliders for minimum and maximum HSI values can also be used to set the colour to be tracked. A second image displays
 * the processed image. The bottom third of the window contains a message console.
 *
 * @author David Bull
 * @version 1.0, 20/03/2004
 **/

public class ColorTrackerGUI extends JFrame implements WindowListener, ChangeListener, SelectionListener, ImageUpdateListener
{
	// [+]CONSTANTS AND CLASS VARIABLES:
	private ColorTracker app;
	private JSelectionImagePanel liveImage;
	private JImagePanel processedImage;
	private JSlider hueLoSlider, hueHiSlider, satLoSlider, satHiSlider, intLoSlider, intHiSlider;
	private JTextArea console;

   /**
	* Creates a new Colour Tracker GUI.
	* @param app the Colour Tracker application.
	**/
	public ColorTrackerGUI(ColorTracker app)
	{
		// [+]Set Up GUI:
		super(ColorTracker.APP_NAME);
		this.app = app;
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		Container c = getContentPane();

		// [+]Set look and feel to that of the current OS:
		try{ javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName()); }
		catch(Exception e) { }


		// [+]Draw widgets:
		JPanel visionPanel = new JPanel();
		GridBagLayout gridBag = new GridBagLayout();
		visionPanel.setLayout(gridBag);

		JLabel liveStreamLabel = new JLabel("Live Stream:");
		liveStreamLabel.setFont(new Font(liveStreamLabel.getName(), Font.BOLD, liveStreamLabel.getFont().getSize()+2));
		gridBag.setConstraints(liveStreamLabel, new GridBagConstraints(0,0,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
		visionPanel.add(liveStreamLabel);

		liveImage = new JSelectionImagePanel();
		liveImage.addSelectionListener(this);
		liveImage.setImage(app.imageProcessor.getLiveImage());
		gridBag.setConstraints(liveImage, new GridBagConstraints(0,1,2,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,10,0,0),0,0));
		visionPanel.add(liveImage);

		JLabel processedImageLabel = new JLabel("Processed Image:");
		processedImageLabel.setFont(new Font(processedImageLabel.getName(), Font.BOLD, processedImageLabel.getFont().getSize()+2));
		gridBag.setConstraints(processedImageLabel, new GridBagConstraints(2,0,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,40,0,0),0,0));
		visionPanel.add(processedImageLabel);

		processedImage = new JImagePanel();
		processedImage.setImage(app.imageProcessor.getProcessedImage());
		gridBag.setConstraints(processedImage, new GridBagConstraints(2,1,2,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,50,0,0),0,0));
		visionPanel.add(processedImage);

		JLabel colourSettingsLabel = new JLabel("Colour Settings");
		colourSettingsLabel.setFont(new Font(colourSettingsLabel.getName(), Font.BOLD, colourSettingsLabel.getFont().getSize()+2));
		gridBag.setConstraints(colourSettingsLabel, new GridBagConstraints(0,2,4,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(20,0,0,0),0,0));
		visionPanel.add(colourSettingsLabel);

		JLabel minHSIlabel = new JLabel("Minimum");
		minHSIlabel.setFont(new Font(minHSIlabel.getName(), Font.BOLD, minHSIlabel.getFont().getSize()+1));
		gridBag.setConstraints(minHSIlabel, new GridBagConstraints(1,3,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
		visionPanel.add(minHSIlabel);

		JLabel hueLoLabel = new JLabel("Hue: ");
		gridBag.setConstraints(hueLoLabel, new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
		visionPanel.add(hueLoLabel);

		hueLoSlider = new JSlider(0,360,0);
		hueLoSlider.setMajorTickSpacing(60);
		hueLoSlider.setMinorTickSpacing(10);
		hueLoSlider.setPaintTicks(true);
		hueLoSlider.addChangeListener(this);
		gridBag.setConstraints(hueLoSlider, new GridBagConstraints(1,4,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
		visionPanel.add(hueLoSlider);

		JLabel satLoLabel = new JLabel("Sat: ");
		gridBag.setConstraints(satLoLabel, new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
		visionPanel.add(satLoLabel);

		satLoSlider = new JSlider(0,100,0);
		satLoSlider.setMajorTickSpacing(10);
		satLoSlider.setMinorTickSpacing(5);
		satLoSlider.setPaintTicks(true);
		satLoSlider.addChangeListener(this);
		gridBag.setConstraints(satLoSlider, new GridBagConstraints(1,5,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
		visionPanel.add(satLoSlider);

		JLabel intLoLabel = new JLabel("Int: ");
		gridBag.setConstraints(intLoLabel, new GridBagConstraints(0,6,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
		visionPanel.add(intLoLabel);

		intLoSlider = new JSlider(0,100,0);
		intLoSlider.setMajorTickSpacing(10);
		intLoSlider.setMinorTickSpacing(5);
		intLoSlider.setPaintTicks(true);
		intLoSlider.addChangeListener(this);
		gridBag.setConstraints(intLoSlider, new GridBagConstraints(1,6,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
		visionPanel.add(intLoSlider);

		JLabel maxHSIlabel = new JLabel("Maximum");
		maxHSIlabel.setFont(new Font(maxHSIlabel.getName(), Font.BOLD, maxHSIlabel.getFont().getSize()+1));
		gridBag.setConstraints(maxHSIlabel, new GridBagConstraints(3,3,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
		visionPanel.add(maxHSIlabel);

		JLabel hueHiLabel = new JLabel("Hue: ");
		gridBag.setConstraints(hueHiLabel, new GridBagConstraints(2,4,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,50,0,0),0,0));
		visionPanel.add(hueHiLabel);

		hueHiSlider = new JSlider(0,360,0);
		hueHiSlider.setMajorTickSpacing(60);
		hueHiSlider.setMinorTickSpacing(10);
		hueHiSlider.setPaintTicks(true);
		hueHiSlider.addChangeListener(this);
		gridBag.setConstraints(hueHiSlider, new GridBagConstraints(3,4,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
		visionPanel.add(hueHiSlider);

		JLabel satHiLabel = new JLabel("Sat: ");
		gridBag.setConstraints(satHiLabel, new GridBagConstraints(2,5,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,50,0,0),0,0));
		visionPanel.add(satHiLabel);

		satHiSlider = new JSlider(0,100,0);
		satHiSlider.setMajorTickSpacing(10);
		satHiSlider.setMinorTickSpacing(5);
		satHiSlider.setPaintTicks(true);
		satHiSlider.addChangeListener(this);
		gridBag.setConstraints(satHiSlider, new GridBagConstraints(3,5,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
		visionPanel.add(satHiSlider);

		JLabel intHiLabel = new JLabel("Int: ");
		gridBag.setConstraints(intHiLabel, new GridBagConstraints(2,6,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,50,0,0),0,0));
		visionPanel.add(intHiLabel);

		intHiSlider = new JSlider(0,100,0);
		intHiSlider.setMajorTickSpacing(10);
		intHiSlider.setMinorTickSpacing(5);
		intHiSlider.setPaintTicks(true);
		intHiSlider.addChangeListener(this);
		gridBag.setConstraints(intHiSlider, new GridBagConstraints(3,6,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
		visionPanel.add(intHiSlider);

		console = new JTextArea();
		JScrollPane consoleScroller = new JScrollPane(console);
		consoleScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		consoleScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, visionPanel, consoleScroller);
		splitPane.setDividerLocation(450);
		c.add(splitPane);

		// [+]Add an action listener to the Image Processor:
		app.imageProcessor.addImageUpdateListener(this);

		// [+]Set size, center window on screen and show:
		addWindowListener(this);
		setSize(800, 600);
		Dimension screen = getToolkit().getScreenSize();
		setLocation(screen.width/2 - getWidth()/2,  screen.height/2 - getHeight()/2);
		setVisible(true);
	}

   /**
	* Appends a message to the console.
	* @param message the message.
	**/
	public void log(String message)
	{
		console.append(message+"\n");
		console.select(console.getText().length(), 0);
	}

   /**
	* Called when a selection box is drawn.
	* Scan the selected region of the image and determine the lowest and highest HSI values.
	* @param e
	**/
	public void selectionPerformed(SelectionEvent e)
	{
		int rgb=0, hsi=0, h=0, s=0, i=0, loHue=360, loSat=100, loInt=100, hiHue=0, hiSat=0, hiInt=0;
		BufferedImage image = ((JSelectionImagePanel)(e.getSource())).getImage();
		app.log("Selection box drawn (X1="+e.getX1()+", Y1="+e.getY1()+", X2="+e.getX2()+", Y2="+e.getY2()+")");
		for(int x=Math.min(e.getX1(), e.getX2()); x<=Math.max(e.getX1(), e.getX2()); x++) // Scan rows.
        for(int y=Math.min(e.getY1(), e.getY2()); y<=Math.max(e.getY1(), e.getY2()); y++) // Scan pixels in current row.
        {
            rgb = image.getRGB(x,y);					// Get the rgb value for the current pixel.
            hsi = ColorSpaceConversions.RGBtoHSI(rgb);	// Convert into hsi.
            h = (hsi & 0xFFFF0000) >> 16;				// Split into hue.
            s = (hsi & 0x0000FF00) >> 8;				// Saturation.
            i = (hsi & 0x000000FF);						// Intensity.
            if (h<loHue) loHue=h;
            if (h>hiHue) hiHue=h;
            if (s<loSat) loSat=s;
            if (s>hiSat) hiSat=s;
            if (i<loInt) loInt=i;
            if (i>hiInt) hiInt=i;
        }
		hueLoSlider.setValue(loHue);
		satLoSlider.setValue(loSat);
		intLoSlider.setValue(loInt);
		hueHiSlider.setValue(hiHue);
		satHiSlider.setValue(hiSat);
		intHiSlider.setValue(hiInt);
		app.log("  Lowest HSI = " + loHue + ", " + loSat + ", " + loInt);
		app.log("  Highest HSI = " + hiHue + ", " + hiSat + ", " + hiInt);
	}

	public void imagesUpdated()
	{
		liveImage.setImage(app.imageProcessor.getLiveImage());
		processedImage.setImage(app.imageProcessor.getProcessedImage());
	}

   /**
	* Called when a Slider is adjusted.
	**/
	public void stateChanged(ChangeEvent e)
	{
		if(e.getSource().equals(hueLoSlider))
		{
			if(hueLoSlider.getValue()>hueHiSlider.getValue())
            hueLoSlider.setValue(hueHiSlider.getValue());
			app.imageProcessor.setHueLo(hueLoSlider.getValue());
		}
		else
        if(e.getSource().equals(satLoSlider))
		{
			if(satLoSlider.getValue()>satHiSlider.getValue())
            satLoSlider.setValue(satHiSlider.getValue());
			app.imageProcessor.setSatLo(satLoSlider.getValue());
		}
		else
        if(e.getSource().equals(intLoSlider))
		{
			if(intLoSlider.getValue()>intHiSlider.getValue())
            intLoSlider.setValue(intHiSlider.getValue());
			app.imageProcessor.setIntLo(intLoSlider.getValue());
		}
		else
        if(e.getSource().equals(hueHiSlider))
		{
			if(hueHiSlider.getValue()<hueLoSlider.getValue())
            hueHiSlider.setValue(hueLoSlider.getValue());
			app.imageProcessor.setHueHi(hueHiSlider.getValue());
		}
		else
        if(e.getSource().equals(satHiSlider))
		{
			if(satHiSlider.getValue()<satLoSlider.getValue())
            satHiSlider.setValue(satLoSlider.getValue());
			app.imageProcessor.setSatHi(satHiSlider.getValue());
		}
		else
        if(e.getSource().equals(intHiSlider))
		{
			if(intHiSlider.getValue()<intLoSlider.getValue())
            intHiSlider.setValue(intLoSlider.getValue());
			app.imageProcessor.setIntHi(intHiSlider.getValue());
		}
	}

	// [+]WINDOWER LISTENER STUFF:
	public void windowClosing(WindowEvent windowevent) { setVisible(false); app.shutdown(); }
	public void windowActivated(WindowEvent e)   { }
	public void windowClosed(WindowEvent e)      { }
	public void windowDeactivated(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowIconified(WindowEvent e)   { }
	public void windowOpened(WindowEvent e)      { }
}
