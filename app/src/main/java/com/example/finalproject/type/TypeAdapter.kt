package com.example.finalproject.type

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
import com.example.finalproject.type.EditType

class TypeAdapter (private val typeList: ArrayList<Types>)
    : RecyclerView.Adapter<TypeAdapter.CourseViewHolder>()  {
    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.type_card, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val currentCourse = typeList[position]
        holder.name.text = currentCourse.name
        holder.description.text = currentCourse.description
        holder.itemView.setOnClickListener {
            // Show dialog options
            val context = holder.itemView.context
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.type_options)
            dialog.setTitle("Choose an Action")
            // Dialog Buttons
            val editButton = dialog.findViewById<TextView>(R.id.edit_button)
            val deleteButton = dialog.findViewById<TextView>(R.id.delete_button)
            //Edit Button
            editButton.setOnClickListener {
                val intent = Intent(holder.itemView.context, EditType::class.java)
                intent.putExtra("id", currentCourse.id)
                intent.putExtra("name", currentCourse.name)
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
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return typeList.size
    }

}