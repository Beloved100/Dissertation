package robot;

import com.mobilerobots.Aria.*;

/**
 * Created by Theo Theodoridis.
 * Class    : Robot
 * Version  : v1.0
 * Date     : © Copyright 28-07-2015
 * User     : ttheod
 * email    : t.theodoridis@salford.ac.uk
 * Comments : To run the Aria run interface for both, simulator and real robot.
 **/

public class Robot
{
    private ArSimpleConnector conn;
    private ArSerialConnection laserCon;

    public ArSick sick;
    public ArRobot arRobot;
    public ArGripper gripper;

    public Sensor sensor;
    public Control control;
    public Kinematics kinematics;

    static
    {
        try
        {
            System.loadLibrary("AriaJava");
        }
        catch (UnsatisfiedLinkError e)
        {
            System.err.println
            (
                "Native code library libAriaJava failed to load. Make sure that its directory is in your library path;"  +
                "See javaExamples/README.txt and the chapter on Dynamic Linking Problems in the SWIG Java documentation" +
                "(http://www.swig.org) for help.\n" + e
            );
            System.exit(1);
        }
    }

   /**
    * Method     : Robot::Robot()
    * Purpose    : Default Robot class constructor.
    * Parameters : None.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public Robot() { }

   /**
    * Method     : Robot::shutDown()
    * Purpose    : To shut down the run.
    * Parameters : None.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public void shutDown()
    {
        System.out.println("\nRobot is shutting down.");
        arRobot.stopRunning(true);
        arRobot.lock();
        arRobot.disconnect();
        com.mobilerobots.Aria.Aria.shutdown();
        System.exit(1);
    }

   /**
    * Method     : Robot::init()
    * Purpose    : To initialize the robot.
    * Parameters : None.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public void init(String args[], Robot aria)
    {
        com.mobilerobots.Aria.Aria.init();
        sick    = new ArSick();
        arRobot = new ArRobot();
        gripper = new ArGripper(arRobot);

        arRobot.moveTo(new ArPose(0, 0, 0));
        conn = new ArSimpleConnector(args);
        //laserCon = new ArSerialConnection();

        if(!com.mobilerobots.Aria.Aria.parseArgs())
        {
            com.mobilerobots.Aria.Aria.logOptions();
            com.mobilerobots.Aria.Aria.shutdown();
            System.exit(1);
        }

        if(!conn.connectRobot(arRobot))
        {
            System.err.println("Could not connect to run, exiting.\n");
            System.exit(1);
        }

        // [+]Configure and connect to the laser.
        // conn.setupLaser(sick);
        // sick.configureShort(SIMULATOR, ArSick.BaudRate.BAUD38400, ArSick.Degrees.DEGREES180, ArSick.Increment.INCREMENT_ONE);
        // sick.runAsync();
        // if(!sick.blockingConnect())
        // {
        //     System.err.println("Could not connect to a laser, it won't be used.");
        // }

        arRobot.setAbsoluteMaxTransVel(150);
        arRobot.setAbsoluteMaxRotVel(25);
        arRobot.addRangeDevice(sick);
        arRobot.runAsync(true);
        arRobot.enableMotors();

        sensor = new Sensor(aria);
        control = new Control(aria);
        kinematics = new Kinematics(aria);
    }
}
