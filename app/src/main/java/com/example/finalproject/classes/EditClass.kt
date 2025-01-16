package com.example.finalproject.classes

import android.app.DatePickerDialog
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
        // Set up Intent Data
        getIntentData()
        // Initialize views
        val classRankSpinner = findViewById<Spinner>(R.id.classRankSpinner)
        val classQuantitySpinner = findViewById<Spinner>(R.id.classQuantitySpinner)
        val classPriceSpinner = findViewById<Spinner>(R.id.classPriceSpinner)
        val classTimeSpinner = findViewById<Spinner>(R.id.classTimeSpinner)
        val classDateSpinner = findViewById<Spinner>(R.id.classDateSpinner)
        val classTeacherSpinner = findViewById<Spinner>(R.id.classTeacherSpinner)
        val classStartDate = findViewById<EditText>(R.id.classStartDate)
        val classLengthSpinner = findViewById<Spinner>(R.id.classLengthSpinner)
        val addCourseButton = findViewById<Button>(R.id.addCourseButton)
        val backButton = findViewById<ImageView>(R.id.backButton)
        // Set up Teacher Spinner
        setupTeacherSpinner()
        // Back button click listener
        backButton.setOnClickListener {
            val courseId = intent.getStringExtra("courseId") ?: ""
            val intent = Intent(this, ClassMenu::class.java)
            intent.putExtra("courseId", courseId)
            startActivity(intent)
        }
        // Clear start date field when classDateSpinner value changes
        classDateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                classStartDate.text.clear() // Clear the start date field
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        // Set click listener for the date field
        classStartDate.setOnClickListener {
            if (classDateSpinner.selectedItem.toString() == "---Choose Class Date---") {
                Toast.makeText(this, "Please select a class date first",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showDatePickerDialog(classDateSpinner, classStartDate)
        }
        // Add button click listener
        addCourseButton.setOnClickListener {
            val spinners = listOf(classRankSpinner, classQuantitySpinner, classPriceSpinner,
                classTimeSpinner, classDateSpinner, classTeacherSpinner)
            val spinnerSelected = spinners.any { it.selectedItemPosition != 0 }

            if (spinnerSelected && classStartDate.text.isNotEmpty()) {
                val time = classTimeSpinner.selectedItem.toString()
                val date = classDateSpinner.selectedItem.toString()
                val startDate = classStartDate.text.toString()
                val length = classLengthSpinner.selectedItem.toString()
                val teacherId = teacherIds[classTeacherSpinner.selectedItemPosition - 1]

                checkTeacherClass(teacherId, date, time, startDate, length) { available ->
                    if (available) {
                        val id = "ClA-" + UUID.randomUUID().toString()
                        val rank = classRankSpinner.selectedItem.toString()
                        val quantity = classQuantitySpinner.selectedItem.toString()
                        val price = classPriceSpinner.selectedItem.toString()
                        val courseId = intent.getStringExtra("courseId") ?: ""

                        val dateParts = startDate.split("/")
                        val day = dateParts[0]
                        val month = dateParts[1]
                        val year = dateParts[2].substring(2)
                        val dateForClassName = "$day$month$year"

                        val rankFirstLetter = rank.split(" ")[0]
                        val type = intent.getStringExtra("type") ?: ""
                        val typeFirstTwoLetters = type.split(" ").take(2).joinToString("") { it.take(1).uppercase() }

                        val courseClassCount = classCount.getOrPut(courseId) { mutableMapOf() }
                        val classNumber  = courseClassCount.getOrDefault(rank, 0) + 1
                        courseClassCount[rank] = classNumber
                        val name = if (classNumber != 1) {
                            "$typeFirstTwoLetters-$rankFirstLetter${classNumber - 1}-$dateForClassName"
                        } else {
                            "$typeFirstTwoLetters-$rankFirstLetter-$dateForClassName"
                        }

                        val fd = FirestoreHelper(this)
                        fd.editClass(id, name, rank, quantity, price, date,
                            time, length, startDate, courseId, teacherId)

                    }else{
                        Toast.makeText(this, "Teacher already has a class on this time and date",
                            Toast.LENGTH_SHORT).show()
                        return@checkTeacherClass
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Getting Intent Data
    private fun getIntentData() {
        // Getting Data from Intent
        val classId = intent.getStringExtra("classId")
        val className = intent.getStringExtra("className")
        val classQuantity = intent.getStringExtra("classQuantity")
        val classDay = intent.getStringExtra("classDay")
        val classTime = intent.getStringExtra("classTime")
        val classRank = intent.getStringExtra("classRank")
        val classStartDate = intent.getStringExtra("classStartDate")
        val classPrice = intent.getStringExtra("classPrice")
        val classLength = intent.getStringExtra("classLength")
        val teacherId = intent.getStringExtra("teacherId")
        val courseId = intent.getStringExtra("courseId")

        // Setting Intent Data
        val classRankSpinner = findViewById<Spinner>(R.id.classRankSpinner)
        val classQuantitySpinner = findViewById<Spinner>(R.id.classQuantitySpinner)
        val classPriceSpinner = findViewById<Spinner>(R.id.classPriceSpinner)
        val classTimeSpinner = findViewById<Spinner>(R.id.classTimeSpinner)
        val classDateSpinner = findViewById<Spinner>(R.id.classDateSpinner)
        val classTeacherSpinner = findViewById<Spinner>(R.id.classTeacherSpinner)
        val classStartDateEditText = findViewById<EditText>(R.id.classStartDate)
        val classLengthSpinner = findViewById<Spinner>(R.id.classLengthSpinner)

        // Set data to the views
        classStartDateEditText.setText(classStartDate)

        // Set Spinner selections based on Intent data
        setSpinnerSelection(classRankSpinner, classRank)
        setSpinnerSelection(classQuantitySpinner, classQuantity)
        setSpinnerSelection(classPriceSpinner, classPrice)
        setSpinnerSelection(classTimeSpinner, classTime)
        setSpinnerSelection(classDateSpinner, classDay)
        setSpinnerSelection(classLengthSpinner, classLength)

        // Select teacher in spinner if available
        val teacherIndex = teacherIds.indexOf(teacherId)
        if (teacherIndex != -1) {
            classTeacherSpinner.setSelection(teacherIndex + 1) // Offset by 1 due to default item
        }
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
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to retrieve teacher names",
                    Toast.LENGTH_SHORT).show()
            }
    }
    // Show date picker dialog
    private fun showDatePickerDialog(classDateSpinner: Spinner, classStartDate: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                // Format the selected date
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                // Validate the selected date
                if (selectedDate.before(Calendar.getInstance())) {
                    Toast.makeText(this, "Selected date cannot be in the past", Toast.LENGTH_SHORT).show()
                    return@DatePickerDialog
                }
                if (validStarDate(selectedDate, classDateSpinner.selectedItem.toString())) {
                    classStartDate.setText(formattedDate)
                } else {
                    Toast.makeText(this, "Invalid start date for the selected schedule.", Toast.LENGTH_SHORT).show()
                    return@DatePickerDialog
                }
            }, year, month, day
        )
        datePickerDialog.show()
    }
    // Check if the selected date matches the schedule
    private fun validStarDate(startDate: Calendar,
                              selectedClassDate: String): Boolean {
        val dayOfWeek = startDate.get(Calendar.DAY_OF_WEEK)
        val classDayName = when(dayOfWeek){
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> ""
        }
        val validClassDays = selectedClassDate.split(" - ").map { it.trim() }
        return validClassDays.contains(classDayName)
    }
    // Check if Teacher did not has same class on same time on the same date
    private fun checkTeacherClass(teacherId: String, date: String, time: String,
                                  startDate: String, length: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        // Get all classes of this teacher
        db.collection("Courses")
            .get()
            .addOnSuccessListener { courses ->
                var conflictFound = false
                var coursesChecked = 0

                if (courses.isEmpty) {
                    callback(true)
                    return@addOnSuccessListener
                }

                for (course in courses) {
                    db.collection("Courses")
                        .document(course.id)
                        .collection("Classes")
                        .whereEqualTo("teacherId", teacherId)
                        .whereEqualTo("time", time)
                        .get()
                        .addOnSuccessListener { classes ->
                            coursesChecked++

                            for (classDoc in classes) {
                                // Get the timetable for each class
                                db.collection("Courses")
                                    .document(course.id)
                                    .collection("Classes")
                                    .document(classDoc.id)
                                    .collection("Timetable")
                                    .get()
                                    .addOnSuccessListener { timetable ->
                                        // Convert our new class dates to Date objects for comparison
                                        val newClassDates = generateClassDates(date, startDate, length)

                                        // Check each session in the existing timetable
                                        for (session in timetable) {
                                            val existingDate = session.getString("date") ?: continue

                                            // Check if this date conflicts with any of our new class dates
                                            if (newClassDates.contains(existingDate)) {
                                                conflictFound = true
                                                callback(false)
                                                return@addOnSuccessListener
                                            }
                                        }

                                        // If we've checked all courses and found no conflicts
                                        if (coursesChecked == courses.size() && !conflictFound) {
                                            callback(true)
                                        }
                                    }
                                    .addOnFailureListener {
                                        callback(false)
                                    }
                            }

                            // If no classes found for this course
                            if (classes.isEmpty && coursesChecked == courses.size() && !conflictFound) {
                                callback(true)
                            }
                        }
                        .addOnFailureListener {
                            callback(false)
                        }
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }
    // Generate Class Dates
    private fun generateClassDates(datePattern: String, startDate: String, length: String): Set<String> {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(startDate) ?: return emptySet()

        val totalSessionsNeeded = length.split(" ")[0].toIntOrNull() ?: return emptySet()

        val classDays = when (datePattern) {
            "Monday - Wednesday - Friday" -> listOf(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY)
            "Tuesday - Thursday - Saturday" -> listOf(Calendar.TUESDAY, Calendar.THURSDAY, Calendar.SATURDAY)
            "Saturday - Sunday" -> listOf(Calendar.SATURDAY, Calendar.SUNDAY)
            else -> return emptySet()
        }

        val dates = mutableSetOf<String>()
        var sessionsCreated = 0

        while (sessionsCreated < totalSessionsNeeded) {
            if (calendar.get(Calendar.DAY_OF_WEEK) in classDays) {
                dates.add(dateFormat.format(calendar.time))
                sessionsCreated++
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }
}