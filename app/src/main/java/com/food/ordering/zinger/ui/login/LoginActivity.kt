package com.food.ordering.zinger.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.databinding.ActivityLoginBinding
import com.food.ordering.zinger.ui.home.HomeActivity
import com.food.ordering.zinger.ui.otp.OtpActivity
import com.food.ordering.zinger.utils.AppConstants
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val preferencesHelper: PreferencesHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setListener()
        if (!preferencesHelper.oauthId.isNullOrEmpty()) {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
    }

    private fun setListener() {
        binding.buttonLogin.setOnClickListener {
            if (binding.editPhone.text.toString().isNotEmpty()) {
                //TODO Phone number validation
                val intent = Intent(applicationContext, OtpActivity::class.java)
                intent.putExtra(AppConstants.PREFS_CUSTOMER_MOBILE, "+91"+binding.editPhone.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "Phone number is blank!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
