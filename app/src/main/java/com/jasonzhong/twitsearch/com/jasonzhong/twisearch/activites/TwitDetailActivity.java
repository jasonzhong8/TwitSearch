package com.jasonzhong.twitsearch.com.jasonzhong.twisearch.activites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jasonzhong.twitsearch.R;
import com.jasonzhong.twitsearch.custom_views.PicassoImageView;
import com.jasonzhong.twitsearch.models.Search;

import java.io.Serializable;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jason.zhong on 14/04/2016.
 */
public class TwitDetailActivity extends AppCompatActivity {

    private Serializable searchItem;
    @Bind(R.id.detailTextView) TextView detailTextView;
    @Bind(R.id.image) PicassoImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twit_detail);
        ButterKnife.bind(this);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            searchItem = b.getSerializable("SearchDetail");
            imageView.setImageUrl(((Search) searchItem).getUser().getProfileImageUrl(), null);//TODO: the image can be scaled
            String display_st = "\n\n"+((Search) searchItem).getDateCreated()+"\n\n"
                    +((Search) searchItem).getSource()+"\n\n"+((Search) searchItem).getText()+"\n\n"
                    +((Search) searchItem).getUser().getName()+"\n\n"
                    +((Search) searchItem).getUser().getScreenName();
            detailTextView.setText(display_st);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
