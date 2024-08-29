package blob;

/**
 * A class to represent the bounding box of the objectr being tracked.
 * @author David Bull
 * @version 1.0, 21/03/2004
 **/

public class BoundingBox
{
	// [+]CONSTANTS AND CLASS VARIABLES:
	private int X1=0, Y1=0, X2=0, Y2=0;

	// [+]CONSTRUCTORS:
	public BoundingBox() {}

   /**
    * @param X1
    * @param Y1
    * @param X2
    * @param Y2
    * @throws IllegalArgumentException
    **/
	public BoundingBox(int X1, int Y1, int X2, int Y2) throws IllegalArgumentException
	{
		if((X1 <= X2) && (Y1 <= Y2))
		{
			this.X1 = X1;
			this.Y1 = Y1;
			this.X2 = X2;
			this.Y2 = Y2;
		}
		else
        throw new IllegalArgumentException();
	}

	// [+]METHODS:
   /**
	*
	* @return
	*/
	public int getX1() { return(X1); }
	public int getY1() { return(Y1); }
	public int getX2() { return(X2); }
	public int getY2() { return(Y2); }
	public int[] getCenter() { return(new int[] {(X2 - X1) / 2, (Y2 - Y1) / 2});         }
	public boolean isEmpty() { return((X1 == 0) && (Y1 == 0) && (X2 == 0) && (Y2 == 0)); }

   /**
	* @param X1
	* @param Y1
	* @param X2
	* @param Y2
	* @throws IllegalArgumentException
	**/
	public void setCoords(int X1, int Y1, int X2, int Y2) throws IllegalArgumentException
	{
		if((X1 <= X2) && (Y1 <= Y2))
		{
			this.X1 = X1;
			this.Y1 = Y1;
			this.X2 = X2;
			this.Y2 = Y2;
		}
		else
        throw new IllegalArgumentException();
	}

   /**
	* @return
	**/
	public boolean equals(Object o)
	{
		if(o instanceof BoundingBox)
		{
			BoundingBox b = (BoundingBox)o;
			if (b.getX1()!=X1) return false;
			if (b.getY1()!=Y1) return false;
			if (b.getX2()!=X2) return false;
			if (b.getY2()!=Y2) return false;
			return true;
		}
		else
        return(false);
	}
}
