package com.jasonzhong.twitsearch.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jasonzhong.twitsearch.R;
import com.jasonzhong.twitsearch.picasso.PicassoImageViewConfig;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jason.zhong on 14/04/2016.
 */
public class PicassoImageView extends RelativeLayout {
    @Bind(R.id.picasso_image_view) ImageView picassoImageView;

    //// FIXME: 2016-01-07 Set debug flags from conf
    boolean debugIndicators = false;

    protected ImageView getImageView() {
        return picassoImageView;
    }

    public PicassoImageView(Context context) {
        super(context);
        instantiateView();
    }

    public PicassoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        instantiateView();
    }

    protected void instantiateView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.picasso_image_view_layout, this);
        ButterKnife.bind(this, v);
    }

    public void setImageWithConfig(PicassoImageViewConfig config) {
        config.setDebugIndicators(debugIndicators).buildPicassoRequest().into(getImageView());
    }

    public void setImageUrl(String url, Integer defaultImage) {
        PicassoImageViewConfig config = new PicassoImageViewConfig(getContext());
        config = config.setDebugIndicators(debugIndicators).imageUrl(url);

        if (defaultImage != null && defaultImage != 0) {
            config = config.defaultImageResource(defaultImage);
        }

        config.buildPicassoRequest().into(getImageView());
    }
}

