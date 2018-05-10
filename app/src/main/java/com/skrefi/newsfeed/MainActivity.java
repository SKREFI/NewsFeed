package com.skrefi.newsfeed;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Event>> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EventAdapter adapter;

    ListView listView = findViewById(R.id.list);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private void setUI(){
        adapter = new EventAdapter(this, new ArrayList<Event>());
        listView.setAdapter(adapter);
        listView.setOnClickListener(new AdapterView.OnItemClickListener(){
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
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> data) {

    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {
        adapter.clear();
    }
}
