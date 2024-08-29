package blob;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;

/**
 * Performs the blob colouring algorithm on frames
 * from the camera and locates the largest object
 * of the required colour.
 *
 * @author David Bull
 * @version 2.0, 30/11/03
 **/

public class ImageProcessor
{
	// [+]CONSTANTS AND CLASS VARIABLES:
	public static final int FRAME_GRAB_FREQUENCY = 10;
	public static final int EMPTY_REGION = 0x00000000;
	public static final Color BOX_COLOR = Color.RED;

    public int cogX = 0;
    public int cogY = 0;

	private ColorTracker app;
	public  BufferedImage liveImage;
	private BufferedImage processedImage;
	private VideoUpdateThread videoUpdateThread;

	private Vector imageListeners = new Vector();
	private Vector boundingBoxListeners = new Vector();

	private int hueLo = 0;
	private int hueHi = 0;
	private int satLo = 0;
	private int satHi = 0;
	private int intLo = 0;
	private int intHi = 0;

	private BoundingBox objectBoundingBox = new BoundingBox();

   /**
	* Creates a new Image Processor.
	* @param app the Colour Tracker application.
	**/
	public ImageProcessor(ColorTracker app)
	{
		this.app = app;
		//liveImage = cvLoadImage("data/images/theoL01.jpg", 1).getBufferedImage();
        liveImage = new BufferedImage(640, 480, 1); // [640, 480], [448, 336], [314, 235], [160, 160]
		processedImage = new BufferedImage(liveImage.getWidth(), liveImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		videoUpdateThread = new VideoUpdateThread();
		videoUpdateThread.start();
	}

   /**
	* Shutsdown the Video Update Thread.
	**/
	public void shutdown()
	{
		videoUpdateThread.shutdown();
	}

   /**
	* Returns the live image from the video capture devive.
	* @return live image from the video capture devive.
	**/
	public BufferedImage getLiveImage()
	{
		synchronized (liveImage)
		{
			return liveImage;
		}
	}

   /**
	* Returns the processed image.
	* @return the processed image.
	**/
	public BufferedImage getProcessedImage()
	{
		synchronized (processedImage)
		{
			return processedImage;
		}
	}

   /**
	* @return
	**/
	public BoundingBox getObjectBoundingBox() { return objectBoundingBox; }

   /**
	* @return Returns the hueHi.
	**/
	public int getHueHi() { return hueHi; }

   /**
	* @param hueHi The hueHi to set.
	* @throws IllegalArgumentException
	**/
	public void setHueHi(int hueHi) throws IllegalArgumentException
    {
        if((hueHi >= 0) && (hueHi <= 360) && (hueHi >= hueLo)) this.hueHi = hueHi;
        else                                                   throw new IllegalArgumentException();
    }

   /**
	* @return Returns the hueLo.
	**/
	public int getHueLo() { return hueLo; }

   /**
	* @param hueLo The hueLo to set.
	* @throws IllegalArgumentException
	**/
	public void setHueLo(int hueLo) throws IllegalArgumentException
    {
        if((hueLo >= 0) && (hueLo <= 360) && (hueLo <= hueHi)) this.hueLo = hueLo;
        else                                                   throw new IllegalArgumentException();
    }

   /**
	* @return Returns the intHi.
	**/
	public int getIntHi() { return intHi; }

   /**
	* @param intHi The intHi to set.
	* @throws IllegalArgumentException
	**/
	public void setIntHi(int intHi) throws IllegalArgumentException
    {
        if((intHi >= 0) && (intHi <= 100) && (intHi >= intLo)) this.intHi = intHi;
        else                                                   throw new IllegalArgumentException();
    }

   /**
	* @return Returns the intLo.
	**/
	public int getIntLo() { return intLo; }

   /**
	* @param intLo The intLo to set.
	* @throws IllegalArgumentException
	**/
	public void setIntLo(int intLo) throws IllegalArgumentException
    {
        if((intLo >= 0) && (intLo <= 100) && (intLo <= intHi)) this.intLo = intLo;
        else                                                   throw new IllegalArgumentException();
    }

   /**
	* @return Returns the satHi.
	**/
	public int getSatHi() { return satHi; }

   /**
	* @param satHi The satHi to set.
	* @throws IllegalArgumentException
	**/
	public void setSatHi(int satHi) throws IllegalArgumentException
    {
        if((satHi >= 0) && (satHi <= 100) && (satHi >= satLo)) this.satHi = satHi;
        else                                                   throw new IllegalArgumentException();
    }

   /**
	* @return Returns the satLo.
	**/
	public int getSatLo() { return satLo; }

   /**
	* @param satLo The satLo to set.
	* @throws IllegalArgumentException
	**/
	public void setSatLo(int satLo) throws IllegalArgumentException
    {
        if((satLo >= 0) && (satLo <= 100) && (satLo <= satHi)) this.satLo = satLo;
        else                                                   throw new IllegalArgumentException();
    }

	// [+]LISTENER METHODS:
	public void addImageUpdateListener(ImageUpdateListener listener)    { imageListeners.add(listener);    }
	public void removeImageUpdateListener(ImageUpdateListener listener) { imageListeners.remove(listener); }
	private void fireImageUpdateEvent()
	{
		for(int i=0 ; i<imageListeners.size() ; i++)
        ((ImageUpdateListener)imageListeners.elementAt(i)).imagesUpdated();
	}

	// [+]IMAGE PROCESSING METHODS:
	private void processImage()
	{
		// [+]STEP 1: Initialise region map and other variables.
		int rgb=0, hsi=0, h=0, s=0, i=0;
		int regionMap[][] = new int[liveImage.getHeight()][liveImage.getWidth()];
		Vector regionEquivalenceTree = new Vector();
		int rgbPixelAbove=0, rgbPixelLeft=0, rgbPixelCurrent=0, regionIndex=0;
		double distLeft=0, distAbove=0;
		int mapFrom;
		Integer mapTo=new Integer(-1), prevMapTo=new Integer(-1), lowerRegion=new Integer(-1);

		// STEP 2: Show only the parts of the live image show colour matches.
		for (int y=0 ; y<liveImage.getHeight() ; y++)   // Scan rows.
        for (int x=0 ; x<liveImage.getWidth()  ; x++)   // Scan pixels in current row.
        {
            rgb = liveImage.getRGB(x, y);			    // Get the rgb value for the current pixel.
            hsi = ColorSpaceConversions.RGBtoHSI(rgb);	// Convert into hsi.
            h = (hsi & 0xFFFF0000) >> 16;				// Split into hue.
            s = (hsi & 0x0000FF00) >> 8;				// Saturation.
            i = (hsi & 0x000000FF);						// Intensity.
            if(y >= liveImage.getHeight()-2)
            {
                processedImage.setRGB(x, y, EMPTY_REGION); // The last 2 rows of pixels on my camera are blue, so ignore them
            }
            else
            {
                if((h>=hueLo) && (h<=hueHi) && (s>=satLo) && (s<=satHi) && (i>=intLo) && (i<=intHi)) processedImage.setRGB(x, y, rgb);
                else                                                                                 processedImage.setRGB(x, y, EMPTY_REGION);
            }
        }

		// STEP 3: Perform blob colouring.
		for(int y=0 ; y<processedImage.getHeight() ; y++) // Scan rows.
        for(int x=0 ; x<processedImage.getWidth()  ; x++) // Scan pixels in current row.
        {
            // [+]Get rgb values for neighbouring pixels:
            if(x!=0) rgbPixelLeft = (processedImage.getRGB(x-1, y) & 0x00FFFFFF);
            rgbPixelCurrent = (processedImage.getRGB(x, y) & 0x00FFFFFF);
            if(y!=0) rgbPixelAbove = (processedImage.getRGB(x, y-1) & 0x00FFFFFF);

            // [+]Calculate distance:
            if(x!=0)
            {
                if(((rgbPixelLeft == EMPTY_REGION) && (rgbPixelCurrent != EMPTY_REGION)) || ((rgbPixelLeft!=EMPTY_REGION) && (rgbPixelCurrent==EMPTY_REGION)))
                {
                    distLeft = 1;
                }
                else
                {
                    distLeft = 0;
                }
            }
            if(y!=0)
            {
                if(((rgbPixelAbove == EMPTY_REGION) && (rgbPixelCurrent != EMPTY_REGION)) || ((rgbPixelAbove != EMPTY_REGION) && (rgbPixelCurrent == EMPTY_REGION)))
                {
                    distAbove = 1;
                }
                else
                {
                    distAbove = 0;
                }
            }
            // check distances against threshold
            if((x==0) || (y==0))
            {
                if((x!=0) && (y==0))
                {
                    if(distLeft == 1) // CASE 1
                    {
                        // [+]Current pixel different to neighbour - assign new region:
                        regionIndex++;
                        regionMap[y][x] = regionIndex;
                        regionEquivalenceTree.add(new TreeSet());
                    }
                    else // CASE 2
                    {
                        // [+]Current pixel similar to left pixel - assign to same region as left pixel:
                        regionMap[y][x] = regionMap[y][x-1];
                    }
                }
                else
                if((x==0) && (y!=0))
                {
                    if(distAbove == 1) // CASE 1
                    {
                        // [+]Current pixel different to neighbour - assign new region:
                        regionIndex++;
                        regionMap[y][x] = regionIndex;
                        regionEquivalenceTree.add(new TreeSet());
                    }
                    else // CASE 3
                    {
                        // [+]Current pixel similar to pixel above - assign to same region as pixel above:
                        regionMap[y][x] = regionMap[y-1][x];
                    }
                }
                else
                if((x==0) && (y==0)) // First pixel in image.
                {
                    // [+]First pixel in image - assign new region number:
                    regionMap[y][x] = regionIndex;
                    regionEquivalenceTree.add(new TreeSet());
                }
            }
            else
            {
                if((distLeft==1) && (distAbove==1)) // CASE 1
                {
                    // [+]Current pixel different to neighbours - assign new region, also
                    // add new region to equivalence map and make equivalent to itself.
                    regionIndex++;
                    regionMap[y][x] = regionIndex;
                    regionEquivalenceTree.add(new TreeSet());
                }
                else
                if((distLeft==0) && (distAbove==1)) // CASE 2
                {
                    // [+]Current pixel similar to left pixel - assign to same region as left pixel:
                    regionMap[y][x] = regionMap[y][x-1];
                }
                else
                if((distLeft==1) && (distAbove==0)) // CASE 3
                {
                    // [+]Current pixel similar to pixel above - assign to same region as pixel above:
                    regionMap[y][x] = regionMap[y-1][x];
                }
                else if ((distLeft==0) && (distAbove==0)) // CASE 4
                {
                    // [+]Pixel similar to both neighbours - assign to same region as neighbours, if
                    // neighbours have different region numbers then add to region equivalence map.
                    if(regionMap[y][x-1] != regionMap[y-1][x])
                    {
                        // [+]Make equivalence pointer point to lower region number:
                        if(regionMap[y][x-1] < regionMap[y-1][x])
                        {
                            mapFrom = regionMap[y-1][x];
                            mapTo = new Integer(regionMap[y][x-1]);
                        }
                        else
                        {
                            mapFrom = regionMap[y][x-1];
                            mapTo = new Integer(regionMap[y-1][x]);
                        }
                        if(mapTo.equals(prevMapTo))
                        {
                            mapTo = lowerRegion;
                        }
                        else
                        {
                            lowerRegion = new Integer(findLowestEquivalentRegion(regionEquivalenceTree, mapTo.intValue()));
                        }
                        ((TreeSet)(regionEquivalenceTree.elementAt(mapFrom))).add(lowerRegion);
                        prevMapTo = mapTo;
                        regionMap[y][x] = lowerRegion.intValue();
                    }
                    else
                    {
                        regionMap[y][x] = regionMap[y][x-1];
                    }
                }
            }
        }

		// [+]STEP 4: Flatten equivalence map.
		flatten(regionEquivalenceTree);	// 1st pass
		flatten(regionEquivalenceTree);	// 2nd pass

		// [+]STEP 5: Re-calculate region map taking into account the region equivalence tree.
		for(int y=0 ; y<liveImage.getHeight() ; y++) // Scan rows.
        for(int x=0 ; x<liveImage.getWidth()  ; x++) // Scan pixels in current row.
        {
            TreeSet region = (TreeSet)(regionEquivalenceTree.elementAt(regionMap[y][x]));
            if(region.size() == 1)
            regionMap[y][x] = ((Integer)(region.first())).intValue();
        }

		// [+]STEP 6: Count regions.
		int regionCount=0;
		for(int j=0 ; j<regionEquivalenceTree.size() ; j++)
		{
			if(((TreeSet)(regionEquivalenceTree.elementAt(j))).size() == 0)
            regionCount++;
		}
		//app.log("Found "+regionCount+" regions");

		if(regionCount > 0)
		{
			// [+]STEP 7: Find biggest region that isnt the same colour as EMPTY_REGION.
			int regionSize[] = new int[regionIndex+2];
			for(int j=0 ; j<=regionIndex ; j++)
            {
                regionSize[j] = 0;
            }
			for(int y=0 ; y<liveImage.getHeight() ; y++) // Scan rows.
            for(int x=0 ; x<liveImage.getWidth()  ; x++) // Scan pixels in current row.
            {
                if((processedImage.getRGB(x,y) & 0x00FFFFFF) != EMPTY_REGION)
                regionSize[regionMap[y][x]]++;
            }

            int largestRegion = 0;
			int maxSize = 0;
			for(int j=0 ; j<=regionIndex ; j++)
            if(regionSize[j]>maxSize)
            {
                maxSize = regionSize[j];
                largestRegion = j;
            }
			//app.log("  Largest region is "+largestRegion+" containing "+maxSize+" pixels.");

			// [+]STEP 8: Now find the bounding coords for the largest region.
			int X1 = liveImage.getWidth(), Y1 = liveImage.getHeight(), X2=0, Y2=0;
			for(int y=0 ; y<liveImage.getHeight() ; y++) // Scan rows.
            for(int x=0 ; x<liveImage.getWidth()  ; x++) // Scan pixels in current row.
            {
                if(regionMap[y][x] == largestRegion)
                {
                    if(x<X1) X1 = x;
                    if(x>X2) X2 = x;
                    if(y<Y1) Y1 = y;
                    if(y>Y2) Y2 = y;
                }
            }

            // [+]compute the blob's Center Of Gravity (COG):
            cogX = ((X2-X1)/2) + X1;
            cogY = ((Y2-Y1)/2) + Y1;

			if((X1 != objectBoundingBox.getX1()) || (Y1 != objectBoundingBox.getY1()) || (X2 != objectBoundingBox.getX2()) || (Y2 != objectBoundingBox.getY2()))
			{
				objectBoundingBox.setCoords(X1,Y1,X2,Y2);
				//app.log("Box: " + X1 + ", " + Y1 + ", " + X2 + ", " + Y2); // Print box coords.

                app.log("COG: x=" + cogX + ", y=" + cogY); // Print cross coords.
			}

			// [+]STEP 9: Draw bounding box.
			Graphics g = processedImage.getGraphics();
			g.setColor(BOX_COLOR);
			g.drawRect(X1, Y1, X2-X1, Y2-Y1);
			g.drawLine(((X2-X1)/2) + X1, ((Y2-Y1)/2) + Y1 - 5, ((X2-X1)/2) + X1, ((Y2-Y1)/2) + Y1 + 5);
			g.drawLine(((X2-X1)/2) + X1-5, ((Y2-Y1)/2)+ Y1, ((X2-X1)/2) + X1 + 5, ((Y2-Y1)/2) + Y1);
		}
		else
		{
			if((objectBoundingBox.getX1() != 0) || (objectBoundingBox.getY1() != 0) || (objectBoundingBox.getX2() != 0) || (objectBoundingBox.getX2() != 0))
			{
				objectBoundingBox.setCoords(0, 0, 0, 0);
				app.log("Object lost!");
			}
		}
	}

   /**
    * <p>
    * Flattens a region equivalence tree.
    * Starting at the highest region, a call to <code>findLowestEquivalentRegion()</code> is made to find the
    * lowest region number that it is equivalent to. This is then propogated down the path in the tree from the
    * current region number to the lowest.
    * </p><p>
    * E.g. If 8 points to 6, which points to 4 points to 2, then starting at 8 its lowest equivalent region is 2.
    * Propogating this down the path sets 6 to 2 and then 4 to 2.
    * </p>
    * @param tree
    **/
	private static void flatten(Vector tree)
	{
		for(int i=(tree.size()-1) ; i>=0 ; i--)
		{
			int lowestEquivalentRegion = findLowestEquivalentRegion(tree, i);
			propogateLowestRegion(tree, i, lowestEquivalentRegion);
		}
	}

   /**
    * Given a region number, this method finds the lowest equivalent region number by recursively following
    * the path of equivalent regions down the tree.
    * @param tree equivalent region
    * @param region the starting region.
    * @return the lowest equivalent region.
    **/
	private static int findLowestEquivalentRegion(Vector tree, int region)
	{
		TreeSet equivalentRegions = (TreeSet)(tree.elementAt(region));
		if(equivalentRegions.size()>0)
		{
			TreeSet lowestEquivalentRegions = new TreeSet();
			Iterator it = equivalentRegions.iterator();
			while(it.hasNext())
			{
				int r = ((Integer)(it.next())).intValue();
				lowestEquivalentRegions.add(new Integer(findLowestEquivalentRegion(tree, r)));
			}
			return(((Integer)(lowestEquivalentRegions.first())).intValue());
		}
		else
		return(region);
	}

   /**
    * Given a region number, and it's lowest equivalent region, this method propogates the lowest
    * equivalent region number down the path from the starting region number to the lowest equivalent
    * region number.
    * @param tree equivalent region.
    * @param start the starting region number.
    * @param lowestEquivalentRegion the lowest equivalent region number.
    **/
	private static void propogateLowestRegion(Vector tree, int start, int lowestEquivalentRegion)
	{
		TreeSet regions = (TreeSet)(tree.elementAt(start));
		if(regions.size() > 0)
		{
			Iterator it = regions.iterator();
			while (it.hasNext())
			{
				int r = ((Integer)(it.next())).intValue();
				propogateLowestRegion(tree, r, lowestEquivalentRegion);
			}
			regions.clear();
			regions.add(new Integer(lowestEquivalentRegion));
		}
		else
        {
            if(start != lowestEquivalentRegion)
            regions.add(new Integer(lowestEquivalentRegion));
        }
	}

   /**
    * Thread to grab images from the video capture device and process the image for colour tracking.
    **/
	private class VideoUpdateThread extends Thread
	{
        //private CanvasFrame canvas = new CanvasFrame("Stitching");
        private FrameGrabber grabber = new OpenCVFrameGrabber(0);
        private boolean running = false;
		public VideoUpdateThread() { }

        private void delayMs(int ms)
        {
            try
            {
                Thread.sleep(ms);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }

		public void run()
		{
			running=true;
			try
            {
                grabber.start();
                while(running)
                {
                    synchronized(liveImage)
                    {
                        //liveImage = cvLoadImage("data/images/theoL01.jpg", 1).getBufferedImage();
                        liveImage = grabber.grab().getBufferedImage();
                        synchronized (processedImage)
                        {
                            processImage();
                        }
                    }
                    fireImageUpdateEvent();
                    delayMs(FRAME_GRAB_FREQUENCY);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
		}
		public void shutdown()
        {
            running = false;
        }
	}
}
