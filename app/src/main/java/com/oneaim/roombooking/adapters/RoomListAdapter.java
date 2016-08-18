package com.oneaim.roombooking.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.oneaim.roombooking.R;
import com.oneaim.roombooking.RoomsActivity;
import com.oneaim.roombooking.helper.APIEndpoints;
import com.oneaim.roombooking.helper.AnimateFirstDisplayListener;
import com.oneaim.roombooking.helper.RoomReadyListener;
import com.oneaim.roombooking.models.Room;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by carloscorreia on 18/08/16.
 */
public class RoomListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private RoomReadyListener listener;
    private LayoutInflater inflater;
    private DisplayImageOptions options, optionsSingle;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public RoomListAdapter(Context context,RoomReadyListener listener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.room_default)
                .showImageOnFail(R.drawable.error_thumb)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(80)).build();

        optionsSingle = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.room_default)
                .showImageOnFail(R.drawable.error_thumb)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(1500)).build();
    }



    @Override
    public int getGroupCount() {
        return listener.getRooms().size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return listener.getRooms().get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listener.getRooms().get(i);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        final RoomMainHolder holder;


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_room_main, viewGroup,false);
            holder = new RoomMainHolder();

            holder.mainPicture = (ImageView) convertView.findViewById(R.id.roomMainPicture);
            holder.name = (TextView) convertView.findViewById(R.id.roomNameValue);
            holder.location = (TextView) convertView.findViewById(R.id.roomLocationValue);
            holder.size = (TextView) convertView.findViewById(R.id.roomSizeValue);
            holder.capacity = (TextView) convertView.findViewById(R.id.roomCapacValue);

            convertView.setTag(holder);

        } else {
            holder = (RoomMainHolder) convertView.getTag();

        }

        Room r = (Room) getGroup(i);

        ImageLoader.getInstance()
                .displayImage(APIEndpoints.API_URL + r.images[0], holder.mainPicture, options, animateFirstListener);

        holder.name.setText(r.name);
        holder.location.setText(r.location);
        holder.size.setText(r.size);
        holder.capacity.setText(String.valueOf(r.capacity));

        return convertView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        final RoomDetailsHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_room_details, viewGroup,false);
            holder = new RoomDetailsHolder();

            holder.availabilityValue = (TextView) convertView.findViewById(R.id.availabilityListValue);
            holder.equipmentValue = (TextView) convertView.findViewById(R.id.equipmentListValue);
            holder.pictureLayout = (LinearLayout) convertView.findViewById(R.id.pictureLayout);
            convertView.setTag(holder);

        } else {
            holder = (RoomDetailsHolder) convertView.getTag();
        }

        Room r = (Room) getGroup(i);
        String equipment = "";
        for(String singleEq : r.equipment) {
            equipment += StringUtils.abbreviate(singleEq,12) + "\n";
        }
        holder.equipmentValue.setText(equipment);

        String availability = "";
        for(String singleAv : r.availability) {
            availability += singleAv + "\n";
        }

        holder.pictureLayout.removeAllViews();
        for(String singlePic : r.images) {
            ImageView image = new ImageView(context);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            image.setMaxHeight(100);
            image.setPadding(15,15,15,15);
            holder.pictureLayout.addView(image);
            ImageLoader.getInstance()
                    .displayImage(APIEndpoints.API_URL + singlePic, image, optionsSingle, animateFirstListener);
        }

        holder.availabilityValue.setText(availability);



        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    static class RoomMainHolder {
        TextView name,location,size,capacity;
        ImageView mainPicture;
    }

    static class RoomDetailsHolder {
        TextView equipmentValue;
        TextView availabilityValue;
        LinearLayout pictureLayout;
    }
}
