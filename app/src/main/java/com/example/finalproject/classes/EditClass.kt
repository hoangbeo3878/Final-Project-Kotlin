package com.example.finalproject.classes

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import com.example.finalproject.courses.CourseMenu
import com.example.finalproject.timetables.AddTitleMenu
import com.example.finalproject.timetables.TimetableMenu
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class EditClass : AppCompatActivity() {
    private val teacherIds = mutableListOf<String>()
    private val classCount = mutableMapOf<String, MutableMap<String, Int>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_class)
        // Initialize views
        val backButton = findViewById<ImageView>(R.id.backButton)
        // Set up Intent Data
        getIntentData()
        // Set up Teacher Spinner
        setupTeacherSpinner()
        // Back button click listener
        backButton.setOnClickListener {
            val courseId = intent.getStringExtra("courseId") ?: ""
            val intent = Intent(this, ClassMenu::class.java)
            intent.putExtra("courseId", courseId)
            startActivity(intent)
        }
    }
    // Getting Intent Data
    private fun getIntentData() {
        val classId = intent.getStringExtra("classId")
        val className = intent.getStringExtra("className")
        val classQuantity = intent.getStringExtra("classQuantity")
        val classDay = intent.getStringExtra("classDay")
        val classTime = intent.getStringExtra("classTime")
        val classRank = intent.getStringExtra("classRank")
        val classStartDate = intent.getStringExtra("classStartDate")
        val classPrice = intent.getStringExtra("classPrice")
        val classLength = intent.getStringExtra("classLength")

        val classRankSpinner = findViewById<Spinner>(R.id.classRankSpinner)
        val classQuantitySpinner = findViewById<Spinner>(R.id.classQuantitySpinner)
        val classPriceSpinner = findViewById<Spinner>(R.id.classPriceSpinner)
        val classTimeSpinner = findViewById<Spinner>(R.id.classTimeSpinner)
        val classDateSpinner = findViewById<Spinner>(R.id.classDateSpinner)
        val classStartDateEditText = findViewById<EditText>(R.id.classStartDate)
        val classLengthSpinner = findViewById<Spinner>(R.id.classLengthSpinner)

        classStartDateEditText.setText(classStartDate)

        setSpinnerSelection(classRankSpinner, classRank)
        setSpinnerSelection(classQuantitySpinner, classQuantity)
        setSpinnerSelection(classPriceSpinner, classPrice)
        setSpinnerSelection(classTimeSpinner, classTime)
        setSpinnerSelection(classDateSpinner, classDay)
        setSpinnerSelection(classLengthSpinner, classLength)
    }

    // Helper function to set Spinner selection based on a value
    private fun setSpinnerSelection(spinner: Spinner, value: String?) {
        val adapter = spinner.adapter
        if (adapter != null) {
            for (i in 0 until adapter.count) {
                if (adapter.getItem(i).toString() == value) {
                    spinner.setSelection(i)
                    return
                }
            }
        }
    }
    // Set Up Teacher Name into Spinner
    private fun setupTeacherSpinner() {
        // Create a list of teacher names
        val teacherNames = mutableListOf<String>()
        teacherNames.add("---Choose Class Teacher---")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, teacherNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val teacherSpinner = findViewById<Spinner>(R.id.classTeacherSpinner)
        teacherSpinner.adapter = adapter

        // Get Teacher names from Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document("Teachers").collection("Details")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.getString("name") ?: ""
                    teacherNames.add(name)
                    teacherIds.add(document.id)
                }
                adapter.notifyDataSetChanged()

                // Set spinner selection if teacherId is provided from Intent
                val teacherId = intent.getStringExtra("teacherId")
                if (!teacherId.isNullOrEmpty()) {
                    val teacherIndex = teacherIds.indexOf(teacherId)
                    if (teacherIndex != -1) {
                        teacherSpinner.setSelection(teacherIndex + 1) // Offset by 1 due to default item
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to retrieve teacher names", Toast.LENGTH_SHORT).show()
            }
    }
}