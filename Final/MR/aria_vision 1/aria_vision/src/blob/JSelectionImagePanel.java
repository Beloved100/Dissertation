package blob;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Extends <code>JImagePanel</code> to allow drawing of boxes around sections of the image.
 * @author David Bull
 * @version 1.0, 20/03/2004
 **/

public class JSelectionImagePanel extends JImagePanel implements MouseListener, MouseMotionListener
{
	// [+]CONSTANTS AND CLASS VARIABLES:
	public static final Color BOX_COLOUR = Color.RED;
	private Vector listeners = new Vector();
	private int X1=0, Y1=0, X2=0, Y2=0;
	private boolean selecting = false;

	public JSelectionImagePanel()
	{
		super();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		if(selecting)
		{
			g.setColor(BOX_COLOUR);
			if((X1<X2) && (Y1<Y2)) g.drawRect(X1, Y1, X2-X1, Y2-Y1);
			else
            if((X1<X2) && (Y1>Y2)) g.drawRect(X1, Y2, X2-X1, Y1-Y2);
			else
            if((X1>X2) && (Y1<Y2)) g.drawRect(X2, Y1, X1-X2, Y2-Y1);
			else
            if((X1>X2) && (Y1>Y2)) g.drawRect(X2, Y2, X1-X2, Y1-Y2);
		}
	}

	// [+]MOUSE LISTENERS:
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e)  { }
	public void mousePressed(MouseEvent e)
	{
		X1 = e.getX();
		Y1 = e.getY();
		X2 = X1;
		Y2 = Y1;
		selecting=true;
	}

    public void mouseReleased(MouseEvent e)
	{
		selecting=false;
		fireSelectionEvent();
	}

	public void mouseDragged(MouseEvent e)
	{
		if (e.getX() > super.getImageWidth()) X2 = super.getImageWidth() - 1;
		else
        if(e.getX() < 0)                      X2 = 0;
		else                                  X2 = e.getX();

		if(e.getY() > super.getImageHeight()) Y2 = super.getImageHeight() - 1;
		else
        if(e.getY() < 0)                      Y2 = 0;
		else                                  Y2 = e.getY();

		repaint();
	}

	public void mouseMoved(MouseEvent e) { }
	public void addSelectionListener(SelectionListener listener)    { listeners.add(listener);    }
	public void removeSelectionListener(SelectionListener listener) { listeners.remove(listener); }
	private void fireSelectionEvent()
	{
		if((X1!=X2) && (Y1!=Y2))
		{
			for(int i=0; i<listeners.size(); i++)
            ((SelectionListener)listeners.elementAt(i)).selectionPerformed(new SelectionEvent(this, X1, Y1, X2, Y2));
		}
	}
}
