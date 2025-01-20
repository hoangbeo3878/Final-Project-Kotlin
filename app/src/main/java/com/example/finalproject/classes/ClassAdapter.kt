package com.example.finalproject.classes

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import com.example.finalproject.timetables.TimetableMenu

class ClassAdapter (private val context: Context, private val classList: ArrayList<Classes>)
                    : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val className: TextView = itemView.findViewById(R.id.className)
        val classTeacher: TextView = itemView.findViewById(R.id.classTeacher)
        val classQuantity: TextView = itemView.findViewById(R.id.classQuantity)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassAdapter.ClassViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.class_card, parent, false)
        return ClassViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val currentClass = classList[position]
        holder.className.text = "Class: " + currentClass.name
        val fb = FirestoreHelper(context)
        fb.getTeacherById(currentClass.teacherId) { teacher ->
            if (teacher != null) {
                holder.classTeacher.text = "Teacher: " + teacher.name
            } else {
                Toast.makeText(context, "Teacher not found", Toast.LENGTH_SHORT).show()
            }
        }
        holder.classQuantity.text = "Quantity: " + currentClass.quantity
        // Show Dialog Options
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.class_options)
            dialog.setTitle("Choose an Action")
            // Dialog Buttons
            val editButton = dialog.findViewById<TextView>(R.id.edit_button)
            val deleteButton = dialog.findViewById<TextView>(R.id.delete_button)
            val showTimetableButton = dialog.findViewById<TextView>(R.id.show_button)
            // Edit Button
            editButton.setOnClickListener {
                val intent = Intent(holder.itemView.context, EditClass::class.java)
                intent.putExtra("classId", currentClass.id)
                intent.putExtra("className", currentClass.name)
                intent.putExtra("classQuantity", currentClass.quantity)
                intent.putExtra("classDay", currentClass.date)
                intent.putExtra("classTime", currentClass.time)
                intent.putExtra("classRank", currentClass.rank)
                intent.putExtra("classStartDate", currentClass.startDate)
                intent.putExtra("classPrice", currentClass.price)
                intent.putExtra("classLength", currentClass.length)
                intent.putExtra("teacherId", currentClass.teacherId)
                intent.putExtra("courseId", currentClass.courseId)
                Log.d("ClassAdapter", "Class ID: ${currentClass.id}")
                Log.d("ClassAdapter", "Course ID: ${currentClass.courseId}")
                Log.d("ClassAdapter", "Teacher ID: ${currentClass.teacherId}")
                Log.d("ClassAdapter", "Class Name: ${currentClass.name}")
                Log.d("ClassAdapter", "Class Quantity: ${currentClass.quantity}")
                Log.d("ClassAdapter", "Class Day: ${currentClass.date}")
                Log.d("ClassAdapter", "Class Time: ${currentClass.time}")
                Log.d("ClassAdapter", "Class Rank: ${currentClass.rank}")
                Log.d("ClassAdapter", "Class Start Date: ${currentClass.startDate}")
                Log.d("ClassAdapter", "Class Price: ${currentClass.price}")
                Log.d("ClassAdapter", "Class Length: ${currentClass.length}")
                holder.itemView.context.startActivity(intent)
                dialog.dismiss()
            }
            // Delete Button
            deleteButton.setOnClickListener {
                ////
            }
            // Show Timetable Button
            showTimetableButton.setOnClickListener {
                val intent = Intent(holder.itemView.context, TimetableMenu::class.java)
                intent.putExtra("classId", currentClass.id)
                intent.putExtra("courseId", currentClass.courseId)
                holder.itemView.context.startActivity(intent)
                dialog.dismiss()
            }
            dialog.show()
        }
    }
    override fun getItemCount(): Int {
        return classList.size
    }
}