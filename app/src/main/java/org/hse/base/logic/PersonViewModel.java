package org.hse.base.logic;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.hse.base.data.db.entities.GroupEntity;
import org.hse.base.data.db.entities.TeacherEntity;
import org.hse.base.data.db.entities.TimetableWithTeacherEntity;

import java.util.Date;
import java.util.List;

public class PersonViewModel extends AndroidViewModel {

    private String TAG = "PersonViewModel";
    private DbRepo dbRepo;
    private TimeRepo timeRepo;
    public PersonViewModel(@NonNull Application application) {
        super(application);
        dbRepo = new DbRepo(application);
        timeRepo = new TimeRepo();
    }

    public LiveData<List<GroupEntity>> getGroups() { return dbRepo.getGroups(); }

    public LiveData<List<TeacherEntity>> getTeachers() { return dbRepo.getTeachers(); }

    public LiveData<List<TimetableWithTeacherEntity>> getTimetablesWithTeacherByDateAndGroupId(Date date, int groupId) {
        return dbRepo.getTimetablesWithTeacherByDateAndGroupId(date, groupId);
    }

    public LiveData<List<TimetableWithTeacherEntity>> getTimetablesWithTeacherByDateAndTeacherId(Date date, int teacherId) {
        return dbRepo.getTimetablesWithTeacherByDateAndTeacherId(date, teacherId);
    }

    public LiveData<List<TimetableWithTeacherEntity>> getStudentsTimetableByDate(Date start, Date end, int groupId) {
        return dbRepo.getStudentsTimetableByDate(start, end, groupId);
    }

    public LiveData<List<TimetableWithTeacherEntity>> getTeacherTimetableByDate(Date start, Date end, int teacherId) {
        return dbRepo.getTeacherTimetableByDate(start, end, teacherId);
    }

    public LiveData<Date> getTime() { return timeRepo.getTime(); }
    public LiveData<String> getErrorMessage() { return timeRepo.getErrorMessage(); }

}
