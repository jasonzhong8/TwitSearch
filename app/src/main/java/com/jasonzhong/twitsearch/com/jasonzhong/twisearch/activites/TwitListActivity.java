package com.jasonzhong.twitsearch.com.jasonzhong.twisearch.activites;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jasonzhong.twitsearch.R;
import com.jasonzhong.twitsearch.adapters.SearchAdapter;
import com.jasonzhong.twitsearch.models.Authenticated;
import com.jasonzhong.twitsearch.models.Search;
import com.jasonzhong.twitsearch.models.SearchResults;
import com.jasonzhong.twitsearch.models.Searches;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TwitListActivity extends AppCompatActivity {

    final static String LOG_TAG = "TwitListActivity";
    private SearchAdapter searchAdapter;

    @Bind(R.id.searchRecyclerView) RecyclerView searchRecyclerView;
    @Bind(R.id.searchEditText) EditText searchEditText;
    @Bind(R.id.searchButton) Button searchButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twit_list);
        ButterKnife.bind(this);

        searchButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(TwitListActivity.this.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String searchTerm = searchEditText.getText().toString();
                if (!searchTerm.isEmpty()) {
                    downloadSearches(searchTerm);
                }
            }
        });

        searchAdapter = new SearchAdapter(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        searchRecyclerView.setLayoutManager(mLayoutManager);

        searchRecyclerView.setLayoutManager(mLayoutManager);
        searchRecyclerView.setAdapter(searchAdapter);
    }

    // download twitter searches after first checking to see if there is a network connection
    public void downloadSearches(String searchTerm) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadTwitterTask().execute(searchTerm);
        } else {
            Log.v(LOG_TAG, "No network connection available.");
        }
    }

    private class DownloadTwitterTask extends AsyncTask<String, Void, String> {
        //TODO: Following strings can be retrieved from resource files
        final static String CONSUMER_KEY = "kbspDjgn3qa3HAG9BREv78Jl8";
        final static String CONSUMER_SECRET = "BeacS45HtTN1VoIfKyd6G7fB9QSsS4fxikczVuTRVnSfvXuZtQ";

        final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
        final static String TwitterStreamURL = "https://api.twitter.com/1.1/search/tweets.json?q=";
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            pDialog = new ProgressDialog(TwitListActivity.this);
            pDialog.setMessage("Loading Tweets....");
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... searchTerms) {
            String result = null;

            if (searchTerms.length > 0) {
                result = getSearchStream(searchTerms[0]);
            }
            return result;
        }

        // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
        @Override
        protected void onPostExecute(String result) {
            if (result == null || result.isEmpty()) {
                pDialog.dismiss();
                return;
            }
            Searches searches = jsonToSearches(result);
            if (searches == null) {
                pDialog.dismiss();
                return;
            }
            List<Search> searchList = new ArrayList<Search>();

            // lets write the results to the console as well
            for (Search search : searches) {
                Log.i(LOG_TAG, search.getText());
                searchList.add(search);
            }

            // send the tweets to the adapter for rendering
            searchAdapter.setDisplayList(searchList);
            searchAdapter.notifyDataSetChanged();
            searchRecyclerView.setVisibility(View.VISIBLE);
            //setListAdapter(adapter);
            pDialog.dismiss();
        }

        // converts a string of JSON data into a SearchResults object
        private Searches jsonToSearches(String result) {
            Searches searches = null;
            if (result != null && result.length() > 0) {
                try {
                    Gson gson = new Gson();
                    // bring back the entire search object
                    SearchResults sr = gson.fromJson(result, SearchResults.class);
                    // but only pass the list of tweets found (called statuses)
                    searches = sr.getStatuses();
                } catch (IllegalStateException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
            return searches;
        }

        // convert a JSON authentication object into an Authenticated object
        private Authenticated jsonToAuthenticated(String rawAuthorization) {
            Authenticated auth = new Authenticated();
            if (rawAuthorization != null && rawAuthorization.length() > 0) {

                try {
                    JSONObject session = new JSONObject(rawAuthorization);
                    auth.access_token = session.getString("access_token");
                    auth.token_type = session.getString("token_type");
                } catch (Exception e) {
                    Log.e("jsonToAuthenticated", "Error retrieving JSON Authenticated Values : " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return auth;
        }

        private String getResponseBody(HttpRequestBase request) {
            StringBuilder sb = new StringBuilder();
            try {

                DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
                HttpResponse response = httpClient.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                String reason = response.getStatusLine().getReasonPhrase();

                if (statusCode == 200) {

                    HttpEntity entity = response.getEntity();
                    InputStream inputStream = entity.getContent();

                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    String line = null;
                    while ((line = bReader.readLine()) != null) {
                        sb.append(line);
                    }
                } else {
                    sb.append(reason);
                }
            } catch (UnsupportedEncodingException ex) {
            } catch (ClientProtocolException ex1) {
            } catch (IOException ex2) {
            }
            return sb.toString();
        }

        private String getTwitterStream(String url) {
            String results = null;

            // Step 1: Encode consumer key and secret
            try {
                // URL encode the consumer key and secret
                String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
                String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");

                // Concatenate the encoded consumer key, a colon character, and the
                // encoded consumer secret
                String combined = urlApiKey + ":" + urlApiSecret;

                // Base64 encode the string
                String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

                // Step 2: Obtain a bearer token
                HttpPost httpPost = new HttpPost(TwitterTokenURL);
                httpPost.setHeader("Authorization", "Basic " + base64Encoded);
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
                String rawAuthorization = getResponseBody(httpPost);
                Log.i("getTwitterStream", "rawAuthoruzation : " + rawAuthorization);
                Authenticated auth = jsonToAuthenticated(rawAuthorization);

                // Applications should verify that the value associated with the
                // token_type key of the returned object is bearer
                if (auth != null && auth.token_type.equals("bearer")) {

                    // Step 3: Authenticate API requests with bearer token
                    HttpGet httpGet = new HttpGet(url);

                    // construct a normal HTTPS request and include an Authorization
                    // header with the value of Bearer <>
                    httpGet.setHeader("Authorization", "Bearer " + auth.access_token);
                    httpGet.setHeader("Content-Type", "application/json");
                    // update the results with the body of the response
                    results = getResponseBody(httpGet);
                } else {
                    Log.e("", "");
                }
            } catch (UnsupportedEncodingException ex) {
                Log.i("UnsupportedEncodingEx", ex.toString());
            } catch (IllegalStateException ex1) {
                Toast.makeText(getApplicationContext(), "Couldn't find specified user : ", Toast.LENGTH_SHORT).show();
                Log.i("IllegalStateException", ex1.toString());
            }
            return results;
        }

        private String getSearchStream(String searchTerm) {
            String results = null;
            try {
                String encodedUrl = URLEncoder.encode(searchTerm, "UTF-8");
                results = getTwitterStream(TwitterStreamURL + encodedUrl);
            } catch (UnsupportedEncodingException ex) {
            } catch (IllegalStateException ex1) {
            }
            return results;
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
