package com.example.finalproject.timetables

import android.content.Intent
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
        val fd = FirestoreHelper(this)

        fd.getTimetableByClassesId(classId, courseId) { timetable ->
            timetableList.clear()
            timetableList.addAll(timetable)
            adapter.notifyDataSetChanged()
        }
    }

    // Hàm này sẽ cập nhật tất cả tiêu đề vào Firestore
    private fun updateAllTitles() {
        val classId = intent.getStringExtra("classId") ?: ""
        val courseId = intent.getStringExtra("courseId") ?: ""

        val fd = FirestoreHelper(this)

        // Lặp qua tất cả các buổi học trong timetableList và cập nhật tiêu đề vào Firestore
        for (timetable in timetableList) {
            val sessionId = timetable.sessionId
            val newTitle = timetable.title
            // Cập nhật title cho từng buổi học trong Firestore
            fd.updateTimetableTitle(classId, courseId, sessionId, newTitle)
        }

        // Thông báo đã cập nhật thành công
        Toast.makeText(this, "Titles updated successfully", Toast.LENGTH_SHORT).show()

        // Quay lại màn hình trước hoặc thông báo cho người dùng nếu cần
        finish() // Hoặc bạn có thể thêm hành động khác
    }
}
