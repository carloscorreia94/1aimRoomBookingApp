package com.oneaim.roombooking.helper;

import android.widget.CheckedTextView;
import android.widget.EditText;

import com.oneaim.roombooking.models.Room;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

/**
 * Created by carloscorreia on 22/08/16.
 */
public final class FilterTools {
    public static boolean hideByQuery(Room room, EditText filterSearch) {
        return !room.name.contains(filterSearch.getEditableText().toString());
    }

    public static boolean hideByCheckMark(Room room, CheckedTextView oneHourFilter) {
        return !availableInOneHour(room) && oneHourFilter.isChecked();
    }

    public static void filterList(List<Room> objs, EditText filterSearch, CheckedTextView oneHourFilter) {
        for(Room room : objs)
            room.hidden = hideByQuery(room,filterSearch) || hideByCheckMark(room, oneHourFilter);
    }


    public static boolean availableInOneHour(Room room) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        DateTimeFormatter formatterHour = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm");

        DateTime currentDate = formatter.parseDateTime(room.date);
        for(String i : room.availability) {
            String startTimeStr = i.split(" -")[0];
            DateTime startDate = formatterHour.parseDateTime(room.date + " " + startTimeStr);
            Duration duration = new Duration(startDate, currentDate);
            if(duration.getStandardMinutes()<60)
                return true;

        }

        return false;
    }
}
