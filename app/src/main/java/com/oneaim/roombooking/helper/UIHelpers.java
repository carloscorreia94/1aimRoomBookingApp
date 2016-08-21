package com.oneaim.roombooking.helper;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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


    public static final class EditTextFocusChangeListener implements View.OnFocusChangeListener {

        private Context context;

        public EditTextFocusChangeListener(Context context) {
            this.context = context;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
}
