package com.example.finalproject

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.finalproject.classes.ClassMenu
import com.example.finalproject.classes.Classes
import com.example.finalproject.timetables.Timetable
import com.example.finalproject.courses.CourseMenu
import com.example.finalproject.courses.Courses
import com.example.finalproject.type.Types
import com.example.finalproject.users.UserMenu
import com.example.finalproject.users.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FirestoreHelper (private val context: Context) {
    val firestore = FirebaseFirestore.getInstance()
    // Sign-in with Firebase Authentication
    fun loginWithFirebase(email: String, password: String){
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, CourseMenu::class.java)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
    // Add Type
    fun addType(typeId: String, name: String, description: String,callback: FirestoreCallbackType) {
        callback.onLoading(true)
        // Check if Course Type already exists
        val courseCollection = firestore
            .collection("Types")
            .whereEqualTo("name", name)
        courseCollection.get().addOnCompleteListener { task ->
            callback.onLoading(false)
            if (task.isSuccessful) {
                val documents = task.result
                if (!documents.isEmpty) {
                    // Course Type already exists
                    Toast.makeText(context, "Course Type already exists", Toast.LENGTH_SHORT).show()
                }else{
                    try {
                        // Add course details to Cloud Firestore
                        val typeCollection = firestore.collection("Types")
                        val typeCloud = hashMapOf(
                            "id" to typeId,
                            "name" to name,
                            "description" to description
                        )
                        typeCollection.document(typeId).set(typeCloud)
                            .addOnSuccessListener {
                                // Added Successfully
                                callback.onSuccess()
                                Toast.makeText(context, "Type added successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                callback.onFailure(e.message ?: "Unknown error")
                            }
                    } catch (e: Exception) {
                        Log.e("addType", "Error inserting data", e)
                    }
                }
            }else{
                Toast.makeText(context, "Failed to check course type: ${task.exception?.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    interface FirestoreCallbackType {
        fun onLoading(isLoading: Boolean)
        fun onSuccess()
        fun onFailure(message: String)
    }
    // Update Type
    fun updateType(typeId: String, name: String, description: String) {
        try {
            // Update course details in Cloud Firestore
            val typeCollection = firestore.collection("Types")
            val typeCloud = hashMapOf(
                "id" to typeId,
                "name" to name,
                "description" to description
                )
            typeCollection.document(typeId).set(typeCloud)
            // Updated Successfully
            Toast.makeText(context,"Type updated successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context,"Error updating type: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    // Get All Types
    fun getAllType(firestoreCallback: (ArrayList<Types>) -> Unit) {
        val typeList = ArrayList<Types>()
        val typeCollection = firestore.collection("Types")
        typeCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val typeId = document.getString("id") ?: ""
                    val name = document.getString("name") ?: ""
                    val description = document.getString("description") ?: ""
                    val type = Types(typeId, name, description)
                    typeList.add(type)
                }
                firestoreCallback(typeList)
            }
            .addOnFailureListener { exception ->
                Log.e("getAllTypes", "Error getting types", exception)
                firestoreCallback(typeList)
            }
    }
    // Get Type by name
    fun getTypeByName(name: String, firestoreCallback: (ArrayList<Types>) -> Unit) {
        val typeList = ArrayList<Types>()
        val typeCollection = firestore.collection("Types")
        typeCollection.whereEqualTo("name", name)
            .get()
        
    }
    // Add Course
    fun addCourse(courseId: String,name: String, type: String, description: String,callback: FirestoreCallback) {
        callback.onLoading(true)
        // Check if Course Type already exists
        val courseCollection = firestore
            .collection("Courses")
            .whereEqualTo("type", type)
        courseCollection.get().addOnCompleteListener { task ->
            callback.onLoading(false)
            if (task.isSuccessful) {
                val documents = task.result
                if (!documents.isEmpty) {
                    // Course Type already exists
                    Toast.makeText(context, "Course Type already exists", Toast.LENGTH_SHORT).show()
                }else{
                    try {
                        // Add course details to Cloud Firestore
                        val courseCollection = firestore.collection("Courses")
                        val courseCloud = hashMapOf(
                            "id" to courseId,
                            "name" to name,
                            "type" to type,
                            "description" to description
                        )
                        courseCollection.document(courseId).set(courseCloud)
                            .addOnSuccessListener {
                                // Added Successfully
                                callback.onSuccess()
                                Toast.makeText(context, "Course added successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                callback.onFailure(e.message ?: "Unknown error")
                            }
                    } catch (e: Exception) {
                        Log.e("addCourse", "Error inserting data", e)
                    }
                }
            }else{
                Toast.makeText(context, "Failed to check course type: ${task.exception?.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    interface FirestoreCallback {
        fun onLoading(isLoading: Boolean)
        fun onSuccess()
        fun onFailure(message: String)
    }
    // Update Course
    fun updateCourse(courseId: String, name: String, type: String, description: String) {
        try {
            // Update course details in Cloud Firestore
            val courseCollection = firestore.collection("Courses")
            val courseCloud = hashMapOf(
                "id" to courseId,
                "name" to name,
                "type" to type,
                "description" to description
            )
            courseCollection.document(courseId).set(courseCloud)
            // Updated Successfully
            Toast.makeText(context, "Course updated successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("updateCourse", "Error updating data", e)
        } finally {

        }
    }
    // Delete Course
    fun deleteCourse(id: String) {
        // Show warning dialog before deleting
        val dialog = android.app.AlertDialog.Builder(context)
        dialog.setTitle("Warning")
        dialog.setMessage("Are you sure you want to delete this Course?")
        dialog.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            // Check if the course has any classes
            val classCollection = firestore
                .collection("Courses")
                .document(id)
                .collection("Classes")

            classCollection.get().addOnSuccessListener { classes ->
                if (!classes.isEmpty) {
                    Toast.makeText(context, "This course has classes and cannot be deleted", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }else {
                    try {
                        // Delete course details from Cloud Firestore
                        val courseCollection = firestore
                            .collection("Courses")
                        courseCollection.document(id).delete()
                        // Deleted Successfully
                        Toast.makeText(context, "Course deleted successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e("deleteCourse", "Error deleting data", e)
                    } finally {
                        val intent = Intent(context, CourseMenu::class.java)
                        context.startActivity(intent)
                    }
                }
            }
        }
        dialog.setNegativeButton("No") { _: DialogInterface, _: Int -> }
        dialog.show()
    }
    // Get All Courses
    fun getAllCourses(firestoreCallback: (ArrayList<Courses>) -> Unit) {
        val courseList = ArrayList<Courses>()
        val courseCollection = firestore.collection("Courses")
        courseCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val courseId = document.getString("id") ?: ""
                    val name = document.getString("name") ?: ""
                    val type = document.getString("type") ?: ""
                    val description = document.getString("description") ?: ""
                    val course = Courses(courseId, name, type, description)
                    courseList.add(course)
                }
                firestoreCallback(courseList)
            }
            .addOnFailureListener { exception ->
                Log.e("getAllCourses", "Error getting courses", exception)
                firestoreCallback(courseList)
            }
    }
    // Search Course by Type
    fun searchCourseByName(name: String, firestoreCallback: (ArrayList<Courses>) -> Unit){
        val courseList = ArrayList<Courses>()
        val courseCollection = firestore.collection("Courses")
        courseCollection.whereGreaterThanOrEqualTo("name", name)
            .whereLessThanOrEqualTo("name", name + "\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val courseId = document.getString("id") ?: ""
                    val name = document.getString("name") ?: ""
                    val type = document.getString("type") ?: ""
                    val description = document.getString("description") ?: ""
                    val course = Courses(courseId, name, type, description)
                    courseList.add(course)
                }
                firestoreCallback(courseList)
            }
            .addOnFailureListener { exception ->
                Log.e("searchCourseByType", "Error searching courses", exception)
                firestoreCallback(courseList)
            }
    }
    // Add Teacher
    fun addTeacher(userId: String, role: String, name: String, email: String,
                   phone: String, address: String, image: String, callback: FirestoreCallback) {
        callback.onLoading(true)
        val auth = FirebaseAuth.getInstance()
        val defaultPassword = "456789"
        val firestore = FirebaseFirestore.getInstance()

        // Check if phone number already exists
        firestore.collection("Users")
            .document("Teachers")
            .collection("Details")
            .whereEqualTo("phone", phone)
            .get()
            .addOnCompleteListener { phoneCheckTask ->
                if (phoneCheckTask.isSuccessful) {
                    val documents = phoneCheckTask.result
                    if (!documents.isEmpty) {
                        // Phone number already exists
                        Toast.makeText(context, "Phone number already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        // Phone number is available, create teacher account
                        auth.createUserWithEmailAndPassword(email, defaultPassword)
                            .addOnCompleteListener { createTask ->
                                if (createTask.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.sendEmailVerification()
                                        ?.addOnCompleteListener { verificationTask ->
                                            callback.onLoading(false)
                                            if (verificationTask.isSuccessful) {
                                                try {
                                                    val teacherCollection = firestore
                                                        .collection("Users")
                                                        .document("Teachers")
                                                    val teacherCloud = hashMapOf(
                                                        "id" to userId,
                                                        "role" to role,
                                                        "name" to name,
                                                        "email" to email,
                                                        "phone" to phone,
                                                        "address" to address,
                                                        "image" to image
                                                    )
                                                    teacherCollection
                                                        .collection("Details").document(userId)
                                                        .set(teacherCloud)
                                                        .addOnSuccessListener {
                                                            callback.onSuccess()
                                                            Toast.makeText(context, "Teacher added successfully",
                                                                Toast.LENGTH_SHORT).show()
                                                        }
                                                        .addOnFailureListener { e ->
                                                            callback.onFailure(e.message ?: "Unknown error")
                                                        }
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Error adding teacher: ${e.message}",
                                                        Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                Toast.makeText(context, "Failed to send verification email.",
                                                    Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                } else {
                                    val exception = createTask.exception
                                    if (exception is FirebaseAuthUserCollisionException) {
                                        // Email already exists
                                        Toast.makeText(context, "Email already exists", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Failed to create teacher account: ${exception?.message}",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    }
                } else {
                    Toast.makeText(context, "Failed to check phone number: ${phoneCheckTask.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
    // Delete Teacher
    fun deleteTeacher(userId: String) {
        // Show warning dialog before deleting
        val dialog = android.app.AlertDialog.Builder(context)
        dialog.setTitle("Warning")
        dialog.setMessage("Are you sure you want to delete this Teacher?")
        dialog.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            // Check if the teacher has any classes
            val classCollection = firestore
                .collection("Classes")
                .whereEqualTo("teacherId", userId)

            classCollection.get().addOnSuccessListener { classes ->
                if (!classes.isEmpty) { Toast.makeText(context,
                    "This teacher has classes and cannot be deleted", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                } else{
                    try {
                        // Delete user account from Firebase Authentication
                        val auth = FirebaseAuth.getInstance()
                        val user = auth.currentUser
                        user?.delete()
                        // Delete Teacher Details in Cloud Firestore
                        val teacherCollection = firestore
                            .collection("Users")
                            .document("Teachers")
                            .collection("Details")
                        teacherCollection.document(userId).delete()
                        // Deleted Successfully
                        Toast.makeText(context, "Teacher deleted successfully", Toast.LENGTH_SHORT).show()
                    }catch (e: Exception) {
                        Toast.makeText(context, "Error deleting teacher: ${e.message}", Toast.LENGTH_SHORT).show()
                    }finally {
                        val intent = Intent(context, UserMenu::class.java)
                        context.startActivity(intent)
                    }
                }
            }
        }
        dialog.setNegativeButton("No") { _: DialogInterface, _: Int -> }
        dialog.show()
    }
    // Get All Teachers
    fun getAllTeachers(firestoreCallback: (ArrayList<Users>) -> Unit) {
        val userList = ArrayList<Users>()
        val teacherCollection = firestore
            .collection("Users")
            .document("Teachers")
            .collection("Details")
        teacherCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val userId = document.getString("id") ?: ""
                    val role = document.getString("role") ?: ""
                    val name = document.getString("name") ?: ""
                    val email = document.getString("email") ?: ""
                    val phone = document.getString("phone") ?: ""
                    val address = document.getString("address") ?: ""
                    val image = document.getString("image") ?: ""
                    val teacher = Users(userId, role, name, email, phone, address, image)
                    userList.add(teacher)
                }
                firestoreCallback(userList)
            }
            .addOnFailureListener { exception ->
                Log.e("getAllTeachers", "Error getting users", exception)
                firestoreCallback(userList)
            }
        }
    // Get All Students
    fun getAllStudents(firestoreCallback: (ArrayList<Users>) -> Unit) {
        val userList = ArrayList<Users>()
        val studentCollection = firestore
            .collection("Users")
            .document("Students")
            .collection("Details")
        studentCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val userId = document.getString("id") ?: ""
                    val role = document.getString("role") ?: ""
                    val name = document.getString("name") ?: ""
                    val email = document.getString("email") ?: ""
                    val phone = document.getString("phone") ?: ""
                    val address = document.getString("address") ?: ""
                    val image = document.getString("image") ?: ""
                    val student = Users(userId, role, name, email, phone, address, image)
                    userList.add(student)
                }
                firestoreCallback(userList)
            }
            .addOnFailureListener { exception ->
                Log.e("getAllStudents", "Error getting users", exception)
                firestoreCallback(userList)
            }
    }
    // Search Teacher by Name
    fun searchTeacherByName(name: String, firestoreCallback: (ArrayList<Users>) -> Unit) {
        val userList = ArrayList<Users>()
        val teacherCollection = firestore
            .collection("Users")
            .document("Teachers")
            .collection("Details")
        teacherCollection.get().addOnSuccessListener { result ->
            for (document in result) {
                val fullName = document.getString("name") ?: ""
                if (fullName.contains(name, ignoreCase = true)) {
                    val userId = document.getString("id") ?: ""
                    val role = document.getString("role") ?: ""
                    val email = document.getString("email") ?: ""
                    val phone = document.getString("phone") ?: ""
                    val address = document.getString("address") ?: ""
                    val image = document.getString("image") ?: ""
                    val teacher = Users(userId, role, fullName, email, phone, address, image)
                    userList.add(teacher)
                }
            }
            firestoreCallback(userList)
        }.addOnFailureListener { exception ->
            Log.e("searchTeacherByName", "Error searching teachers", exception)
            firestoreCallback(userList)
        }
    }
    // Search Student by Name
    fun searchStudentByName(name: String, firestoreCallback: (ArrayList<Users>) -> Unit) {
        val userList = ArrayList<Users>()
        val studentCollection = firestore
            .collection("Users")
            .document("Students")
            .collection("Details")
        studentCollection.get().addOnSuccessListener { result ->
            for (document in result) {
                val fullName = document.getString("name") ?: ""
                if (fullName.contains(name, ignoreCase = true)) {
                    val userId = document.getString("id") ?: ""
                    val role = document.getString("role") ?: ""
                    val email = document.getString("email") ?: ""
                    val phone = document.getString("phone") ?: ""
                    val address = document.getString("address") ?: ""
                    val image = document.getString("image") ?: ""
                    val student = Users(userId, role, fullName, email, phone, address, image)
                    userList.add(student)
                }
            }
            firestoreCallback(userList)
        }.addOnFailureListener { exception ->
            Log.e("searchStudentByName", "Error searching students", exception)
            firestoreCallback(userList)
        }
    }
    // Add Class
    fun addClass(classId: String, className: String, rank: String, quantity: String, price: String,
                 date: String, time: String, length: String, startDate: String, courseId: String,
                 teacherId: String, jitsiMeetLink: String, status: String) {
        try {
            val classCollection = firestore
                .collection("Courses")
                .document(courseId)
                .collection("Classes")
            // Add main class document
            val classCloud = hashMapOf(
                "id" to classId,
                "name" to className,
                "rank" to rank,
                "quantity" to quantity,
                "price" to price,
                "date" to date,
                "time" to time,
                "length" to length,
                "startDate" to startDate,
                "courseId" to courseId,
                "teacherId" to teacherId,
                "jitsiMeetLink" to jitsiMeetLink,
                "status" to status
            )
            classCollection.document(classId).set(classCloud)
            // Generate and add timetable
            generateTimetable(
                classId = classId,
                startDate = startDate,
                datePattern = date,
                length = length,
                courseId = courseId,
                firestore = firestore
            )
            Toast.makeText(context, "Class and timetable added successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error adding class and timetable: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("addClass", "Error adding class and timetable", e)
        }
    }
    // Edit Class
    fun editClass(classId: String, className: String, rank: String, quantity: String, price: String,
                  date: String, time: String, length: String, startDate: String, courseId: String,
                  teacherId: String) {
        try {
            val classCollection = firestore
                .collection("Courses")
                .document(courseId)
                .collection("Classes")
            val classCloud = hashMapOf(
                "id" to classId,
                "name" to className,
                "rank" to rank,
                "quantity" to quantity,
                "price" to price,
                "date" to date,
                "time" to time,
                "length" to length,
                "startDate" to startDate,
                "courseId" to courseId,
                "teacherId" to teacherId
            )
            classCollection.document(classId).set(classCloud)
            Toast.makeText(context, "Class updated successfully", Toast.LENGTH_SHORT).show()
        }catch (e: Exception) {
            Toast.makeText(context, "Error updating class: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("editClass", "Error updating class", e)
        }finally {
            val intent = Intent(context, CourseMenu::class.java)
            context.startActivity(intent)
        }
    }
    // Update Class Status
    fun updateClassStatus(courseId: String, classId: String, onComplete: (Boolean) -> Unit) {
        val classRef = firestore
            .collection("Courses")
            .document(courseId)
            .collection("Classes")
            .document(classId)
        val timetableCollection = classRef.collection("Timetable")
        // Get current date
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Fetch current status and startDate
        classRef.get().addOnSuccessListener { classSnapshot ->
            val currentStatus = classSnapshot.getString("status") ?: "Unknown"
            if (currentStatus == "Canceled") {
                onComplete(true)
                return@addOnSuccessListener
            }
            val startDateStr = classSnapshot.getString("startDate") ?: ""
            val startDate = dateFormat.parse(startDateStr)

            if (startDate == null) {
                onComplete(false)
                return@addOnSuccessListener
            }
            timetableCollection.get().addOnSuccessListener { sessions ->
                if (sessions.isEmpty) {
                    // If no sessions, status depends on startDate
                    val newStatus = if (currentDate.before(startDate)) "Upcoming" else "Ongoing"
                    classRef.update("status", newStatus)
                        .addOnSuccessListener {
                            onComplete(true)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context,
                                "Error updating class status: ${e.message}", Toast.LENGTH_SHORT).show()
                            onComplete(false)
                        }
                    return@addOnSuccessListener
                }
                // Check session statuses
                var allCompleted = true
                var hasOngoing = false
                for (session in sessions) {
                    val sessionStatus = session.getString("status") ?: "Unknown"
                    val sessionDateStr = session.getString("date") ?: ""
                    val sessionDate = dateFormat.parse(sessionDateStr)

                    if (sessionStatus != "Completed") {
                        allCompleted = false
                        if (sessionDate != null && !currentDate.before(sessionDate) && currentDate.after(sessionDate)) {
                            hasOngoing = true
                        }
                    }
                }
                // Determine new status
                val newStatus = when {
                    allCompleted -> "Completed"
                    hasOngoing || (currentDate.after(startDate) && !allCompleted) -> "Ongoing"
                    currentDate.before(startDate) -> "Upcoming"
                    else -> "Ongoing"
                }
                classRef.update("status", newStatus)
                    .addOnSuccessListener {
                        onComplete(true)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context,
                            "Error updating class status: ${e.message}", Toast.LENGTH_SHORT).show()
                        onComplete(false)
                    }
            }.addOnFailureListener { e ->
                Toast.makeText(context,
                    "Error checking timetable: ${e.message}", Toast.LENGTH_SHORT).show()
                onComplete(false)
            }
        }.addOnFailureListener { e ->
            Toast.makeText(context,
                "Error fetching class: ${e.message}", Toast.LENGTH_SHORT).show()
            onComplete(false)
        }
    }
    // Cancel Class
    // Trong FirestoreHelper.kt
    fun cancelClass(courseId: String, classId: String, context: Context, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        try {
            db.collection("Courses")
                .document(courseId)
                .collection("Classes")
                .document(classId)
                .update("status", "Canceled")
                .addOnSuccessListener {
                    db.collection("Courses")
                        .document(courseId)
                        .collection("Classes")
                        .document(classId)
                        .collection("Timetable")
                        .get()
                        .addOnSuccessListener { timetableDocs ->
                            for (doc in timetableDocs) {
                                doc.reference.delete()
                            }
                            Toast.makeText(context,
                                "Class canceled and timetable cleared", Toast.LENGTH_SHORT).show()
                            callback(true)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context,
                                "Error clearing timetable: ${e.message}", Toast.LENGTH_SHORT).show()
                            callback(false)
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context,
                        "Error canceling class: ${e.message}", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
        } catch (e: Exception) {
            Toast.makeText(context,
                "Error canceling class: ${e.message}", Toast.LENGTH_SHORT).show()
            callback(false)
        }
    }
    // Generate Timetable
    private fun generateTimetable(classId: String, startDate: String, datePattern: String,
                                  length: String, courseId: String, firestore: FirebaseFirestore) {

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // Format for day of week
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(startDate) ?: return

        val totalSessionsNeeded = length.split(" ")[0].toIntOrNull() ?: return

        val classDays = when (datePattern) {
            "Monday - Wednesday - Friday" -> listOf(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY)
            "Tuesday - Thursday - Saturday" -> listOf(Calendar.TUESDAY, Calendar.THURSDAY, Calendar.SATURDAY)
            "Saturday - Sunday" -> listOf(Calendar.SATURDAY, Calendar.SUNDAY)
            else -> return
        }

        val timetableCollection = firestore
            .collection("Courses")
            .document(courseId)
            .collection("Classes")
            .document(classId)
            .collection("Timetable")

        var sessionNumber = 1
        var dateNumber = 1
        var sessionsCreated = 0
        // Status Logic
        val currentDate = Date()
        val status = if (calendar.time > currentDate) "Upcoming" else "Completed"

        while (sessionsCreated < totalSessionsNeeded) {
            if (calendar.get(Calendar.DAY_OF_WEEK) in classDays) {
                val sessionDate = dateFormat.format(calendar.time)
                val dayOfWeek = dayFormat.format(calendar.time)
                val  sessionId = "$classId-session-$sessionNumber"

                val session = hashMapOf(
                    "sessionId" to sessionId,
                    "title" to "",
                    "dateNumber" to dateNumber,
                    "sessionNumber" to sessionNumber,
                    "date" to sessionDate,
                    "dayOfWeek" to dayOfWeek,
                    "status" to status
                )

                val paddedNumber = String.format("%02d", dateNumber)
                timetableCollection.document(sessionId).set(session)
                sessionNumber++
                dateNumber++
                sessionsCreated++
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
    // Update Timetable Title
    fun updateTimetableTitle(classId: String, courseId: String, sessionId: String,
                             newTitle: String, jitsiMeetLink: String) {
        val timetableRef = firestore.collection("Courses")
            .document(courseId)
            .collection("Classes")
            .document(classId)
            .collection("Timetable")
            .document(sessionId)

        timetableRef.update(
            mapOf(
                "title" to newTitle,
                "jitsiMeetLink" to jitsiMeetLink
            )
        )
            .addOnSuccessListener {
                Log.d("Firestore", "Timetable title updated successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating timetable title", e)
            }
    }
    // Update Timetable Status
    fun updateTimetableStatus(courseId: String, classId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val timetableCollection = firestore
            .collection("Courses")
            .document(courseId)
            .collection("Classes")
            .document(classId)
            .collection("Timetable")

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        timetableCollection.get().addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                for (document in task.result) {
                    val sessionDateStr = document.getString("date")
                    val sessionDate = dateFormat.parse(sessionDateStr)

                    val status = when {
                        sessionDate == null -> "Unknown"
                        sessionDate > currentDate -> "Upcoming"
                        sessionDate < currentDate -> "Completed"
                        else -> "Ongoing"
                    }

                    // Update the status in the Firestore
                    timetableCollection.document(document.id).update("status", status)
                        .addOnFailureListener { e ->
                            Log.e("updateTimetableStatus", "Error updating status", e)
                        }
                }
            } else {
                Log.e("updateTimetableStatus", "Error getting timetable", task.exception)
            }
        }
    }
    // Get Timetable by Classes Id
    fun getTimetableByClassesId(classId: String, courseId: String, firestoreCallback: (ArrayList<Timetable>) -> Unit) {
        val timetableList = ArrayList<Timetable>()
        val timetableCollection = firestore
            .collection("Courses")
            .document(courseId)
            .collection("Classes")
            .document(classId)
            .collection("Timetable")
        timetableCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e("getTimetableByClassesId", "Error getting timetable", exception)
                firestoreCallback(timetableList)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                timetableList.clear()
                for (document in snapshot) {
                    val sessionId = document.getString("sessionId") ?: ""
                    val dateNumber = document.getLong("dateNumber")?.toInt()?.toString() ?: ""
                    val sessionNumber = document.getLong("sessionNumber")?.toInt()?.toString() ?: ""
                    val date = document.getString("date") ?: ""
                    val dayOfWeek = document.getString("dayOfWeek") ?: ""
                    val title = document.getString("title") ?: ""
                    val status = document.getString("status") ?: ""
                    val jitsiMeetLink = document.getString("jitsiMeetLink") ?: ""
                    val timetable = Timetable(sessionId, dateNumber, sessionNumber, date, dayOfWeek, title, status, jitsiMeetLink)
                    timetableList.add(timetable)
                }
                firestoreCallback(timetableList)
            }
        }
    }
    // Get Classes by CourseId
    fun getClassesByCourseId(courseId: String, firestoreCallback: (ArrayList<Classes>) -> Unit) {
        val classList = ArrayList<Classes>()
        val classCollection = firestore
            .collection("Courses")
            .document(courseId)
            .collection("Classes")
        classCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val classId = document.getString("id") ?: ""
                    val className = document.getString("name") ?: ""
                    val rank = document.getString("rank") ?: ""
                    val quantity = document.getString("quantity") ?: ""
                    val price = document.getString("price") ?: ""
                    val date = document.getString("date") ?: ""
                    val time = document.getString("time") ?: ""
                    val length = document.getString("length") ?: ""
                    val startDate = document.getString("startDate") ?: ""
                    val teacherId = document.getString("teacherId") ?: ""
                    val jitsiMeetLink = document.getString("jitsiMeetLink") ?: ""
                    val status = document.getString("status") ?: ""
                    val classes = Classes(classId, className, rank, quantity, price, date, time,
                        length, startDate, courseId, teacherId, jitsiMeetLink, status)
                    classList.add(classes)
                }
                // Return the classList
                firestoreCallback(classList)
            }
            .addOnFailureListener { exception ->
                Log.e("getAllClasses", "Error getting classes", exception)
                firestoreCallback(classList)
            }
    }
    // Get Teacher by Id
    fun getTeacherById(teacherId: String, firestoreCallback: (Users?) -> Unit) {
        val teacherCollection = firestore
            .collection("Users")
            .document("Teachers")
            .collection("Details")
        teacherCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val userId = document.getString("id") ?: ""
                    if (userId == teacherId) {
                        val role = document.getString("role") ?: ""
                        val name = document.getString("name") ?: ""
                        val email = document.getString("email") ?: ""
                        val phone = document.getString("phone") ?: ""
                        val address = document.getString("address") ?: ""
                        val image = document.getString("image") ?: ""
                        val teacher = Users(userId, role, name, email, phone, address, image)
                        firestoreCallback(teacher)
                        return@addOnSuccessListener
                    }
                }
                firestoreCallback(null)
            }
            .addOnFailureListener { exception ->
                Log.e("getTeacherById", "Error getting teacher", exception)
                firestoreCallback(null)
            }
    }
}



