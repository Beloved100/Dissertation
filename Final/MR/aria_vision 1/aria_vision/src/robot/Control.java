package robot;

/**
 * Created by Theo Theodoridis.
 * Class    : Control
 * Version  : v1.0
 * Date     : © Copyright 28-07-2015
 * User     : ttheod
 * email    : t.theodoridis@salford.ac.uk
 * Comments : None.
 **/

public class Control
{
    private Robot robot;

    /**
     * Method     : Control::Control()
     * Purpose    : Secondary Control class constructor that initialises a Robot object.
     * Parameters : robot : An object of Class Robot.
     * Returns    : Nothing.
     * Notes      : This class contains robot control methods.
     **/
    public Control(Robot robot)
    {
        this.robot = robot;
    }

    /**
     * Method     : Control::setVel()
     * Purpose    : To set the robot's velocities.
     * Parameters : - lvel : The left velocity.
     *              - rvel : The right velocity.
     * Returns    : Nothing.
     * Notes      : None.
     **/
    public void setVel(double lvel, double rvel)
    {
        robot.arRobot.setVel2(lvel, rvel);
    }

    /**
     * Method     : Control::stop()
     * Purpose    : To stop the robot.
     * Parameters : None
     * Returns    : Nothing.
     * Notes      : None.
     **/
    public void stop()
    {
        robot.arRobot.stop();
    }

    /**
     * Method     : Control::move()
     * Purpose    : To move forward and backward.
     * Parameters : vel : The robot velocity.
     * Returns    : Nothing.
     * Notes      : None.
     **/
    public void move(double vel)
    {
        setVel(vel, vel);
    }

    /**
     * Method     : Control::turnSpot()
     * Purpose    : To turn on the spot.
     * Parameters : vel : The robot velocity.
     * Returns    : Nothing.
     * Notes      : None.
     **/
    public void turnSpot(double vel)
    {
        setVel(vel, -vel);
    }

    /**
     * Method     : Control::turnSharp()
     * Purpose    : To turn on one wheel.
     * Parameters : vel : The robot velocity.
     * Returns    : Nothing.
     * Notes      : None.
     **/
    public void turnSharp(double vel)
    {
        if(vel >= 0) setVel(vel, 0);
        else         setVel(0, Math.abs(vel));
    }

    /**
     * Method     : Control::turnSmooth()
     * Purpose    : To turn with half speed on one wheel.
     * Parameters : vel : The robot velocity.
     * Returns    : Nothing.
     * Notes      : None.
     **/
    public void turnSmooth(double vel)
    {
        if(vel > 0) setVel(vel, vel/2);
        else        setVel(Math.abs(vel)/2, Math.abs(vel));
    }
}
