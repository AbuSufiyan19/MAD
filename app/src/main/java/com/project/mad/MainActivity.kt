package com.project.mad

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Delay for 3 seconds (3000 milliseconds) and then navigate to the login page
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserSession()
        }, 1500)
    }

    private fun checkUserSession() {
        // Retrieve the user token and email from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val userToken = sharedPreferences.getString("userToken", null)
        val userEmail = sharedPreferences.getString("userEmail", "")

        // Check if user token and email are not null and not empty
        if (!userToken.isNullOrEmpty() && !userEmail.isNullOrEmpty()) {
            // Proceed with checking the database using the token and email
            checkDatabaseWithToken(userEmail)
        } else {
            // If user token or email is not found or empty, navigate to the login page
            navigateToLoginPage()
        }
    }

    private fun checkDatabaseWithToken(userEmail: String) {
        val email = userEmail
        val databaseReference = FirebaseDatabase.getInstance().reference.child("serviceMan")

        databaseReference.orderByChild("email").equalTo(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Email exists in the serviceMan collection
                        navigateToServiceManServiceSelection()
                    } else {
                        checkUsersDB(email)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Toast.makeText(
                        this@MainActivity,
                        "Error checking collection: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // In case of error, navigate to login page or handle as appropriate
                    navigateToLoginPage()
                }
            })
    }
private fun checkUsersDB(email: String) {
    val databaseReference = FirebaseDatabase.getInstance().reference.child("users")

    databaseReference.orderByChild("email").equalTo(email)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Email exists in the users collection
                    Toast.makeText(this@MainActivity, "Customer login", Toast.LENGTH_LONG)
                        .show()
                    navigateToUserHomePage()
                } else {
                    // Email does not exist in the serviceMan collection
                    Toast.makeText(this@MainActivity, "Not an User", Toast.LENGTH_LONG)
                        .show()
                    navigateToLoginPage()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Toast.makeText(
                    this@MainActivity,
                    "Error checking collection: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

}

    private fun navigateToServiceManServiceSelection() {
        val intent = Intent(this, ServiceManHomePageActivity::class.java)
        startActivity(intent)
        finish() // Close the current activity
    }

    private fun navigateToUserHomePage() {
        val intent = Intent(this, UserHomePageActivity::class.java)
        startActivity(intent)
        finish() // Close the current activity
    }


    private fun navigateToLoginPage() {
        // Implement your logic to navigate to the login page
        // For example:
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Finish the current activity, as we don't want the user to go back to the login page
    }
}