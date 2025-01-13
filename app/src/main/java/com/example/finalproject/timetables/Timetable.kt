package com.example.finalproject.timetables

data class Timetable (
    val sessionId: String,
    val dateNumber: String,
    val sessionNumber: String,
    val date: String,
    val dayOfWeek: String,
    var title: String,
    val status: String
)