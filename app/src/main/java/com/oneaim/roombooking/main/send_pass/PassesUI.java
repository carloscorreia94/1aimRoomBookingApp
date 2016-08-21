package com.oneaim.roombooking.main.send_pass;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oneaim.roombooking.R;
import com.oneaim.roombooking.helper.UIHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by carloscorreia on 21/08/16.
 */
public class PassesUI {

    private View view;
    private Context context;
    private List<Person> peopleList;

    private EditText vName, vEmail, vPhone;
    private Button addPerson;
    private ListView vPersonList;


    public PassesUI(View view, Context context) {
        this.view = view;
        this.context = context;
        this.peopleList = new ArrayList<>();
    }


    public void renderLayout() {
        vName = (EditText) view.findViewById(R.id.pass_person_name);
        vEmail = (EditText) view.findViewById(R.id.pass_email);
        vPhone = (EditText) view.findViewById(R.id.pass_phone);
        addPerson = (Button) view.findViewById(R.id.add_person);
        vPersonList = (ListView) view.findViewById(R.id.person_list);

        vName.setOnFocusChangeListener(new UIHelpers.EditTextFocusChangeListener(context));
        vEmail.setOnFocusChangeListener(new UIHelpers.EditTextFocusChangeListener(context));
        vPhone.setOnFocusChangeListener(new UIHelpers.EditTextFocusChangeListener(context));


        final PersonAdapter adapter = new PersonAdapter();
        vPersonList.setAdapter(adapter);

        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name,email,phone;
                name = vName.getText().toString();
                email = vEmail.getText().toString();
                phone = vPhone.getText().toString();
                if(verifyPerson(name,email,phone)) {
                    Person p = new Person();
                    p.name = name;
                    p.email = email;
                    p.phone =  phone;
                    peopleList.add(p);
                    adapter.notifyDataSetChanged();

                    vName.setText("");
                    vEmail.setText("");
                    vPhone.setText("");

                    InputMethodManager inputMethodManager =
                            (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

            }
        });

    }

    public boolean canProceed() {
        if(peopleList.size()==0) {
            Toast.makeText(context,R.string.sendpass_passes_error_no_persons,Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    private boolean verifyPerson(String name, String email, String phone) {
        vName.setError(null);
        vEmail.setError(null);
        vPhone.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            vName.setError(context.getString(R.string.sendpass_passes_empty_name));
            focusView = vName;
            cancel = true;
        }

        if (!isEmailValid(email)) {
            vEmail.setError(context.getString(R.string.sendpass_passes_invalid_email));
            focusView = vEmail;
            cancel = true;
        }

        if (phone.length()<8) {
            vPhone.setError(context.getString(R.string.sendpass_passes_invalid_phone));
            focusView = vPhone;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;

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
