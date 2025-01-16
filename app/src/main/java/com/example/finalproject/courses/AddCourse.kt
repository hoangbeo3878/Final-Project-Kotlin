package com.example.finalproject.courses

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalproject.DatabaseHelper
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import java.util.UUID

class AddCourse : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_course)

        val course_type = findViewById<EditText>(R.id.courseTypeEditText)
        val course_description = findViewById<EditText>(R.id.courseDescription)
        val back_button = findViewById<ImageView>(R.id.backButton)
        val add_button = findViewById<Button>(R.id.addCourseButton)

        val progressDialog = ProgressDialog(this).apply {
            setMessage("Adding Course, please wait...")
            setCancelable(false)
        }

        back_button.setOnClickListener {
            val intent = Intent(this, CourseMenu::class.java)
            startActivity(intent)
        }

        add_button.setOnClickListener {
            val courseId = "COU-" + UUID.randomUUID().toString()
            val courseType = course_type.text.toString()
            val courseDescription = course_description.text.toString()

            if (courseType.isEmpty() || courseDescription.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val fd = FirestoreHelper(this)
                fd.addCourse(courseId, courseType, courseDescription,
                    object : FirestoreHelper.FirestoreCallback {
                    override fun onLoading(isLoading: Boolean) {
                        if (isLoading) progressDialog.show() else progressDialog.dismiss()
                    }

                    override fun onSuccess() {
                        course_type.text.clear()
                        course_description.text.clear()
                    }

                    override fun onFailure(message: String) {
                        Toast.makeText(this@AddCourse, "Error: $message", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}
