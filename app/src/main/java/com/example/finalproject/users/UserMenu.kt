package com.example.finalproject.users

import android.Manifest

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.DatabaseHelper
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import com.example.finalproject.courses.CourseMenu
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserMenu : AppCompatActivity() {

    private lateinit var adapter: UserAdapter
    private var userlist = ArrayList<Users>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_menu)
        // Initialize views
        val searchInput = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.search_input)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val addTeacherBtn = findViewById<Button>(R.id.btn_add_teacher)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // Set selected item in BottomNavigationView
        bottomNavigation.selectedItemId = R.id.users
        // Search functionality
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                val fb = FirestoreHelper(this@UserMenu)
                userlist.clear()
                // Clear user list only once when the search input changes
                userlist.clear()

                if (query.isNotEmpty()) {
                    // Search for teachers and students separately
                    fb.searchTeacherByName(query) { teachers ->
                        userlist.addAll(teachers)
                        adapter.notifyDataSetChanged()
                    }

                    fb.searchStudentByName(query) { students ->
                        userlist.addAll(students)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    // Reload all teachers and students when input is empty
                    loadAllUsers(fb)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        // Move to Add Teacher Activity
        addTeacherBtn.setOnClickListener {
            val intent = Intent(this, AddTeacher::class.java)
            startActivity(intent)
        }
        // Setup RecyclerView with LayoutManager and Adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(userlist)
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
//                    startActivity(Intent(this, UserMenu::class.java))
                    true
                }
                else -> false
            }
        }
    }
    // Load all Users
    private fun loadAllUsers(fb: FirestoreHelper) {
        fb.getAllTeachers { teachers ->
            userlist.clear()
            userlist.addAll(teachers)
            adapter.notifyDataSetChanged()
        }
        fb.getAllStudents { students ->
            userlist.addAll(students)
            adapter.notifyDataSetChanged()
        }
    }
    // Update RecyclerView when the activity is resumed
    override fun onResume() {
        super.onResume()
        val fb = FirestoreHelper(this)
        loadAllUsers(fb)
    }
}

