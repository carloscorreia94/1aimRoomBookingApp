package com.oneaim.roombooking.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/**
 * Created by carloscorreia on 19/08/16.
 */
public class NonScrollableGridView extends GridView {
    public NonScrollableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Do not use the highest two bits of Integer.MAX_VALUE because they are
        // reserved for the MeasureSpec mode
        int heightSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightSpec);
        getLayoutParams().height = getMeasuredHeight();
    }
}