package com.oneaim.roombooking.helper;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.oneaim.roombooking.R;

/**
 * Created by carloscorreia on 18/08/16.
 */
public class UIHelpers {

     public static final class Constants{
         public static DisplayImageOptions optionsMainRoom = new DisplayImageOptions.Builder()
                 .showImageOnLoading(R.drawable.loading)
                 .showImageForEmptyUri(R.drawable.room_default)
                 .showImageOnFail(R.drawable.error_thumb)
                 .cacheInMemory(true)
                 .cacheOnDisk(true)
                 .considerExifParams(true)
                 .displayer(new RoundedBitmapDisplayer(80)).build();
         public static ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

     }
}
