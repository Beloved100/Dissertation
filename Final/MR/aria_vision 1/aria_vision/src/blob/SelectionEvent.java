package blob;

/**
 * A class to store information about a selection event.
 * Stores the top-left and bottom-right coordinates of the selection,
 * as well as the object that the selection was drawn on.
 *
 * @author David Bull
 * @version 1.0, 20/03/2004
 **/

public class SelectionEvent
{
	// [+]CONSTANTS AND CLASS VARIABLES:
	private Object source;
	private int X1, Y1, X2, Y2;

   /**
	* @param source
	* @param X1
	* @param Y1
	* @param X2
	* @param Y2
	**/
	public SelectionEvent(Object source, int X1, int Y1, int X2, int Y2)
	{
		this.source = source;
		this.X1 = X1;
		this.Y1 = Y1;
		this.X2 = X2;
		this.Y2 = Y2;
	}

	public Object getSource() { return(source); }
	public int getX1() { return(X1); }
	public int getY1() { return(Y1); }
	public int getX2() { return(X2); }
	public int getY2() { return(Y2); }
}
