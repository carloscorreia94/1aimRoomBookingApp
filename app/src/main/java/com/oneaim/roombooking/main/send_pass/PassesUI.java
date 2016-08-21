package com.oneaim.roombooking.main.send_pass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oneaim.roombooking.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carloscorreia on 21/08/16.
 */
public class PassesUI {

    private View view;
    private Context context;
    private List<Person> peopleList;

    private EditText name, email, phone;
    private Button addPerson;
    private ListView vPersonList;


    public PassesUI(View view, Context context) {
        this.view = view;
        this.context = context;
        this.peopleList = new ArrayList<>();
    }


    public void renderLayout() {
        name = (EditText) view.findViewById(R.id.pass_person_name);
        email = (EditText) view.findViewById(R.id.pass_email);
        phone = (EditText) view.findViewById(R.id.pass_phone);
        addPerson = (Button) view.findViewById(R.id.add_person);
        vPersonList = (ListView) view.findViewById(R.id.person_list);

        final PersonAdapter adapter = new PersonAdapter();
        vPersonList.setAdapter(adapter);

        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Person p = new Person();
                p.name = name.getText().toString();
                p.email = email.getText().toString();
                p.phone = phone.getText().toString();
                peopleList.add(p);
                adapter.notifyDataSetChanged();
            }
        });

    }

    class Person {
        String name, email,phone;
    }

    class PersonAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public PersonAdapter() {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return peopleList.size();
        }

        @Override
        public Person getItem(int i) {
            return peopleList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final PersonVHolder holder;

            if (view == null) {
                view = inflater.inflate(R.layout.sendpass_people_list_item, viewGroup,false);
                holder = new PersonVHolder();

                holder.name = (TextView) view.findViewById(R.id.person_name);
                holder.delete = (ImageView) view.findViewById(R.id.person_delete);

                view.setTag(holder);
            } else {
                holder = (PersonVHolder) view.getTag();

            }

            Person p = getItem(i);

            holder.name.setText(p.name);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    peopleList.remove(i);
                    notifyDataSetChanged();
                }
            });

            return view;
        }

        class PersonVHolder {
            TextView name;
            ImageView delete;
        }
    }
}
