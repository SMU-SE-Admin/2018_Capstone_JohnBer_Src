package smu.ac.kr.johnber.my;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
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
  private RequestManager glide;

  public MyCourseAdapter(Context context, List<Record> data, MyCourseViewHolder.itemClickListener listener) {
    this.data = data;
    this.listener = listener;
    this.context = context;
  }

  @Override
  public MyCourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.course_item_collapsed, parent, false);
    MyCourseViewHolder viewHolder = new MyCourseViewHolder(view, listener);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(MyCourseViewHolder holder, int position) {
    final Record courseItem = data.get(position);
    holder.courseName.setText(courseItem.getTitle());
    holder.calories.setText(Double.toString(courseItem.getCalories()));
    String dist = RecordUtil.distanceToStringFormat(courseItem.getDistance());
    holder.distance.setText(dist);
    String time = RecordUtil.milliseconsToStringFormat(courseItem.getElapsedTime());
    holder.time.setText(time);

    /*
    String startPoint = LocationUtil.latlngtoStringLocation(data.get(0).getJBLocation().get(0),context);
    String startPointAddress[] = null;
    //String startPointAddress[] = startPoint.split(" ");
=======

    String startPoint = LocationUtil.latlngtoStringLocation(courseItem.getJBLocation().get(0),context);
    String startPointAddress[] = startPoint.split(" ");

    if(startPointAddress[2]!=null)
      startPoint = startPointAddress[2] + " " + startPointAddress[3];
    holder.startPoint.setText(startPoint);
    */

    holder.startPoint.setText("테스트");

    holder.CAL.setVisibility(View.VISIBLE);
    holder.CAL.setText("CAL");
    holder.KM.setText("KM");
    holder.TIME.setText("TIME");

    //썸네일 다운로드
    Uri uri = Uri.parse(data.get(position).getImgUrl());
//    LOGD(TAG,"DownloadIMG_path : "+ data.get(position).getImgUrl());
    if (uri != null) {
      //placeholder : 이미지 로딩중 미리 보여지는 이미지
    Glide.with(context)
            .load(uri)
            .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.ic_glide_placeholder)
                      .error(R.drawable.ic_glide_placeholder)
            )
            .thumbnail(0.1f)
            .into(holder.thumnail);
    //listener
    }
  }

  @Override
  public int getItemCount() {
    return data.size();
  }


  }

