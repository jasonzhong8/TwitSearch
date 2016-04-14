package com.jasonzhong.twitsearch.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jason.zhong on 14/04/2016.
 */
public class SearchResults {

    @SerializedName("statuses")
    private Searches statuses;

    @SerializedName("search_metadata")
    private SearchMetadata metadata;


    public Searches getStatuses() {
        return statuses;
    }

    public void setStatuses(Searches statuses) {
        this.statuses = statuses;
    }

    public SearchMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SearchMetadata metadata) {
        this.metadata = metadata;
    }
}
