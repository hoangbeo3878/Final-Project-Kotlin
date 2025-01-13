package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.finalproject.courses.CourseMenu
import com.google.firebase.auth.FirebaseAuth

class LoginAdmin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_admin)
        // Initialize views
        val emailField = findViewById<EditText>(R.id.emailLoggedIn)
        val passwordField = findViewById<EditText>(R.id.passwordLoggedIn)
        val loginButton = findViewById<Button>(R.id.addCourseButton)
        // Set up click listener for login button
        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            // Validate email and password
            if (email.isNotEmpty() && password.isNotEmpty()) {
                val db = DatabaseHelper(this)
                db.loginWithFirebase(email, password)
            }else
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
    }
    override fun onStart() {
        super.onStart()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
                val intent = Intent(this, CourseMenu::class.java)
                startActivity(intent)
                finish()
        }
    }
}