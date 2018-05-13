package smu.ac.kr.johnber.run;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

/**
 * start
 * stop
 * resume
 * handler
 * currentTimemills
 * ~runnable
 */
public class Timer {

    /**
     * RunningFragment의 Time UI 표시를 위한 Timer
     */
    // status flag
    private static final int INIT = 20000;
    private static final int START = 20001;
    private static final int PAUSE = 20002;
    private static final int RESUME = 20003;
    private static final int STOP = 20003;
    private static final int MSG_TIME = 323;
    private static Handler mhandler;
    private static int mSTATE = INIT;
    private double startTime;
    private double elapsedTime;
    //resume하는경우 elapsedTime  = elasepdTimeBuff + timeInterval
    private double elapsedTimeBuff;

    private static final String TAG = makeLogTag(Timer.class);
    public void start(Handler handler) {
        LOGD(TAG,"startTimer");
        if(mSTATE == INIT){
            elapsedTime =0;
            startTime = SystemClock.elapsedRealtime();
            elapsedTime = 0;
            elapsedTimeBuff = 0;
        }
        if (mSTATE == RESUME) {
            //reload elapsedTimeBuff & startTime
        }
        mSTATE = START;
        mhandler = handler;
        Thread thread = new Thread(timerThread);
        thread.start();
        LOGD(TAG,"starttimerthread");

    }
    public void stop(){
        mSTATE = STOP;
        LOGD(TAG,"stopTimer");
    }
    private void resume(){

    }

    private Runnable timerThread = new Runnable() {
        public double timeInterval;

        @Override
        public void run() {
            LOGD(TAG,"runnalbe");

            while(mSTATE == START) {
                timeInterval = SystemClock.elapsedRealtime() - startTime;
                elapsedTime = elapsedTimeBuff + timeInterval;

                int seconds = (int) (elapsedTime / 1000);
                int minutes = (seconds / 60)%60;
                seconds = seconds % 60;
                String stringTime = "";
                stringTime += "" + String.format("%02d", minutes);
                stringTime += ":" + String.format("%02d", seconds);
                //UI 처리를 위한 hanlder, message
                Message msg = mhandler.obtainMessage(MSG_TIME);
                Bundle bundle = new Bundle();
                bundle.putString("time", stringTime);
                msg.setData(bundle);
                mhandler.sendMessage(msg);
                try {
                    //1초간격
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    };
}
