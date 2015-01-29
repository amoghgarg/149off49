package productions.mousedroid;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.Charset;

/**
 * Created by 0000101795 on 2015/01/07.
 */
public class connection {

    private DatagramSocket sock;
    private InetAddress address;
    private int port;

    float oldX, oldY, oldZ;

    public connection(InetAddress addr, int portNo){
        address = addr;
        port = portNo;

        try {
            sock = new DatagramSocket();
        }
        catch (SocketException a){
            Log.v("Socket Creation",a.getMessage());
        }

        oldX = 0;
        oldY = 0;
        oldZ = 0;
    }

    public void SendUDP(InetAddress address, int port, float[] values){

        DatagramPacket packet=null;
        //Log.v("Packet","Sending");
        try {
            float x = values[0] - oldX;
            float y = values[1] - oldY;
            float z = values[2] - oldZ;
            oldX = values[0];
            oldY = values[1];
            oldZ = values[2];

            String temp = String.format("%.4f",x) + ',' + String.format("%.4f",y) + ',' + String.format("%.4f",z);
            //Log.d("Sending:", temp);
            byte[] data = temp.getBytes(Charset.forName("UTF-8"));
            packet = new DatagramPacket(data, data.length, address, port);
            sock.send(packet);
        }
        catch (IOException a) {
            Log.v("Send failed",a.getMessage());
            //Toast.makeText(getApplicationContext(), a.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void leftClick(){
        DatagramPacket packet=null;
        Log.v("Packet","Sending");
        try {
            byte[] data = "left".getBytes(Charset.forName("UTF-8"));
            packet = new DatagramPacket(data, data.length, address, port);
            sock.send(packet);
        }
        catch (IOException a) {
            Log.v("Send failed",a.getMessage());
            //Toast.makeText(getApplicationContext(), a.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void rightClick(){
        DatagramPacket packet=null;
        Log.v("Packet","Sending");
        try {
            byte[] data = "right".getBytes(Charset.forName("UTF-8"));
            packet = new DatagramPacket(data, data.length, address, port);
            sock.send(packet);
        }
        catch (IOException a) {
            Log.v("Send failed",a.getMessage());
            //Toast.makeText(getApplicationContext(), a.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void close(){
        sock.close();
    }
}
