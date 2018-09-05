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
    private static int mSTATE;
    private double startTime;
    private double elapsedTime;
    private double elapsedTimeBuff;
    private Thread mthread;
    private SharedPreferences preferences;

    public Timer(Context context) {
        mContext = context;
        preferences = mContext.getSharedPreferences("saveRecord", Context.MODE_PRIVATE)
        ;

    }

    private static final String TAG = makeLogTag(Timer.class);

    public void start(Handler handler, int action) {
//        LOGD(TAG, "startTimer, action : " + action);
        switch (action) {
            case INIT:
                mSTATE = INIT;
                break;

            case RESUME:
                mSTATE = RESUME;
                break;
        }

        if (mSTATE == INIT || mSTATE == STOP) {
            elapsedTime = 0;
            startTime = SystemClock.elapsedRealtime();
            elapsedTime = 0;
            elapsedTimeBuff = 0;
        }
        if (mSTATE == RESUME) {
            resume();
//            LOGD(TAG, "RESUME TIMER " + elapsedTimeBuff);
        }
        mSTATE = START;
        mhandler = handler;

        mthread = new Thread(new TimerThread());
        mthread.start();
    }

    public void stop() {
        mSTATE = STOP;

        //sharedPreferences에 시간 기록 저장
        LOGD(TAG, "stopTimer");
        double endTime = SystemClock.elapsedRealtime();
        SharedPreferences.Editor editor;
        preferences = mContext.getSharedPreferences("saveRecord", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("ENDTIME", Double.toString(endTime));
        editor.putString("ELAPSEDTIME", Double.toString(elapsedTime));
        editor.commit();
    }

    private void resume() {

        //sharedPreferences로부터 시간 기록 복원
        startTime = SystemClock.elapsedRealtime();
        elapsedTimeBuff = Double.parseDouble(preferences.getString("ELAPSEDTIME", ""));
        elapsedTime = 0;
    }

    class TimerThread implements Runnable {
        public double timeInterval;

        @Override
        public void run() {

            while (mSTATE == START) {
                timeInterval = SystemClock.elapsedRealtime() - startTime;

                // 현재 시간 = 측정 재개(시작) 시간으로 부터 경과한 시간 + 이전 시간기록
                elapsedTime = timeInterval + elapsedTimeBuff;
                String stringTime = RecordUtil.milliseconsToStringFormat(elapsedTime);

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

    }

}
