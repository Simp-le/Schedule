package org.hse.base.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.hse.base.R;
import org.hse.base.data.db.entities.TimetableEntity;
import org.hse.base.data.db.entities.TimetableWithTeacherEntity;
import org.hse.base.logic.PersonViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class PersonActivity extends AppCompatActivity {
    protected String TAG = "PersonActivity";

    View buttonScheduleDay, buttonScheduleWeek;
    TextView time, status, subject, office, building, teacher;
    protected PersonViewModel personViewModel;
    Spinner spinner;
    Date currentTime;
    protected ArrayAdapter<Group> adapter;
    LinearLayout subjectLayout;


    abstract protected void initGroupList(List<Group> groups);

    abstract ScheduleMode getScheduleMode();

    abstract int getLayoutResourceId();



    private void initData() {
        initDataFromTimetable(null);
    }

    protected void initDataFromTimetable(TimetableWithTeacherEntity timetableWithTeacherEntity) {
        if (timetableWithTeacherEntity == null) {
            status.setText(R.string.status);
            subjectLayout.setVisibility(View.GONE);
            return;
        }


        subjectLayout.setVisibility(View.VISIBLE);
        TimetableEntity timetableEntity = timetableWithTeacherEntity.timetableEntity;
        status.setText("Идёт пара");
        subject.setText(timetableEntity.subjName);
        office.setText(String.format("Кабинет %s", timetableEntity.cabinet));
        building.setText(timetableEntity.corp);
        teacher.setText(timetableWithTeacherEntity.teacherEntity.fio);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        personViewModel = new ViewModelProvider(this).get(PersonViewModel.class);
        setContentView(getLayoutResourceId());

        ScheduleMode scheduleMode = getScheduleMode();

        spinner = findViewById(R.id.groupList);
        buttonScheduleDay = findViewById(R.id.button_day);
        buttonScheduleWeek = findViewById(R.id.button_week);

        buttonScheduleDay.setOnClickListener(v -> showSchedule(ScheduleType.DAY, scheduleMode));
        buttonScheduleWeek.setOnClickListener(v -> showSchedule(ScheduleType.WEEK, scheduleMode));

        List<Group> groups = new ArrayList<>();
        initGroupList(groups);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = adapter.getItem(position);
                Log.d(TAG, "selectedItem: " + item);

                showTime(currentTime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        time = findViewById(R.id.time);
        personViewModel.getTime().observe(this, dateTime -> {
            if (dateTime != null) {
                showTime(dateTime);
            }
        });
        personViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                //Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, errorMessage);
            }
        });
        personViewModel.fetchTime();


        subjectLayout = findViewById(R.id.subject_layout);

        status = findViewById(R.id.status);
        subject = findViewById(R.id.subject);
        office = findViewById(R.id.office);
        building = findViewById(R.id.building);
        teacher = findViewById(R.id.teacher);

        initData();
    }

    protected Group getSelectedGroup() {
        return adapter.getItem(spinner.getSelectedItemPosition());
    }

    protected void showScheduleImpl(ScheduleMode mode, ScheduleType type, Group group) {
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra(ScheduleActivity.ARG_ID, group.getId());
        intent.putExtra(ScheduleActivity.ARG_NAME, group.getName());
        intent.putExtra(ScheduleActivity.ARG_TYPE, type);
        intent.putExtra(ScheduleActivity.ARG_MODE, mode);
        intent.putExtra(ScheduleActivity.ARG_TIME, currentTime);
        startActivity(intent);
    }



    protected void showSchedule(ScheduleType type, ScheduleMode mode) {
        Object selectedItem = spinner.getSelectedItem();
        if (!(selectedItem instanceof Group)) {
            return;
        }
        showScheduleImpl(mode, type, (Group) selectedItem);
    }

    protected void showTime(Date dateTime) {
        if (dateTime == null) {
            return;
        }

        currentTime = dateTime;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm, EEEE", Locale.forLanguageTag("ru"));
        time.setText(simpleDateFormat.format(currentTime));
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



    enum ScheduleType {
        DAY,
        WEEK
    }

    enum ScheduleMode {
        STUDENT,
        TEACHER
    }
}
