package au.com.hbuy.aotong.base.impl;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import au.com.hbuy.aotong.R;
import au.com.hbuy.aotong.view.photoview.PhotoViewAttacher;


public class OrderUserGuideFragment extends Fragment {
    private static Context mContext;
    private int mImageUrl;
    private ImageView mImageView;
    private PhotoViewAttacher mAttacher;

    public static OrderUserGuideFragment newInstance(int imageUrl, Context context) {
        mContext = context;
        final OrderUserGuideFragment f = new OrderUserGuideFragment();
        final Bundle args = new Bundle();
        args.putInt("url", imageUrl);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getInt("url") : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_user_guide_image_detail, container, false);
        mImageView = (ImageView) v.findViewById(R.id.image);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mImageView.setImageResource(mImageUrl);
    }
}
