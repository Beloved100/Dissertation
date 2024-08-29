package utils;

/**
 * Created by Theo Theodoridis.
 * Class    : Delay
 * Version  : v1.0
 * Date     : © Copyright 22-Mar-2010
 * User     : ttheod
 * email    : ttheod@gmail.com
 * Comments : None.
 **/

public class Delay
{
   /**
    * Method     : Delay::Delay()
    * Purpose    : Default Delay class constructor.
    * Parameters : None.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public Delay() { }

   /**
    * Method     : Delay::ms()
    * Purpose    : To delay in milliseconds (ms).
    * Parameters : - ms : The milliseconds scalar.
    * Returns    : Nothing.
    * Notes      : None.
    **/
    public static void ms(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(Exception e)
        {
            System.out.println("Exception<Delay>: " + e);
        };
    }
}