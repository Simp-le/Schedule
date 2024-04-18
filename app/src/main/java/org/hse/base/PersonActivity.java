package org.hse.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.hse.base.schedule.TimeResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class PersonActivity extends AppCompatActivity {
    public static final String URL = "https://api.ipgeolocation.io/ipgeo?apiKey=b03018f75ed94023a005637878ec0977";

    // PERSON part
    protected String TAG = "PersonActivity";
    View buttonScheduleDay, buttonScheduleWeek;

    TextView time, status, subject, office, building, teacher;
    Spinner spinner;
    // BASE part
    Date currentTime;
    private OkHttpClient client = new OkHttpClient();

    abstract protected void initGroupList(List<Group> groups);

    abstract int getLayoutResourceId();

    private void initData() {
        status.setText(R.string.status);
        subject.setText(R.string.subject);
        office.setText(R.string.office);
        building.setText(R.string.building);
        teacher.setText(R.string.teacher);
    }

    abstract ScheduleMode getScheduleMode();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        ScheduleMode scheduleMode = getScheduleMode();

        spinner = findViewById(R.id.groupList);
        buttonScheduleDay = findViewById(R.id.button_day);
        buttonScheduleWeek = findViewById(R.id.button_week);

        buttonScheduleDay.setOnClickListener(v -> showSchedule(ScheduleType.DAY, scheduleMode));
        buttonScheduleWeek.setOnClickListener(v -> showSchedule(ScheduleType.WEEK, scheduleMode));

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

    private void initTime() {
//        currentTime = new Date();
//        String current_time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentTime);
//        String date = new SimpleDateFormat("EEEE", new Locale("ru")).format(currentTime);
//
//        date = Character.toUpperCase(date.charAt(0)) + date.substring(1);
//        time.setText(String.format("%s, %s", current_time, date));
        getTime();
    }

    protected void showSchedule(ScheduleType type, ScheduleMode mode) {
        Object selectedItem = spinner.getSelectedItem();
        if (!(selectedItem instanceof Group)) {
            return;
        }
        showScheduleImpl(mode, type, (Group) selectedItem);
    }

    protected void showScheduleImpl(ScheduleMode mode, ScheduleType type, Group group) {
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra(ScheduleActivity.ARG_ID, group.getId());
        intent.putExtra(ScheduleActivity.ARG_NAME, group.getName());
        intent.putExtra(ScheduleActivity.ARG_TYPE, type);
        intent.putExtra(ScheduleActivity.ARG_MODE, mode);
        startActivity(intent);
    }

    protected void getTime() {
        Request request = new Request.Builder().url(URL).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                parseResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "getTime", e);
            }
        });
    }

    private void parseResponse(Response response) {
        Gson gson = new Gson();
        ResponseBody body = response.body();
        if (body == null) {
            return;
        }
        try {
            String string = body.string();
            Log.d(TAG, string);
            TimeResponse timeResponse = gson.fromJson(string, TimeResponse.class);
            String currentTimeVal = timeResponse.getTimeZone().getCurrentTime();
            SimpleDateFormat simleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            Date dateTime = simleDateFormat.parse(currentTimeVal);
            runOnUiThread(() -> showTime(dateTime));
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    private void showTime(Date dateTime) {
        if (dateTime == null) {
            return;
        }

        currentTime = dateTime;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm, EEEE", Locale.forLanguageTag("ru"));
        time.setText(simpleDateFormat.format(currentTime));
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
