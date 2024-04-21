package org.hse.base.ui.activities;

import android.util.Log;

import org.hse.base.R;
import org.hse.base.data.db.entities.TeacherEntity;

import java.util.ArrayList;
import java.util.Date;
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
        personViewModel.getTeachers().observe(this, teacherEntities -> {
            List<Group> groupsResult = new ArrayList<>();

            for (TeacherEntity listEntity : teacherEntities) {
                groupsResult.add(new Group(listEntity.id, listEntity.fio));
            }
            adapter.clear();
            adapter.addAll(groupsResult);
        });
    }

    @Override
    protected void showTime(Date dateTime) {
        if (dateTime == null) {
            return;
        }
        super.showTime(dateTime);

        personViewModel.getTimetablesWithTeacherByDateAndTeacherId(dateTime, getSelectedGroup().getId()).observe(this, timetableWithTeacherEntities -> {
            Log.d(TAG, timetableWithTeacherEntities.toString());
            if (!timetableWithTeacherEntities.isEmpty()) {
                initDataFromTimetable(timetableWithTeacherEntities.get(0));
            } else initDataFromTimetable(null);
        });
    }
}
