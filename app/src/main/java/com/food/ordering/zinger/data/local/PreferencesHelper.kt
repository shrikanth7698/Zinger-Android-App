package com.food.ordering.zinger.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.food.ordering.zinger.data.model.MenuItem
import com.food.ordering.zinger.data.model.PlaceModel
import com.food.ordering.zinger.data.model.ShopsResponseData
import com.food.ordering.zinger.data.model.UserModel
import com.food.ordering.zinger.utils.AppConstants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesHelper(context: Context) : AppPreferencesHelper {

    private val loginPreferences: SharedPreferences =
            context.getSharedPreferences(AppConstants.LOGIN_PREFS, MODE_PRIVATE)
    private val customerPreferences: SharedPreferences =
            context.getSharedPreferences(AppConstants.CUSTOMER_PREFS, MODE_PRIVATE)
    private val cartPreferences: SharedPreferences =
            context.getSharedPreferences(AppConstants.CART_PREFERENCES, MODE_PRIVATE)

    override var name: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_NAME, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_NAME, value).apply()

    override var email: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_EMAIL, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_EMAIL, value).apply()

    override var mobile: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_MOBILE, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_MOBILE, value).apply()

    override var role: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_ROLE, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_ROLE, value).apply()

    override var oauthId: String?
        get() = loginPreferences.getString(AppConstants.AUTH_TOKEN, null)
        set(value) = loginPreferences.edit().putString(AppConstants.AUTH_TOKEN, value).apply()

    override var place: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_PLACE, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_PLACE, value).apply()

    override var cart: String?
        get() = cartPreferences.getString(AppConstants.CART, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART, value).apply()

    override var cartShop: String?
        get() = cartPreferences.getString(AppConstants.CART_SHOP, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART_SHOP, value).apply()

    override var cartDeliveryPref: String?
        get() = cartPreferences.getString(AppConstants.CART_DELIVERY, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART_DELIVERY, value).apply()

    override var cartShopInfo: String?
        get() = cartPreferences.getString(AppConstants.CART_SHOP_INFO, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART_SHOP_INFO, value).apply()

    override var cartDeliveryLocation: String?
        get() = cartPreferences.getString(AppConstants.CART_DELIVERY_LOCATION, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART_DELIVERY_LOCATION, value).apply()

    override fun saveUser(name: String?, email: String?, mobile: String?, role: String?, oauthId: String?, place: String?) {
        customerPreferences.edit().putString(AppConstants.CUSTOMER_NAME, name).apply()
        customerPreferences.edit().putString(AppConstants.CUSTOMER_EMAIL, email).apply()
        customerPreferences.edit().putString(AppConstants.CUSTOMER_MOBILE, mobile).apply()
        customerPreferences.edit().putString(AppConstants.CUSTOMER_ROLE, role).apply()
        loginPreferences.edit().putString(AppConstants.AUTH_TOKEN, oauthId).apply()
        customerPreferences.edit().putString(AppConstants.CUSTOMER_PLACE, place).apply()
    }

    override fun clearPreferences() {
        loginPreferences.edit().clear().apply()
        customerPreferences.edit().clear().apply()
        cartPreferences.edit().clear().apply()
    }

    override fun clearCartPreferences(){
        cartPreferences.edit().clear().apply()
    }

    fun getPlace(): PlaceModel? {
        return Gson().fromJson(place, PlaceModel::class.java)
    }

    fun getUser(): UserModel? {
        return UserModel(email, mobile, name, oauthId, role)
    }

    fun getCart(): List<MenuItem>? {
        val listType = object : TypeToken<List<MenuItem?>?>() {}.type
        return Gson().fromJson(cart, listType)
    }

    fun getCartShop(): ShopsResponseData? {
        return Gson().fromJson(cartShop, ShopsResponseData::class.java)
    }

}