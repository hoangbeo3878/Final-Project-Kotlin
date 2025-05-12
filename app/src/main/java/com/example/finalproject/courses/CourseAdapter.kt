package com.example.finalproject.courses

import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import com.example.finalproject.classes.AddClass
import com.example.finalproject.classes.ClassMenu
import com.google.firebase.firestore.FirebaseFirestore

class CourseAdapter(private val courseList: ArrayList<Courses>) :
    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.title)
        val type: TextView = itemView.findViewById(R.id.course_type)
        val description: TextView = itemView.findViewById(R.id.description)
        val classDetails: TextView = itemView.findViewById(R.id.class_details)
        val studentDetails: TextView = itemView.findViewById(R.id.student_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_card, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val currentCourse = courseList[position]
        holder.name.text = currentCourse.name
        holder.type.text = "Type: ${currentCourse.type}"
        holder.description.text = currentCourse.description
        holder.classDetails.text = "Classes: ${currentCourse.classCount} Existed"
        holder.studentDetails.text = "Students: ${currentCourse.studentCount} Studying"

        // Initialize Firestore
        val firestore = FirebaseFirestore.getInstance()

        // Get number of Classes (excluding Canceled ones)
        val classesCollection = firestore
            .collection("Courses")
            .document(currentCourse.id)
            .collection("Classes")

        classesCollection.get().addOnSuccessListener { classesSnapshot ->
            val activeClasses = classesSnapshot.documents.filter { classDoc ->
                val status = classDoc.getString("status") ?: "Ongoing"
                status != "Canceled"
            }
            val classCount = activeClasses.size
            holder.classDetails.text = "Classes: $classCount Existed"

            // Get number of Students across all active Classes
            if (classCount == 0) {
                holder.classDetails.text = "Classes: There are no classes yet for this course."
                holder.studentDetails.text = "Students: There are no students yet for this course."
                return@addOnSuccessListener
            }

            var totalStudents = 0
            var classesProcessed = 0

            for (classDoc in activeClasses) {
                val classId = classDoc.id
                val registrationsCollection = classesCollection
                    .document(classId)
                    .collection("Registrations")

                registrationsCollection.get().addOnSuccessListener { registrationsSnapshot ->
                    totalStudents += registrationsSnapshot.size()
                    classesProcessed++

                    // Update student count once all active Classes are processed
                    if (classesProcessed == classCount) {
                        holder.studentDetails.text = "Students: $totalStudents Studying"
                    }
                }.addOnFailureListener { e ->
                    Log.e("CourseAdapter", "Error fetching registrations for class $classId", e)
                    classesProcessed++
                    if (classesProcessed == classCount) {
                        holder.studentDetails.text = "Students: $totalStudents Studying"
                    }
                }
            }
        }.addOnFailureListener { e ->
            Log.e("CourseAdapter", "Error fetching classes for course ${currentCourse.id}", e)
            holder.classDetails.text = "Classes: N/A"
            holder.studentDetails.text = "Students: N/A"
        }

        // Set onClickListener for the item
        holder.itemView.setOnClickListener {
            // Show dialog options
            val context = holder.itemView.context
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.course_options)
            dialog.setTitle("Choose an Action")

            // Dialog Buttons
            val editButton = dialog.findViewById<TextView>(R.id.edit_button)
            val deleteButton = dialog.findViewById<TextView>(R.id.delete_button)
            val addClassButton = dialog.findViewById<TextView>(R.id.add_class_button)
            val showClassesButton = dialog.findViewById<TextView>(R.id.show_classes_button)

            // Edit Button
            editButton.setOnClickListener {
                val intent = Intent(context, EditCourse::class.java)
                intent.putExtra("id", currentCourse.id)
                intent.putExtra("name", currentCourse.name)
                intent.putExtra("type", currentCourse.type)
                intent.putExtra("description", currentCourse.description)
                context.startActivity(intent)
                dialog.dismiss()
            }

            // Delete Button
            deleteButton.setOnClickListener {
                val fd = FirestoreHelper(context)
                fd.deleteCourse(currentCourse.id)
                dialog.dismiss()
            }

            // Add Class Button
            addClassButton.setOnClickListener {
                val intent = Intent(context, AddClass::class.java)
                intent.putExtra("name", currentCourse.name)
                intent.putExtra("type", currentCourse.type)
                intent.putExtra("courseId", currentCourse.id)
                context.startActivity(intent)
                dialog.dismiss()
            }

            // Show Classes Button
            showClassesButton.setOnClickListener {
                val intent = Intent(context, ClassMenu::class.java)
                intent.putExtra("courseId", currentCourse.id)
                context.startActivity(intent)
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return courseList.size
    }
}