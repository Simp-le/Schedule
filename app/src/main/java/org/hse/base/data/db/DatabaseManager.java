package org.hse.base.data.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.hse.base.data.db.entities.GroupEntity;
import org.hse.base.data.db.entities.TeacherEntity;
import org.hse.base.data.db.entities.TimetableEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class DatabaseManager {

    private static DatabaseManager manager;
    private ScheduleDatabase db;
    private DatabaseManager(Context context) {
        db = Room.databaseBuilder(context, ScheduleDatabase.class, ScheduleDatabase.DATABASE_NAME)
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        Executors.newSingleThreadScheduledExecutor().execute(() -> initData(context));
                    }
                }).build();
    }

    public static DatabaseManager getInstance(Context context) {
        if (manager == null) {
            manager = new DatabaseManager(context.getApplicationContext());
        }
        return manager;
    }

    public ScheduleDao getDao() { return db.dao(); }





    private void initData(Context context) {
        //region Groups
        List<GroupEntity> groups = new ArrayList<>();

        GroupEntity group = new GroupEntity();
        group.id = 1;
        group.name = "ПИ-21-1";
        groups.add(group);

        group = new GroupEntity();
        group.id = 2;
        group.name = "ПИ-21-2";
        groups.add(group);

        group = new GroupEntity();
        group.id = 3;
        group.name = "ПИ-21-3";
        groups.add(group);

        DatabaseManager.getInstance(context).getDao().insertGroups(groups);
        //endregion


        //region Teachers
        List<TeacherEntity> teachers = new ArrayList<>();

        TeacherEntity teacher = new TeacherEntity();
        teacher.id = 1;
        teacher.fio = "Волков Семён Алексеевич";
        teachers.add(teacher);

        teacher = new TeacherEntity();
        teacher.id = 2;
        teacher.fio = "Яборов Андрей Владимирович";
        teachers.add(teacher);

        teacher = new TeacherEntity();
        teacher.id = 3;
        teacher.fio = "Шестакова Лидия Валентиновна";
        teachers.add(teacher);

        DatabaseManager.getInstance(context).getDao().insertTeachers(teachers);
        //endregion


        //region Timetables
        List<TimetableEntity> timetables = new ArrayList<>();

        //region Web
        TimetableEntity timetable = new TimetableEntity();
        timetable.id = 1;
        timetable.cabinet = "501";
        timetable.subGroup = "ПИ";
        timetable.subjName = "Web-программирование (лекция)";
        timetable.corp = "3 корпус";
        timetable.type = 0;
        timetable.timeStart = dateFromString("2024-04-22 9:40");
        timetable.timeEnd = dateFromString("2024-04-22 11:00");
        timetable.groupId = 1;
        timetable.teacherId = 1;
        timetables.add(timetable);

        timetable = new TimetableEntity();
        timetable.id = 2;
        timetable.cabinet = "501";
        timetable.subGroup = "ПИ";
        timetable.subjName = "Web-программирование (лекция)";
        timetable.corp = "3 корпус";
        timetable.type = 0;
        timetable.timeStart = dateFromString("2024-04-22 9:40");
        timetable.timeEnd = dateFromString("2024-04-22 11:00");
        timetable.groupId = 2;
        timetable.teacherId = 1;
        timetables.add(timetable);

        timetable = new TimetableEntity();
        timetable.id = 3;
        timetable.cabinet = "501";
        timetable.subGroup = "ПИ";
        timetable.subjName = "Web-программирование (лекция)";
        timetable.corp = "3 корпус";
        timetable.type = 0;
        timetable.timeStart = dateFromString("2024-04-22 9:40");
        timetable.timeEnd = dateFromString("2024-04-22 11:00");
        timetable.groupId = 3;
        timetable.teacherId = 1;
        timetables.add(timetable);

        timetable = new TimetableEntity();
        timetable.id = 4;
        timetable.cabinet = "510";
        timetable.subGroup = "ПИ";
        timetable.subjName = "Web-программирование (практ.)";
        timetable.corp = "3 корпус";
        timetable.type = 0;
        timetable.timeStart = dateFromString("2024-04-22 11:30");
        timetable.timeEnd = dateFromString("2024-04-22 12:50");
        timetable.groupId = 1;
        timetable.teacherId = 1;
        timetables.add(timetable);

        timetable = new TimetableEntity();
        timetable.id = 5;
        timetable.cabinet = "510";
        timetable.subGroup = "ПИ";
        timetable.subjName = "Web-программирование (практ.)";
        timetable.corp = "3 корпус";
        timetable.type = 0;
        timetable.timeStart = dateFromString("2024-04-22 11:30");
        timetable.timeEnd = dateFromString("2024-04-22 12:50");
        timetable.groupId = 2;
        timetable.teacherId = 1;
        timetables.add(timetable);

        timetable = new TimetableEntity();
        timetable.id = 6;
        timetable.cabinet = "510";
        timetable.subGroup = "ПИ";
        timetable.subjName = "Web-программирование (практ.)";
        timetable.corp = "3 корпус";
        timetable.type = 0;
        timetable.timeStart = dateFromString("2024-04-22 11:30");
        timetable.timeEnd = dateFromString("2024-04-22 12:50");
        timetable.groupId = 3;
        timetable.teacherId = 1;
        timetables.add(timetable);
        //endregion

        //region Android
        timetable = new TimetableEntity();
        timetable.id = 7;
        timetable.cabinet = "509";
        timetable.subGroup = "ПИ";
        timetable.subjName = "НИС \"Разработка мобильных приложений\"";
        timetable.corp = "3 корпус";
        timetable.type = 0;
        timetable.timeStart = dateFromString("2024-04-23 9:40");
        timetable.timeEnd = dateFromString("2024-04-23 11:00");
        timetable.groupId = 1;
        timetable.teacherId = 2;
        timetables.add(timetable);

        timetable = new TimetableEntity();
        timetable.id = 8;
        timetable.cabinet = "509";
        timetable.subGroup = "ПИ";
        timetable.subjName = "НИС \"Разработка мобильных приложений\"";
        timetable.corp = "3 корпус";
        timetable.type = 0;
        timetable.timeStart = dateFromString("2024-04-23 9:40");
        timetable.timeEnd = dateFromString("2024-04-23 11:00");
        timetable.groupId = 2;
        timetable.teacherId = 2;
        timetables.add(timetable);

        timetable = new TimetableEntity();
        timetable.id = 9;
        timetable.cabinet = "509";
        timetable.subGroup = "ПИ";
        timetable.subjName = "НИС \"Разработка мобильных приложений\"";
        timetable.corp = "3 корпус";
        timetable.type = 0;
        timetable.timeStart = dateFromString("2024-04-23 9:40");
        timetable.timeEnd = dateFromString("2024-04-23 11:00");
        timetable.groupId = 3;
        timetable.teacherId = 2;
        timetables.add(timetable);
        //endregion

        //region Requirements development and analysis
        timetable = new TimetableEntity();
        timetable.id = 10;
        timetable.cabinet = "511";
        timetable.subGroup = "ПИ";
        timetable.subjName = "Разработка и анализ требований";
        timetable.corp = "3 корпус";
        timetable.type = 0;
        timetable.timeStart = dateFromString("2024-04-24 11:30");
        timetable.timeEnd = dateFromString("2024-04-24 12:50");
        timetable.groupId = 3;
        timetable.teacherId = 3;
        timetables.add(timetable);
        //endregion

        // DatabaseManager.getInstance(context).getDao().insertTimetables(timetables);
        //endregion



        timetable = new TimetableEntity();
        timetable.id = 11;
        timetable.cabinet = "511";
        timetable.subGroup = "ПИ";
        timetable.subjName = "Разработка и анализ требований";
        timetable.corp = "3 корпус";
        timetable.type = 0;
        timetable.timeStart = dateFromString("2024-04-21 3:00");
        timetable.timeEnd = dateFromString("2024-04-21 21:00");
        timetable.groupId = 1;
        timetable.teacherId = 3;
        timetables.add(timetable);

        timetable = new TimetableEntity();
        timetable.id = 12;
        timetable.cabinet = "511";
        timetable.subGroup = "ПИ";
        timetable.subjName = "Разработка и анализ требований";
        timetable.corp = "3 корпус";
        timetable.type = 0;
        timetable.timeStart = dateFromString("2024-04-21 3:00");
        timetable.timeEnd = dateFromString("2024-04-21 23:50");
        timetable.groupId = 2;
        timetable.teacherId = 3;
        timetables.add(timetable);

        DatabaseManager.getInstance(context).getDao().insertTimetables(timetables);

    }

    private Date dateFromString(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            Log.e("DatabaseManager", e.toString());
        }
        return null;
    }
}
