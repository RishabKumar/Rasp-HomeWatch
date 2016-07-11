/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homewatchserver;

import static homewatchserver.HomeWatchLiveServer.my_port;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import portMapper.MapPort;

/**
 *
 * @author Ri$habh
 */
public class HomeWatchLiveServer implements Runnable
{
    static int my_port;
    static String my_address;
     
    Socket s;
    OutputStream os;
    public HomeWatchLiveServer(Socket s)
    {
        this.s = s;
        System.out.println("===> "+s.getRemoteSocketAddress());
        try 
        {
            os = s.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(HomeWatchLiveServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run()
    {   
        try
        {
            System.out.println("Processing new request.....");
            
            //BufferedOutputStream bos = new BufferedOutputStream(os);
            //DataOutputStream dos = new DataOutputStream(os);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            while(s.isConnected())
            {
                BufferedImage bi = HomeWatchServer.cam.clickImage();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write( bi, "jpg", baos );
                byte[] imageInByte = baos.toByteArray();
               // byte[] imageInByte = ((DataBufferByte)(bi.getData().getDataBuffer())).getData();
                System.out.println((int)imageInByte.length);
                oos.writeObject(new FramePacket.FramePacketInfo(imageInByte));
                oos.flush();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
         
    }
    
    public static void initVideoServer(int port)
    {
        my_port = port;
        MapPort map = null;
        System.out.println("Setting up Live Video Server.....");
        try 
        {
            map = new MapPort();
            if(map.MapTCPPort(port) == false)
            {
                System.out.println("Port mapping process failed....trying to bind to port....");
            }
            my_address = MapPort.localAdr;
            final ServerSocket ss = new ServerSocket(my_port, 5, InetAddress.getByName(MapPort.localAdr));
            
            System.out.println("Server ready for Live Video Requests.......");
            Thread req = new Thread()
            {
                public void run()
                {
                    while(true)
                    {
                        try
                        {
                            new Thread(new HomeWatchLiveServer(ss.accept())).start();
                        }
                        catch(IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            };
            req.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    //use this for UDP based video streaming.....
    /*/
    class send implements Runnable
    {
       
        DatagramPacket dp;
        BufferedImage bi;
        ByteArrayOutputStream baos;
        
        @Override
        public void run() 
        {
            try 
            {
                Webcam webcam = Webcam.getDefault();
                webcam.setViewSize(new Dimension(320,240));
                webcam.open();
                
                DatagramSocket ds;
                ds = new DatagramSocket();
                
               // dp = new DatagramPacket(new byte[5], 5,InetAddress.getByName(f_address),f_port);

                while(true)
                {
                    bi = webcam.getImage();
                    baos = new ByteArrayOutputStream();
                    ImageIO.write( bi, "jpg", baos );
                    baos.flush();
                    byte[] imageInByte = baos.toByteArray();
                    dp.setData(imageInByte);
                    dp.setLength(imageInByte.length);
                    ds.send(dp); 
                }
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    /*/
}

