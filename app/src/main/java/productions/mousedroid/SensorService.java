package productions.mousedroid;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RemoteViews;
import android.os.IBinder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.hardware.SensorManager;
import android.hardware.Sensor;


import android.app.Notification;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by 0000101795 on 2014/12/31.
 */
public class SensorService extends Service implements View.OnKeyListener{
    //private Looper mServiceLooper;

    private Boolean running;
    private Boolean alive;
    private InetAddress address;
    private int port;
    private workThread backThread;
    private Handler mHandler;


    private Notification buildNotif(int type) {

        Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pStartIntent = PendingIntent.getActivity(this, 10, startIntent, 0);

        Intent pauseIntent = new Intent(getApplicationContext(), SensorService.class);
        pauseIntent.setAction("pause");
        PendingIntent pPauseIntent = PendingIntent.getService(this, 10, pauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent leftIntent = new Intent(getApplicationContext(), SensorService.class);
        leftIntent.setAction("left");
        PendingIntent pLeftIntent = PendingIntent.getService(this, 10, leftIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent rightIntent = new Intent(getApplicationContext(), SensorService.class);
        rightIntent.setAction("right");
        PendingIntent pRightIntent = PendingIntent.getService(this, 10, rightIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteViews notifViewSmall = new RemoteViews(this.getPackageName(), R.layout.notif_small);
        RemoteViews notifViewBig = new RemoteViews(this.getPackageName(), R.layout.notify_big);

        if(type==0){
            notifViewBig.setTextViewText(R.id.notifyPauseBig,"Pause");
            notifViewSmall.setTextViewText(R.id.notifyPause,"Pause");
        }
        else{
            notifViewBig.setTextViewText(R.id.notifyPauseBig,"Resume");
            notifViewSmall.setTextViewText(R.id.notifyPause,"Resume");
        }

        notifViewSmall.setOnClickPendingIntent(R.id.notifyPause,pPauseIntent);
        notifViewSmall.setOnClickPendingIntent(R.id.notifyLeft,pLeftIntent);
        notifViewBig.setOnClickPendingIntent(R.id.notifyPauseBig,pPauseIntent);
        notifViewBig.setOnClickPendingIntent(R.id.notifyLeftBig, pLeftIntent);
        notifViewBig.setOnClickPendingIntent(R.id.notifyRightBig, pRightIntent);


        Notification.Builder b = new Notification.Builder(this).
                setContentTitle("Notification title")
                .setContentText("Notification content")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pStartIntent);

        Notification notification = b.build();

        notification.contentView = notifViewSmall;
        notification.bigContentView = notifViewBig;
        return(notification);
    }

    private class workThread extends HandlerThread implements SensorEventListener{

        private Handler mHandler;
        private connection mConnect;
        private InetAddress address;
        private int port;

        public workThread(String name, InetAddress add, int portNo) {
            super(name);
            start();
            address=add;
            port=portNo;
            mHandler = new Handler(getLooper()){
                @Override
                public void handleMessage(Message msg){
                    Integer msgValue = msg.what;
                    if(msgValue.equals(1)){
                        mConnect.leftClick();
                    }
                    else if(msgValue.equals(2)){
                        mConnect.rightClick();
                    }
                }
            };
            mConnect = new connection(add, portNo);
        }

        public Handler getmHandler(){
            return mHandler;
        }

        public void close(){
            mConnect.close();
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(running) {
                //Log.d("sendingCommand",Float.toString(event.values[1]));
                mConnect.SendUDP(address, port, event.values);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void leftClick(){
            mConnect.leftClick();
        }
    }


    @Override
    public void onCreate() {

        running = true;
        alive = true;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event){

        Log.d("KeyPress","FunctionEntered");
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            Log.d("Down","Pressed");
            Message msg = Message.obtain(mHandler,1);
            mHandler.sendMessage(msg);
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            Log.d("UP","Pressed");
            Message msg = Message.obtain(mHandler,2);
            mHandler.sendMessage(msg);
            return true;
        }

        return false;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Toast.makeText(getApplicationContext(), "onStartCalled", Toast.LENGTH_SHORT).show();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        String action = intent.getAction();
        Log.d("service",action);
        if(action.equals("pause")) {

            if(running){
                running = false;
                ((MyApplication) this.getApplication()).setBackRunning(true);
                ((MyApplication) this.getApplication()).setPaused(true);
                NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notifManager.notify(21, buildNotif(1));
            }
            else{
                running = true;
                ((MyApplication) this.getApplication()).setBackRunning(true);
                ((MyApplication) this.getApplication()).setPaused(false);
                NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notifManager.notify(21, buildNotif(0));
            }
        }

        else if(action.equals("stop")){
            running=false;
            alive = false;

            HandlerThread temp = backThread;
            backThread.close();
            temp.interrupt();
            backThread=null;

            ((MyApplication) this.getApplication()).setBackRunning(false);
            ((MyApplication) this.getApplication()).setPaused(false);

            stopSelf();
        }
        else if(action.equals("start")){
                address =  ((MyApplication) this.getApplication()).getAddr();
                port =  ((MyApplication) this.getApplication()).getPortNo();

                startForeground(21, buildNotif(0) );
                backThread = new workThread("hi", address, port );
                mHandler = backThread.getmHandler();

                SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
                Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                manager.registerListener(backThread,sensor,SensorManager.SENSOR_DELAY_FASTEST,mHandler);

                Log.d("handlercrear","should be main");
                ((MyApplication) this.getApplication()).setBackRunning(true);
                ((MyApplication) this.getApplication()).setPaused(false);
        }
        else if(action.equals("left")){
            //Toast.makeText(getApplicationContext(), "Left", Toast.LENGTH_SHORT).show();
            Message msg = Message.obtain(mHandler,1);
            mHandler.sendMessage(msg);
        }
        else if(action.equals("right")){
            //Toast.makeText(getApplicationContext(), "Right", Toast.LENGTH_SHORT).show();
            Message msg = Message.obtain(mHandler,2);
            mHandler.sendMessage(msg);
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(getApplicationContext(), "Destroyed", Toast.LENGTH_SHORT).show();
    }
}
