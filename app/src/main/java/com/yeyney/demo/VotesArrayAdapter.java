package com.yeyney.demo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

public class VotesArrayAdapter extends ArrayAdapter<VotesActivity.Shared> {

    private Context context;
    private int resource;

    private ImageLoader imageLoader;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_stub)
            .showImageForEmptyUri(R.drawable.ic_empty)
            .showImageOnFail(R.drawable.ic_error)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .displayer(new RoundedBitmapDisplayer(20))
            .build();

    public VotesArrayAdapter(Context context, int resource, List<VotesActivity.Shared> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, resource, null);
            holder.message = (TextView) convertView.findViewById(R.id.textView_shared_message);
            holder.image = (ImageView) convertView.findViewById(R.id.imageView_shared_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        VotesActivity.Shared shared = getItem(position);
        holder.message.setText(shared.message);
        imageLoader.displayImage(shared.image, holder.image, options);

        return convertView;
    }

    private class ViewHolder {
        TextView message;
        ImageView image;
    }
}
