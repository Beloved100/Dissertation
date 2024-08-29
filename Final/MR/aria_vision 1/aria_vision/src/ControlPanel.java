import robot.Robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Theo Theodoridis.
 * Class    : ControlPanel
 * Version  : v1.0
 * Date     : © Copyright 20-01-2015
 * User     : ttheod
 * email    : ttheod@gmail.com
 * Comments : The class creates GUI used to control the robot.
 **/

public class ControlPanel extends JFrame implements ActionListener
{
    private float vel;
    private Robot robot;

    private static JLabel MainLabel = new JLabel();
    private static JFrame MainFrame = new JFrame("Control Panel");

    private static JButton empty    = new JButton("Empty");
    private static JButton forward  = new JButton("Forward");
    private static JButton backward = new JButton("Backward");
    private static JButton right    = new JButton("Right");
    private static JButton left     = new JButton("Left");
    private static JButton stop     = new JButton("Stop");
    private static JButton finish   = new JButton("Finish");
    private static JButton plot     = new JButton("Plot");
    private static JButton shutdown = new JButton("Shutdown");

   /**
    * Method     : ControlPanel::ControlPanel()
    * Purpose    : Default ControlPanel class constructor.
    * Parameters : robot : An object of Class Robot.
    *              vel   : The robot velocity.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public ControlPanel(Robot robot, float vel)
    {
        this.robot = robot;
        this.vel = vel;
    }

   /**
    * Method     : ControlPanel::ShowGUI()
    * Purpose    : To show the GUI.
    * Parameters : None.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public void ShowGUI()
    {
        empty.setLocation(0, 0);
        empty.setSize(150, 150);
        empty.addActionListener(this);

        forward.setLocation(150, 0);
        forward.setSize(150, 150);
        forward.addActionListener(this);

        shutdown.setLocation(300, 300);
        shutdown.setSize(150, 150);
        shutdown.addActionListener(this);

        left.setLocation(0, 150);
        left.setSize(150, 150);
        left.addActionListener(this);

        stop.setLocation(150, 150);
        stop.setSize(150, 150);
        stop.addActionListener(this);

        right.setLocation(300, 150);
        right.setSize(150, 150);
        right.addActionListener(this);

        plot.setLocation(300, 0);
        plot.setSize(150, 150);
        plot.addActionListener(this);

        backward.setLocation(150, 300);
        backward.setSize(150, 150);
        backward.addActionListener(this);

        finish.setLocation(0, 300);
        finish.setSize(150, 150);
        finish.addActionListener(this);

        MainLabel.setOpaque(true);
        MainLabel.setBackground(new Color(200, 200, 200));
        MainLabel.setPreferredSize(new Dimension(450, 450));

        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainFrame.getContentPane().add(MainLabel, BorderLayout.CENTER);
        MainLabel.add(empty);
        MainLabel.add(forward);
        MainLabel.add(backward);
        MainLabel.add(right);
        MainLabel.add(left);
        MainLabel.add(stop);
        MainLabel.add(finish);
        MainLabel.add(plot);
        MainLabel.add(shutdown);

        MainFrame.pack();
        MainFrame.setVisible(true);
    }

   /**
    * Method     : ControlPanel::actionPerformed()
    * Purpose    : This is the action listener.
    * Parameters : e : An object of the class ActionEvent.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    @Override
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        if(command.equals("Forward"))  robot.control.move(+vel);
        if(command.equals("Backward")) robot.control.move(-vel);
        if(command.equals("Right"))    robot.control.turnSpot(+vel/2);
        if(command.equals("Left"))     robot.control.turnSpot(-vel/2);
        if(command.equals("Stop"))     robot.control.stop();

        if(command.equals("Shutdown"))
        {
            System.out.println("Shutdown");
            robot.shutDown();
            System.exit(1);
        }

        if(command.equals("Finish"))
        {
            System.out.println("Finish");
        }

        if(command.equals("Plot"))
        {
            System.out.println("Plot");
        }

        if(command.equals("Empty"))
        {
            System.out.println("Empty");
        }
    }
}
