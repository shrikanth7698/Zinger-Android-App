package com.food.ordering.zinger.ui.placeorder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.databinding.ActivityPlaceOrderBinding
import com.food.ordering.zinger.ui.home.HomeActivity
import com.food.ordering.zinger.ui.otp.OtpActivity
import com.food.ordering.zinger.utils.AppConstants
import org.koin.android.ext.android.inject

class PlaceOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaceOrderBinding
    private val preferencesHelper: PreferencesHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setListener()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_place_order)
    }

    private fun setListener() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
