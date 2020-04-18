package com.food.ordering.zinger.ui.payment

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.PlaceModel
import com.food.ordering.zinger.data.model.UpdateUserRequest
import com.food.ordering.zinger.data.model.UserModel
import com.food.ordering.zinger.databinding.ActivityPaymentBinding
import com.food.ordering.zinger.databinding.ActivitySignUpBinding
import com.food.ordering.zinger.databinding.BottomSheetCampusListBinding
import com.food.ordering.zinger.ui.home.HomeActivity
import com.food.ordering.zinger.ui.placeorder.PlaceOrderActivity
import com.food.ordering.zinger.utils.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private val viewModel: PaymentViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private var orderId: String? = null
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
        setListener()
        setObservers()
    }

    private fun getArgs() {
        orderId = intent.getStringExtra(AppConstants.ORDER_ID)
        token = intent.getStringExtra(AppConstants.TRANSACTION_TOKEN)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener { onBackPressed() }
        binding.buttonPay.setOnClickListener {
            val intent = Intent(applicationContext,PlaceOrderActivity::class.java)
            intent.putExtra(AppConstants.ORDER_ID,orderId)
            startActivity(intent)
            finish()
        }
    }

    private fun setObservers() {

    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this@PaymentActivity)
                .setTitle("Cancel process?")
                .setMessage("Are you sure want to cancel the order")
                .setPositiveButton("Yes") { dialog, which ->
                    dialog.dismiss()
                    super.onBackPressed()
                }
                .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                .show()
    }

}
