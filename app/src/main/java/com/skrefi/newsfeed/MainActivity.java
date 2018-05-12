package com.skrefi.newsfeed;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Event>> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String URL_API = "https://content.guardianapis.com/search?q=debate&tag=politics/politics&from-date=2014-01-01&api-key=test";

    private EventAdapter adapter;

    ListView listView;
    TextView tvErrorMessage;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list);
        tvErrorMessage = findViewById(R.id.mainactivity_error_message_textview);
        progressBar = findViewById(R.id.mainactivity_indicator_progressbar);

        setUI();

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if(isConected){
            getLoaderManager().initLoader(0,null,this);
        }else{
            listView.setVisibility(View.GONE);
            tvErrorMessage.setText(R.string.no_internet_message);
            tvErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    private void setUI(){
        adapter = new EventAdapter(this, new ArrayList<Event>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = adapter.getItem(position);
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(event.getUrl())));
                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse(URL_API);
        Uri.Builder builder = uri.buildUpon();

        return new EventLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> data) {
        progressBar.setVisibility(View.GONE);

        adapter.clear();

        if(data != null && !data.isEmpty()){
            adapter.addAll(data);
        }else{
            tvErrorMessage.setText(R.string.no_event_found);
            tvErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {
        adapter.clear();
    }
}
