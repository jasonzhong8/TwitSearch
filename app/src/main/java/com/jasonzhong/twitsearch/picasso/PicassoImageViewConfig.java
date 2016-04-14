package com.jasonzhong.twitsearch.picasso;

import android.content.Context;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/**
 * Created by jason.zhong on 14/04/2016.
 */
public class PicassoImageViewConfig {
    private Context context;
    private String imageUrl;
    private Integer defaultImageResource;
    private Integer errorImageResource;
    private Boolean debugIndicators;
    private Boolean fitImage;

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getDefaultImageResource() {
        return defaultImageResource;
    }

    public Integer getErrorImageResource() {
        return errorImageResource;
    }

    public Boolean getDebugIndicators() {
        return debugIndicators;
    }

    public PicassoImageViewConfig(Context context) {
        this.context = context;
    }

    public PicassoImageViewConfig imageUrl(String imageUrl) {
        // TODO: 2016-03-02 Revisit https header issue, should be done on the backend
        String imageUrlString = imageUrl.startsWith("https:") || imageUrl.startsWith("http:") ? "" : "https:";
        imageUrlString += imageUrl;
        this.imageUrl = imageUrlString;
        return this;
    }

    public PicassoImageViewConfig defaultImageResource(Integer defaultImageResource) {
        this.defaultImageResource = defaultImageResource;
        return this;
    }

    public PicassoImageViewConfig errorImageResource(Integer errorImageResource) {
        this.errorImageResource = errorImageResource;
        return this;
    }

    public PicassoImageViewConfig fit() {
        this.fitImage = true;
        return this;
    }

    public PicassoImageViewConfig setDebugIndicators(boolean enabled) {
        this.debugIndicators = enabled;
        return this;
    }

    public RequestCreator buildPicassoRequest() {
        Picasso picasso = Picasso.with(context);

        if (debugIndicators != null) {
            picasso.setIndicatorsEnabled(debugIndicators);
        }

        RequestCreator picassoRequest = picasso.load(imageUrl);

        if (defaultImageResource != null) {
            picassoRequest.placeholder(defaultImageResource);
        }

        if (errorImageResource != null) {
            picassoRequest.error(errorImageResource);
        }

        if (fitImage != null && fitImage) {
            picassoRequest.fit();
        }

        return picassoRequest;
    }
}

