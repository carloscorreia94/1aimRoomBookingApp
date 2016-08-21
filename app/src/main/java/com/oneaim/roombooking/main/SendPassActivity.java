package com.oneaim.roombooking.main;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneaim.roombooking.R;
import com.oneaim.roombooking.helper.APIEndpoints;
import com.oneaim.roombooking.helper.JSONRequestListener;
import com.oneaim.roombooking.helper.NetworkHelper;
import com.oneaim.roombooking.helper.UIHelpers;
import com.oneaim.roombooking.main.send_pass.ConfirmationUI;
import com.oneaim.roombooking.main.send_pass.InfoUI;
import com.oneaim.roombooking.main.send_pass.PassesUI;
import com.oneaim.roombooking.main.send_pass.SendPassesListener;
import com.oneaim.roombooking.models.Room;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SendPassActivity extends AppCompatActivity implements SendPassesListener, JSONRequestListener {

    public static final String TAG = SendPassActivity.class.getSimpleName();

    private ViewOnScreen viewOnScreen = ViewOnScreen.Base;
    private Room sendPassRoom;
    private NetworkHelper networkHelper;
    private boolean processingRequest = false;

    //UI Components

    private TextView toolBarTitle;
    private RelativeLayout toolBarLayout;

    private LinearLayout vBase, vPasses, vConfirm;
    private ImageView bGoBack, bGoFront, bComplete, bClose;

    //UI Header Components
    private ImageView roomMainPicture;
    private TextView roomName, roomDate;

    //UI Helpers
    private InfoUI infoUI;
    private PassesUI passesUI;
    private ConfirmationUI confirmUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkHelper = new NetworkHelper(APIEndpoints.SEND_PASS, Request.Method.POST);
        networkHelper.setListener(this);

        setContentView(R.layout.activity_send_pass);

        toolBarLayout = (RelativeLayout) findViewById(R.id.toolbar_layout);
        toolBarTitle = (TextView) toolBarLayout.findViewById(R.id.toolbar_title);

        bGoBack = (ImageView) toolBarLayout.findViewById(R.id.btn_img_go_back);
        bGoFront = (ImageView) toolBarLayout.findViewById(R.id.btn_img_go_front);
        bComplete = (ImageView) toolBarLayout.findViewById(R.id.btn_img_complete_task);
        bClose = (ImageView) toolBarLayout.findViewById(R.id.btn_img_close);

        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        vBase = (LinearLayout) findViewById(R.id.view_send_pass_base);
        vPasses = (LinearLayout) findViewById(R.id.view_send_pass_passes);
        vConfirm = (LinearLayout) findViewById(R.id.view_send_pass_confirm);

        infoUI = new InfoUI(vBase,getApplicationContext());
        infoUI.renderLayout();

        bGoBack.setOnClickListener(new GoBackClickHandler());
        bGoFront.setOnClickListener(new GoFrontClickHandler());
        bComplete.setOnClickListener(new CompleteClickHandler());

        sendPassRoom = Room.getCurrentRoom();
        roomName = (TextView) findViewById(R.id.room_number_desc);
        roomDate = (TextView) findViewById(R.id.room_date);
        roomMainPicture = (ImageView) findViewById(R.id.room_main_picture);

        Bundle args = getIntent().getExtras();

        roomName.setText(String.
                format(getString(R.string.send_pass_room_desc),sendPassRoom.name));
        roomDate.setText(args.getString("chosen_date"));

        ImageLoader.getInstance()
                .displayImage(APIEndpoints.API_URL + sendPassRoom.images[0],
                        roomMainPicture, UIHelpers.Constants.optionsMainRoom,
                        UIHelpers.Constants.animateFirstListener);





       // test = (TextView) findViewById(R.id.test);
       // test.setText(String.valueOf(args.getInt("room")));
    }

    public void sendPasses() {
        processingRequest = true;
        networkHelper.process();
    }

    public JSONObject getRequestBody() {
        HashMap<String, Object> values = new HashMap<>();
        HashMap<String, Object> bookingValues = new HashMap<>();

        long unixStamp = RoomsActivity.getSelectedDate().getMillis() / 1000;

        //Static values for timeline values, as it's not implemented.
        long staticFromTime = RoomsActivity.getSelectedDate().plusHours(12).getMillis() / 1000;
        long staticToTime = RoomsActivity.getSelectedDate().plusHours(23).getMillis() / 1000;

        bookingValues.put("date",unixStamp);
        bookingValues.put("time_start",staticFromTime);
        bookingValues.put("time_end",staticToTime);
        bookingValues.put("title",infoUI.getEventName());
        bookingValues.put("description",infoUI.getEventDescription());
        bookingValues.put("room",sendPassRoom.name);

        values.put("booking",bookingValues);
        values.put("passes",passesUI.getPasses());

        try {
            String json = new Gson().toJson(values);
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void successResponse(String response) {
        processingRequest = false;
        Log.i(TAG,response);
        try {
            JSONObject resp = new JSONObject(response);
            if(resp.has("success") && resp.getBoolean("success"))
                setSuccessConfirmation();
            else if(resp.has("error")) {
                switch(resp.getJSONObject("error").getInt("code")) {
                    case 3000:
                        setConfirmError(getString(R.string.sendpass_confirm_error_phone));
                        break;
                    default:
                        setConfirmError(String.format(getString(R.string.sendpass_confirm_error_code)
                                ,resp.getJSONObject("error").getInt("code")));
                }
            } else
                setConfirmError(getString(R.string.sendpass_confirm_error));

        } catch (JSONException e) {
            e.printStackTrace();
            setConfirmError(getString(R.string.error_parsing_data));
        }
    }

    public void errorResponse() {
        processingRequest = false;
        setConfirmError(getString(R.string.error_network_unknown));
    }

    public void setSuccessConfirmation() {
        confirmUI.setSuccess();
        toolBarTitle.setText(R.string.toolbar_title_success);
        bClose.setVisibility(View.VISIBLE);
    }

    public void setConfirmError(String error) {
        confirmUI.setError(error);
        toolBarTitle.setText(R.string.toolbar_title_error);
        bGoBack.setVisibility(View.VISIBLE);
        bClose.setVisibility(View.VISIBLE);
    }


    class GoBackClickHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if(processingRequest)
                return;

            switch(viewOnScreen) {
                case Base:
                    finish();
                    break;
                case Passes:
                    viewOnScreen = ViewOnScreen.Base;
                    toolBarTitle.setText(R.string.toolbar_title_sendpass);

                    vPasses.setVisibility(View.GONE);
                    vBase.setVisibility(View.VISIBLE);

                    bComplete.setVisibility(View.GONE);
                    bGoFront.setVisibility(View.VISIBLE);
                    break;
                case Confirm:
                    viewOnScreen = ViewOnScreen.Passes;
                    toolBarTitle.setText(R.string.toolbar_title_passes);

                    vConfirm.setVisibility(View.GONE);
                    vPasses.setVisibility(View.VISIBLE);

                    bComplete.setVisibility(View.VISIBLE);
                    bClose.setVisibility(View.GONE);
                    break;
            }

        }
    }

    class GoFrontClickHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(viewOnScreen) {
                case Base:
                    if(infoUI.canProceed()) {
                        viewOnScreen = ViewOnScreen.Passes;
                        toolBarTitle.setText(R.string.toolbar_title_passes);

                        if(passesUI==null) {
                            passesUI = new PassesUI(vPasses,getApplicationContext());
                            passesUI.renderLayout();
                        }

                        vBase.setVisibility(View.GONE);
                        vPasses.setVisibility(View.VISIBLE);

                        bComplete.setVisibility(View.VISIBLE);
                        bGoFront.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }

    class CompleteClickHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if(passesUI.canProceed()) {
                viewOnScreen = ViewOnScreen.Confirm;
                toolBarTitle.setText(R.string.toolbar_title_sending);

                bGoBack.setVisibility(View.GONE);
                bComplete.setVisibility(View.GONE);

                if(confirmUI==null) {
                    confirmUI = new ConfirmationUI(vConfirm,getApplicationContext(),SendPassActivity.this);
                    confirmUI.renderLayout();
                }

                confirmUI.showProgress(true);
                sendPasses();

                vPasses.setVisibility(View.GONE);
                vConfirm.setVisibility(View.VISIBLE);
            }

        }
    }

    enum ViewOnScreen {
        Base,Passes,Confirm
    }

    @Override
    public void onBackPressed() {
        new GoBackClickHandler().onClick(null);
    }
}
