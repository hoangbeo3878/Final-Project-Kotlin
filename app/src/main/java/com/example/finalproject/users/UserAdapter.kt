package com.example.finalproject.users

import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import com.example.finalproject.classes.ClassMenu

class UserAdapter(private val userlist: ArrayList<Users>) : RecyclerView.Adapter<UserAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.name_details)
        val email: TextView = itemView.findViewById(R.id.email_details)
        val role: TextView = itemView.findViewById(R.id.role_details)
        val address: TextView = itemView.findViewById(R.id.address_details)
        val phone: TextView = itemView.findViewById(R.id.phone_number_details)
        val avatar: ImageView = itemView.findViewById(R.id.profile_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_card, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val user = userlist[position]

        // Set text fields
        holder.role.text = user.role
        holder.username.text = "Name: ${user.name}"
        holder.address.text = "Address: ${user.address}"
        holder.phone.text = "Phone Number: ${user.phone}"
        holder.email.text = "Email: ${user.email}"

        // Load image from URL
        Glide.with(holder.itemView.context)
            .load(user.image) // Directly use the image URL
            .circleCrop()
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_stats)
            .into(holder.avatar)

        // Item click listener for options dialog
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.teacher_options)
            dialog.setTitle("Choose an Action")

            val editButton = dialog.findViewById<TextView>(R.id.edit_button)
            val deleteButton = dialog.findViewById<TextView>(R.id.delete_button)
            val showButton = dialog.findViewById<TextView>(R.id.show_classes_button)

            // Edit Button
            editButton.setOnClickListener {
                dialog.dismiss()
                // Add edit functionality if needed
            }

            // Delete Button
            deleteButton.setOnClickListener {
                val fb = FirestoreHelper(context)
                fb.deleteTeacher(user.id)
                dialog.dismiss()
            }

            // Show Classes Button (commented out in original code)
            // showButton.setOnClickListener {
            //     val intent = Intent(holder.itemView.context, ClassMenu::class.java)
            //     intent.putExtra("userId", user.id)
            //     holder.itemView.context.startActivity(intent)
            //     dialog.dismiss()
            // }

            dialog.show()
        }
    }

    override fun getItemCount(): Int = userlist.size

    // Glide Module
    @GlideModule
    class MyAppGlideModule : AppGlideModule() {
        override fun isManifestParsingEnabled(): Boolean = false
    }
}