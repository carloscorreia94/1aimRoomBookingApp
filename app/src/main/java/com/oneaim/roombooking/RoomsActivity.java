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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RoomsActivity extends AppCompatActivity implements JSONRequestListener, RoomReadyListener {
    public static final String TAG = RoomsActivity.class.getSimpleName();

    private Map<String, String> mapRequest;
    private NetworkHelper networkHelper;
    private List<Room> rooms = new ArrayList<>();
    private RoomListAdapter adapter;
    private DateTime currentDate;

    //UI Components
    private ExpandableListView vListView;
    private ProgressBar vLoadingBar;
    private TextView tCurrentDate;
    private FloatingActionButton btnRoomDetails;

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
        showProgress(false);
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
        btnRoomDetails = (FloatingActionButton) findViewById(R.id.fab);

        configureHeader();

        adapter = new RoomListAdapter(getApplicationContext(),this);
        vListView.setAdapter(adapter);

        vListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (adapter == null) {return;}
                for (int i = 0; i < adapter.getGroupCount(); i++) {
                    if (i != groupPosition) {
                        vListView.collapseGroup(i);
                    }
                }
                btnRoomDetails.setVisibility(View.VISIBLE);
            }
        });

        vListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                btnRoomDetails.setVisibility(View.GONE);
            }
        });

        showProgress(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */
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


    public void configureHeader() {
        ImageView prevDay = (ImageView) findViewById(R.id.prevDayImageBtn);
        ImageView nextDay = (ImageView) findViewById(R.id.nextDayImageBtn);
        tCurrentDate = (TextView) findViewById(R.id.currentDate);

        currentDate = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy");
        fmt = fmt.withLocale(Locale.GERMANY);

        tCurrentDate.setText(fmt.print(currentDate));

        prevDay.setOnClickListener(new TimeClickListener(DayType.Previous));
        nextDay.setOnClickListener(new TimeClickListener(DayType.Next));
    }

    public enum DayType {
        Previous,Next
    }

    class TimeClickListener implements View.OnClickListener {
        DayType type;

        public TimeClickListener(DayType type) {
            this.type = type;
        }

        @Override
        public void onClick(View view) {
            currentDate = type.equals(DayType.Previous) ? currentDate.minusDays(1) : currentDate.plusDays(1);
            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy");
            fmt = fmt.withLocale(Locale.GERMANY);

            tCurrentDate.setText(fmt.print(currentDate));

            final long unixStamp = currentDate.getMillis() / 1000;
            mapRequest = new HashMap<>();
            mapRequest.put(RequestValues.ROOMS_DATE_KEY, String.valueOf(unixStamp));
            networkHelper.process();
        }
    }
}
