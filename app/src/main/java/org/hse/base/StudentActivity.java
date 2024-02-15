package org.hse.base;

import java.util.List;
import java.util.Locale;

public class StudentActivity extends PersonActivity {

    private static final String TAG = "StudentActivity";

    public StudentActivity() {
        super(TAG);
    }

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_student;
    }

    @Override
    protected void initGroupList(List<Group> groups) {
        for (int i = 20; i < 22; i++) {
            for (int k = 1; k < 4; k++) {
                groups.add(new Group(1, String.format(Locale.getDefault(),"ПИ-%d-%d", i, k)));
            }
        }
    }
}















