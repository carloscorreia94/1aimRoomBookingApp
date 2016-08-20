package com.oneaim.roombooking.main;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oneaim.roombooking.R;

public class SendPassActivity extends AppCompatActivity {

    private ViewOnScreen viewOnScreen = ViewOnScreen.Base;

    private TextView test;
    private RelativeLayout toolBarLayout;

    private LinearLayout vBase, vPasses, vConfirm;
    private ImageView bGoBack, bGoFront, bComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_pass);

        toolBarLayout = (RelativeLayout) findViewById(R.id.toolbar_layout);
        bGoBack = (ImageView) toolBarLayout.findViewById(R.id.btn_img_go_back);
        bGoFront = (ImageView) toolBarLayout.findViewById(R.id.btn_img_go_front);
        bComplete = (ImageView) toolBarLayout.findViewById(R.id.btn_img_complete_task);

        vBase = (LinearLayout) findViewById(R.id.view_send_pass_base);
        vPasses = (LinearLayout) findViewById(R.id.view_send_pass_passes);
        vConfirm = (LinearLayout) findViewById(R.id.view_send_pass_confirm);

        bGoBack.setOnClickListener(new GoBackClickHandler());
        bGoFront.setOnClickListener(new GoFrontClickHandler());
        bComplete.setOnClickListener(new CompleteClickHandler());

        Bundle args = getIntent().getExtras();


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
                    vPasses.setVisibility(View.GONE);
                    vBase.setVisibility(View.VISIBLE);
                    break;
                case Confirm:
                    viewOnScreen = ViewOnScreen.Passes;
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

                    vBase.setVisibility(View.GONE);
                    vPasses.setVisibility(View.VISIBLE);
                    break;
                case Passes:
                    viewOnScreen = ViewOnScreen.Confirm;
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
}
