package com.example.myapplication;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;



public class AddClassActivity extends AppCompatActivity {

    private LinearLayout linearLayoutCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        linearLayoutCourses = findViewById(R.id.linearLayoutCourses);

        Button buttonAddCourse = findViewById(R.id.buttonAddCourse);
        buttonAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogToAddCourse();
            }
        });
    }

    private void showDialogToAddCourse() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_course, null);
        final EditText editTextCourseName = dialogView.findViewById(R.id.editTextCourseName);

        builder.setView(dialogView)
                .setTitle("Add New Course")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String courseName = editTextCourseName.getText().toString();
                        addCourseBox(courseName);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addCourseBox(String courseName) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 16);

        LinearLayout courseBoxLayout = new LinearLayout(this);
        courseBoxLayout.setLayoutParams(layoutParams);
        courseBoxLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textViewCourseName = new TextView(this);
        textViewCourseName.setText(courseName);
        textViewCourseName.setTextColor(Color.BLACK);
        textViewCourseName.setTypeface(null, Typeface.BOLD);
        courseBoxLayout.addView(textViewCourseName);

        Button viewCourseButton = new Button(this);
        viewCourseButton.setText("View Course");
        viewCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddClassActivity.this, CourseDetailsActivity.class);
                intent.putExtra("courseName", courseName);
                startActivity(intent);
            }
        });
        courseBoxLayout.addView(viewCourseButton);

        linearLayoutCourses.addView(courseBoxLayout);
    }
}
