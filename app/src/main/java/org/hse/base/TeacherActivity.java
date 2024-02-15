package org.hse.base;

import java.util.List;

public class TeacherActivity extends PersonActivity {

    private static final String TAG = "TeacherActivity";

    public TeacherActivity() {
        super(TAG);
    }

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_teacher;
    }

    @Override
    protected void initGroupList(List<Group> groups) {
        groups.add(new Group(1, "Преподаватель 1"));
        groups.add(new Group(2, "Преподаватель 2"));
    }
}
