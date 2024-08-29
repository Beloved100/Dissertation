package robot;

/**
 * Created by Theo Theodoridis.
 * Class    : Kinematics
 * Version  : v1.0
 * Date     : © Copyright 28-07-2015
 * User     : ttheod
 * email    : t.theodoridis@salford.ac.uk
 * Comments : This class contains robot kinematic methods.
 **/

public class Kinematics
{
    private Robot robot;

   /**
    * Method     : Kinematics::Kinematics()
    * Purpose    : Secondary Kinematics class constructor that initialises a Robot object.
    * Parameters : robot : An object of Class Robot.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public Kinematics(Robot robot)
    {
        this.robot = robot;
    }

   /**
    * Methods : Kinematics::Arial odometry model methods.
    * Purpose : To acquire the odometry coordinates, and linear/rotational velocities.
    **/
    public double getX()  { return(robot.arRobot.getX());  }
    public double getY()  { return(robot.arRobot.getY());  }
    public double getTh() { return(robot.arRobot.getTh()); }

    public double getLinVel()   { return(robot.arRobot.getVel());      }
    public double getRotVel()   { return(robot.arRobot.getRotVel());   }
    public double getLeftVel()  { return(robot.arRobot.getLeftVel());  }
    public double getRightVel() { return(robot.arRobot.getRightVel()); }
}
