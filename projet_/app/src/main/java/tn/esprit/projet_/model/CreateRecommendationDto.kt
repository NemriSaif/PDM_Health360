package tn.esprit.projet_.model

import com.google.gson.annotations.SerializedName

data class CreateRecommendationDto(
    @SerializedName("name") val name: String,  // The name of the recommendation
    @SerializedName("recommendation") val recommendation: String,  // The content of the recommendation
    @SerializedName("image") val image: String?  // Optional image URL
)
