package org.hse.base.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import org.hse.base.data.db.entities.GroupEntity;
import org.hse.base.data.db.entities.TeacherEntity;
import org.hse.base.data.db.entities.TimetableEntity;

@Database(entities = {GroupEntity.class, TeacherEntity.class, TimetableEntity.class},
        version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ScheduleDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "timetable";

    public abstract ScheduleDao dao();
}
