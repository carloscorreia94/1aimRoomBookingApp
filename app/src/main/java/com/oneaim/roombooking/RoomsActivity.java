package com.oneaim.roombooking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.oneaim.roombooking.adapters.RoomListAdapter;
import com.oneaim.roombooking.helper.APIEndpoints;
import com.oneaim.roombooking.helper.JSONRequestListener;
import com.oneaim.roombooking.helper.MainController;
import com.oneaim.roombooking.helper.NetworkHelper;
import com.oneaim.roombooking.helper.RequestValues;
import com.oneaim.roombooking.helper.RoomReadyListener;
import com.oneaim.roombooking.models.Room;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomsActivity extends AppCompatActivity implements JSONRequestListener, RoomReadyListener {
    public static final String TAG = RoomsActivity.class.getSimpleName();

    private Map<String, String> mapRequest;
    private NetworkHelper networkHelper;
    private List<Room> rooms = new ArrayList<>();
    private RoomListAdapter adapter;

    //UI Components
    private ExpandableListView vListView;
    private ProgressBar vLoadingBar;
    private View mHeaderView;

    public List<Room> getRooms() {
        return rooms;
    }

    public JSONObject getRequestBody() {
        return new JSONObject(mapRequest);
    }

    public void successResponse(String response) {
        Log.i(TAG,response);
        try {
            rooms = Room.getRooms(response);
            adapter.notifyDataSetChanged();
            showProgress(false);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    R.string.error_parsing_data, Toast.LENGTH_LONG).show();
        }
    }

    public void errorResponse() {
        Toast.makeText(getApplicationContext(),
                R.string.error_network_unknown, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainController.initImageLoader(getApplicationContext());

        networkHelper = new NetworkHelper(APIEndpoints.GET_ROOMS,Request.Method.POST);
        networkHelper.setListener(this);

        mapRequest = new HashMap<>();
        mapRequest.put(RequestValues.ROOMS_DATE_KEY, RequestValues.ROOMS_DATE_TODAY_VALUE);

        networkHelper.process();

        setContentView(R.layout.activity_rooms);
        vListView = (ExpandableListView) findViewById(R.id.expandableListView);
        vLoadingBar = (ProgressBar) findViewById(R.id.loading_progress);

        adapter = new RoomListAdapter(getApplicationContext(),this);
        vListView.setAdapter(adapter);

        showProgress(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rooms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        vListView.setVisibility(show ? View.GONE : View.VISIBLE);
        vListView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                vListView.setVisibility(show ? View.GONE : View.VISIBLE);
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
