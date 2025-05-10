package com.example.finalproject.type

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R

class EditType : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_type)
        // Initialize views
        val courseTypeEditText = findViewById<EditText>(R.id.TypeEditText)
        val courseDescriptionEditText = findViewById<EditText>(R.id.DescriptionEditText)
        val backButton = findViewById<ImageView>(R.id.backButton)
        val updateCourseButton = findViewById<Button>(R.id.addTypeButton)

        // Back Button
        backButton.setOnClickListener {
            val intent = Intent(this, TypeMenu::class.java)
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
    // Getting Intent Data
    private fun getIntentData(){
        //Getting Data from Intent
        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("description")
        //Setting Intent Data
        val courseTypeEditText = findViewById<EditText>(R.id.TypeEditText)
        val courseDescriptionEditText = findViewById<EditText>(R.id.DescriptionEditText)
        courseTypeEditText.setText(name)
        courseDescriptionEditText.setText(description)
    }
}