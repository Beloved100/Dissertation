import robot.Robot;
import utils.Delay;




public class Run {
    private static Robot robot;
    private static SmartControl smartControl;


    /**
     * Method     : Run::Run()
     * Purpose    : Secondary Run class constructor.
     * Parameters : args : The program's arguments.
     * Returns    : Nothing.
     * Notes      : None.
     **/
    public Run(String args[]) {
        robot = new Robot();
        robot.init(args, robot);
        smartControl = new SmartControl(robot, 100);



        // [+]Thread setup:
        update.setPriority(Thread.MAX_PRIORITY);
        update.start();
    }

    /**
     * Thread     : Run::update()
     * Purpose    : To run the update thread.
     * Parameters : None.
     * Returns    : Nothing.
     * Notes      : None.
     **/
    Thread update = new Thread() {
        public void run() {
            while (true) {
                // your code...
                Delay.ms(1);
            }
        }
    };

    /**
     * Method     : Run::main()
     * Purpose    : Default main method which runs the Run class.
     * Parameters : - args : Initialization parameters.
     * Returns    : Nothing.
     * Notes      : None.
     **/
    public static void main(String args[]) {
        boolean omFlag = false;
        boolean trackFlag = true;
        new Run(args);
//        FiniteStateMachine fsm = new FiniteStateMachine(robot);
        while(true)
        {
            if(!smartControl.avoid(100.0)){  //this line of code    `
                // will implement avoid and Odometry
//                   r_navigation.track(100.0);
            } else {
                smartControl.startSmartControl();
            }

            Delay.ms(50);

        }







    }
}





