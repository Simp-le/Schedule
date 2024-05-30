package org.hse.base.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.hse.base.R;
import org.hse.base.data.db.entities.TimetableEntity;
import org.hse.base.data.db.entities.TimetableWithTeacherEntity;
import org.hse.base.logic.OnItemClick;
import org.hse.base.logic.PersonViewModel;
import org.hse.base.ui.activities.PersonActivity.ScheduleMode;
import org.hse.base.ui.activities.PersonActivity.ScheduleType;
import org.hse.base.ui.components.ScheduleItem;
import org.hse.base.ui.components.ScheduleItemHeader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    public static final String ARG_TYPE = "type";
    public static final String ARG_MODE = "mode";
    public static final String ARG_ID = "id";
    public static final String ARG_NAME = "name";

    public static final String ARG_TIME = "current time";
    public static final int DEFAULT_ID = 0;
    private static final String TAG = "ScheduleActivity";
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private ScheduleType type;
    private ScheduleMode mode;
    private int groupId;
    private String groupName;
    private Date currentTime;
    private TextView noScheduleText;

    private PersonViewModel personViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        personViewModel = new ViewModelProvider(this).get(PersonViewModel.class);
        setContentView(R.layout.activity_schedule);

        type = (ScheduleType) getIntent().getSerializableExtra(ARG_TYPE);
        mode = (ScheduleMode) getIntent().getSerializableExtra(ARG_MODE);
        currentTime = (Date) getIntent().getSerializableExtra(ARG_TIME);
        groupId = getIntent().getIntExtra(ARG_ID, DEFAULT_ID);
        groupName = getIntent().getStringExtra(ARG_NAME);

        noScheduleText = findViewById(R.id.noScheduleTextView);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("ru"));
        TextView title = findViewById(R.id.title);

        if (currentTime != null)
            title.setText(String.format("%s %s %s %s", groupName, type, mode, sdf.format(currentTime)));
        else
            title.setText(String.format("%s %s %s %s", groupName, type, mode, getResources().getString(R.string.time)));

        recyclerView = findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        adapter = new ItemAdapter(this::onScheduleItemClick);
        recyclerView.setAdapter(adapter);

        filterItem(type, mode);
    }

    private void filterItem(ScheduleType type, ScheduleMode mode) {
        if (currentTime == null) {
            showNoSchedule();
            return;
        }

        Calendar calendarDayStart = Calendar.getInstance();
        calendarDayStart.setTime(currentTime);
        Calendar calendarDayEnd = (Calendar) calendarDayStart.clone();

        calendarDayStart = resetTime(calendarDayStart, 0, 0, 0);
        calendarDayEnd = resetTime(calendarDayEnd, 23, 59, 59);

        Calendar calendarWeekStart = Calendar.getInstance(Locale.forLanguageTag("ru"));
        calendarWeekStart.setTime(currentTime);
        int daysUntilSunday = Calendar.SUNDAY - calendarWeekStart.get(Calendar.DAY_OF_WEEK);

        Calendar calendarWeekEnd = (Calendar) calendarWeekStart.clone();
        calendarWeekEnd.add(Calendar.DAY_OF_YEAR, daysUntilSunday);

        calendarWeekStart = resetTime(calendarWeekStart, 0, 0, 0);
        calendarWeekEnd = resetTime(calendarWeekEnd, 23, 59, 59);

        Date dayStart, dayEnd, weekStart, weekEnd;

        dayStart = calendarDayStart.getTime();
        dayEnd = calendarDayEnd.getTime();
        weekStart = calendarWeekStart.getTime();
        weekEnd = calendarWeekEnd.getTime();

        if (mode == ScheduleMode.STUDENT && type == ScheduleType.DAY) {
            personViewModel.getStudentsTimetableByDate(dayStart, dayEnd, groupId)
                    .observe(this, this::initData);
        }
        else if (mode == ScheduleMode.STUDENT && type == ScheduleType.WEEK) {
            personViewModel.getStudentsTimetableByDate(weekStart, weekEnd, groupId)
                    .observe(this, this::initData);
        }
        else if (mode == ScheduleMode.TEACHER && type == ScheduleType.DAY) {
            personViewModel.getTeacherTimetableByDate(dayStart, dayEnd, groupId)
                    .observe(this, this::initData);
        }
        else if (mode == ScheduleMode.TEACHER && type == ScheduleType.WEEK) {
            personViewModel.getTeacherTimetableByDate(weekStart, weekEnd, groupId)
                    .observe(this, this::initData);
        }
    }

    private Calendar resetTime(Calendar c, int hour, int min, int sec) {
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);
        c.set(Calendar.SECOND, sec);
        return c;
    }


    private void initData(List<TimetableWithTeacherEntity> scheduleItems) {

        if (scheduleItems.isEmpty()) {
            showNoSchedule();
            return;
        }

        SimpleDateFormat sdfHeader = new SimpleDateFormat("EEEE, dd MMMM", new Locale("ru"));
        SimpleDateFormat sdfItem = new SimpleDateFormat("HH:mm", new Locale("ru"));

        List<String> days = new ArrayList<>();
        List<ScheduleItem> newScheduleItems = new ArrayList<>();

        for (TimetableWithTeacherEntity entity : scheduleItems) {
            TimetableEntity timetableEntity = entity.timetableEntity;

            String time = sdfHeader.format(timetableEntity.timeStart);
            time = time.substring(0, 1).toUpperCase() + time.substring(1);

            if (!days.contains(time)) {
                ScheduleItemHeader header = new ScheduleItemHeader();
                header.setTitle(time);

                newScheduleItems.add(header);
                days.add(time);
            }

            ScheduleItem item = new ScheduleItem();
            item.setStart(sdfItem.format(timetableEntity.timeStart));
            item.setEnd(sdfItem.format(timetableEntity.timeEnd));
            item.setType(timetableEntity.subGroup);
            item.setName(timetableEntity.subjName);
            item.setPlace(timetableEntity.corp + ", " + timetableEntity.cabinet);
            item.setTeacher(entity.teacherEntity.fio);

            newScheduleItems.add(item);
        }

        adapter.clear();
        adapter.setDataList(newScheduleItems);
    }

    private void showNoSchedule() {
        recyclerView.setVisibility(View.GONE);
        noScheduleText.setVisibility(View.VISIBLE);
    }

    private void onScheduleItemClick(ScheduleItem scheduleItem) {
        Log.d(TAG, "Schedule Item Click");
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        private OnItemClick onItemClick;
        private TextView start;
        private TextView end;
        private TextView type;
        private TextView name;
        private TextView place;
        private TextView teacher;

        public ViewHolder(@NonNull View itemView, Context context, OnItemClick onItemClick) {
            super(itemView);
            this.context = context;
            this.onItemClick = onItemClick;
            start = itemView.findViewById(R.id.textStart);
            end = itemView.findViewById(R.id.textEnd);
            type = itemView.findViewById(R.id.textType);
            name = itemView.findViewById(R.id.textName);
            place = itemView.findViewById(R.id.textPlace);
            teacher = itemView.findViewById(R.id.textTeacher);
        }

        public void bind(final ScheduleItem data) {
            start.setText(data.getStart());
            end.setText(data.getEnd());
            type.setText(data.getType());
            name.setText(data.getName());
            place.setText(data.getPlace());
            teacher.setText(data.getTeacher());
        }


    }
    public static class ViewHolderHeader extends RecyclerView.ViewHolder {

        private Context context;
        private OnItemClick onItemClick;
        private TextView title;

        public ViewHolderHeader(@NonNull View itemView, Context context, OnItemClick onItemClick) {
            super(itemView);
            this.context = context;
            this.onItemClick = onItemClick;
            title = itemView.findViewById(R.id.titleHeader);
        }

        public void bind(final ScheduleItemHeader data) {
            title.setText(data.getTitle());
        }
    }

    public final static class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final static int TYPE_ITEM = 0;
        private final static int TYPE_HEADER = 1;

        private List<ScheduleItem> dataList = new ArrayList<>();
        private OnItemClick onItemClick;

        public ItemAdapter(OnItemClick onItemClick) {
            this.onItemClick = onItemClick;
        }

        public List<ScheduleItem> getDataList() {
            return dataList;
        }

        public void setDataList(List<ScheduleItem> list) {
            this.dataList = list;
            notifyDataSetChanged();
        }

        public void clear() {
            dataList.clear();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            if (viewType == TYPE_ITEM) {
                View contactView = inflater.inflate(R.layout.item_schedule, parent, false);
                return new ViewHolder(contactView, context ,onItemClick);
            } else if (viewType == TYPE_HEADER) {
                View contactView = inflater.inflate(R.layout.item_schedule_header, parent, false);
                return new ViewHolderHeader(contactView, context, onItemClick);
            }

            throw new IllegalArgumentException("Invalid view type");
        }

        public int getItemViewType(int position) {
            ScheduleItem data = dataList.get(position);
            if (data instanceof ScheduleItemHeader) {
                return TYPE_HEADER;
            }
            return TYPE_ITEM;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ScheduleItem data = dataList.get(position);
            if (holder instanceof ViewHolder) {
                ((ViewHolder) holder).bind(data);
            } else if (holder instanceof ViewHolderHeader) {
                ((ViewHolderHeader) holder).bind((ScheduleItemHeader) data);
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

    }
}
