package com.oneaim.roombooking.main.send_pass;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oneaim.roombooking.R;

/**
 * Created by carloscorreia on 21/08/16.
 */
public class ConfirmationUI {

    private View view;
    private Context context;
    private SendPassesListener listener;

    private LinearLayout holdingView;
    private TextView networkCompleteDesc;
    private Button tryAgainBtn;
    private ProgressBar vLoadingBar;



    public ConfirmationUI(View view, Context context, SendPassesListener listener) {
        this.view = view;
        this.context = context;
        this.listener = listener;
    }


    public void renderLayout() {
        holdingView = (LinearLayout) view.findViewById(R.id.holding_view);
        vLoadingBar = (ProgressBar) view.findViewById(R.id.loading_progress);
        tryAgainBtn = (Button) view.findViewById((R.id.sendpass_confirm_try_again));
        networkCompleteDesc = (TextView) view.findViewById(R.id.network_complete_desc);
    }


    public void setError(String error) {
        showProgress(false);
        tryAgainBtn.setVisibility(View.VISIBLE);
        networkCompleteDesc.setVisibility(View.VISIBLE);
        networkCompleteDesc.setText(error);
        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);
                tryAgainBtn.setVisibility(View.GONE);
                listener.sendPasses();
            }
        });

    }

    public void setSuccess() {
        showProgress(false);
        networkCompleteDesc.setText(R.string.sendpass_confirm_success);

    }

    public void showProgress(final boolean show) {

        int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

        holdingView.setVisibility(show ? View.GONE : View.VISIBLE);
        holdingView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                holdingView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        vLoadingBar.setVisibility(show ? View.VISIBLE : View.GONE);
        vLoadingBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                vLoadingBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
