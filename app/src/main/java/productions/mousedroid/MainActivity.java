package productions.mousedroid;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "someMessage";
    public final static String Stop_Message = "some";

    private Boolean connected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume(){

        super.onResume();
        boolean back = ((MyApplication) this.getApplication()).isBackRunningRunning();
        boolean paused = ((MyApplication) this.getApplication()).isPaused();


        if(back && paused){
            Toast.makeText(getApplicationContext(), "Paused", Toast.LENGTH_SHORT).show();
            findViewById(R.id.pauseButton).setVisibility(Button.GONE);
            findViewById(R.id.resumeButton).setVisibility(Button.VISIBLE);
            findViewById(R.id.stopButton).setVisibility(Button.VISIBLE);
            findViewById(R.id.startButton).setVisibility(Button.GONE);
            return;
        }
        if(back && !paused){
            Toast.makeText(getApplicationContext(), "Not Paused", Toast.LENGTH_SHORT).show();
            findViewById(R.id.pauseButton).setVisibility(Button.VISIBLE);
            findViewById(R.id.resumeButton).setVisibility(Button.GONE);
            findViewById(R.id.stopButton).setVisibility(Button.VISIBLE);
            findViewById(R.id.startButton).setVisibility(Button.GONE);
            return;
        }
        if(!back){
            Toast.makeText(getApplicationContext(), "Not Started", Toast.LENGTH_SHORT).show();
            findViewById(R.id.pauseButton).setVisibility(Button.GONE);
            findViewById(R.id.stopButton).setVisibility(Button.GONE);
            findViewById(R.id.startButton).setVisibility(Button.VISIBLE);
            return;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void pauseService(View view) throws UnknownHostException{

        Button temp = (Button) findViewById(R.id.resumeButton);
        temp.setVisibility(Button.VISIBLE);
        temp = (Button) findViewById(R.id.pauseButton);
        temp.setVisibility(Button.GONE);
        Intent intent = new Intent(this, SensorService.class);
        String message  = "Pause";
        intent.setAction("pause");
        //intent.putExtra(EXTRA_MESSAGE, message);
        startService(intent);
    }

    public void stopService(View view) throws UnknownHostException{
        //stopService(new Intent(getBaseContext(), SensorService.class));
        findViewById(R.id.resumeButton).setVisibility(Button.GONE);
        findViewById(R.id.pauseButton).setVisibility(Button.GONE);
        findViewById(R.id.stopButton).setVisibility(Button.GONE);
        findViewById(R.id.startButton).setVisibility(Button.VISIBLE);
        Intent intent = new Intent(this, SensorService.class);
        String message  = "Stop";
        intent.setAction("stop");
        //intent.putExtra(EXTRA_MESSAGE, message);
        startService(intent);
    }

    public void startService(View view) throws UnknownHostException{

        EditText tempText = (EditText) findViewById(R.id.portNo);
        ((MyApplication) getApplication()).setPortNo(Integer.parseInt(tempText.getText().toString()));
        tempText = (EditText) findViewById(R.id.ip_addr);
        ((MyApplication) getApplication()).setAddr(InetAddress.getByName(tempText.getText().toString()));

        Intent intent = new Intent(this, SensorService.class);
        String message  = "Start";
        intent.setAction("start");
        //intent.putExtra(EXTRA_MESSAGE, message);
        startService(intent);

        findViewById(R.id.resumeButton).setVisibility(Button.GONE);
        findViewById(R.id.pauseButton).setVisibility(Button.VISIBLE);
        findViewById(R.id.stopButton).setVisibility(Button.VISIBLE);
        findViewById(R.id.startButton).setVisibility(Button.GONE);


    }

    public void resumeService(View view) {
        Button temp = (Button) findViewById(R.id.resumeButton);
        temp.setVisibility(Button.GONE);
        temp = (Button) findViewById(R.id.pauseButton);
        temp.setVisibility(Button.VISIBLE);

        Intent intent = new Intent(this, SensorService.class);
        intent.setAction("pause");
        //intent.putExtra(EXTRA_MESSAGE, message);
        startService(intent);
    }


}
