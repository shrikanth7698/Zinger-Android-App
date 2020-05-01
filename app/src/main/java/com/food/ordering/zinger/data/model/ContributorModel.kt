package com.food.ordering.zinger.data.model
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class ContributorModel(
    var name: String,
    var role: String,
    var github: String,
    var linkedIn: String,
    var mail: String,
    var website: String,
    var image: String
)