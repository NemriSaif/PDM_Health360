package tn.esprit.projet_.model

data class SignupDto(
    val firstname: String,
    val lastname: String,
    val username: String,
    val email: String,
    val password: String,
    val confirmpasSsingup: String
)
