package com.example.finalproject.type

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import java.util.UUID

class AddType : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_type)

        val type_name = findViewById<EditText>(R.id.typeNameEditText)
        val type_description = findViewById<EditText>(R.id.typeDescription)
        val back_button = findViewById<ImageView>(R.id.backButton)
        val add_button = findViewById<Button>(R.id.addTypeButton)

        val progressDialog = ProgressDialog(this).apply {
            setMessage("Adding Type, please wait...")
            setCancelable(false)
        }

        back_button.setOnClickListener {
            val intent = Intent(this, TypeMenu::class.java)
            startActivity(intent)
        }

        add_button.setOnClickListener {
            val typeId = "TYP-" + UUID.randomUUID().toString()
            val typeName = type_name.text.toString()
            val typeDescription = type_description.text.toString()

            if (typeName.isEmpty() || typeDescription.isEmpty()) {
                Toast.makeText(this,
                    "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val fd = FirestoreHelper(this)
                fd.addType(typeId, typeName, typeDescription,
                    object : FirestoreHelper.FirestoreCallbackType {
                        override fun onLoading(isLoading: Boolean) {
                            if (isLoading) progressDialog.show() else progressDialog.dismiss()
                        }

                        override fun onSuccess() {
                            type_name.text.clear()
                            type_description.text.clear()
                        }

                        override fun onFailure(message: String) {
                            Toast.makeText(this@AddType,
                                "Error: $message", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }
}