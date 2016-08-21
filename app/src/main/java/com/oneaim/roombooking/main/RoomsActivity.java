package com.oneaim.roombooking.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.oneaim.roombooking.R;
import com.oneaim.roombooking.adapters.RoomListAdapter;
import com.oneaim.roombooking.helper.APIEndpoints;
import com.oneaim.roombooking.helper.JSONRequestListener;
import com.oneaim.roombooking.helper.MainController;
import com.oneaim.roombooking.helper.NetworkHelper;
import com.oneaim.roombooking.helper.RequestValues;
import com.oneaim.roombooking.helper.RoomReadyListener;
import com.oneaim.roombooking.models.Room;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private static DateTime currentDate;
    private DateTimeFormatter fmt;

    //UI Components
    private RelativeLayout sendPassClickSection;
    private TextView sendPassDesc;
    private ExpandableListView vListView;
    private ProgressBar vLoadingBar;
    private TextView tCurrentDate;

    /**
     * Adapter can easily get room list without adding memory complexity on copying values or
     * having other strong references
     * @return List of the rooms available from the API call
     */

    public List<Room> getRooms() {
        return rooms;
    }

    public static DateTime getSelectedDate() {
        return currentDate;
    }

    /**
     * Method conforming to the interface JSONRequestListener.
     * Easily changing request body this way.
     */

    public JSONObject getRequestBody() {
        return new JSONObject(mapRequest);
    }

    /**
     * Next two methods are also conforming to the interface JSONRequestListener.
     * Needed as async callbacks to the
     * Network Process.
     */

    public void successResponse(String response) {
        Log.i(TAG,response);
        try {
            rooms = Room.getRooms(response);
            adapter.notifyDataSetChanged();
            vListView.setSelectionFromTop(0,0);
            showProgress(false);

        } catch (JSONException e) {
            e.printStackTrace();
            showProgress(false);
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
        sendPassClickSection = (RelativeLayout) findViewById(R.id.sendPassClickSection);
        sendPassDesc = (TextView) findViewById(R.id.sendPassDesc);

        configureHeader();

        adapter = new RoomListAdapter(getApplicationContext(),this);
        vListView.setAdapter(adapter);

        vListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(final int groupPosition) {
                collapseAllGroups(groupPosition);
                sendPassDesc.setText(String.format(getString(R.string.send_pass_desc), rooms.get(groupPosition).name));
                sendPassClickSection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openSendPass(groupPosition);
                    }
                });
                sendPassClickSection.setVisibility(View.VISIBLE);
            }
        });

        vListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                sendPassClickSection.setVisibility(View.GONE);
            }
        });

        vListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                openSendPass(i);
                return true;
            }
        });

        showProgress(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    /**
     * Here we open the new screen (Activity) and save the current room as a static member of
     * the class room. More transparent way of dealing with data instead of sending it through
     * an intent, serializing and de-serializing again
     * @param roomNumber room number on the list
     */
    public void openSendPass(int roomNumber) {
        Room.setCurrentRoom(rooms.get(roomNumber));
        Intent intent = new Intent(RoomsActivity.this,SendPassActivity.class);
        intent.putExtra("chosen_date",fmt.print(currentDate));
        startActivity(intent);
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

    public void collapseAllGroups(int groupPosition) {
        if (adapter == null) {return;}
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            if (groupPosition==-1 || i != groupPosition) {
                vListView.collapseGroup(i);
            }
        }
    }


    public void configureHeader() {
        ImageView prevDay = (ImageView) findViewById(R.id.prevDayImageBtn);
        ImageView nextDay = (ImageView) findViewById(R.id.nextDayImageBtn);
        tCurrentDate = (TextView) findViewById(R.id.currentDate);

        currentDate = new DateTime();
        fmt = DateTimeFormat.forPattern("dd-MM-yyyy")
                .withLocale(Locale.GERMANY);

        tCurrentDate.setText(fmt.print(currentDate));

        prevDay.setOnClickListener(new TimeClickListener(DayType.Previous));
        nextDay.setOnClickListener(new TimeClickListener(DayType.Next));
    }

    public enum DayType {
        Previous,Next
    }

    /**
     * Time is obtained with resource to the JODA Library for ease of time manipulation. This
     * click listener changes the current date variable on the Activity and requests the API
     * right next.
     */

    class TimeClickListener implements View.OnClickListener {
        private DayType type;

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

            //This solution is really stupid, though it's a last minute resource
            collapseAllGroups(-1);

            sendPassClickSection.setVisibility(View.GONE);
            showProgress(true);
            networkHelper.process();
        }
    }
}
