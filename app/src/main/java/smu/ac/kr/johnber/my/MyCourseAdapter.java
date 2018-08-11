package smu.ac.kr.johnber.my;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.my.MyCourseViewHolder;
import smu.ac.kr.johnber.run.Record;
import smu.ac.kr.johnber.util.LocationUtil;
import smu.ac.kr.johnber.util.RecordUtil;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

public class MyCourseAdapter extends RecyclerView.Adapter<MyCourseViewHolder>{
  private final static String TAG = makeLogTag(MyCourseAdapter.class);
  private List<Record> data;
  private MyCourseViewHolder.itemClickListener listener;
  private Context context;

  public MyCourseAdapter(Context context, List<Record> data, MyCourseViewHolder.itemClickListener listener) {
    this.data = data;
    this.listener = listener;
    this.context = context;
  }

  @Override
  public MyCourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.my_course_item_collapsed, parent, false);
    MyCourseViewHolder viewHolder = new MyCourseViewHolder(view, listener);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(MyCourseViewHolder holder, int position) {
    final Record courseItem = data.get(position);
    holder.courseName.setText(courseItem.getTitle());
    holder.calories.setText(Double.toString(courseItem.getCalories()));
    holder.distance.setText(Double.toString(courseItem.getDistance()));
    String time = RecordUtil.milliseconsToStringFormat(courseItem.getElapsedTime());
    holder.time.setText(time);
    String startPoint = LocationUtil.latlngtoStringLocation(data.get(0).getJBLocation().get(0),context);
    String startPointAddress[] = startPoint.split(" ");
    startPoint = startPointAddress[2] + " " + startPointAddress[3];
    holder.startPoint.setText(startPoint);
  }

  @Override
  public int getItemCount() {
    return data.size();
  }
}
