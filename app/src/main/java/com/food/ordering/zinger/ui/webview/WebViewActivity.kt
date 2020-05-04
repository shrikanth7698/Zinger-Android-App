package com.food.ordering.zinger.ui.webview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.databinding.ActivityWebViewBinding
import com.food.ordering.zinger.utils.AppConstants
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private var url:String? = null
    private var title:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
        setListener()
    }

    private fun getArgs(){
        url = intent.getStringExtra(AppConstants.URL)
        title = intent.getStringExtra(AppConstants.NOTIFICATION_TITLE)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)
        binding.textTitle.text = title
        binding.webView.settings.javaScriptEnabled = true
        if(!url.isNullOrEmpty()){
            binding.webView.loadUrl(url)
        }else{
            binding.webView.loadUrl("https://zinger.tech")
        }
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
    }

}
