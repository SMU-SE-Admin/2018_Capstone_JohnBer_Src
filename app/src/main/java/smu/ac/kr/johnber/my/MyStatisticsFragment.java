package smu.ac.kr.johnber.my;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import smu.ac.kr.johnber.R;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;

public class MyStatisticsFragment extends android.support.v4.app.Fragment{
    private Double km;
    private Double time;
    private Double cal;

    public MyStatisticsFragment() {
    }

    public static android.support.v4.app.Fragment newInstance(Double km, Double time, Double cal) {
        MyStatisticsFragment fragment = new MyStatisticsFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("km", km);
        bundle.putDouble("time", time);
        bundle.putDouble("cal",cal);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.km = getArguments().getDouble("km",0);
        this.time = getArguments().getDouble("time",0);
        this.cal = getArguments().getDouble("cal",0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_statistics_daily_view,container,false);


        TextView KM = view.findViewById(R.id.tv_statistics_daily_km);
        TextView TIME=  view.findViewById(R.id.tv_statistics_daily_time);
        TextView CAL = view.findViewById(R.id.tv_statistics_daily_calories);
        KM.setText(String.valueOf(km));
        TIME.setText(String.valueOf(time));
        CAL.setText(String.valueOf(cal));

        return view;
    }
}
