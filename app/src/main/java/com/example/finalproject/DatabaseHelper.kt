package com.example.finalproject

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.example.finalproject.classes.Classes
import com.example.finalproject.courses.CourseMenu
import com.example.finalproject.courses.Courses
import com.example.finalproject.users.UserMenu
import com.example.finalproject.users.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class DatabaseHelper (private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "intercourse.db"
        private const val DATABASE_VERSION = 13
        //Course Table
        private const val TABLE_COURSES = "courses"
        private const val COLUMN_COURSE_ID = "course_id"
        private const val COLUMN_COURSE_TYPE = "course_type"
        private const val COLUMN_COURSE_DESCRIPTION = "course_description"
        //User Table
        private const val TABLE_USER = "users"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_USER_ROLE = "user_role"
        private const val COLUMN_USER_NAME = "user_name"
        private const val COLUMN_USER_EMAIL = "user_email"
        private const val COLUMN_USER_PHONE = "user_phone"
        private const val COLUMN_USER_ADDRESS = "user_address"
        private const val COLUMN_USER_IMAGE = "user_image"
        //Class Table
        private const val TABLE_CLASSES = "classes"
        private const val COLUMN_CLASS_ID = "class_id"
        private const val COLUMN_CLASS_NAME = "class_name"
        private const val COLUMN_CLASS_RANK = "class_rank"
        private const val COLUMN_CLASS_QUANTITY = "class_quantity"
        private const val COLUMN_CLASS_PRICE = "class_price"
        private const val COLUMN_CLASS_TIME = "class_time"
        private const val COLUMN_CLASS_DATE = "class_date"
        private const val COLUMN_CLASS_LENGTH = "class_length"
        private const val COLUMN_CLASS_START_DATE = "class_start_date"
        private const val COLUMN_CLASS_COURSE_ID = "course_id"
        private const val COLUMN_CLASS_TEACHER_ID = "teacher_id"
    }
    override fun onCreate(db: SQLiteDatabase) {
        // Create the courses table
        db.execSQL(
            "CREATE TABLE $TABLE_COURSES (" +
                    "$COLUMN_COURSE_ID TEXT PRIMARY KEY ," +
                    "$COLUMN_COURSE_TYPE TEXT," +
                    "$COLUMN_COURSE_DESCRIPTION TEXT)"
        )
        // Create the users table
        db.execSQL(
            "CREATE TABLE $TABLE_USER (" +
                    "$COLUMN_USER_ID TEXT PRIMARY KEY ," +
                    "$COLUMN_USER_ROLE TEXT," +
                    "$COLUMN_USER_NAME TEXT," +
                    "$COLUMN_USER_EMAIL TEXT," +
                    "$COLUMN_USER_PHONE TEXT," +
                    "$COLUMN_USER_ADDRESS TEXT," +
                    "$COLUMN_USER_IMAGE TEXT)"
        )
        // Create the classes table
        db.execSQL(
            "CREATE TABLE $TABLE_CLASSES (" +
                    "$COLUMN_CLASS_ID TEXT PRIMARY KEY ," +
                    "$COLUMN_CLASS_NAME TEXT," +
                    "$COLUMN_CLASS_RANK TEXT," +
                    "$COLUMN_CLASS_QUANTITY INTEGER," +
                    "$COLUMN_CLASS_PRICE REAL," +
                    "$COLUMN_CLASS_DATE TEXT," +
                    "$COLUMN_CLASS_TIME TEXT," +
                    "$COLUMN_CLASS_LENGTH TEXT," +
                    "$COLUMN_CLASS_START_DATE TEXT," +
                    "$COLUMN_CLASS_COURSE_ID TEXT," +
                    "$COLUMN_CLASS_TEACHER_ID TEXT," +
                    "FOREIGN KEY ($COLUMN_CLASS_COURSE_ID) REFERENCES $TABLE_COURSES($COLUMN_COURSE_ID)," +
                    "FOREIGN KEY ($COLUMN_CLASS_TEACHER_ID) REFERENCES $TABLE_USER($COLUMN_USER_ID))"
        )
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COURSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLASSES")
        onCreate(db)
    }
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
//    // Add Course
//    fun addCourse(courseId: String, type: String, description: String) {
//        val db = this.writableDatabase
//        try {
//            db.beginTransaction()
//            // Add course details to Cloud Firestore
//            val firestore = FirebaseFirestore.getInstance()
//            val courseCollection = firestore.collection("Courses")
//            val courseCloud = hashMapOf(
//                "id" to courseId,
//                "type" to type,
//                "description" to description
//            )
//            courseCollection.document(courseId).set(courseCloud)
//            // Added Successfully
//            db.setTransactionSuccessful()
//            Toast.makeText(context, "Course added successfully", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            Log.e("addCourse", "Error inserting data", e)
//        } finally {
//            db.endTransaction()
//            db.close()
//        }
//    }
//    // Update Course
//    fun updateCourse(courseId: String, type: String, description: String) {
//        val db = this.writableDatabase
//        try {
//            db.beginTransaction()
//            // Update course details in Cloud Firestore
//            val firestore = FirebaseFirestore.getInstance()
//            val courseCollection = firestore.collection("Courses")
//            val courseCloud = hashMapOf(
//                "id" to courseId,
//                "type" to type,
//                "description" to description
//            )
//            courseCollection.document(courseId).set(courseCloud)
//            // Updated Successfully
//            db.setTransactionSuccessful()
//            Toast.makeText(context, "Course updated successfully", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            Log.e("updateCourse", "Error updating data", e)
//        } finally {
//            db.endTransaction()
//            db.close()
//        }
//    }
//    // Delete Course
//    fun deleteCourse(id: String) {
//        val db = this.writableDatabase
//        // Show warning dialog before deleting
//        val dialog = android.app.AlertDialog.Builder(context)
//        dialog.setTitle("Warning")
//        dialog.setMessage("Are you sure you want to delete this Course?")
//        dialog.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
//            try {
//                db.beginTransaction()
//                // Delete course details from Cloud Firestore
//                val firestore = FirebaseFirestore.getInstance()
//                val courseCollection = firestore.collection("Courses")
//                courseCollection.document(id).delete()
//                // Deleted Successfully
//                db.setTransactionSuccessful()
//                Toast.makeText(context, "Course deleted successfully", Toast.LENGTH_SHORT).show()
//            } catch (e: Exception) {
//                Log.e("deleteCourse", "Error deleting data", e)
//            } finally {
//                db.endTransaction()
//                db.close()
//                val intent = Intent(context, CourseMenu::class.java)
//                context.startActivity(intent)
//            }
//        }
//        dialog.setNegativeButton("No") { _: DialogInterface, _: Int -> }
//        dialog.show()
//    }
//    // Get All Courses
//    fun getAllCourses(firestoreCallback: (ArrayList<Courses>) -> Unit) {
//        val courseList = ArrayList<Courses>()
//        val firestore = FirebaseFirestore.getInstance()
//        val courseCollection = firestore.collection("Courses")
//        courseCollection.get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    val courseId = document.getString("id") ?: ""
//                    val type = document.getString("type") ?: ""
//                    val description = document.getString("description") ?: ""
//                    val course = Courses(courseId, type, description)
//                    courseList.add(course)
//                }
//                firestoreCallback(courseList)
//            }
//            .addOnFailureListener { exception ->
//                Log.e("getAllCourses", "Error getting courses", exception)
//                firestoreCallback(courseList)
//            }
//    }
//    // Search Course by Type
//    fun searchCourse(query: String): ArrayList<Courses> {
//        val courseList = ArrayList<Courses>()
//        val db = this.readableDatabase
//        val selectQuery = "SELECT * FROM $TABLE_COURSES WHERE $COLUMN_COURSE_TYPE LIKE ?"
//        val cursor = db.rawQuery(selectQuery, arrayOf("%$query%"))
//
//        if (cursor.moveToFirst()) {
//            do {
//                val course = Courses(
//                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID)),
//                    type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_TYPE)),
//                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_DESCRIPTION))
//                )
//                courseList.add(course)
//            } while (cursor.moveToNext())
//        }
//        cursor.close()
//        db.close()
//        return courseList
//    }
//    // Add Teacher
//    fun addTeacher(userId: String ,role: String, name: String, email: String,
//        phone: String, address: String, image: String
//    ) {
//        val auth = FirebaseAuth.getInstance()
//        val db = this.writableDatabase
//        val defaultPassword = "456789"
//        // Create a new user with email and a randomly generated password
//        auth.createUserWithEmailAndPassword(email, defaultPassword)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val user = auth.currentUser
//                    user?.sendEmailVerification()
//                        ?.addOnCompleteListener { verificationTask ->
//                            if (verificationTask.isSuccessful) {
//                                try {
//                                    db.beginTransaction()
//                                    // Add user details to the local database
//                                    val values = ContentValues().apply {
//                                        put(COLUMN_USER_ID, userId )
//                                        put(COLUMN_USER_ROLE, role)
//                                        put(COLUMN_USER_NAME, name)
//                                        put(COLUMN_USER_EMAIL, email)
//                                        put(COLUMN_USER_PHONE, phone)
//                                        put(COLUMN_USER_ADDRESS, address)
//                                        put(COLUMN_USER_IMAGE, image)
//                                    }
//                                    // Add teacher details to Cloud Firestore
//                                    val firestore = FirebaseFirestore.getInstance()
//                                    val teacherCollection = firestore.collection("Teachers")
//                                    val teacherCloud = hashMapOf(
//                                        "id" to userId,
//                                        "role" to role,
//                                        "name" to name,
//                                        "email" to email,
//                                        "phone" to phone,
//                                        "address" to address,
//                                        "image" to image
//                                    )
//                                    // Added Successfully
//                                    teacherCollection.document(userId).set(teacherCloud)
//                                    db.insertOrThrow(TABLE_USER, null, values)
//                                    db.setTransactionSuccessful()
//                                    Toast.makeText(context, "Teacher added successfully",
//                                        Toast.LENGTH_SHORT).show()
////                                    // Add teacher details to Firebase Realtime Database
////                                    val database = FirebaseDatabase.getInstance()
////                                    val reference = database.getReference("Teachers")
////                                    val teacher = Users(userId, role, name, email, phone, address, image)
//                                } catch (e: Exception) {
//                                    Log.e("addTeacher", "Error inserting data into local database", e)
//                                    Toast.makeText(context, "Error adding teacher: ${e.message}",
//                                        Toast.LENGTH_SHORT).show()
//                                } finally {
//                                    db.endTransaction()
//                                    db.close()
//                                }
//                            } else {
//                                Log.e("addTeacher", "Failed to send verification email",
//                                    verificationTask.exception)
//                                Toast.makeText(context, "Failed to send verification email.",
//                                    Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                } else {
//                    Log.e("addTeacher", "User creation failed", task.exception)
//                    Toast.makeText(context, "Failed to create teacher account.",
//                        Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//    // Delete Teacher
//    fun deleteTeacher(userId: String) {
//        val db = this.writableDatabase
//        // Show warning dialog before deleting
//        val dialog = android.app.AlertDialog.Builder(context)
//        dialog.setTitle("Warning")
//        dialog.setMessage("Are you sure you want to delete this Teacher?")
//        dialog.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
//        try {
//            db.beginTransaction()
//            val whereClause = "$COLUMN_USER_ID = ?"
//            val whereArgs = arrayOf(userId)
//            db.delete(TABLE_USER, whereClause, whereArgs)
//            // Delete user account from Firebase Authentication
//            val auth = FirebaseAuth.getInstance()
//            val user = auth.currentUser
//            user?.delete()
//            // Delete Teacher Details in Cloud Firestore
//            val firestore = FirebaseFirestore.getInstance()
//            val teacherCollection = firestore.collection("Teachers")
//            teacherCollection.document(userId).delete()
//            // Deleted Successfully
//            db.setTransactionSuccessful()
//            Toast.makeText(context, "Teacher deleted successfully", Toast.LENGTH_SHORT).show()
////            // Delete teacher details in Firebase Realtime Database
////            val database = FirebaseDatabase.getInstance()
////            val reference = database.getReference("Teachers")
////            reference.child(userId).removeValue()
//        }catch (e: Exception) {
//            Log.e("deleteTeacher", "Error deleting data", e)
//            Toast.makeText(context, "Error deleting teacher: ${e.message}", Toast.LENGTH_SHORT).show()
//        }finally {
//            db.endTransaction()
//            db.close()
//            val intent = Intent(context, UserMenu::class.java)
//            context.startActivity(intent)
//        }
//            }
//        dialog.setNegativeButton("No") { _: DialogInterface, _: Int -> }
//        dialog.show()
//    }
//    // Get All Users
//    fun getAllUsers(): ArrayList<Users> {
//        val userList = ArrayList<Users>()
//        val db = this.readableDatabase
//        val selectQuery = "SELECT * FROM $TABLE_USER"
//        val cursor = db.rawQuery(selectQuery, null)
//        if (cursor.moveToFirst()) {
//            do {
//                val user = Users(
//                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
//                    role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE)),
//                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
//                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
//                    phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE)),
//                    address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ADDRESS)),
//                    image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_IMAGE))
//                )
//                userList.add(user)
//            } while (cursor.moveToNext())
//        }
//        cursor.close()
//        db.close()
//        return userList
//    }
//    // Search User by Name
//    fun searchUser(query: String): ArrayList<Users> {
//        val userList = ArrayList<Users>()
//        val db = this.readableDatabase
//        val selectQuery = "SELECT * FROM $TABLE_USER WHERE $COLUMN_USER_NAME LIKE ?"
//        val cursor = db.rawQuery(selectQuery, arrayOf("%$query%"))
//        if (cursor.moveToFirst()) {
//            do {
//                val user = Users(
//                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
//                    role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE)),
//                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
//                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
//                    phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE)),
//                    address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ADDRESS)),
//                    image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_IMAGE))
//                )
//                userList.add(user)
//            } while (cursor.moveToNext())
//        }
//        cursor.close()
//        db.close()
//        return userList
//    }
//    // Add Class
//    fun addClass(classId: String, className: String, rank: String, quantity: String, price: String,
//        date: String, time: String, length: String, startDate: String,
//        courseId: String, teacherId: String) {
//        val db = this.writableDatabase
//        try {
//            db.beginTransaction()
//            // Add class details to the local database
//            val values = ContentValues().apply {
//                put(COLUMN_CLASS_ID, classId)
//                put(COLUMN_CLASS_NAME, className)
//                put(COLUMN_CLASS_RANK, rank)
//                put(COLUMN_CLASS_QUANTITY, quantity)
//                put(COLUMN_CLASS_PRICE, price)
//                put(COLUMN_CLASS_DATE, date)
//                put(COLUMN_CLASS_TIME, time)
//                put(COLUMN_CLASS_LENGTH, length)
//                put(COLUMN_CLASS_START_DATE, startDate)
//                put(COLUMN_CLASS_COURSE_ID, courseId)
//                put(COLUMN_CLASS_TEACHER_ID, teacherId)
//            }
//            db.insertOrThrow(TABLE_CLASSES, null, values)
//            // Add class details to Cloud Firestore
//            val firestore = FirebaseFirestore.getInstance()
//            val classCollection = firestore.collection("Classes")
//            val classCloud = hashMapOf(
//                "id" to classId,
//                "name" to className,
//                "rank" to rank,
//                "quantity" to quantity,
//                "price" to price,
//                "date" to date,
//                "time" to time,
//                "length" to length,
//                "startDate" to startDate,
//                "courseId" to courseId,
//                "teacherId" to teacherId
//            )
//            classCollection.document(classId).set(classCloud)
//            // Added Successfully
//            db.setTransactionSuccessful()
//            Toast.makeText(context, "Class added successfully", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            Log.e("addClass", "Error inserting data", e)
//        }finally {
//            db.endTransaction()
//            db.close()
//        }
//    }
//    // Get all Classes
//    fun getAllClasses(): ArrayList<Classes> {
//        val classList = ArrayList<Classes>()
//        val db = this.readableDatabase
//        val selectQuery = "SELECT * FROM $TABLE_CLASSES"
//        val cursor = db.rawQuery(selectQuery, null)
//        if (cursor.moveToFirst()) {
//            do {
//                val classes = Classes(
//                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_ID)),
//                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_NAME)),
//                    rank = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_RANK)),
//                    quantity = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_QUANTITY)),
//                    price = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_PRICE)),
//                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_DATE)),
//                    time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_TIME)),
//                    length = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_LENGTH)),
//                    startDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_START_DATE)),
//                    courseId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_COURSE_ID)),
//                    teacherId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_TEACHER_ID))
//                )
//                classList.add(classes)
//            } while (cursor.moveToNext())
//        }
//        cursor.close()
//        db.close()
//        return classList
//    }
    // Get Teacher by ID
    fun getTeacherById(userId: String): Users? {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_USER WHERE $COLUMN_USER_ID = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(userId))
        if (cursor.moveToFirst()) {
            val user = Users(
                id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE)),
                address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ADDRESS)),
                image = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_IMAGE))
            )
            cursor.close()
            db.close()
            return user
        }
        cursor.close()
        db.close()
        return null
    }
    // Get Classes by Course
    fun getClassesByCourseId(courseId: String): ArrayList<Classes> {
        val classList = ArrayList<Classes>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_CLASSES WHERE $COLUMN_CLASS_COURSE_ID = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(courseId))
        if (cursor.moveToFirst()) {
            do {
                val classes = Classes(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_NAME)),
                    rank = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_RANK)),
                    quantity = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_QUANTITY)),
                    price = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_PRICE)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_DATE)),
                    time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_TIME)),
                    length = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_LENGTH)),
                    startDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_START_DATE)),
                    courseId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_COURSE_ID)),
                    teacherId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_TEACHER_ID))
                )
                classList.add(classes)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return classList
    }
}

