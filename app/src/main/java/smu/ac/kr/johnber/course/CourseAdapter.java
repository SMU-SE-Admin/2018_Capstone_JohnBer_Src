package smu.ac.kr.johnber.course;


import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;

/**
 * Created by yj34 on 22/03/2018.
 */

public class CourseAdapter extends RealmBasedRecyclerViewAdapter<RunningCourse, CourseViewHolder> implements Filterable {

    private final static String TAG = makeLogTag(CourseAdapter.class);
    private RealmResults<RunningCourse> data;
    private CourseViewHolder.itemClickListener listener;
    private Realm mRealm;

    public CourseAdapter(Context context, RealmResults<RunningCourse> data, boolean automaticUpdate,
                         boolean animateResults, CourseViewHolder.itemClickListener listener) {
        super(context, data, automaticUpdate, animateResults);
        this.data = data;
        this.listener = listener;
        Realm.init(getContext());
        mRealm = Realm.getDefaultInstance();


    }

    @Override

    public CourseViewHolder onCreateRealmViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.course_item_collapsed, parent, false);
        CourseViewHolder viewHolder = new CourseViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindRealmViewHolder(CourseViewHolder holder, int position) {
        final RunningCourse courseItem = data.get(position);
        holder.courseName.setText(courseItem.getCourseName());
        holder.startPoint.setText(courseItem.getStartPoint());
        holder.distance.setText(courseItem.getDistance());
        holder.time.setText(courseItem.getTime());

        holder.KM.setText("KM");
        holder.TIME.setText("TIME");


    }

    @Override
    public int getItemCount() {
        LOGD(TAG, "item size :  " + data.size());
        return data.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                return new FilterResults();
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                String charString = charSequence.toString();
                if (!charString.isEmpty()) {
                    RealmResults<RunningCourse> filteredList = mRealm.where(RunningCourse.class)
                            .contains("startPointRoadAddr", charString)
                            .or()
                            .contains("startPointAddr", charString)
                            .or()
                            .contains("endPointRoadAddr", charString)
                            .or()
                            .contains("endPointAddr", charString)
                            .or()
                            .contains("courseName", charString)
                            .findAll();
                    data = filteredList;
                    LOGD(TAG, "filtered result : " + data.size());
                }


                notifyDataSetChanged();
            }
        };
    }
}
