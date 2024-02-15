package org.hse.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View buttonStudent = findViewById(R.id.button_student);
        View buttonTeacher = findViewById(R.id.button_teacher);

        buttonStudent.setOnClickListener(v -> showStudent());
        buttonTeacher.setOnClickListener(v -> showTeacher());
    }

    private void showStudent() {
        Intent intent = new Intent(this, StudentActivity.class);
        startActivity(intent);
    }

    private void showTeacher() {
        Intent intent = new Intent(this, TeacherActivity.class);
        startActivity(intent);
    }
}
