/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package portMapper;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.bitlet.weupnp.*;
import org.xml.sax.SAXException;


/**
 *
 * @author Rishabh
 */
public class MapPort 
{
    int portnum = 2;
    int startingportnum = 20230;
    int[] port = new int[portnum];
    static public String localAdr = "127.0.0.1";
    static public String pubAdr = "127.0.0.1";
    GatewayDiscover gatewayDiscover;
    GatewayDevice device;
    PortMappingEntry portMapping;
    
    public MapPort() throws IOException, SAXException
    {
        gatewayDiscover = new GatewayDiscover();
        try
        {
            gatewayDiscover.discover();
        }
        catch(IOException | SAXException | ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        device = gatewayDiscover.getValidGateway();
        portMapping = new PortMappingEntry();
        pubAdr = device.getExternalIPAddress();
        localAdr = device.getLocalAddress().getHostAddress();
        for(int i = 0; i < portnum;i++)
        {
            port[i] = startingportnum+i;
        }
    }
    
    public int MapTcpPort()
    {
        int mappedPort = -1;
        try
        {
            pubAdr = device.getExternalIPAddress();
            localAdr = device.getLocalAddress().getHostAddress();
            System.out.println("Local Address: "+localAdr);
            System.out.println("Public Address: "+pubAdr);
            System.out.println("Checking available port.......");
            for(int p : port)
            {
                if(!device.getSpecificPortMappingEntry(p,"TCP",portMapping) && device.addPortMapping(p,p,localAdr,"TCP","test"))
                {
                    System.out.println("Testing if TCP Port: "+p+ " is mapped correctly");
                    if(TestPortMapping(p))
                    {
                        mappedPort = p;
                        System.out.println("Test passed, port "+mappedPort+" has been mapped successfully");
                        break;
                    }
                }
                else
                {
                    System.out.println("PortMapper failed to map "+p+" port......");
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return mappedPort;
    }
    
    public boolean TestPortMapping(final int port)
    {
        Thread client = new Thread()
        {
            public void run()
            {
                Socket s = null;
                try 
                {
                    Thread.sleep(1000);
                    s = new Socket(pubAdr, port);
                } 
                catch (UnknownHostException ex)
                {
                    Logger.getLogger(MapPort.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) 
                {
                    Logger.getLogger(MapPort.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MapPort.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally
                {
                    try {
                        s.close();
                    } catch (IOException ex) {
                        Logger.getLogger(MapPort.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        ServerSocket ss = null;
        Socket s = null;
        try
        {
            ss = new ServerSocket(port,1,Inet4Address.getByName(localAdr));
            ss.setSoTimeout(3000);
            client.start();
            s = ss.accept();
            System.out.println("Port "+port+" has been mapped successfully");
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try 
            {
                ss.close();
                s.close();
            }
            catch (IOException ex) 
            {
                Logger.getLogger(MapPort.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }
    
    public boolean deletePort(int port)
    {
        try 
        {
            System.out.println("Attempting to delete port "+port);
            boolean flag = device.deletePortMapping(port,"TCP");
            flag = device.deletePortMapping(port,"TCP");
            if(flag)
            {
                System.out.println("TCP Port Deleted");
            }
            flag = device.deletePortMapping(port,"UDP");
            if(flag)
            {
                System.out.println("UDP Port Deleted");
            }
            return flag;
        } catch (IOException ex)
        {
            Logger.getLogger(MapPort.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(MapPort.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public void deleteAllPorts()
    {
        for(int i = startingportnum; i < startingportnum+portnum; i++)
        {
            System.out.println("Attempting to delete port: "+i);
            deletePort(i);
        }
    }
    
    public boolean MapTCPPort(int port)
    {
        try {
            if(!device.getSpecificPortMappingEntry(port,"TCP",portMapping) && device.addPortMapping(port,port,localAdr,"TCP","test"))
            {
                if(TestPortMapping(port))
                {
                    System.out.println("Test passed, port "+port+" has been mapped successfully");    
                    return true;
                }
                else
                {
                    System.out.println("PortMapper failed to map "+port+" port. Trying to fix.....");
                }
            }
            else
            {
                return TestPortMapping(port);
            }
        } catch (IOException | SAXException ex) {
            Logger.getLogger(MapPort.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public void updateAddresses()
    {
        try 
        {
            pubAdr = device.getExternalIPAddress();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(MapPort.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) 
        {
            Logger.getLogger(MapPort.class.getName()).log(Level.SEVERE, null, ex);
        }
        localAdr = device.getLocalAddress().getHostAddress();
    }
    
     
       
    
      
    public boolean MapUDPPort(int port)
    {
        try {
            if(!device.getSpecificPortMappingEntry(port,"UDP",portMapping) && device.addPortMapping(port,port,localAdr,"UDP","test"))
            {return true;}
        } catch (IOException | SAXException ex) {
            Logger.getLogger(MapPort.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
