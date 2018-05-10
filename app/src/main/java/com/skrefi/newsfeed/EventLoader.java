package com.skrefi.newsfeed;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class EventLoader extends AsyncTaskLoader<List<Event>>{

    private final String mUrl;

    public EventLoader(Context context,String url){
        super(context);
        mUrl=url;
    }

    @Override
    public List<Event> loadInBackground() {
        return Utils.fetchEventsData(mUrl);
    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }
}
