package com.example.finalproject.courses

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import com.example.finalproject.type.Types
import java.util.UUID

class AddCourse : AppCompatActivity() {
    private lateinit var selectedType: String // Variable to store the selected type from Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_course)

        // Initialize views
        val courseName = findViewById<EditText>(R.id.courseTypeEditText) // EditText for course name
        val courseDescription = findViewById<EditText>(R.id.courseDescription)
        val courseTypeSpinner = findViewById<Spinner>(R.id.courseTypeSpinner)
        val backButton = findViewById<ImageView>(R.id.backButton)
        val addButton = findViewById<Button>(R.id.addCourseButton)

        // Initialize ProgressDialog
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Adding Course, please wait...")
            setCancelable(false)
        }

        // Set up Spinner with course types
        val typeNames = mutableListOf<String>("---Select Course Type---") // List of type names for Spinner
        val typesList = ArrayList<Types>() // List of Types for mapping with Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseTypeSpinner.adapter = adapter

        // Fetch course types from Firestore and populate Spinner
        val fd = FirestoreHelper(this)
        fd.getAllType { types ->
            if (types.isEmpty()) {
                Toast.makeText(
                    this,
                    "No course types available. Please add a type first.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                typesList.clear()
                typesList.addAll(types)
                typeNames.clear()
                typeNames.add("---Select Course Type---")
                typeNames.addAll(types.map { it.name }) // Add type names to the list
                adapter.notifyDataSetChanged()
            }
        }

        // Listen for Spinner selection
        courseTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    selectedType = "" // If default option is selected, set to empty
                } else {
                    selectedType = typesList[position - 1].name // Get the name of the selected type
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedType = "" // Nothing selected
            }
        }

        // Handle back button click
        backButton.setOnClickListener {
            val intent = Intent(this, CourseMenu::class.java)
            startActivity(intent)
        }

        // Handle add button click
        addButton.setOnClickListener {
            val courseId = "COU-" + UUID.randomUUID().toString()
            val courseNameText = courseName.text.toString() // Get the course name
            val courseDescriptionText = courseDescription.text.toString()

            // Validate required fields
            if (selectedType.isEmpty() || courseNameText.isEmpty() || courseDescriptionText.isEmpty()) {
                Toast.makeText(this,
                    "Please fill all required fields", Toast.LENGTH_SHORT).show()
            } else {
                fd.addCourse(courseId, courseNameText, selectedType, courseDescriptionText,
                    object : FirestoreHelper.FirestoreCallback {
                        override fun onLoading(isLoading: Boolean) {
                            if (isLoading) progressDialog.show() else progressDialog.dismiss()
                        }

                        override fun onSuccess() {
                            courseName.text.clear() // Clear the name field
                            courseDescription.text.clear()
                            courseTypeSpinner.setSelection(0) // Reset Spinner to default selection
                            selectedType = "" // Reset selected type
                        }

                        override fun onFailure(message: String) {
                            Toast.makeText(this@AddCourse,
                                "Error: $message", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }
}