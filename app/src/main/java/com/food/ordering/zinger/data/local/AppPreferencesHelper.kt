package com.food.ordering.zinger.data.local

interface AppPreferencesHelper {

    val name: String?
    val email: String?
    val mobile: String?
    val role: String?
    val oauthId: String?
    val place: String?
    val cart: String?
    val cartShop: String?
    val cartDeliveryPref: String?

    fun saveUser(name: String?,email: String?, mobile: String?, role: String?, oauthId: String?, place: String?)

    fun clearPreferences()
}