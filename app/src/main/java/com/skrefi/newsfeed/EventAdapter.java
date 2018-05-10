package com.skrefi.newsfeed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(Context context, List<Event> events){
        super(context,0,events);
    }

    public static class ViewHolder{
        TextView tvTitle;
        TextView tvCategory;
        TextView tvAuthor;
        TextView tvDate;

        ViewHolder(View v){
            tvTitle = v.findViewById(R.id.template_title_textview);
            tvCategory = v.findViewById(R.id.template_category_textview);
            tvAuthor = v.findViewById(R.id.template_author_textview);
            tvDate = v.findViewById(R.id.template_date_textview);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Event event = getItem(position);
        if(event!=null){
            viewHolder.tvTitle.setText(event.getTitle());
            viewHolder.tvCategory.setText(event.getCategory());
            viewHolder.tvAuthor.setText(event.getAuthor());
            viewHolder.tvDate.setText(event.getDate());
        }

        return convertView;
    }
}
