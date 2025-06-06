package com.example.finalproject.type

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import com.example.finalproject.courses.AddCourse
import com.example.finalproject.courses.CourseAdapter
import com.example.finalproject.courses.CourseMenu
import com.example.finalproject.courses.Courses
import com.example.finalproject.users.UserMenu
import com.google.android.material.bottomnavigation.BottomNavigationView

class TypeMenu : AppCompatActivity() {
    private lateinit var adapter: TypeAdapter
    private val typeList = ArrayList<Types>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_type_menu)
        // Initialize
        val searchInput = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.search_input)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val addtypeBtn = findViewById<Button>(R.id.btn_add_course)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // Set selected item in BottomNavigationView
        bottomNavigation.selectedItemId = R.id.stats
        // Search functionality
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                val fd = FirestoreHelper(this@TypeMenu)
                typeList.clear()
                if (query.isNotEmpty()) {

                } else {
                    // Reload all courses when input is empty
                    fd.getAllType { types ->
                        typeList.clear()
                        typeList.addAll(types)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        // Add Course button click listener
        addtypeBtn.setOnClickListener {
            val intent = Intent(this, AddType::class.java)
            startActivity(intent)
        }
        // Setup RecyclerView with LayoutManager and Adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TypeAdapter(typeList)
        recyclerView.adapter = adapter
        // Set up BottomNavigationView item selection listener
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.stats -> {
                // Current activity; do nothing or return true
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
        typeList.clear()
        fd.getAllType { types ->
            typeList.addAll(types)
            adapter.notifyDataSetChanged()
        }
    }
}