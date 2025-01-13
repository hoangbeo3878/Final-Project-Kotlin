package com.example.finalproject.timetables

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R

class AddTitleAdapter (private val timetableList: ArrayList<Timetable>,
                       private val onTitleUpdated: (Int, String) -> Unit)
    : RecyclerView.Adapter<AddTitleAdapter.TimetableViewHolder>() {

    class TimetableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val dateNumber: TextView = itemView.findViewById(R.id.dateNumber)
        val session: TextView = itemView.findViewById(R.id.session)
        val date_dayOfWeek: TextView = itemView.findViewById(R.id.date_dayOfWeek)
        val status: TextView = itemView.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.timetable_card, parent, false)
        return TimetableViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TimetableViewHolder, position: Int) {
        val currentItem = timetableList[position]
        holder.dateNumber.text = "Date: " + currentItem.dateNumber
        holder.session.text = "Session: " + currentItem.sessionNumber
        holder.date_dayOfWeek.text = "Time: " + currentItem.date + " - " + currentItem.dayOfWeek
        holder.status.text = "Status: " + currentItem.status
        // Show Title if not null
//        if (!currentItem.title.isNullOrEmpty()) {
//            holder.session.text = "Session: ${currentItem.sessionNumber} - ${currentItem.title}"
//        }
        //
        holder.itemView.setOnClickListener {
            showTitleInputDialog(holder.itemView.context, position, currentItem.title ?: "")
        }
    }

    override fun getItemCount(): Int {
        return timetableList.size
    }

    private fun showTitleInputDialog(context: Context, position: Int, currentTitle: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Enter Title for Session ${position + 1}")

        val input = EditText(context)
        input.hint = "Enter session title"
        input.setText(currentTitle)
        builder.setView(input)

        // Nút "Save"
        builder.setPositiveButton("Save") { _, _ ->
            val newTitle = input.text.toString().trim()
            timetableList[position].title = newTitle
            onTitleUpdated(position, newTitle)
            notifyItemChanged(position)
        }

        // Nút "Cancel"
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}