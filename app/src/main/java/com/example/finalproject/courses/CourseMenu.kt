package com.example.finalproject.courses

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.DatabaseHelper
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import com.example.finalproject.users.UserMenu
import com.google.android.material.bottomnavigation.BottomNavigationView

class CourseMenu : AppCompatActivity() {

    private lateinit var adapter: CourseAdapter
    private val courseList = ArrayList<Courses>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Initialize
        val searchInput = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.search_input)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val addcourseBtn = findViewById<Button>(R.id.btn_add_course)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // Set selected item in BottomNavigationView
        bottomNavigation.selectedItemId = R.id.courses
        // Search functionality
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                val fd = FirestoreHelper(this@CourseMenu)
                courseList.clear()
                if (query.isNotEmpty()) {
                    // Perform search when there's input
                    fd.searchCourseByType(query) { courses ->
                        courseList.clear()
                        courseList.addAll(courses)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    // Reload all courses when input is empty
                    fd.getAllCourses { courses ->
                        courseList.clear()
                        courseList.addAll(courses)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        // Add Course button click listener
        addcourseBtn.setOnClickListener {
            val intent = Intent(this, AddCourse::class.java)
            startActivity(intent)
        }
        // Setup RecyclerView with LayoutManager and Adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CourseAdapter(courseList)
        recyclerView.adapter = adapter
        // Set up BottomNavigationView item selection listener
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.stats -> {
//                    startActivity(Intent(this, StatsActivity::class.java))
                    true
                }
                R.id.courses -> {
                    // Current activity; do nothing or return true
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
    // Update RecyclerView when the activity is resumed
    override fun onResume() {
        super.onResume()
        val fd = FirestoreHelper(this)
        courseList.clear()
        fd.getAllCourses { courses ->
            courseList.addAll(courses)
            adapter.notifyDataSetChanged()
        }
    }
}