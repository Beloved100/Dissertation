import robot.Robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import utils.Delay;

import java.util.concurrent.TimeUnit;

/**
 * Created by Theo Theodoridis.
 * Class    : ControlPanel
 * Version  : v1.0
 * Date     : © Copyright 20-01-2015
 * User     : ttheod
 * email    : ttheod@gmail.com
 * Comments : The class creates GUI used to control the robot.
 **/

public class SmartControl
{
    private float vel;
    private Robot robot;
    private static String apiUrl = "http://wazobiareportersng.com/testapi.php";
    private static final double GAMMA_A  = 350;



   /**
    * Method     : ControlPanel::ControlPanel()
    * Purpose    : Default ControlPanel class constructor.
    * Parameters : robot : An object of Class Robot.
    *              vel   : The robot velocity.
    * Returns    : Nothing.
    * Notes      : None.
    **/


    public SmartControl(Robot robot, float vel)
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


   public boolean startSmartControl(){
       // Create a scheduled executor service to run the task every second
       ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

       // Schedule the task to run every second
       executorService.scheduleAtFixedRate(new Runnable() {
           @Override
           public void run() {
               connectToApi(apiUrl);
           }
       }, 0, 1, TimeUnit.SECONDS);
       return(true);
   }


   /**
    * Method     : ControlPanel::actionPerformed()
    * Purpose    : This is the action listener.
    * Parameters : e : An object of the class ActionEvent.
    * Returns    : Nothing.
    * Notes      : None.
    **/



    public void connectToApi(String apiUrl) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            // Create a URL object
            URL url = new URL(apiUrl);
            // Open a connection to the URL
            connection = (HttpURLConnection) url.openConnection();
            // Set the request method to GET
            connection.setRequestMethod("GET");
            // Set the request property to accept JSON response
            connection.setRequestProperty("Accept", "application/json");

            // Check if the response code is 200 (HTTP_OK)
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Create a BufferedReader to read the response
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                // Read each line of the response and append it to the StringBuilder
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Print the response to the log
//                LOGGER.log(Level.INFO, "API Response: {0}", response.toString());

                String jsonString = response.toString();

                // Use JsonParser to parse the string into a JsonObject
                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

                // Extract the "status" field from the JsonObject
                String status = jsonObject.get("status").getAsString();
                System.out.println("Status: " + status);  // Should print "Status: success"

                System.out.printf
                        ("I am here" + "API Response: {0}"+  response.toString()
                        );




                String command = jsonObject.get("status").getAsString();
                if(command.equals("forward"))  robot.control.move(+vel);
                if(command.equals("backward")) robot.control.move(-vel);
                if(command.equals("right"))    robot.control.turnSpot(+vel/2);
                if(command.equals("left"))     robot.control.turnSpot(-vel/2);
                if(command.equals("stop"))     robot.control.stop();

                if(command.equals("Shutdown"))
                {
                    System.out.println("Shutdown");
                    robot.shutDown();
                    System.exit(1);
                }



            } else {
                System.out.printf
                        ("Connection errorAAA" + connection.getResponseCode()
                        );
//                LOGGER.log(Level.SEVERE, "Failed to connect to the API. Response code: {0}", connection.getResponseCode());
            }
        } catch (Exception e) {
            System.out.printf
                    ("Exceptional error" + e
                    );
//            LOGGER.log(Level.SEVERE, "Exception occurred while connecting to the API", e);
        } finally {
            // Close the BufferedReader and HttpURLConnection
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    System.out.printf
                            ("Exceptional error closed error"
                            );
//                    LOGGER.log(Level.SEVERE, "Exception occurred while closing BufferedReader", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }




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

}
