package com.food.ordering.zinger.ui.order

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.OrderData
import com.food.ordering.zinger.data.model.OrderItems
import com.food.ordering.zinger.data.model.RatingRequest
import com.food.ordering.zinger.data.model.RatingShopModel
import com.food.ordering.zinger.databinding.ActivityOrderDetailBinding
import com.food.ordering.zinger.databinding.BottomSheetDeliveryLocationBinding
import com.food.ordering.zinger.databinding.BottomSheetRateFoodBinding
import com.food.ordering.zinger.utils.AppConstants
import com.food.ordering.zinger.utils.StatusHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.hsalf.smileyrating.SmileyRating
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.Exception
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


class OrderDetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityOrderDetailBinding
    private val viewModel: OrderViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var orderAdapter: OrderItemAdapter
    private lateinit var progressDialog: ProgressDialog
    private var orderList: ArrayList<OrderItems> = ArrayList()
    private lateinit var errorSnackBar: Snackbar
    private lateinit var order: OrderData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
        setListeners()
        setObservers()
        errorSnackBar.setAction("Try again") {
           //getOrders()
        }
        //getOrders()
    }

    private fun getArgs(){
        order = Gson().fromJson(intent.getStringExtra(AppConstants.ORDER_DETAIL), OrderData::class.java)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        binding.imageClose.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        //val text = "<font color=#000000>Manage and track<br>your </font> <font color=#FF4141>orders</font>"
        //binding.titleOrderStatus.text = Html.fromHtml(text)
        setupShopRecyclerView()
        updateUI()
    }

    private fun updateUI(){
        binding.textShopName.text = order.transactionModel.orderModel.shopModel?.name
        try {
            val apiDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val appDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm aaa")
            val date = apiDateFormat.parse(order.transactionModel.orderModel.date)
            val dateString = appDateFormat.format(date)
            binding.textOrderTime.text = dateString
        }catch (e: Exception){
            e.printStackTrace()
        }
        //binding.textOrderPrice.text = "₹ " + order.transactionModel.orderModel.price.toInt().toString()
        binding.textSecretKey.text = order.transactionModel.orderModel.secretKey
        Picasso.get().load(order.transactionModel.orderModel.shopModel?.photoUrl).placeholder(R.drawable.ic_shop).into(binding.imageShop)
        binding.titleOrderStatus.text = StatusHelper.getStatusDetailedMessage(order.transactionModel.orderModel.orderStatus)
        binding.textOrderId.text = "#"+order.transactionModel.orderModel.id
        binding.textTransactionId.text = "#"+order.transactionModel.transactionId
        binding.textTotalPrice.text = "₹"+order.transactionModel.orderModel.price.toInt().toString()
        binding.textPaymentMode.text = "Paid via "+order.transactionModel.paymentMode
        if(!order.transactionModel.orderModel.cookingInfo.isNullOrEmpty()){
            binding.textInfo.text = order.transactionModel.orderModel.cookingInfo
        }else{
            binding.textInfo.visibility = View.GONE
        }
        if(!order.transactionModel.orderModel.deliveryLocation.isNullOrEmpty()){
            binding.textDeliveryLocation.text = order.transactionModel.orderModel.deliveryLocation
        }else{
            binding.textDeliveryLocation.text = "Pick up from restaurant"
        }
        var itemTotal = 0.0
        order.orderItemsList.forEach {
            itemTotal+=it.price
        }
        binding.textItemTotalPrice.text = "₹"+itemTotal.toInt().toString()
        if(order.transactionModel.orderModel.deliveryPrice!=null){
            if(order.transactionModel.orderModel.deliveryPrice!!>0.0){
                binding.textDeliveryPrice.text = "₹"+ order.transactionModel.orderModel.deliveryPrice!!.toInt().toString()
            }else{
                binding.layoutDeliveryCharge.visibility = View.GONE
            }
        }else{
            binding.layoutDeliveryCharge.visibility = View.GONE
        }
        when(order.transactionModel.orderModel.orderStatus){
            AppConstants.ORDER_STATUS_COMPLETED,
            AppConstants.ORDER_STATUS_DELIVERED,
            AppConstants.ORDER_STATUS_REFUND_COMPLETED -> {
                binding.textRate.visibility = View.VISIBLE
                binding.textCancelReorder.visibility = View.VISIBLE
                binding.textRate.text = "RATE FOOD"
                binding.textCancelReorder.text = "REORDER"
            }

            AppConstants.ORDER_STATUS_CANCELLED_BY_SELLER,
            AppConstants.ORDER_STATUS_CANCELLED_BY_USER,
            AppConstants.ORDER_STATUS_TXN_FAILURE -> {
                binding.textRate.visibility = View.VISIBLE
                binding.textCancelReorder.visibility = View.GONE
                binding.textRate.text = "RATE ORDER"
            }

            else -> {
                binding.textRate.visibility = View.GONE
                binding.textRate.isEnabled = false
                binding.textCancelReorder.visibility = View.VISIBLE
                binding.textCancelReorder.isEnabled = true
                binding.textCancelReorder.text = "CANCEL"
            }
        }
        when(order.transactionModel.orderModel.orderStatus){
            AppConstants.ORDER_STATUS_READY,
            AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY -> {
                binding.layoutSecretKey.visibility = View.VISIBLE
            }
            else -> {
                binding.layoutSecretKey.visibility = View.GONE
            }
        }
        if(order.transactionModel.orderModel.rating!=null){
            if(order.transactionModel.orderModel.rating!!>0.0){
                binding.layoutRating.visibility = View.VISIBLE
                binding.textRating.text = order.transactionModel.orderModel.rating.toString()
                binding.textRate.visibility = View.GONE
            }
        }

    }

    private fun setListeners() {
        binding.textCancelReorder.setOnClickListener {
            //TODO cancel or reorder
        }
        binding.textRate.setOnClickListener {
            showRatingDialog()
        }
    }

    private fun setObservers() {
        viewModel.rateOrderStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Please wait...")
                        errorSnackBar.dismiss()
                        progressDialog.show()
                    }
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        errorSnackBar.dismiss()
                        binding.layoutRating.visibility = View.VISIBLE
                        binding.textRating.text = rating.toString()
                        binding.textRate.visibility = View.GONE
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        errorSnackBar.setText("No Internet Connection")
                        errorSnackBar.show()

                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        errorSnackBar.setText("Something went wrong")
                        errorSnackBar.show()
                    }
                }
            }
        })
    }

    var rating = 0.0
    private fun showRatingDialog(){
        val dialogBinding: BottomSheetRateFoodBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.bottom_sheet_rate_food, null, false)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()
        Handler().postDelayed({
            dialogBinding.smileyRating.setRating(SmileyRating.Type.GOOD,true)
        },200)
        dialogBinding.buttonRate.setOnClickListener {
            var smiley = dialogBinding.smileyRating.selectedSmiley
            rating = 0.0
            when(smiley){
                SmileyRating.Type.TERRIBLE -> {
                    rating = 1.0
                }
                SmileyRating.Type.BAD -> {
                    rating = 2.0
                }
                SmileyRating.Type.OKAY -> {
                    rating = 3.0
                }
                SmileyRating.Type.GOOD -> {
                    rating = 4.0
                }
                SmileyRating.Type.GREAT -> {
                    rating = 5.0
                }
            }
            val orderId = order?.transactionModel?.orderModel?.id
            val shopId = order?.transactionModel?.orderModel?.shopModel?.id
            viewModel.rateOrder(
                    RatingRequest(
                            orderId?.toInt(),
                            rating,
                            RatingShopModel(shopId?.toInt())

                    )
            )
            dialog.dismiss()
        }
    }

    private fun showCancelOrderDialog(){
        MaterialAlertDialogBuilder(this@OrderDetailActivity)
                .setTitle("Cancel order")
                .setMessage("Are you sure want to place this order?")
                .setPositiveButton("Yes") { dialog, _ ->

                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    private fun setupShopRecyclerView() {
        orderList.addAll(order.orderItemsList)
        orderAdapter = OrderItemAdapter(applicationContext, orderList, object : OrderItemAdapter.OnItemClickListener {
            override fun onItemClick(item: OrderItems?, position: Int) {
                //val intent = Intent(applicationContext, RestaurantActivity::class.java)
                //intent.putExtra("shop", item)
                //startActivity(intent)
            }
        })
        binding.recyclerOrderItems.layoutManager = LinearLayoutManager(this@OrderDetailActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerOrderItems.adapter = orderAdapter
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.image_close -> {
                onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

}
