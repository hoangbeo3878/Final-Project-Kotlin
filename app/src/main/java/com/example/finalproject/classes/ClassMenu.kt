package com.example.finalproject.classes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.DatabaseHelper
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import com.example.finalproject.courses.CourseAdapter
import com.example.finalproject.courses.CourseMenu
import com.example.finalproject.courses.Courses
import com.example.finalproject.users.UserMenu
import com.google.android.material.bottomnavigation.BottomNavigationView

class ClassMenu : AppCompatActivity() {

    private lateinit var adapter: ClassAdapter
    private val classList = ArrayList<Classes>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_class_menu)
        // Initialize
        val searchInput = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.search_input)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // Set selected item in BottomNavigationView
        bottomNavigation.selectedItemId = R.id.courses
        // Setup RecyclerView with LayoutManager and Adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ClassAdapter(this, classList)
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
    // Update RecyclerView when the activity is resumed
    override fun onResume() {
        super.onResume()
        val fd = FirestoreHelper(this)
        val courseId = intent.getStringExtra("courseId") ?: ""
        fd.getClassesByCourseId(courseId) { classes ->
            classList.clear()
            classList.addAll(classes)
            adapter.notifyDataSetChanged()
        }
//        val teacherId = intent.getStringExtra("userId") ?: ""
//        fd.getClassesByTeacher(teacherId) { classes ->
//            classList.clear()
//            classList.addAll(classes)
//            adapter.notifyDataSetChanged()
//        }
    }
}