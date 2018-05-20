package com.skrefi.newsfeed;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Event>> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String URL_API = "https://content.guardianapis.com/search";
    private static final String API_KEY_VALUE = "?api-key=183ff7b6-9144-47ff-800c-dd77882d5d0c&show-tags=contributor";
    private static final String API_KEY_TAG = "api-key";
    private static final String SECTION_TAG = "section";
    private static final String DATE_TAG = "from-date";
    private static final String CONTRIBUTOR_TAG = "show-tags";
    private static final String CONTRIBUTOR_VALUE = "contributor";

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
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if (isConnected) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            listView.setVisibility(View.GONE);
            tvErrorMessage.setText(R.string.no_internet_message);
            tvErrorMessage.setVisibility(View.VISIBLE);
        }


    }

    private void setUI() {
        adapter = new EventAdapter(this, new ArrayList<Event>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = adapter.getItem(position);
                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(event.getUrl())));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String category = preferences.getString("order_by_tag", getString(R.string.tag_list_default_value));

        String date = preferences.getString("order_by_date", getString(R.string.date_list_2018_default_value));

        Uri uri = Uri.parse(URL_API);
        Uri.Builder builder = uri.buildUpon();

        builder.appendQueryParameter(SECTION_TAG, category);
        builder.appendQueryParameter(CONTRIBUTOR_TAG, CONTRIBUTOR_VALUE);
        builder.appendQueryParameter(DATE_TAG, date);
        builder.appendQueryParameter(API_KEY_TAG, API_KEY_VALUE);

        return new EventLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> data) {
        progressBar.setVisibility(View.GONE);

        adapter.clear();

        if (data != null && !data.isEmpty()) {
            adapter.addAll(data);
        } else {
            tvErrorMessage.setText(R.string.no_event_found);
            tvErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {
        adapter.clear();
    }
}
