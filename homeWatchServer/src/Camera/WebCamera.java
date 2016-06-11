/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Camera;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import driverloader.V4l4jDriver;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rishabh
 */
public class WebCamera 
{
    private static WebCamera obj;
    private Webcam webcam;
    
    private WebCamera()
    {
        System.out.println(System.getenv("PROCESSOR_IDENTIFIER"));
        System.out.println(System.getenv("PROCESSOR_ARCHITECTURE"));
        System.out.println(System.getenv("PROCESSOR_ARCHITEW6432"));
        System.out.println(System.getenv("NUMBER_OF_PROCESSORS"));
        
        // For Raspberry Pi
        //Webcam.setDriver(new V4l4jDriver()); 
        
        try
        {
            System.out.println("Loading and configuring webcam...");
            webcam = Webcam.getDefault();
            Dimension[] sizes = webcam.getViewSizes();
            
            for(Dimension size: sizes)
            {
                System.out.println("Valid Size: "+size.getSize());
            }
                    
            //webcam.setViewSize(new Dimension(176,144));

            webcam.setViewSize(new Dimension(320,240));
           // webcam.setViewSize(new Dimension(640,480));
            webcam.open(true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static WebCamera getInstance()
    {
        if(obj == null)
            obj = new WebCamera();
        return obj;
    }
    
    public BufferedImage clickImage()
    { 
        System.out.println("Taking a click...");
        return webcam.getImage();
    }
    
    public void close()
    {
        webcam.close();
    }
    
    public boolean motionDetect()
    {
        WebcamMotionDetector motion = new WebcamMotionDetector(webcam, 255, 100);
        motion.setInterval(100);
        motion.start();
        while(!motion.isMotion())
        {
        }
        System.out.println("Detected motion...");
        
        return true;
        
        
    }
}
