package com.jasonzhong.twitsearch.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasonzhong.twitsearch.R;
import com.jasonzhong.twitsearch.com.jasonzhong.twisearch.activites.TwitDetailActivity;
import com.jasonzhong.twitsearch.com.jasonzhong.twisearch.activites.TwitListActivity;
import com.jasonzhong.twitsearch.custom_views.PicassoImageView;
import com.jasonzhong.twitsearch.models.Search;

import java.util.List;

/**
 * Created by jason.zhong on 14/04/2016.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.CustomViewHolder> {
    static Context context;
    static private List<Search> searchItems;

    public SearchAdapter(Context context) {
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // create a new view
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.search_item, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, final int i) {
        final Search searchItem = searchItems.get(i);
        customViewHolder.mRootView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                Bundle b = new Bundle();
                b.putSerializable("SearchDetail", searchItem);
                i.putExtras(b);
                i.setClass(context, TwitDetailActivity.class);
                context.startActivity(i);
            }
        });

        customViewHolder.avatarImageView.setImageUrl(searchItem.getUser().getProfileImageUrl(), null);
        ;
        customViewHolder.nameTextView.setText(searchItem.getUser().getName());
        customViewHolder.messageTextView.setText(searchItem.getText());
    }

    public void setDisplayList(List<Search> displayItems) {
        this.searchItems = displayItems;
    }

    @Override
    public int getItemCount() {
        return (null != searchItems ? searchItems.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected PicassoImageView avatarImageView;
        protected TextView nameTextView;
        protected TextView messageTextView;
        protected View mRootView;

        public CustomViewHolder(View view) {
            super(view);
            mRootView = view;

            this.avatarImageView = (PicassoImageView) view.findViewById(R.id.avatar);
            this.nameTextView = (TextView) view.findViewById(R.id.username);
            this.messageTextView = (TextView) view.findViewById(R.id.message);
        }
    }
}

