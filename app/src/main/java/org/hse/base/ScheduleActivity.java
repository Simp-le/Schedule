package org.hse.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.hse.base.PersonActivity.ScheduleMode;
import org.hse.base.PersonActivity.ScheduleType;
import org.hse.base.schedule.OnItemClick;
import org.hse.base.schedule.ScheduleItem;
import org.hse.base.schedule.ScheduleItemHeader;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    public static final String ARG_TYPE = "type";
    public static final String ARG_MODE = "mode";
    public static final String ARG_ID = "id";
    public static final String ARG_NAME = "name";
    public static final int DEFAULT_ID = 0;
    private static final String TAG = "ScheduleActivity";
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private ScheduleType type;
    private ScheduleMode mode;
    private int id;
    private String groupName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        type = (ScheduleType) getIntent().getSerializableExtra(ARG_TYPE);
        mode = (ScheduleMode) getIntent().getSerializableExtra(ARG_MODE);
        id = getIntent().getIntExtra(ARG_ID, DEFAULT_ID);
        groupName = getIntent().getStringExtra(ARG_NAME);

        TextView title = findViewById(R.id.title);
        title.setText(String.format("%s %s %s", groupName, type, mode));

        recyclerView = findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        adapter = new ItemAdapter(this::onScheduleItemClick);
        recyclerView.setAdapter(adapter);

        initData();
    }


    private void initData() {
        List<ScheduleItem> list = new ArrayList<>();

        ScheduleItemHeader header = new ScheduleItemHeader();
        header.setTitle("Понедельник, 28 января");
        list.add(header);

        ScheduleItem item = new ScheduleItem();
        item.setStart("9:40");
        item.setEnd("11:00");
        item.setType("Практическое задание");
        item.setName("Анализ данных (анг)");
        item.setPlace("Ауд. 503, Кочновской пр-д, д.3");
        item.setTeacher("Пред. Гущим Михаил Иванович");
        list.add(item);

        item = new ScheduleItem();
        item.setStart("11:30");
        item.setEnd("12:50");
        item.setType("Практическое задание");
        item.setName("Анализ данных (анг)");
        item.setPlace("Ауд. 503, Кочновской пр-д, д.3");
        item.setTeacher("Пред. Гущим Михаил Иванович");
        list.add(item);

        adapter.setDataList(list);
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
            dataList = list;
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
                return new ViewHolder.ViewHolderHeader(contactView, context, onItemClick);
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
            } else if (holder instanceof ViewHolder.ViewHolderHeader) {
                ((ViewHolder.ViewHolderHeader) holder).bind((ScheduleItemHeader) data);
            }
        }


        @Override
        public int getItemCount() {
            return dataList.size();
        }

    }
}
