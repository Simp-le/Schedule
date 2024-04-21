package org.hse.base.data.db.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

public class TimetableWithTeacherEntity {
    @Embedded
    public TimetableEntity timetableEntity;

    @Relation(parentColumn = "teacher_id", entityColumn = "id")
    public TeacherEntity teacherEntity;
}
