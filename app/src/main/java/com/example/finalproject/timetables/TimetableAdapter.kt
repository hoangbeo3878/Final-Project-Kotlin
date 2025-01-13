package com.example.finalproject.timetables

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R

class TimetableAdapter (private val timetableList: ArrayList<Timetable>)
    : RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder>() {

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
    }

    override fun getItemCount(): Int {
        return timetableList.size
    }
}