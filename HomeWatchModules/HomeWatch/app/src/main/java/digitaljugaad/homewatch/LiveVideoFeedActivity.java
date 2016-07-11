package digitaljugaad.homewatch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.Inflater;

import FramePacket.FramePacketInfo;

public class LiveVideoFeedActivity extends Activity {

    ImageView iv;
    String my_address;
    int my_port = 33059;
    int f_port;
    String f_address;
    TextView info;
    TextView fps;
    long lastPacketTime;
    ArrayDeque<Bitmap> videoBuffer;
    String serverAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lastPacketTime = 1l;

        videoBuffer = new ArrayDeque<>();

        setContentView(R.layout.activity_live_video_feed);
        iv = (ImageView) findViewById(R.id.videoview);
        info = (TextView) findViewById(R.id.InfoView);
        fps = (TextView) findViewById(R.id.fpsView);
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        my_address = inetAddress.getHostAddress().toString();
                        System.out.println(inetAddress.getHostAddress().toString());
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
            Toast.makeText(getBaseContext(),"Error:"+ex.toString(),Toast.LENGTH_LONG).show();
        }
        System.out.println("MY IP: " + my_address);
        info.append("My IP: " + my_address);
   //     startWebServices.execute();

    }

    public void startStreaming(View view)
    {
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.serverinfobox);
        EditText address = (EditText) findViewById(R.id.serverAddressTxt);
        serverAddress = address.getText().toString();
        System.out.println("==> "+serverAddress);
        if(serverAddress.length() > 2) {
            rl.setVisibility(View.GONE);
            startWebServices.execute();
        }
    }

    //present the reveiced bytes as bitmap and play the live video (the ugly way of playing video,: need to rework on this)
    class FetchAndPlay extends AsyncTask<String, String, String>
    {
        Bitmap bm;
        String newfps;
        public FetchAndPlay(Bitmap bm) throws IOException {
            this.bm = bm;
        }

        @Override
        protected String doInBackground(String... params) {

            long time = System.currentTimeMillis();
            try {
                double d = (double)((double)1d / (double)(time - lastPacketTime));
                DecimalFormat dfmt = new DecimalFormat(",000.00000000");
                newfps = dfmt.format(d);
            }
            catch(ArithmeticException ae)
            {
                ae.printStackTrace();
            }
            lastPacketTime = time;

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

        fps.setText("fps: " + newfps);
        if(bm != null)
            iv.setImageBitmap(bm);

            super.onPostExecute(s);
        }
    };

    //reveive and process the bytes
    class LiveStream extends Thread
    {
        InputStream is;
        ExecutorService es;
        public LiveStream(InputStream is)
        {
            this.is = is;
            es = Executors.newFixedThreadPool(10);
        }

        public void run() {
            try
            {
                //BufferedInputStream bis = new BufferedInputStream(is,12000);
                ObjectInputStream ois = new ObjectInputStream(is);
                byte[] bytes = new byte[20000];
                int size = 0;

                //while((size = bis.read(bytes,0,12000))!=-1)
                while((bytes = ((FramePacketInfo)ois.readObject()).getBytes() )!= null)
                {
                   // Bitmap bm = BitmapFactory.decodeStream(bis);
                    System.out.println("Byte Size: "+bytes.length);
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if(bm != null) {
                        new FetchAndPlay(bm).executeOnExecutor(es);
                    }
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(),"Error:"+e.toString(),Toast.LENGTH_LONG).show();
            } catch (SocketException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(),"Error:"+e.toString(),Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(),"Error:"+e.toString(),Toast.LENGTH_LONG).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    };

    //initiates Live video request
    AsyncTask<String, String, String> startWebServices = new AsyncTask<String, String, String>() {
        @Override
        protected String doInBackground(String... params)
        {
            URL url = null;
            try
            {
                //String serverAddress = "provide the server address that will return the dynamic ip of the raspberry pi/webcam server";
                url = new URL(serverAddress);
                HttpURLConnection   conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                ObjectOutputStream oos = new ObjectOutputStream(conn.getOutputStream());
                oos.writeObject(new JSONObject().put("fetchAddress", true).toString());
                oos.flush();
                oos.close();

                ObjectInputStream ois = new ObjectInputStream(conn.getInputStream());
                f_address = new JSONObject((String) ois.readObject()).getString("address");
                ois.close();

                String[] _address = f_address.split(":");
                f_port = Integer.parseInt(_address[1])+1;
                f_address = _address[0];
            //    System.out.println("ADDRESS 1=====>" + f_address+":"+f_port);
                String total_my_address = my_address+":"+my_port;
            //    System.out.println("ADDRESS 2=====>" + total_my_address);

                //TCP socket
                Socket socket = new Socket(InetAddress.getByName(f_address), f_port);
                System.out.println("==>"+socket.getLocalPort());
                socket.getOutputStream().write(1);
                new Thread(new LiveStream(socket.getInputStream())).start();

            } catch (MalformedURLException e)
            {
                e.printStackTrace();
                Toast.makeText(getBaseContext(),"Error:"+e.toString(),Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(),"Error:"+e.toString(),Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(),"Error:"+e.toString(),Toast.LENGTH_LONG).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(),"Error:"+e.toString(),Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            info.append("\nConnection Established with Server....");
            super.onPostExecute(s);
        }
    };

}
