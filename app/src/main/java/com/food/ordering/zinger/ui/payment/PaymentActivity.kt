package com.food.ordering.zinger.ui.payment

import android.animation.LayoutTransition
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.databinding.ActivityPaymentBinding
import com.food.ordering.zinger.ui.placeorder.PlaceOrderActivity
import com.food.ordering.zinger.utils.AppConstants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class PaymentActivity : AppCompatActivity(),View.OnClickListener {

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
        setupPaymentModes()
    }

    private fun getArgs() {
        orderId = intent.getStringExtra(AppConstants.ORDER_ID)
        token = intent.getStringExtra(AppConstants.TRANSACTION_TOKEN)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        binding.layoutContent.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener { onBackPressed() }
        binding.buttonCreditPay.setOnClickListener(this)
        binding.buttonDebitPay.setOnClickListener(this)
        binding.buttonBhimPay.setOnClickListener(this)
        binding.buttonPaytmPay.setOnClickListener(this)
        binding.radioCredit.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.cardCredit.visibility = View.VISIBLE
                binding.radioDebit.isChecked = false
                binding.radioBhim.isChecked = false
                binding.radioPaytm.isChecked = false
            }else{
                binding.cardCredit.visibility = View.GONE
            }
        }
        binding.radioDebit.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.cardDebitDetails.visibility = View.VISIBLE
                binding.radioCredit.isChecked = false
                binding.radioBhim.isChecked = false
                binding.radioPaytm.isChecked = false
            }else{
                binding.cardDebitDetails.visibility = View.GONE
            }
        }
        binding.radioBhim.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.cardBhim.visibility = View.VISIBLE
                binding.radioCredit.isChecked = false
                binding.radioDebit.isChecked = false
                binding.radioPaytm.isChecked = false
            }else{
                binding.cardBhim.visibility = View.GONE
            }
        }
        binding.radioPaytm.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.cardPaytm.visibility = View.VISIBLE
                binding.radioCredit.isChecked = false
                binding.radioDebit.isChecked = false
                binding.radioBhim.isChecked = false
            }else{
                binding.cardPaytm.visibility = View.GONE
            }
        }
    }

    private fun setObservers() {

    }

    private fun setupPaymentModes(){

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

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.button_bhim_pay,
            R.id.button_credit_pay,
            R.id.button_debit_pay,
            R.id.button_paytm_pay -> {
                val intent = Intent(applicationContext,PlaceOrderActivity::class.java)
                intent.putExtra(AppConstants.ORDER_ID,orderId)
                startActivity(intent)
                finish()
            }
        }
    }

}
