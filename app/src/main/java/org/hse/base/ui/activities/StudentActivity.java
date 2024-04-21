package org.hse.base.ui.activities;

import org.hse.base.R;
import org.hse.base.data.db.entities.GroupEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudentActivity extends PersonActivity {

    private static final String TAG = "StudentActivity";

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_student;
    }

    @Override
    ScheduleMode getScheduleMode() {
        return ScheduleMode.STUDENT;
    }

    @Override
    protected void initGroupList(List<Group> groups) {
        personViewModel.getGroups().observe(this, groupEntities -> {
            List<Group> groupsResult = new ArrayList<>();

            for (GroupEntity listEntity : groupEntities) {
                groupsResult.add(new Group(listEntity.id, listEntity.name));
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

        personViewModel.getTimetablesWithTeacherByDateAndGroupId(dateTime, getSelectedGroup().getId()).observe(this, timetableWithTeacherEntities -> {
            if (!timetableWithTeacherEntities.isEmpty()) {
                initDataFromTimetable(timetableWithTeacherEntities.get(0));
            } else initDataFromTimetable(null);
        });
    }
}


