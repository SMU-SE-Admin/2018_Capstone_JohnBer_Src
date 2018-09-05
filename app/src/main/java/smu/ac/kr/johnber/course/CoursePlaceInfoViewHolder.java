package smu.ac.kr.johnber.course;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import smu.ac.kr.johnber.R;

public class  CoursePlaceInfoViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgview;


    public CoursePlaceInfoViewHolder(View itemView) {
        super(itemView);

        imgview = itemView.findViewById(R.id.cv_course_detail_info_additional_imgview);

    }
}
