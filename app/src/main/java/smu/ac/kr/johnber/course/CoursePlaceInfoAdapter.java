package smu.ac.kr.johnber.course;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import smu.ac.kr.johnber.R;
import smu.ac.kr.johnber.opendata.PlaceAPImodel.Photo;
import smu.ac.kr.johnber.opendata.PlaceAPImodel.Result;
import smu.ac.kr.johnber.opendata.PlaceAPImodel.Review;
import smu.ac.kr.johnber.opendata.network.ApiService;
import smu.ac.kr.johnber.opendata.network.ApiServiceGenerator;

import static smu.ac.kr.johnber.util.LogUtils.LOGD;
import static smu.ac.kr.johnber.util.LogUtils.makeLogTag;

public class CoursePlaceInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = makeLogTag(CoursePlaceInfoAdapter.class);

    private final static int VIEW_TYPE_IMG = 0;
    private final static int VIEW_TYPE_REVIEW = 1;

    private Result data;
    private List<Photo> photos;
    private Context context;
    private RequestManager glide;
    private final int VEIW_TYPE;

    public CoursePlaceInfoAdapter(Result data, Context context, int layouttype) {
        this.data = data;
        this.context = context;
        this.VEIW_TYPE = layouttype;
    }


    @Override
    public int getItemViewType(int position) {
        return VEIW_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_IMG) {
            View view = inflater.inflate(R.layout.course_detail_additional_item, parent, false);
            CoursePlaceInfoViewHolder viewHolder = new CoursePlaceInfoViewHolder(view);
            return viewHolder;
        } else if (viewType == VIEW_TYPE_REVIEW) {
            View view = inflater.inflate(R.layout.course_detail_additional_review_item, parent, false);
            CourseReviewHolder viewHolder = new CourseReviewHolder(view);
            return viewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CoursePlaceInfoViewHolder) {
            //해당 위치의 사진정보있는경우 리사이클러뷰에 표시
            final String BASE_URL_PLACE = "https://maps.googleapis.com/maps/api/place/photo?";
            final int MAX_WIDTH = 400;
            final String photoref = data.getPhotos().get(position).getPhotoReference();
            final String url = BASE_URL_PLACE + "maxwidth=" + Integer.toString(MAX_WIDTH) + "&photoreference=" + photoref + "&key=" + ApiService.GOOGLE_MAPS_API_SERVICE_KEY;
            Uri uri = Uri.parse(url);
            LOGD(TAG, "DownloadIMG_path : " + uri.toString());
            if (uri != null) {
                //placeholder : 이미지 로딩중 미리 보여지는 이미지
                Glide.with(context)
                        .load(uri)
                        .apply(new RequestOptions().centerCrop()
                                .placeholder(R.drawable.ic_glide_placeholder)
                                .error(R.drawable.ic_glide_placeholder)
                        )
                        .thumbnail(0.1f)
                        .into(((CoursePlaceInfoViewHolder) holder).imgview);
            }
        } else if (holder instanceof CourseReviewHolder) {
            Review review = data.getReviews().get(position);
            ((CourseReviewHolder) holder).comment.setText(review.getText());
            ((CourseReviewHolder) holder).name.setText(review.getAuthorName());
            ((CourseReviewHolder) holder).date.setText(review.getRelativeTimeDescription());
            //프로파일사진 가져오기
            Uri uri = Uri.parse(review.getProfilePhotoUrl());
            if (uri != null) {
                Glide.with(context)
                        .load(uri)
                        .apply(new RequestOptions().centerCrop()
                                .placeholder(R.drawable.ic_glide_placeholder)
                                .error(R.drawable.ic_glide_placeholder)
                        )
                        .thumbnail(0.1f)
                        .into(((CourseReviewHolder) holder).profile);
            }
        }


    }

    @Override
    public int getItemCount() {
        if (VEIW_TYPE == VIEW_TYPE_IMG) {
            return data.getPhotos().size();
        } else if (VEIW_TYPE == VIEW_TYPE_REVIEW) {
            return data.getReviews().size();
        }
        return 0;
    }

}
