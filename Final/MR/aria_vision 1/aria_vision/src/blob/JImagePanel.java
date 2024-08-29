package blob;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * A panel on which images are drawn.
 * @author David Bull
 * @version 1.0, 24/10/03
 **/

public class JImagePanel extends JPanel implements SwingConstants, Scrollable
{
	// [+]CONSTANTS AND CLASS VARIABLES:
	private BufferedImage image;
	private float scaleFactor = 1.0f;

   /**
	* Creates a image panel.
	**/
	public JImagePanel()
	{
		super(true);
		image = null;
	}

   /**
	* Paint method to draw the image.
	* @param g
	**/
	public void paint(Graphics g)
	{
		super.paint(g);
		if(image!=null)
		{
			g.drawImage(image, 0, 0, (int)(image.getWidth()*scaleFactor),  (int)(image.getHeight()*scaleFactor), null);
		}
	}

   /**
	* Changes the scale factor for the image.
	* When the scale factor is changed the image is automatically repainted.
	* @param scaleFactor the new scale factor.
	**/
	public void setScaleFactor(float scaleFactor)
	{
		this.scaleFactor = scaleFactor;
		Dimension dim = new Dimension((int)(image.getWidth()*scaleFactor),  (int)(image.getHeight()*scaleFactor));
		setMinimumSize(dim);
		setPreferredSize(dim);
		setMaximumSize(dim);
		repaint();
		revalidate();
	}

   /**
	* Returns the scale factor tor the image.
	* @return the scale factor tor the image.
	**/
	public float getScaleFactor()
	{
		return(scaleFactor);
	}

   /**
	* Returns a copy of the current image.
	* @return a copy of the current image.
	**/
	public BufferedImage getImage()
	{
		synchronized(image)
		{
			BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
			for(int y=0 ; y<image.getHeight() ; y++) // Scan rows.
            for(int x=0 ; x<image.getWidth()  ; x++) // Scan pixels in current row.
            newImage.setRGB(x,y,image.getRGB(x,y));	 // Copy pixel.
			return(newImage);
		}
	}

   /**
	* Loads a new image.
	* @param image the new image.
	**/
	public void setImage(BufferedImage image)
	{
		synchronized (image)
		{
			this.image = image;
			if(image == null) scaleFactor = 1f;
			Dimension dim = new Dimension((int)(getImageWidth()*scaleFactor),  (int)(getImageHeight()*scaleFactor));
			setMinimumSize(dim);
			setPreferredSize(dim);
			setMaximumSize(dim);
			repaint();
			revalidate();
		}
	}

   /**
	* Returns the width of the current image, or 0 if no image loaded.
	* @return the width of the current image, or 0 if no image loaded.
	**/
	public int getImageWidth()
	{
		if(image != null)
        return(image.getWidth());
		return(0);
	}

   /**
	* Returns the height of the current image, or 0 if no image loaded.
	* @return the height of the current image, or 0 if no image loaded.
	**/
	public int getImageHeight()
	{
		if(image != null)
        return(image.getHeight());
		return(0);
	}

   /**
	* Returns the dimensions of the current image.
	* @return the dimensions of the current image.
	**/
	public Dimension getPreferredScrollableViewportSize()
	{
		return(new Dimension((int)(getImageWidth()*scaleFactor), (int)(getImageHeight()*scaleFactor)));
	}

   /**
	* Causes the panel to scroll in 1/8 image sized blocks.
	* @return 1/8 image size;
	**/
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		if(orientation==HORIZONTAL)
        return((int)(getImageWidth()  * scaleFactor) / 8);
		return((int)(getImageHeight() * scaleFactor) / 8);
	}

   /**
	* Returns <code>false</code>.
	* @return <code>false</code>.
	**/
	public boolean getScrollableTracksViewportHeight()
	{
		 return(false);
	}

   /**
	* Returns <code>false</code>.
	* @return <code>false</code>.
	**/
	public boolean getScrollableTracksViewportWidth()
	{
		return(false);
	}

   /**
	* Causes the panel to scroll in 1/16 image sized blocks.
	* @return 1/16 image size;
	**/
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		if(orientation==HORIZONTAL)
        return((int)(getImageWidth()  * scaleFactor) / 16);
		return((int)(getImageHeight() * scaleFactor) / 16);
	}
}