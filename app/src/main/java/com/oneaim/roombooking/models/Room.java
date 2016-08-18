package com.oneaim.roombooking.models;

import com.oneaim.roombooking.helper.ResponseValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carloscorreia on 18/08/16.
 */
public class Room {

    public String name,location,size;
    public int capacity;
    public String[] equipment,availability,images;

    private static List<Room> rooms;

    public Room(String name, String location, String size, int capacity,
                String[] equipment, String[] availability, String[] images) {
        this.name = name;
        this.location = location;
        this.size = size;
        this.capacity = capacity;
        this.equipment = equipment;
        this.availability = availability;
        this.images = images;

    }

    public static List<Room> getRooms(String rooms) throws JSONException {
        Room.rooms = new ArrayList<>();
        JSONArray roomsSerializable = new JSONArray(rooms);
        for(int i=0;i<roomsSerializable.length();i++) {
            JSONObject roomSerializable = roomsSerializable.getJSONObject(i);

            JSONArray equipmentSer = roomSerializable.getJSONArray(ResponseValues.Room.EQUIPMENT_KEY);
            String[] equipment = new String[equipmentSer.length()];
            for(int j=0; j<equipmentSer.length(); j++) {
                equipment[j]=equipmentSer.optString(i);
            }

            JSONArray availabilitySer = roomSerializable.getJSONArray(ResponseValues.Room.AVAILABILITY_KEY);
            String[] availability  = new String[availabilitySer.length()];
            for(int j=0; j<availabilitySer.length(); j++) {
                availability[j]=availabilitySer.optString(i);
            }

            JSONArray imagesSer = roomSerializable.getJSONArray(ResponseValues.Room.IMAGES_KEY);
            String[] images  = new String[imagesSer.length()];
            for(int j=0; j<imagesSer.length(); j++) {
                images[j]=imagesSer.optString(i);
            }

            Room room = new Room(roomSerializable.getString(ResponseValues.Room.NAME_KEY)
                                ,roomSerializable.getString(ResponseValues.Room.LOCATION_KEY)
                                ,roomSerializable.getString(ResponseValues.Room.SIZE_KEY)
                                ,roomSerializable.getInt(ResponseValues.Room.CAPACITY_KEY)
                                ,equipment,availability,images);
            Room.rooms.add(room);
        }
        return Room.rooms;
    }
}
