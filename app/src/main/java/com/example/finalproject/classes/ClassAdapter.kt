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
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import com.example.finalproject.courses.CourseMenu
import com.example.finalproject.timetables.TimetableMenu

class ClassAdapter(private val context: Context, private val classList: ArrayList<Classes>) :
    RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val className: TextView = itemView.findViewById(R.id.className)
        val classTeacher: TextView = itemView.findViewById(R.id.classTeacher)
        val classQuantity: TextView = itemView.findViewById(R.id.classQuantity)
        val classStatus: TextView = itemView.findViewById(R.id.classStatus)
    }

    // Filter out Canceled classes
    private val filteredClassList: List<Classes>
        get() = classList.filter { it.status != "Canceled" }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.class_card, parent, false)
        return ClassViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val currentClass = filteredClassList[position]
        holder.className.text = "Class: ${currentClass.name}"

        val fb = FirestoreHelper(context)
        // Get teacher name
        fb.getTeacherById(currentClass.teacherId) { teacher ->
            if (teacher != null) {
                holder.classTeacher.text = "Teacher: ${teacher.name}"
            } else {
                Toast.makeText(context, "Teacher not found", Toast.LENGTH_SHORT).show()
            }
        }
        holder.classQuantity.text = "Quantity: ${currentClass.quantity}"
        holder.classStatus.text = "Status: ${currentClass.status}"
        // Update Class status based on Timetable
        fb.updateClassStatus(currentClass.courseId, currentClass.id) { success ->
            if (!success) {
                Log.w("ClassAdapter", "Failed to update status for Class ${currentClass.id}")
            } else {
                // Update the local classList with the new status
                val classIndex = classList.indexOfFirst { it.id == currentClass.id }
                if (classIndex != -1) {
                    val updatedClass = classList[classIndex].copy(
                        status = if (currentClass.status == "Canceled") "Canceled" else {
                            currentClass.status
                        }
                    )
                    classList[classIndex] = updatedClass
                    notifyDataSetChanged()
                }
            }
        }

        // Show Dialog Options
        holder.itemView.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.class_options)
            dialog.setTitle("Choose an Action")

            // Dialog Buttons
            val editButton = dialog.findViewById<TextView>(R.id.edit_button)
            val cancelButton = dialog.findViewById<TextView>(R.id.cancle_button)
            val showTimetableButton = dialog.findViewById<TextView>(R.id.show_button)

            // Edit Button
            editButton.setOnClickListener {
                val intent = Intent(context, EditClass::class.java)
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
                context.startActivity(intent)
                dialog.dismiss()
            }
            // Cancel Class Button with Confirmation Dialog
            cancelButton.setOnClickListener {
                val confirmDialog = AlertDialog.Builder(context)
                confirmDialog.setTitle("Confirm Cancellation")
                confirmDialog.setMessage("Are you sure you want to cancel this class ?")
                confirmDialog.setPositiveButton("Yes") { _, _ ->
                    fb.cancelClass(currentClass.courseId, currentClass.id, context) { success ->
                        if (success) {
                            val classIndex = classList.indexOfFirst { it.id == currentClass.id }
                            if (classIndex != -1) {
                                classList[classIndex] = classList[classIndex].copy(status = "Canceled")
                                val intent = Intent(context, CourseMenu::class.java)
                                context.startActivity(intent)
                                notifyDataSetChanged()
                            }
                        } else {
                            Toast.makeText(context,
                                "Failed to cancel class", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.dismiss()
                }
                confirmDialog.setNegativeButton("No") { _, _ ->
                    // Do nothing
                }
                confirmDialog.show()
            }

            // Show Timetable Button
            showTimetableButton.setOnClickListener {
                val intent = Intent(context, TimetableMenu::class.java)
                intent.putExtra("classId", currentClass.id)
                intent.putExtra("courseId", currentClass.courseId)
                context.startActivity(intent)
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return filteredClassList.size
    }
}