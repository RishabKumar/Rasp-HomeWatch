/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package homewatchserver;

import Camera.WebCamera;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.xml.sax.SAXException;
import portMapper.MapPort;

/**
 *
 * @author Rishabh
 */
public class HomeWatchServer extends Thread{

    /**
     * @param args the command line arguments
     */
    private Socket socket;
    public static WebCamera cam;
    static int port = 20230;
    static String address;
    
    private HomeWatchServer(Socket socket)
    {
        this.socket = socket;
    }
    
    public void run()
    {
        try
        {
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            // get image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage image = null;
     //       synchronized(cam)
            {
                image = cam.clickImage();
            }
            ImageIO.write(image, "jpg", baos);
            System.out.println("Sending headers....");
            dos.writeBytes("HTTP/1.1 200 OK\r\n");
            dos.writeBytes("Content-disposition: attachment; filename="+"CamClick.jpg"+"\r\n");
            dos.writeBytes("Content-Type:application/force-download\r\n");
            dos.writeBytes("Content-Length:"+baos.size());
            dos.writeBytes("\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
            System.out.println("Sending image....");
            baos.writeTo(bos);
            bos.flush();
            System.out.println("Done....");
        }
        catch(IOException io)
        {
            io.printStackTrace();
        }
    }
    
    public static void main(String[] args) 
    {
        updateIP.start();
        camServer();
    }
    
    public static void camServer()
    {
        cam = WebCamera.getInstance();
        
        MapPort map = null;
        ServerSocket ss;
        try 
        {
            map = new MapPort();
            map.deleteAllPorts();
            if(map.MapTCPPort(port) == false)
            {
                System.out.println("Port mapping process failed....trying to bind to port....");
            }
            address = MapPort.localAdr;
            HomeWatchLiveServer.initVideoServer(port+1);
            System.out.println("Public address:"+MapPort.pubAdr+":"+port);
            ss = new ServerSocket(port, 10, InetAddress.getByName(address));
            while(true)
            {
                System.out.println("Waiting for new request...");
                new Thread(new HomeWatchServer(ss.accept())).start();
            }
        } 
        catch (IOException | SAXException ex) 
        {
            Logger.getLogger(HomeWatchServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            map.deleteAllPorts();
            System.exit(1);
        }
    }
    
    public static Thread updateIP = new Thread()
    {
        @Override
        public void run()
        {
            MapPort map = null;
            while(true)
            {
                try 
                {
                    System.out.println("Updating public address...");
                    map = new MapPort();
                    //you need to create your Http web server.
                    //pass the public address and port number to the server via query strings.
                    URL url = new URL("http://localhost:8080/ECloudWebService/HomeWatch?ip="+MapPort.pubAdr+":"+port);
                    System.out.println("Public address: "+MapPort.pubAdr);
                    URLConnection conn = url.openConnection();
                    //this will hit the url
                    conn.getContentType();
                    System.gc();
                    System.out.println("Done updating...");
                    Thread.sleep(1000*60*10);
                }
                catch (IOException | SAXException | InterruptedException ex) 
                {
                    Logger.getLogger(HomeWatchServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
        }
    };
}
