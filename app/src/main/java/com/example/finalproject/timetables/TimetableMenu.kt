package com.example.finalproject.timetables

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import com.example.finalproject.courses.CourseMenu
import com.example.finalproject.users.UserMenu
import com.google.android.material.bottomnavigation.BottomNavigationView

class TimetableMenu : AppCompatActivity() {

    private lateinit var adapter: TimetableAdapter
    private val timetableList = ArrayList<Timetable>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timetable_menu)
        // Initialize
        val searchInput = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.search_input)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // Set selected item in BottomNavigationView
        bottomNavigation.selectedItemId = R.id.courses
        // Setup RecyclerView with LayoutManager and Adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TimetableAdapter(timetableList)
        recyclerView.adapter = adapter
        // Set up BottomNavigationView item selection listener
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.stats -> {
//                    startActivity(Intent(this, StatsActivity::class.java))
                    true
                }
                R.id.courses -> {
                    startActivity(Intent(this, CourseMenu::class.java))
                    true
                }
                R.id.users -> {
                    startActivity(Intent(this, UserMenu::class.java))
                    true
                }
                else -> false
            }
        }
    }
    // Update timetable status when the activity starts
    override fun onStart(){
        super.onStart()
        val classId = intent.getStringExtra("classId") ?: ""
        val courseId = intent.getStringExtra("courseId") ?: ""
        val fd = FirestoreHelper(this)
        fd.updateTimetableStatus(courseId, classId)
    }
    // Update RecyclerView when the activity is resumed
    override fun onResume() {
        super.onResume()
        val classId = intent.getStringExtra("classId") ?: ""
        val courseId = intent.getStringExtra("courseId") ?: ""
        val fd = FirestoreHelper(this)
        fd.getTimetableByClassesId(classId, courseId) { timetable ->
            timetableList.clear()
            timetableList.addAll(timetable)
            adapter.notifyDataSetChanged()
        }
    }
}