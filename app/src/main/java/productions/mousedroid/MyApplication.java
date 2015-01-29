package productions.mousedroid;

import android.app.Application;

import java.net.InetAddress;

/**
 * Created by 0000101795 on 2015/01/05.
 */
public class MyApplication extends Application {

    private boolean backRunning = false;
    private boolean paused = false;
    private int portNo = 0;
    private InetAddress address = null;


    public boolean isBackRunningRunning() {
        return backRunning;
    }
    public boolean isPaused() {return paused;}
    public int getPortNo(){return portNo;}
    public InetAddress getAddr(){return address;}

    public void setBackRunning(boolean value) {
        this.backRunning = value;
    }
    public void setPaused(boolean value) {this.paused = value;}
    public void setPortNo(int port){portNo=port;}
    public void setAddr(InetAddress addr){address=addr;}
}
