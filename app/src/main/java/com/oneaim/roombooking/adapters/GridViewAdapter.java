package com.oneaim.roombooking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.oneaim.roombooking.R;
import com.oneaim.roombooking.helper.APIEndpoints;
import com.oneaim.roombooking.helper.AnimateFirstDisplayListener;


/**
 * Created by carloscorreia on 19/08/16.
 */
public class GridViewAdapter extends BaseAdapter {
    private String[] mItems = new String[1];
    private LayoutInflater mInflater;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private DisplayImageOptions options;


    public GridViewAdapter(Context context,String[] items) {
        mInflater = LayoutInflater.from(context);
        mItems = items;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.room_default)
                .showImageOnFail(R.drawable.error_thumb)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(1500)).build();
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public String getItem(int i) {
        return mItems[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;

        if (v == null) {
            v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView) v.getTag(R.id.picture);
        name = (TextView) v.getTag(R.id.text);

        ImageLoader.getInstance()
                .displayImage(APIEndpoints.API_URL + getItem(i), picture, options, animateFirstListener);


        name.setText("Image number #" + i);

        return v;
    }

}
