package org.hse.base.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import org.hse.base.data.db.entities.GroupEntity;
import org.hse.base.data.db.entities.TeacherEntity;
import org.hse.base.data.db.entities.TimetableEntity;
import org.hse.base.data.db.entities.TimetableWithTeacherEntity;

import java.util.Date;
import java.util.List;

@Dao
public interface ScheduleDao {

    // Group
    @Insert
    void insertGroups(List<GroupEntity> groups);

    @Query("SELECT * FROM `group`")
    LiveData<List<GroupEntity>> getGroups();

    @Delete
    void deleteGroup(GroupEntity group);




    // Teacher
    @Insert
    void insertTeachers(List<TeacherEntity> teachers);

    @Query("SELECT * FROM `teacher`")
    LiveData<List<TeacherEntity>> getTeachers();

    @Delete
    void deleteTeacher(TeacherEntity teacher);




    // Timetable
    @Insert
    void insertTimetables(List<TimetableEntity> timetables);

    @Query("SELECT * FROM `time_table`")
    LiveData<List<TimetableEntity>> getTimetables();

    @Transaction
    @Query("SELECT * FROM `time_table`")
    LiveData<List<TimetableWithTeacherEntity>> getTimetablesWithTeachers();



    @Transaction
    @Query("SELECT * FROM `time_table` WHERE time_start <= :date and time_end >= :date and group_id == :groupId group by subj_name, time_start order by time_start")
    LiveData<List<TimetableWithTeacherEntity>> getTimetablesWithTeacherByDateAndGroupId(Date date, int groupId);
    @Transaction
    @Query("SELECT * FROM `time_table` WHERE time_start <= :date and time_end >= :date and teacher_id == :teacherId group by subj_name, time_start order by time_start")
    LiveData<List<TimetableWithTeacherEntity>> getTimetablesWithTeacherByDateAndTeacherId(Long date, int teacherId);
    @Transaction
    @Query("SELECT * FROM `time_table` WHERE time_start >= :start and time_end <= :end and group_id == :groupId group by subj_name, time_start order by time_start")
    LiveData<List<TimetableWithTeacherEntity>> getStudentsTimetableByDate(Long start, Long end, int groupId);
    @Transaction
    @Query("SELECT * FROM `time_table` WHERE time_start >= :start and time_end <= :end and teacher_id == :teacherId group by subj_name, time_start order by time_start")
    LiveData<List<TimetableWithTeacherEntity>> getTeacherTimetableByDate(Long start, Long end, int teacherId);



    @Delete
    void deleteTimetable(TimetableEntity timetable);
}
