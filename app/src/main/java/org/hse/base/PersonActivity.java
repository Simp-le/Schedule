package org.hse.base;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class PersonActivity extends AppCompatActivity {
    protected String TAG;

    public PersonActivity(String tag) {
        this.TAG = tag;
    }
    Date currentTime;

    TextView time, status, subject, office, building, teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        final Spinner spinner = findViewById(R.id.groupList);
        View buttonDay = findViewById(R.id.button_day);
        View buttonWeek = findViewById(R.id.button_week);
        buttonDay.setOnClickListener(v -> showToast("Timetable for day"));
        buttonWeek.setOnClickListener(v -> showToast("Timetable for week"));

        List<Group> groups = new ArrayList<>();
        initGroupList(groups);

        ArrayAdapter<?> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = adapter.getItem(position);
                Log.d(TAG, "selectedItem: " + item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        time = findViewById(R.id.time);
        initTime();

        status = findViewById(R.id.status);
        subject = findViewById(R.id.subject);
        office = findViewById(R.id.office);
        building = findViewById(R.id.building);
        teacher = findViewById(R.id.teacher);

        initData();
    }

    abstract int getLayoutResourceId();

    private void showToast(CharSequence text) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    abstract protected void initGroupList(List<Group> groups);

    private void initTime() {
        currentTime = new Date();
        String current_time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentTime);
        String date = new SimpleDateFormat("EEEE", new Locale("ru")).format(currentTime);

        date = Character.toUpperCase(date.charAt(0)) + date.substring(1);
        time.setText(String.format("%s, %s", current_time, date));
    }

    private void initData() {
        status.setText(R.string.status);
        subject.setText(R.string.subject);
        office.setText(R.string.office);
        building.setText(R.string.building);
        teacher.setText(R.string.teacher);
    }

    static class Group {
        private Integer id;
        private String name;

        public Group(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }

        public String getName() {
            return name;
        }

        public void setName() {
            this.name = name;
        }
    }
}
