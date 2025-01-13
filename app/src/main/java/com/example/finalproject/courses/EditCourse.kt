package com.example.finalproject.courses

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalproject.DatabaseHelper
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R

class EditCourse : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_course)
        // Initialize views
        val courseTypeEditText = findViewById<EditText>(R.id.courseTypeEditText)
        val courseDescriptionEditText = findViewById<EditText>(R.id.courseDescription)
        val backButton = findViewById<ImageView>(R.id.backButton)
        val updateCourseButton = findViewById<Button>(R.id.updateCourseButton)
        // Back Button
        backButton.setOnClickListener {
            val intent = Intent(this, CourseMenu::class.java)
            startActivity(intent)
        }
        // Update Button
        updateCourseButton.setOnClickListener {
            val id = intent.getStringExtra("id") ?: ""
            val type = courseTypeEditText.text.toString()
            val description = courseDescriptionEditText.text.toString()
            val fd = FirestoreHelper(this)
            fd.updateCourse(id, type, description)
        }
        getIntentData()
    }
    // Getting Intent Data func
    private fun getIntentData(){
        //Getting Data from Intent
        val id = intent.getStringExtra("id")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        //Setting Intent Data
        val courseTypeEditText = findViewById<EditText>(R.id.courseTypeEditText)
        val courseDescriptionEditText = findViewById<EditText>(R.id.courseDescription)
        courseTypeEditText.setText(title)
        courseDescriptionEditText.setText(description)
    }
}