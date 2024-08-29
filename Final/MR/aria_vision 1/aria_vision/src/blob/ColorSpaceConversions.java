package blob;

/**
 * This class contains static methods to convert between various colour spaces.
 * Currently this class contains only one method to convert an RGB value to
 * an HSI value.
 *
 * @author David Bull
 * @version 1.0, 25/10/03
 **/
public class ColorSpaceConversions
{
   /**
	* <p>
	* Converts an RGB value to HSI. The <code>rgb</code> parameter is assumed to be
	* in the default RGB colour model and default sRGB colourspace. The resulting HSI value
	* is encoded into an integer where the least significant byte contains the intentisy, the second
	* least significant byte contains the saturation and the remaining bytes contains the hue.
	* Hue values are between 0-360. Saturation and Intensity values are between 0-100.
	* </p><p>
	* Code to get the HSI values back is:<br>
	* <code>
	* int hsi = RGBtoHSI(rgb);<br>
	* int h = (hsi & 0xFFFF0000) >> 16;<br>
	* int s = (hsi & 0x0000FF00) >> 8;<br>
	* int i = (hsi & 0x000000FF);<br>
	* </code>
	* </p>
	*
	* @param rgb an integer pixel in the default RGB colour model and default sRGB colourspace.
	* @return an integer pixel representing the HSI value.
	**/
	public static int RGBtoHSI(int rgb)
	{
		// [+]Initialise HSI values:
		double h = 0;
		double s = 0;
		double i = 0;

		// [+]Separate RGB values:
		int r = (rgb & 0x00FF0000) >> 16;
		int g = (rgb & 0x0000FF00) >> 8;
		int b = (rgb & 0x000000FF);

		// [+]Calculate maximum, and minimum of the RGB component values:
		int max, min;
		if((r>g) && (r>b))
		{
			max = r;
			min = Math.min(g, b);
		}
		else
		{
			if(g>b)
			{
				max = g;
				min = Math.min(r,b);
			}
			else
			{
				max = b;
				min = Math.min(r,g);
			}
		}

		// [+]Calculate intensity:
		i=Math.round(((float)max/255)*100);
		if(i==0) return((int)(((int)h << 16) + ((int)s << 8) + (int)i)); // No intensity - Colour is black.

		// [+]Calculate saturation:
		if(max==min) return((int)(((int)h << 16) + ((int)s << 8) + (int)i)); // No saturation - Colour is grey.
		s = Math.round((((float)max/255) - ((float)min/255))/((float)max/255)*100);

		// [+]Calculate hue:
		double dbl_hue = Math.acos((0.5*((r-g)+(r-b)))/(Math.sqrt(Math.pow((r-g),2)+(r-b)*(g-b))));
		if(b>g) dbl_hue = (2*Math.PI)-dbl_hue;
		dbl_hue = Math.toDegrees(dbl_hue);
		h = Math.round(Math.round(dbl_hue));
		return((int)(((int)h << 16) + ((int)s << 8) + (int)i));
	}
}
