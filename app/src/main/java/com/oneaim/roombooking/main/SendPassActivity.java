package com.oneaim.roombooking.main;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneaim.roombooking.R;
import com.oneaim.roombooking.helper.APIEndpoints;
import com.oneaim.roombooking.helper.UIHelpers;
import com.oneaim.roombooking.models.Room;

public class SendPassActivity extends AppCompatActivity {

    private ViewOnScreen viewOnScreen = ViewOnScreen.Base;
    private Room sendPassRoom;

    //UI Components

    private TextView toolBarTitle;
    private RelativeLayout toolBarLayout;

    private LinearLayout vBase, vPasses, vConfirm;
    private ImageView bGoBack, bGoFront, bComplete;

    //UI Header Components
    private ImageView roomMainPicture;
    private TextView roomName, roomDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_pass);

        toolBarLayout = (RelativeLayout) findViewById(R.id.toolbar_layout);
        toolBarTitle = (TextView) toolBarLayout.findViewById(R.id.toolbar_title);

        bGoBack = (ImageView) toolBarLayout.findViewById(R.id.btn_img_go_back);
        bGoFront = (ImageView) toolBarLayout.findViewById(R.id.btn_img_go_front);
        bComplete = (ImageView) toolBarLayout.findViewById(R.id.btn_img_complete_task);

        vBase = (LinearLayout) findViewById(R.id.view_send_pass_base);
        vPasses = (LinearLayout) findViewById(R.id.view_send_pass_passes);
        vConfirm = (LinearLayout) findViewById(R.id.view_send_pass_confirm);

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

    class GoBackClickHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(viewOnScreen) {
                case Base:
                    finish();
                    break;
                case Passes:
                    viewOnScreen = ViewOnScreen.Base;
                    toolBarTitle.setText(R.string.toolbar_title_sendpass);

                    vPasses.setVisibility(View.GONE);
                    vBase.setVisibility(View.VISIBLE);
                    break;
                case Confirm:
                    viewOnScreen = ViewOnScreen.Passes;
                    toolBarTitle.setText(R.string.toolbar_title_passes);

                    vConfirm.setVisibility(View.GONE);
                    vPasses.setVisibility(View.VISIBLE);

                    bComplete.setVisibility(View.GONE);
                    bGoFront.setVisibility(View.VISIBLE);
                    break;
            }

        }
    }

    class GoFrontClickHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch(viewOnScreen) {
                case Base:
                    viewOnScreen = ViewOnScreen.Passes;
                    toolBarTitle.setText(R.string.toolbar_title_passes);

                    vBase.setVisibility(View.GONE);
                    vPasses.setVisibility(View.VISIBLE);
                    break;
                case Passes:
                    viewOnScreen = ViewOnScreen.Confirm;
                    toolBarTitle.setText(R.string.toolbar_title_confirmation);

                    vPasses.setVisibility(View.GONE);
                    vConfirm.setVisibility(View.VISIBLE);

                    bComplete.setVisibility(View.VISIBLE);
                    bGoFront.setVisibility(View.GONE);
                    break;
            }
        }
    }

    class CompleteClickHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            finish();
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
