package com.example.finalproject.users

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.finalproject.FirestoreHelper
import com.example.finalproject.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.HashMap
import java.util.UUID

class AddTeacher : AppCompatActivity() {
    private lateinit var imageUri: Uri
    private lateinit var profileImageView: ImageView

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 100
        private const val REQUEST_CODE_PERMISSION = 101
        private const val TAG = "AddTeacherActivity"

        // Cloudinary Configuration
        private const val CLOUD_NAME = "dmuhn7oxo"
        private const val API_KEY = "646537237855688"
        private const val API_SECRET = "NH-_blq9qKYs6FGy-o2C4cMind0"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_teacher)

        // Initialize Cloudinary
        initCloudinaryConfig()

        // Initialize Views
        val backButton: ImageView = findViewById(R.id.backButton)
        val browseImageButton: Button = findViewById(R.id.browse_image_button)
        val submitButton: MaterialButton = findViewById(R.id.submit_button)
        val teacherName: TextInputEditText = findViewById(R.id.teacher_name)
        val teacherEmail: TextInputEditText = findViewById(R.id.teacher_email)
        val teacherAddress: TextInputEditText = findViewById(R.id.teacher_address)
        val teacherPhoneNumber: TextInputEditText = findViewById(R.id.teacher_phone_number)
        profileImageView = findViewById(R.id.profile_image)

        // Progress Dialog
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Adding Teacher, please wait...")
            setCancelable(false)
        }

        // Back Button
        backButton.setOnClickListener {
            startActivity(Intent(this, UserMenu::class.java))
        }

        // Browse Image Button
        browseImageButton.setOnClickListener {
            if (hasStoragePermission()) {
                pickImage()
            } else {
                requestStoragePermission()
            }
        }

        // Submit Button
        submitButton.setOnClickListener {
            val name = teacherName.text.toString().trim()
            val email = teacherEmail.text.toString().trim()
            val address = teacherAddress.text.toString().trim()
            val phone = teacherPhoneNumber.text.toString().trim()

            when {
                name.isEmpty() || email.isEmpty()
                        || address.isEmpty() || phone.isEmpty() -> {
                    Toast.makeText(this,
                        "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                }
                !isValidEmail(email) -> {
                    Toast.makeText(this,
                        "Invalid email address.", Toast.LENGTH_SHORT).show()
                }
                !isValidPhoneNumber(phone) -> {
                    Toast.makeText(this,
                        "Invalid phone number.", Toast.LENGTH_SHORT).show()
                }
                !::imageUri.isInitialized -> {
                    Toast.makeText(this,
                        "Please select a profile image.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val userId = "TEA-${UUID.randomUUID()}"
                    uploadImageToCloudinary(userId, imageUri) { imageUrl ->
                        val firestoreHelper = FirestoreHelper(this)
                        firestoreHelper.addTeacher(
                            userId,
                            "Teacher",
                            name,
                            email,
                            phone,
                            address,
                            imageUrl,
                            object : FirestoreHelper.FirestoreCallback {
                                override fun onLoading(isLoading: Boolean) {
                                    if (isLoading) progressDialog.show() else progressDialog.dismiss()
                                }

                                override fun onSuccess() {
                                    // Clear fields
                                    teacherName.text?.clear()
                                    teacherEmail.text?.clear()
                                    teacherAddress.text?.clear()
                                    teacherPhoneNumber.text?.clear()
                                    profileImageView.setImageResource(R.drawable.ic_user)

                                    Toast.makeText(this@AddTeacher,
                                        "Teacher added successfully", Toast.LENGTH_SHORT).show()
                                }

                                override fun onFailure(message: String) {
                                    Toast.makeText(this@AddTeacher,
                                        "Error: $message", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun initCloudinaryConfig() {
        val config = HashMap<String, String>().apply {
            put("cloud_name", CLOUD_NAME)
            put("api_key", API_KEY)
            put("api_secret", API_SECRET)
        }

        try {
            MediaManager.init(this, config)
        } catch (e: Exception) {
            Log.e(TAG, "Cloudinary initialization failed", e)
            Toast.makeText(this, "Cloudinary setup failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageToCloudinary(userId: String, uri: Uri, onSuccess: (String) -> Unit) {
        try {
            val fileName = "TEA-${userId}_${System.currentTimeMillis()}.jpg"
            MediaManager.get().upload(uri)
                .option("public_id", fileName)
                .option("resource_type", "image")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Log.d(TAG, "Upload started")
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        Log.d(TAG, "Upload progress: $bytes / $totalBytes")
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val imageUrl = resultData["secure_url"] as? String
                        if (imageUrl != null) {
                            onSuccess(imageUrl)
                        } else {
                            Log.e(TAG, "No image URL returned")
                            Toast.makeText(this@AddTeacher, "Upload failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e(TAG, "Cloudinary upload error: ${error.description}")
                        Toast.makeText(this@AddTeacher, "Upload failed: ${error.description}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        Log.w(TAG, "Upload rescheduled: ${error.description}")
                    }
                }).dispatch()
        } catch (e: Exception) {
            Log.e(TAG, "Image upload exception", e)
            Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    private fun hasStoragePermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            } else {
                Toast.makeText(this, "Permission is required to select an Image.", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Unable to process the Image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "No Image selected.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailRegex.toRegex())
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        val phoneRegex = "^\\d{10}\$"
        return phone.matches(phoneRegex.toRegex())
    }
}