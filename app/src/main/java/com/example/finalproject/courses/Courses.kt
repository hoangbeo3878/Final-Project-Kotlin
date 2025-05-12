package com.example.finalproject.courses

data class Courses (
    val id: String,
    val name: String,
    val type: String,
    val description: String,
    val classCount: Int = 0,
    val studentCount: Int = 0
)
