package tn.esprit.projet_.model

data class User(
    val _id: String,
    val firstname: String,
    val lastname: String,
    val username: String,
    val email: String,
    val password: String, // Include this if you need to handle the password
    val __v: Int // Include this if you need the version key
)
