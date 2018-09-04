package smu.ac.kr.johnber.course;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import smu.ac.kr.johnber.R;

public class CourseReviewHolder extends RecyclerView.ViewHolder{

    //구글 place detail 에서 받아온 review
    public ImageView profile;
    public TextView name;
    public TextView date;
    public TextView comment;




    public CourseReviewHolder(View itemView) {
        super(itemView);
        profile = itemView.findViewById(R.id.iv_course_detail_ad_profile);
        name = itemView.findViewById(R.id.tv_course_detail_ad_review_name);
        date = itemView.findViewById(R.id.tv_course_detail_ad_review_dates);
        comment = itemView.findViewById(R.id.tv_course_detail_ad_review_comments);
    }


}
