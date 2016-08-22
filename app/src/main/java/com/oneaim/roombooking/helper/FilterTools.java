package com.oneaim.roombooking.helper;

import android.util.Log;
import android.widget.CheckedTextView;
import android.widget.EditText;

import com.oneaim.roombooking.models.Room;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
        DateTimeFormatter formatterHour = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm");

        /**
         * I couldn't figure out how to get the right timezone from the phone yet. So, static
         * solution, add 2 hours.
         */
        DateTime currentDate = formatterHour
                .parseDateTime(room.date + " " + DateTimeFormat.forPattern("HH:mm")
                        .print(new DateTime())).plusHours(2);

        for(String i : room.availability) {
            String startTimeStr = i.split(" - ")[0];
            String endTimeStr = i.split(" - ")[1];

            DateTime startDate = formatterHour.parseDateTime(room.date + " " + startTimeStr);
            DateTime endDate = formatterHour.parseDateTime(room.date + " " + endTimeStr);
            Duration duration = new Duration(currentDate,startDate);

            if(duration.getStandardMinutes()<60 && currentDate.isBefore(endDate))
                return true;

        }

        return false;
    }



}
