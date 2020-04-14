package com.food.ordering.zinger.ui.otp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.food.ordering.zinger.R
import com.food.ordering.zinger.databinding.ActivityLoginBinding
import com.food.ordering.zinger.databinding.ActivityOtpBinding
import com.food.ordering.zinger.ui.home.HomeActivity
import com.food.ordering.zinger.ui.signup.SignUpActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setListener()
    }

    private fun initView(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp)
    }

    private fun setListener(){
        binding.buttonLogin.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
            finish()
        }
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this@OtpActivity)
                .setTitle("Cancel process?")
                .setMessage("Are you sure want to cancel the OTP process?")
                .setPositiveButton("Yes") { dialog, which ->
                    super.onBackPressed()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                .show()
    }


}
