package com.example.finalproject.courses

import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.DatabaseHelper
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import com.example.finalproject.classes.AddClass
import com.example.finalproject.classes.ClassMenu

class CourseAdapter (private val courseList: ArrayList<Courses>)
    : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>()  {
    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val class_details: TextView = itemView.findViewById(R.id.class_details)
        val student_details: TextView = itemView.findViewById(R.id.student_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_card, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val currentCourse = courseList[position]
        holder.title.text = currentCourse.type
        holder.description.text = currentCourse.description
        holder.itemView.setOnClickListener {
            // Show dialog options
            val context = holder.itemView.context
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.course_options)
            dialog.setTitle("Choose an Action")
            // Dialog Buttons
            val editButton = dialog.findViewById<TextView>(R.id.edit_button)
            val deleteButton = dialog.findViewById<TextView>(R.id.delete_button)
            val adClassButton = dialog.findViewById<TextView>(R.id.add_class_button)
            val showClassesButton = dialog.findViewById<TextView>(R.id.show_classes_button)
            //Edit Button
            editButton.setOnClickListener {
                val intent = Intent(holder.itemView.context, EditCourse::class.java)
                intent.putExtra("id", currentCourse.id)
                intent.putExtra("title", currentCourse.type)
                intent.putExtra("description", currentCourse.description)
                holder.itemView.context.startActivity(intent)
                dialog.dismiss()
            }
            //Delete Button
            deleteButton.setOnClickListener {
                val fd = FirestoreHelper(context)
                fd.deleteCourse(currentCourse.id)
                dialog.dismiss()
            }
            //Add Class Button
            adClassButton.setOnClickListener {
                val intent = Intent(holder.itemView.context, AddClass::class.java)
                intent.putExtra("type", currentCourse.type)
                intent.putExtra("courseId", currentCourse.id)
                holder.itemView.context.startActivity(intent)
                dialog.dismiss()
            }
            //Show Classes Button
            showClassesButton.setOnClickListener {
                val intent = Intent(holder.itemView.context, ClassMenu::class.java)
                intent.putExtra("courseId", currentCourse.id)
                holder.itemView.context.startActivity(intent)
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

}