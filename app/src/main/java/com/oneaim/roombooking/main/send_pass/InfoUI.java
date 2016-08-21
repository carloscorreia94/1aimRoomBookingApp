package com.oneaim.roombooking.main.send_pass;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.oneaim.roombooking.R;
import com.oneaim.roombooking.helper.UIHelpers;


/**
 * Created by carloscorreia on 21/08/16.
 */
public class InfoUI {

    private View view;
    private Context context;

    private EditText vEventName,vEventDescription;
    private String eventName, eventDescription;
    public InfoUI(View view, Context context) {
        this.view = view;
        this.context = context;
    }


    public void renderLayout() {
        vEventName = (EditText) view.findViewById(R.id.event_name);
        vEventDescription = (EditText) view.findViewById(R.id.event_description);

        vEventName.setOnFocusChangeListener(new UIHelpers.EditTextFocusChangeListener(context));
        vEventDescription.setOnFocusChangeListener(new UIHelpers.EditTextFocusChangeListener(context));
    }


    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public boolean canProceed() {
        eventName = vEventName.getText().toString();
        eventDescription = vEventDescription.getText().toString();

        vEventName.setError(null);
        vEventDescription.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(eventName)) {
            vEventName.setError(context.getString(R.string.sendpass_info_empty_event_name));
            focusView = vEventName;
            cancel = true;
        }

        if (TextUtils.isEmpty(eventDescription)) {
            vEventDescription.setError(context.getString(R.string.sendpass_info_empty_event_desc));
            focusView = vEventDescription;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }


}
