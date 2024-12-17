package tn.esprit.projet_.model

data class Article(
    val id: Int,
    val title: String,
    val description: String,
    val content: String, // Added the missing content property
    val imageResId: Int?, // Drawable resource ID (nullable Int)
    val imageUrl: String? // URL (nullable String)
)