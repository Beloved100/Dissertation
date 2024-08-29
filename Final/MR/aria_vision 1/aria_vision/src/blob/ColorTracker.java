package blob;

/**
 * A Java Colour Tracker!
 * The main application class. This class initialises the camera,
 * the image processor, the internet server, and the GUI.
 * http://www.uk-dave.com/projects/misc/java-colour-tracker/
 *
 * @author David Bull
 * @version 1.0, 20/03/2004
 **/

public class ColorTracker
{
	// [+]CONSTANTS AND CLASS VARIABLES:
	public static final String APP_NAME = "Colour Tracker";
    public ImageProcessor imageProcessor;	 // The image processor.
    public ColorTrackerGUI colourTrackerGUI; // The GUI.

   /**
	* Creates a new Vision application.
	**/
	public ColorTracker() throws Exception
	{
		imageProcessor = new ImageProcessor(this);
		colourTrackerGUI = new ColorTrackerGUI(this);
		log("Ready...");
	}

   /**
	* Shuts down the Vision application.
	**/
	protected void shutdown()
	{
		imageProcessor.shutdown();
		//cameraController.shutdown();
		System.exit(0);
	}

   /**
	* Logs a message
	* @param message the message.
	**/
	public void log(String message)
	{
		if(colourTrackerGUI != null)
		colourTrackerGUI.log(message);
	}

   /**
    * Get the COG coordinates.
    **/
    public int getBlobX() { return(imageProcessor.cogX); }
    public int getBlobY() { return(imageProcessor.cogY); }

   /**
    * Get the image size.
    **/
    public int getWidth()  { return(imageProcessor.liveImage.getWidth());  }
    public int getHeight() { return(imageProcessor.liveImage.getHeight()); }
}
