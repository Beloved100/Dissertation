package robot;

import blob.ColorTracker;

/**
 * Created by Theo Theodoridis.
 * Class    : Sensor
 * Version  : v1.0
 * Date     : © Copyright 28-07-2015
 * User     : ttheod
 * email    : t.theodoridis@salford.ac.uk
 * Comments : This class contains robot sensor methods.
 **/

public class Sensor
{
    private Robot robot;
    private ColorTracker ct;

   /**
    * Method     : Sensor::Sensor()
    * Purpose    : Secondary Sensor class constructor that initialises a Robot object.
    * Parameters : robot : An object of Class Robot.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public Sensor(Robot robot)
    {
        this.robot = robot;
//        try
//        {
//            ct = new ColorTracker();
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
    }

   /**
    * Method     : Sensor::getSonarRange()
    * Purpose    : To get the sonar range.
    * Parameters : index : The sonar index.
    * Returns    : The range of the chosen sonar.
    * Notes      : None.
    **/
    public double getSonarRange(int index)
    {
        return(robot.arRobot.getSonarRange(index));
    }

   /**
    * Method     : Sensor::getSonarTh()
    * Purpose    : To get the sonar angular position.
    * Parameters : index : The sonar index.
    * Returns    : The angle Th of the chosen sonar.
    * Notes      : The angle is fixed per chosen sonar.
    **/
    public double getSonarTh(int index)
    {
        return(robot.arRobot.getSonarReading(index).getSensorTh());
    }

   /**
    * Method     : Sensor::getSonarX()
    * Purpose    : To get the sonar x coordinate position.
    * Parameters : index : The sonar index.
    * Returns    : The x coordinate of the chosen sonar.
    * Notes      : The x coordinate is fixed per chosen sonar.
    **/
    public double getSonarX(int index)
    {
        return(robot.arRobot.getSonarReading(index).getSensorX());
    }

   /**
    * Method     : Sensor::getSonarY()
    * Purpose    : To get the sonar y coordinate position.
    * Parameters : index : The sonar index.
    * Returns    : The y coordinate of the chosen sonar.
    * Notes      : The y coordinate is fixed per chosen sonar.
    **/
    public double getSonarY(int index)
    {
        return(robot.arRobot.getSonarReading(index).getSensorY());
    }

   /**
    * Method     : Sensor::getBlobX, getBlobY()
    * Purpose    : To read the color blob's x and y coordinate.
    * Parameters : none.
    * Returns    : The x or y image coordinate.
    * Notes      : None.
    **/
    public int getBlobX() { return(ct.getBlobX()); }
    public int getBlobY() { return(ct.getBlobY()); }

   /**
    * Method     : Sensor::getImageWidth, getImageHeight()
    * Purpose    : To get the image size in width and height.
    * Parameters : none.
    * Returns    : The width or height image size.
    * Notes      : None.
    **/
    public int getImageWidth()  { return(ct.getWidth());  }
    public int getImageHeight() { return(ct.getHeight()); }
}
