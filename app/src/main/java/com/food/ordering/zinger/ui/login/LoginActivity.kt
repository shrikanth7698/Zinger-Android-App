package com.food.ordering.zinger.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.food.ordering.zinger.R
import com.food.ordering.zinger.databinding.ActivityLoginBinding
import com.food.ordering.zinger.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setListener()
    }

    private fun initView(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
    }

    private fun setListener(){
        binding.buttonLogin.setOnClickListener {
            startActivity(Intent(applicationContext,HomeActivity::class.java))
        }
    }
}
