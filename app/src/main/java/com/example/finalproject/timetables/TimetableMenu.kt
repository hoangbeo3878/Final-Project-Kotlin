package com.example.finalproject.timetables

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import com.example.finalproject.classes.ClassMenu
import com.example.finalproject.courses.CourseMenu
import com.example.finalproject.users.UserMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimetableMenu : AppCompatActivity() {

    private lateinit var adapter: TimetableAdapter
    private val timetableList = ArrayList<Timetable>()
    private val handler = Handler(Looper.getMainLooper())
    private val updateStatusRunnable = object : Runnable {
        override fun run() {
            updateStatusLocally()
            handler.postDelayed(this, 60000) // Cập nhật mỗi 60 giây
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timetable_menu)
        // Back button click listener
        val backButton = findViewById<ImageView>(R.id.backButton)
        val courseId = intent.getStringExtra("courseId") ?: ""
        backButton.setOnClickListener {
            val intent = Intent(this, ClassMenu::class.java)
            intent.putExtra("courseId", courseId)
            startActivity(intent)
        }
        // Setup RecyclerView with LayoutManager and Adapter
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TimetableAdapter(timetableList)
        recyclerView.adapter = adapter

        // Lắng nghe dữ liệu từ Firestore
        val classId = intent.getStringExtra("classId") ?: ""
        val fd = FirestoreHelper(this)
        fd.getTimetableByClassesId(classId, courseId) { timetable ->
            timetableList.clear()
            timetableList.addAll(timetable)
            adapter.notifyDataSetChanged()
        }

        // Bắt đầu timer cập nhật status
        handler.post(updateStatusRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Dừng timer khi activity bị hủy
        handler.removeCallbacks(updateStatusRunnable)
    }

    private fun updateStatusLocally() {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var hasChanges = false

        timetableList.forEachIndexed { index, timetable ->
            val sessionDateStr = timetable.date
            val sessionDate = dateFormat.parse(sessionDateStr) ?: return@forEachIndexed
            val newStatus = when {
                sessionDate > currentDate -> "Upcoming"
                sessionDate < currentDate -> "Completed"
                else -> "Ongoing"
            }
            if (timetable.status != newStatus) {
                timetable.status = newStatus
                hasChanges = true
                adapter.notifyItemChanged(index) // Cập nhật từng item
            }
        }

        if (hasChanges) {
            // Nếu có thay đổi, cập nhật Firestore
            val classId = intent.getStringExtra("classId") ?: ""
            val courseId = intent.getStringExtra("courseId") ?: ""
            val fd = FirestoreHelper(this)
            val batch = fd.firestore.batch()
            timetableList.forEach { timetable ->
                val timetableRef = fd.firestore.collection("Courses")
                    .document(courseId)
                    .collection("Classes")
                    .document(classId)
                    .collection("Timetable")
                    .document(timetable.sessionId)
                batch.update(timetableRef, "status", timetable.status)
            }
            batch.commit()
                .addOnFailureListener { e ->
                    android.util.Log.e("TimetableMenu", "Error updating status in Firestore", e)
                }
        }
    }
}