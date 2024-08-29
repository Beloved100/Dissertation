import org.jfree.ui.RefineryUtilities;
//import robot.Mapping;
import robot.Robot;
import robot.Sensor;
import utils.Delay;


import javax.print.attribute.standard.Destination;
import java.awt.*;



public class LabExercises
{
    private static final double GAMMA_A  = 350;
    private static final double GAMMA_N  = 200;

    private Robot robot;


   /**
    * Method     : LabExercises::LabExercises()
    * Purpose    : Default LabExercises class constructor.                                                                                                                  
    * Parameters : robot : An object of Class Robot.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public LabExercises(Robot robot)
    {
        this.robot = robot;

    }

   /**
    * Method     : LabExercises::navigate()
    * Purpose    : To implement waypoint navigation method using an odometry/trajectory model.
    * Parameters : - vel : The robot velocity.
    * Returns    : True if navigation is completed (last node), False otherwise.
    * Notes      : None.
    **/
    private static final double GAMMA_TH = 2.5;
    private static final double GAMMA_D  = 100.0;  //50

    private static final int TURN = 0;
    private static final int MOVE = 1;

    private int step = MOVE;
    private int i = 0;

    private static double NODE[][] =
    {
            {2176,   0},
            {2176, -2509},
            {3962, -2509},
            {3962, -3584},
            {-1758, -3584}
    };

    //private double DESTI[] =   {-3787, 368};
        private double DESTI[] =   {-3137, -266};
 //   private double DESTI[] =   {2123, -3568};
//            {2123, -3568};







    public boolean navigate(double vel)
    {
        // [1]Extract node coordinates:
        double x  = NODE[i][0];
        double y  = NODE[i][1];
        double th = getAngle(x, y);                                     

        double l_vec[] = {
                robot.sensor.getSonarRange(1),
                robot.sensor.getSonarRange(2),
                robot.sensor.getSonarRange(3)
        };
        double r_vec[] = {
                robot.sensor.getSonarRange(4),
                robot.sensor.getSonarRange(5),
                robot.sensor.getSonarRange(6),
        };

        double l_min = Math.min(Math.min(l_vec[0], l_vec[1]), l_vec[2]);
        double r_min = Math.min(Math.min(r_vec[0], r_vec[1]), r_vec[2]);

        if(r_min < GAMMA_N || l_min < GAMMA_N) {
            step = TURN;
        }

        switch(step)
        {
            // [2]Turn and stop to the node's location:
            case TURN:
            {
                robot.control.turnSpot(turnVelocity(th, vel));
                if(isAngularDestination(th))
                {
                    robot.control.stop();
                    step = MOVE;
                }
            }break;

            // [3]Move and stop to the node's location:
            case MOVE:
            {
                robot.control.move(vel);
                if(isLinearDestination(x, y))
                {
                    robot.control.stop();
                    step = TURN;
                    System.out.printf
                            ("\rNode[%d]: X = %.1f, Y = %.1f",i, robot.kinematics.getX(), robot.kinematics.getY());
                    if(++i == NODE.length)
                    {
                        return(true);
                    }
                }
            }break;
        }
        return(false);
    }

    public double getAngle(double nX, double nY)
    {
        return(get360(getRadToDeg(Math.atan2(nY - robot.kinematics.getY(), nX - robot.kinematics.getX()))));
    }

    public double getRadToDeg(double th)
    {
        return(Math.floor(Math.toDegrees(th)));
    }

    public double get360(double th)
    {
        return(th - 360.0 * Math.floor(th / 360.0));
    }


    public boolean isAngularDestination(double nTh)
    {
        double rTh = get360(robot.arRobot.getTh());
        return (rTh >= (nTh - GAMMA_TH)) && (rTh <= (nTh + GAMMA_TH));
    }

    public double turnVelocity(double nTh, double vel)
    {
        double rTh = get360(robot.arRobot.getTh());
        double diff = nTh -rTh;
        if(diff > 180){
            diff -=360;
        } else if(diff < -180){
            diff += 360;
        }
        if(diff > 0){
            return -vel/2.5;
        } else {
            return vel/2.5;
        }
    }

    public boolean isLinearDestination(double nX, double nY)
    {
        double d = Math.sqrt(Math.pow(nX - robot.kinematics.getX(), 2) + Math.pow(nY - robot.kinematics.getY(), 2));
        return d <= GAMMA_D;
    }


   /**
    * Method     : LabExercises::avoid()
    * Purpose    : To implement an obstacle avoidance and collision detection algorithm. The escape method is not implemented here.
    * Parameters : - vel : The robot velocity.
    * Returns    : true if an obstacle is detected, false otherwise.
    * Notes      : None.
    **/
    public boolean avoid(double vel)
    {
        // [1]Collect left/right sensor ranges:
        double l_vec[] = {
            robot.sensor.getSonarRange(0),
            robot.sensor.getSonarRange(1),
            robot.sensor.getSonarRange(2),
            robot.sensor.getSonarRange(3)
        };
        double r_vec[] = {
            robot.sensor.getSonarRange(4),
            robot.sensor.getSonarRange(5),
            robot.sensor.getSonarRange(6),
            robot.sensor.getSonarRange(7),
        };

        // [2]Calculate weighted sums:
        double l_weighted_sum = 0;
        for (int i = 0; i < l_vec.length - 1; i++) {
            // Assign weights based on the number of times obstacles are detected
            if (l_vec[i] < GAMMA_A) {
                l_weighted_sum += 1; // Increasing weight for left sensors
            }
        }

        double r_weighted_sum = 0;
        for (int i = 1; i < r_vec.length; i++) {
            // Assign weights based on the number of times obstacles are detected
            if (r_vec[i] < GAMMA_A) {
                r_weighted_sum += 1; // Increasing weight for right sensors
            }
        }


        // [2]Calculate the left/right min sensor vector:
        double l_min = Math.min(Math.min(Math.min(l_vec[0], l_vec[1]), l_vec[2]), l_vec[3]);
//        double l_min =Math.min(Math.min(l_vec[0], l_vec[1]), l_vec[2]);
        double r_min = Math.min(Math.min(Math.min(r_vec[0], r_vec[1]), r_vec[2]),r_vec[3]);
//        double r_min = Math.min(Math.min(r_vec[0], r_vec[1]), r_vec[2]);

        if(r_min < GAMMA_A && l_min < GAMMA_A) {

            if(l_weighted_sum < r_weighted_sum){
                robot.control.turnSpot(vel);
                return(true);
            } else if(l_weighted_sum > r_weighted_sum) {
                robot.control.turnSpot(-vel);
                return (true);
            } else {
                robot.control.move(-vel); // Move backward.
                Delay.ms(2000);
                double p = Math.random();
                System.out.printf
                        (
                                "\rl_min = %.1f, r_min = %.1f, p = %.1f",
                                l_min, r_min, p
                        );
                if(p < 0.5) robot.control.turnSpot(vel);
                else        robot.control.turnSpot(-vel);
                Delay.ms(2000);
            }
        }  else

        // [4]Validate if an obstacle is detected right and turn left:
        if(r_min < GAMMA_A)
        {
            robot.control.turnSpot(-vel);
            return(true);
        }
        else
            // [3]Validate if an obstacle is detected left and turn right:
            if(l_min < GAMMA_A) {
                robot.control.turnSpot(vel);
                return(true);
            }
        // [5]Otherwise, invoke decollider:
        else
        {
			boolean initPose = (robot.kinematics.getX() == 0) && (robot.kinematics.getY() == 0);
			boolean zeroVel  = (robot.kinematics.getLeftVel() == 0) && (robot.kinematics.getRightVel() == 0);

			if(zeroVel && !initPose)
			{
				robot.control.move(-vel); // Move backward.
				Delay.ms(2000);

				double p = Math.random();
                System.out.printf
                        (
                                "\rl_min = %.1f, r_min = %.1f, p = %.1f",
                                l_min, r_min, p
                        );
				if(p < 0.5) robot.control.turnSpot(vel);
				else        robot.control.turnSpot(-vel);
				Delay.ms(2000);
			}
			else
			{
				robot.control.move(vel); // Move forward.
			}
        }

        return(false);
    }

   /**
    * Method     : LabExercises::track()
    * Purpose    : To perform target tracking using 3 discrete zones.
    * Parameters : - vel : The robot velocity.
    * Returns    : True if detecting a target, false otherwise.
    * Notes      : Make use of the camera sensors and blob detector.
    **/
    public boolean track(double vel)
    {
        // [1]Validate left image zone and turn left:
        if((robot.sensor.getBlobX() > 0) && (robot.sensor.getBlobX() < (robot.sensor.getImageWidth() / 3)))
        {
            robot.control.turnSpot(-vel);
            System.out.println("< L <");
            return(true);
        }
        else
        // [2]Validate right image zone and turn right:
        if((robot.sensor.getBlobX() > ((2 * robot.sensor.getImageWidth()) / 3)) && (robot.sensor.getBlobX() < robot.sensor.getImageWidth()))
        {
            robot.control.turnSpot(+vel);
            System.out.println("> R >");
            return(true);
        }
        else
        // [3]Alternatively, approach target:
        {
			double f_vec[] = {robot.sensor.getSonarRange(3), robot.sensor.getSonarRange(4)};
			double min = Math.min(f_vec[0], f_vec[1]);

			if(min <= GAMMA_A) robot.control.stop();
			else			   robot.control.move(vel);

            System.out.println("| M |");
            return(false);
        }
    }

    public boolean stop(){
        double x = DESTI[0];
        double y = DESTI[1];
        return isLinearDestination(x, y);
    }

}
