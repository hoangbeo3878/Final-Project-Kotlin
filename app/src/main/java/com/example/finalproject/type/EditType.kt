package com.example.finalproject.type

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R

class EditType : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_type)
        // Initialize views
        val typeNameEditText = findViewById<EditText>(R.id.typeNameEditText)
        val typeDescriptionEditText = findViewById<EditText>(R.id.typeDescriptionEditText)
        val backButton = findViewById<ImageView>(R.id.backButton)
        val editTypeButton = findViewById<Button>(R.id.editTypeButton)

        // Back Button
        backButton.setOnClickListener {
            val intent = Intent(this, TypeMenu::class.java)
            startActivity(intent)
        }
        // Update Button
        editTypeButton.setOnClickListener {
            val id = intent.getStringExtra("id") ?: ""
            val name = typeNameEditText.text.toString()
            val description = typeDescriptionEditText.text.toString()
            val fd = FirestoreHelper(this)
            fd.updateType(id, name, description)
        }
        getIntentData()
    }
    // Getting Intent Data
    private fun getIntentData(){
        //Getting Data from Intent
        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("description")
        //Setting Intent Data
        val typeNameEditText = findViewById<EditText>(R.id.typeNameEditText)
        val typeDescriptionEditText = findViewById<EditText>(R.id.typeDescriptionEditText)
        typeNameEditText.setText(name)
        typeDescriptionEditText.setText(description)
    }
}