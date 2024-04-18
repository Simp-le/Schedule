package org.hse.base;

import java.util.List;

public class TeacherActivity extends PersonActivity {

    private static final String TAG = "TeacherActivity";

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_teacher;
    }

    @Override
    ScheduleMode getScheduleMode() {
        return ScheduleMode.TEACHER;
    }

    @Override
    protected void initGroupList(List<Group> groups) {
        groups.add(new Group(1, "Гущин Михаил Иванович"));
        groups.add(new Group(2, "Александр Дмитрий Владимирович"));
    }
}
