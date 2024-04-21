package org.hse.base.logic;

import android.content.Context;

import androidx.lifecycle.LiveData;

import org.hse.base.data.db.Converters;
import org.hse.base.data.db.DatabaseManager;
import org.hse.base.data.db.ScheduleDao;
import org.hse.base.data.db.entities.GroupEntity;
import org.hse.base.data.db.entities.TeacherEntity;
import org.hse.base.data.db.entities.TimetableWithTeacherEntity;

import java.util.Date;
import java.util.List;

public class DbRepo {

    private DatabaseManager manager;
    private ScheduleDao dao;

    public DbRepo(Context context) {
        manager = DatabaseManager.getInstance(context);
        dao = manager.getDao();
    }

    public LiveData<List<GroupEntity>> getGroups() { return dao.getGroups(); }
    public LiveData<List<TeacherEntity>> getTeachers() { return dao.getTeachers(); }
    public LiveData<List<TimetableWithTeacherEntity>> getTimetablesWithTeachers() { return dao.getTimetablesWithTeachers(); }



    public LiveData<List<TimetableWithTeacherEntity>> getTimetablesWithTeacherByDateAndGroupId(Date date, int groupId) {
        return dao.getTimetablesWithTeacherByDateAndGroupId(Converters.dateToTimestamp(date), groupId);
    }
    public LiveData<List<TimetableWithTeacherEntity>> getTimetablesWithTeacherByDateAndTeacherId(Date date, int teacherId) {
        return dao.getTimetablesWithTeacherByDateAndTeacherId(Converters.dateToTimestamp(date), teacherId);
    }
    public LiveData<List<TimetableWithTeacherEntity>> getStudentsTimetableByDate(Date start, Date end, int groupId) {
        return dao.getStudentsTimetableByDate(Converters.dateToTimestamp(start), Converters.dateToTimestamp(end), groupId);
    }
    public LiveData<List<TimetableWithTeacherEntity>> getTeacherTimetableByDate(Date start, Date end, int teacherId) {
        return dao.getTeacherTimetableByDate(Converters.dateToTimestamp(start), Converters.dateToTimestamp(end), teacherId);
    }
}
