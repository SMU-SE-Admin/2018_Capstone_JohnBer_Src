package smu.ac.kr.johnber.run;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import smu.ac.kr.johnber.util.RecordUtil;

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

    private static Context mContext;
    // status flag
    private static final int INIT = 20000;
    private static final int START = 20001;
    private static final int PAUSE = 20002;
    private static final int RESUME = 20003;
    private static final int STOP = 20003;
    private static final int MSG_TIME = 323;
    private static Handler mhandler;
    private static int mSTATE ;
    private double startTime;
    private double elapsedTime;
    //resume하는경우 elapsedTime  = elasepdTimeBuff + timeInterval
    private double elapsedTimeBuff;
    private Thread mthread;
    private SharedPreferences preferences;
    public Timer(Context context) {
        mContext = context;
        preferences = mContext.getSharedPreferences("saveRecord",Context.MODE_PRIVATE)
                ;

    }
//TODO : 같은 쓰레드를 참조할수있도록 , thread 종료
    private static final String TAG = makeLogTag(Timer.class);
    public void start(Handler handler, int action) {
        LOGD(TAG,"startTimer, action : "+action);
        switch (action) {
            case INIT:
                mSTATE = INIT;
                break;

            case RESUME:
                mSTATE = RESUME;
                break;
        }

        if(mSTATE == INIT || mSTATE == STOP){
            elapsedTime =0;
            startTime = SystemClock.elapsedRealtime();
            elapsedTime = 0;
            elapsedTimeBuff = 0;
        }
        if (mSTATE == RESUME) {
            //TODO : reload elapsedTimeBuff & startTime
            resume();
            LOGD(TAG,"RESUME TIMER " + elapsedTimeBuff);
        }
            LOGD(TAG , "elapsed after start : " + elapsedTimeBuff);
        mSTATE = START;
        mhandler = handler;

//        if(mthread == null) {
            mthread = new Thread(new TimerThread());

            LOGD(TAG, "Thread is alive? : " + mthread.isAlive());

            mthread.start();

            LOGD(TAG, "starttimerthread");
            LOGD(TAG, "timer state is : " + mSTATE);
//        }
    }

    public void stop(){
        mSTATE = STOP;
        LOGD(TAG,"stopTimer");

        double endTime = SystemClock.elapsedRealtime();
//        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        preferences = mContext.getSharedPreferences("saveRecord", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("ELAPSEDTIME", Double.toString(elapsedTime));
        editor.commit();
    }

    private void resume(){
//        preferences = mContext.getSharedPreferences("saveRecord",Context.MODE_PRIVATE)
//                ;
        startTime = SystemClock.elapsedRealtime();
//      double endTime = Double.parseDouble(preferences.getString("ENDTIME", "0"));

        elapsedTimeBuff = Double.parseDouble(preferences.getString("ELAPSEDTIME", ""));
        elapsedTime = 0;
//        startTime = startTime - elapsedTimeBuff;

        LOGD(TAG, "restored elapsedBuff: " + RecordUtil.milliseconsToStringFormat(elapsedTimeBuff));

    }

  class TimerThread implements Runnable {
        public double timeInterval;

        @Override
        public void run() {
            LOGD(TAG,"runnable");


            while(mSTATE == START) {
                timeInterval = SystemClock.elapsedRealtime() - startTime;
                LOGD(TAG, "stat time! : " + startTime);

                elapsedTime = timeInterval+elapsedTimeBuff;

                int seconds = (int) ((elapsedTime)/ 1000);
                int minutes = (seconds / 60)%60;
                seconds = seconds % 60;
                String stringTime = RecordUtil.milliseconsToStringFormat(elapsedTime);
                //UI 처리를 위한 hanlder, message
                Message msg = mhandler.obtainMessage(MSG_TIME);
                Bundle bundle = new Bundle();
                bundle.putString("time", stringTime);
                LOGD(TAG, "TIME BEFORE MESSAGING : " +RecordUtil.milliseconsToStringFormat(elapsedTime));
                msg.setData(bundle);
                mhandler.sendMessage(msg);
                try {
                    //1초간격
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            LOGD(TAG,"Thread is alive? : "+mthread.isAlive());
            LOGD(TAG, "while문 빠져나감" + "state " + mSTATE);
        }

    }

}
