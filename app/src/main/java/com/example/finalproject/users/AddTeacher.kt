package com.example.finalproject.users

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.finalproject.DatabaseHelper
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class AddTeacher : AppCompatActivity() {
    private lateinit var imageUri: Uri
    private lateinit var profileImageView: ImageView
    companion object {
        const val REQUEST_CODE_PICK_IMAGE = 100
        const val REQUEST_CODE_PERMISSION = 101
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_teacher)
        // Initialize views
        val backButton = findViewById<ImageView>(R.id.backButton)
        val browseImageButton = findViewById<Button>(R.id.browse_image_button)
        val submitButton = findViewById<com.google.android.material.button.MaterialButton>(R.id.submit_button)
        val teacherName = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.teacher_name)
        val teacherEmail = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.teacher_email)
        val teacherAddress = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.teacher_address)
        val teacherPhoneNumber = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.teacher_phone_number)
        profileImageView = findViewById(R.id.profile_image)
        // Back Button
        backButton.setOnClickListener {
            val intent = Intent(this, UserMenu::class.java)
            startActivity(intent)
        }
        // Button to choose an image
        browseImageButton.setOnClickListener {
            if (hasStoragePermission()) {
                pickImage()
            } else {
                requestStoragePermission()
            }
        }
        // Submit button
        submitButton.setOnClickListener {
            val name = teacherName.text.toString().trim()
            val email = teacherEmail.text.toString().trim()
            val address = teacherAddress.text.toString().trim()
            val phone = teacherPhoneNumber.text.toString().trim()
            if (name.isEmpty() || email.isEmpty() || address.isEmpty() || phone.isEmpty() || !::imageUri.isInitialized) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            } else if (!isValidEmail(email)) {
                Toast.makeText(this, "Invalid email address.", Toast.LENGTH_SHORT).show()
            } else if (!isValidPhoneNumber(phone)) {
                Toast.makeText(this, "Invalid phone number.", Toast.LENGTH_SHORT).show()
            } else {
                val userId = "TEA-" + UUID.randomUUID().toString()
                val imagePath = getRealPathFromURI(imageUri)
                if (imagePath != null) {
                    val fb = FirestoreHelper(this)
                    fb.addTeacher(userId,"Teacher", name, email, phone, address, imagePath)
                    // Clear fields after saving
                    teacherName.text?.clear()
                    teacherEmail.text?.clear()
                    teacherAddress.text?.clear()
                    teacherPhoneNumber.text?.clear()
                    profileImageView.setImageResource(R.drawable.ic_user)
                } else {
                    Toast.makeText(this, "Unable to process the image.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    // Open image picker
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }
    // Check storage permission
    private fun hasStoragePermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                REQUEST_CODE_PERMISSION
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSION
            )
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            } else {
                Toast.makeText(this, "Permission is required to select an Image.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    imageUri = uri
                    profileImageView.setImageURI(imageUri)
                } catch (e: Exception) {
                    Toast.makeText(this, "Unable to process the Image: ${e.message}",
                        Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "No Image selected.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Get real path from URI
    private fun getRealPathFromURI(uri: Uri): String? {
        return try {
            val userId = "TEA-" + UUID.randomUUID().toString()
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = "$userId"+"_"+"${System.currentTimeMillis()}.jpg"
            val outputFile = File(filesDir, fileName)
            inputStream?.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
            outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    // Validate email
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }
    // Validate phone number
    private fun isValidPhoneNumber(phone: String): Boolean {
        val phonePattern = "^\\d{10}\$"
        return phone.matches(phonePattern.toRegex())
    }
}
