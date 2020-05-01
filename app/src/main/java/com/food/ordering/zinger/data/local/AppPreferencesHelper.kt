package com.food.ordering.zinger.data.local

interface AppPreferencesHelper {

    val name: String?
    val email: String?
    val mobile: String?
    val role: String?
    val oauthId: String?
    val userId: Int?
    val fcmToken: String?
    val place: String?
    val cart: String?
    val shopList: String?
    val cartShop: String?
    val cartDeliveryPref: String?
    val cartShopInfo: String?
    val cartDeliveryLocation: String?
    val tempMobile: String?
    val tempOauthId: String?

    fun saveUser(userId: Int?, name: String?, email: String?, mobile: String?, role: String?, oauthId: String?, place: String?)

    fun clearPreferences()

    fun clearCartPreferences()
}