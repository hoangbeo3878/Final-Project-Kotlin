package com.example.finalproject.timetables

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R


class AddTitleMenu : AppCompatActivity() {

    private lateinit var adapter: AddTitleAdapter
    private val timetableList = ArrayList<Timetable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_title)

        val doneBtn = findViewById<Button>(R.id.btn_done)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // Setup RecyclerView with LayoutManager and AddTitleAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AddTitleAdapter(timetableList) { position, newTitle ->
            // Khi title thay đổi, cập nhật vào danh sách timetableList
            timetableList[position].title = newTitle
        }
        recyclerView.adapter = adapter

        doneBtn.setOnClickListener {
            updateAllTitles()
        }
    }

    override fun onResume() {
        super.onResume()
        val classId = intent.getStringExtra("classId") ?: ""
        val courseId = intent.getStringExtra("courseId") ?: ""
        val jitsiMeetLink = intent.getStringExtra("jitsiMeetLink") ?: ""
        val fd = FirestoreHelper(this)

        fd.getTimetableByClassesId(classId, courseId) { timetable ->
            timetableList.clear()
            timetable.forEach { session ->
                session.jitsiMeetLink = jitsiMeetLink
            }
            timetableList.addAll(timetable)
            adapter.notifyDataSetChanged()
        }
    }

    private fun updateAllTitles() {
        val classId = intent.getStringExtra("classId") ?: ""
        val courseId = intent.getStringExtra("courseId") ?: ""
        val jitsiMeetLink = intent.getStringExtra("jitsiMeetLink") ?: ""

        val fd = FirestoreHelper(this)

        for (timetable in timetableList) {
            val sessionId = timetable.sessionId
            val newTitle = timetable.title
            fd.updateTimetableTitle(classId, courseId, sessionId, newTitle, jitsiMeetLink)
        }

        Toast.makeText(this, "Titles updated successfully", Toast.LENGTH_SHORT).show()

        finish()
    }
}
