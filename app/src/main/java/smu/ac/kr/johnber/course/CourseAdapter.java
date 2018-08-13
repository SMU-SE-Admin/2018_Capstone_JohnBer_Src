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
import io.realm.RealmResults;
import io.realm.Sort;
import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.APImodel.RunningCourse;

/**
 * Created by yj34 on 22/03/2018.
 */

public class CourseAdapter extends RealmBasedRecyclerViewAdapter<RunningCourse, CourseViewHolder> implements Filterable{

  private final static String TAG = makeLogTag(CourseAdapter.class);
  private RealmResults<RunningCourse> data;
  private RealmResults<RunningCourse> dataFiltered;
  private CourseViewHolder.itemClickListener listener;

  public CourseAdapter(Context context, RealmResults<RunningCourse> data, boolean automaticUpdate,
                       boolean animateResults,CourseViewHolder.itemClickListener listener) {
      super(context, data, automaticUpdate, animateResults);
      this.data  = data;
      this.listener = listener;
//      Realm realm = Realm.getDefaultInstance();
//      this.data= realm
//              .where(RunningCourse.class).not()
//              .beginGroup().equalTo("distance", "null").and().equalTo("time","null").endGroup()
//              .sort("length", Sort.DESCENDING ).findAllAsync();

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

  }

  @Override
  public int getItemCount() {
      LOGD(TAG,"item size :  "+data.size());
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
                if (charString.isEmpty()) {
                    dataFiltered = data;
                } else {
                    List<RunningCourse> filteredList = new ArrayList<>();
                    for (RunningCourse course : data) {

                        if (course.getStartPointRoadAddr().contains(charString) ||
                                course.getStartPointAddr().contains(charString) ||
                                course.getEndPointRoadAddr().contains(charString) ||
                                course.getEndPointAddr().contains(charString) ||
                                course.getCourse().contains(charString)) {
                            filteredList.add(course);
                        }
                    }

                    ArrayList<> list = new ArrayList(mRealm.where(People.class).findAll())
                    dataFiltered = (RealmResults<RunningCourse>) filteredList;
                }

//                FilterResults filterResults = new FilterResults();
                filterResults.values = dataFiltered;

               dataFiltered = (RealmResults<RunningCourse>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
