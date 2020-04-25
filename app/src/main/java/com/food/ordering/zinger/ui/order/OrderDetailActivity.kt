package com.food.ordering.zinger.ui.order

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
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
import com.food.ordering.zinger.data.model.*
import com.food.ordering.zinger.databinding.ActivityOrderDetailBinding
import com.food.ordering.zinger.databinding.BottomSheetRateFoodBinding
import com.food.ordering.zinger.ui.cart.CartActivity
import com.food.ordering.zinger.ui.home.HomeActivity
import com.food.ordering.zinger.utils.AppConstants
import com.food.ordering.zinger.utils.StatusHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.hsalf.smileyrating.SmileyRating
import com.squareup.picasso.Picasso
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
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
    private lateinit var orderTimelineAdapter: OrderTimelineAdapter
    private lateinit var progressDialog: ProgressDialog
    private var orderList: ArrayList<OrderItems> = ArrayList()
    private lateinit var errorSnackBar: Snackbar
    private lateinit var order: OrderItemListModel
    var isPickup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
        setListeners()
        setObservers()
        errorSnackBar.setAction("Try again") {

        }
    }

    private fun getArgs() {
        order = Gson().fromJson(intent.getStringExtra(AppConstants.ORDER_DETAIL), OrderItemListModel::class.java)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        binding.imageClose.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        setupShopRecyclerView()
        setupOrderStatusRecyclerView()
        updateUI()
    }

    private fun updateUI() {
        binding.textShopName.text = order.transactionModel.orderModel.shopModel?.name
        try {
            val apiDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val appDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm aaa")
            val date = apiDateFormat.parse(order.transactionModel.orderModel.date)
            val dateString = appDateFormat.format(date)
            binding.textOrderTime.text = dateString
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //binding.textOrderPrice.text = "₹ " + order.transactionModel.orderModel.price.toInt().toString()
        binding.textSecretKey.text = order.transactionModel.orderModel.secretKey
        Picasso.get().load(order.transactionModel.orderModel.shopModel?.photoUrl).placeholder(R.drawable.ic_shop).into(binding.imageShop)
        //binding.titleOrderStatus.text = StatusHelper.getStatusDetailedMessage(order.transactionModel.orderModel.orderStatus)
        binding.titleOrderStatus.text = "Track your order"
        binding.textOrderId.text = "#" + order.transactionModel.orderModel.id
        binding.textTransactionId.text = "#" + order.transactionModel.transactionId
        binding.textTotalPrice.text = "₹" + order.transactionModel.orderModel.price.toInt().toString()
        binding.textPaymentMode.text = "Paid via " + order.transactionModel.paymentMode
        if (!order.transactionModel.orderModel.cookingInfo.isNullOrEmpty()) {
            binding.textInfo.text = order.transactionModel.orderModel.cookingInfo
        } else {
            binding.textInfo.visibility = View.GONE
        }
        if (!order.transactionModel.orderModel.deliveryLocation.isNullOrEmpty()) {
            binding.textDeliveryLocation.text = order.transactionModel.orderModel.deliveryLocation
            isPickup = false
        } else {
            binding.textDeliveryLocation.text = "Pick up from restaurant"
            isPickup = true
        }
        var itemTotal = 0.0
        order.orderItemsList.forEach {
            itemTotal += it.price*it.quantity
        }
        binding.textItemTotalPrice.text = "₹" + itemTotal.toInt().toString()
        if (order.transactionModel.orderModel.deliveryPrice != null) {
            if (order.transactionModel.orderModel.deliveryPrice!! > 0.0) {
                binding.textDeliveryPrice.text = "₹" + order.transactionModel.orderModel.deliveryPrice!!.toInt().toString()
            } else {
                binding.layoutDeliveryCharge.visibility = View.GONE
            }
        } else {
            binding.layoutDeliveryCharge.visibility = View.GONE
        }
        when (order.transactionModel.orderModel.orderStatus) {
            AppConstants.ORDER_STATUS_COMPLETED,
            AppConstants.ORDER_STATUS_DELIVERED -> {
                binding.textRate.visibility = View.VISIBLE
                binding.textCancelReorder.visibility = View.VISIBLE
                binding.textRate.isEnabled = true
                binding.textCancelReorder.isEnabled = true
                binding.textRate.text = "RATE FOOD"
                binding.textCancelReorder.text = "REORDER"
            }

            AppConstants.ORDER_STATUS_CANCELLED_BY_SELLER,
            AppConstants.ORDER_STATUS_CANCELLED_BY_USER,
            AppConstants.ORDER_STATUS_REFUND_COMPLETED -> {
                binding.textRate.isEnabled = true
                binding.textCancelReorder.isEnabled = false
                binding.textRate.visibility = View.VISIBLE
                binding.textCancelReorder.visibility = View.GONE
                binding.textRate.text = "RATE ORDER"
            }

            AppConstants.ORDER_STATUS_PLACED -> {
                binding.textRate.visibility = View.GONE
                binding.textRate.isEnabled = false
                binding.textCancelReorder.visibility = View.VISIBLE
                binding.textCancelReorder.isEnabled = true
                binding.textCancelReorder.text = "CANCEL"
            }

            else -> {
                binding.textRate.visibility = View.GONE
                binding.textCancelReorder.visibility = View.GONE
            }
        }
        when (order.transactionModel.orderModel.orderStatus) {
            AppConstants.ORDER_STATUS_READY,
            AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY -> {
                binding.layoutSecretKey.visibility = View.VISIBLE
            }
            else -> {
                binding.layoutSecretKey.visibility = View.GONE
            }
        }
        if (order.transactionModel.orderModel.rating != null) {
            if (order.transactionModel.orderModel.rating!! > 0.0) {
                binding.layoutRating.visibility = View.VISIBLE
                binding.textRating.text = order.transactionModel.orderModel.rating.toString()
                binding.textRate.visibility = View.GONE
            }
        }
        when (order.transactionModel.orderModel.orderStatus) {
            AppConstants.ORDER_STATUS_PENDING -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PENDING)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_ACCEPTED)))
                if (isPickup) {
                    orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_READY)))
                    orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_COMPLETED)))
                } else {
                    orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY)))
                    orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_DELIVERED)))
                }
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_CANCELLED_BY_USER -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED)))
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED_BY_USER)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_INITIATED)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_COMPLETED)))
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_CANCELLED_BY_SELLER -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED)))
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED_BY_SELLER)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_INITIATED)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_COMPLETED)))
                orderTimelineAdapter.notifyDataSetChanged()
            }

            AppConstants.ORDER_STATUS_PLACED -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_ACCEPTED)))
                if (isPickup) {
                    orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_READY)))
                    orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_COMPLETED)))
                } else {
                    orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY)))
                    orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_DELIVERED)))
                }
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_ACCEPTED -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED)))
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_ACCEPTED)))
                if (isPickup) {
                    orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_READY)))
                    orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_COMPLETED)))
                } else {
                    orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY)))
                    orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_DELIVERED)))
                }
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_READY -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_ACCEPTED)))
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_READY)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_COMPLETED)))
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_ACCEPTED)))
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_DELIVERED)))
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_COMPLETED -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_ACCEPTED)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_READY)))
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_COMPLETED)))
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_DELIVERED -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_ACCEPTED)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY)))
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_DELIVERED)))
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_REFUND_INITIATED -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED_BY_USER)))
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_INITIATED)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_COMPLETED)))
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_REFUND_COMPLETED -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED_BY_SELLER)))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_INITIATED)))
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_COMPLETED)))
                orderTimelineAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setListeners() {
        binding.textCancelReorder.setOnClickListener {
            if (binding.textCancelReorder.text.toString().toUpperCase() != "REORDER") {
                showCancelOrderDialog()
            } else {
                //REORDER (Add items to cart)
                val cartItems = preferencesHelper.getCart()
                if (cartItems.isNullOrEmpty()) {
                    reOrder()
                } else {
                    MaterialAlertDialogBuilder(this@OrderDetailActivity)
                            .setTitle("Replace cart?")
                            .setMessage("Cart already contains some items. Are you sure want to replace the cart with this order items?")
                            .setPositiveButton("Yes") { dialog, _ ->
                                preferencesHelper.clearCartPreferences()
                                reOrder()
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                }

            }
        }
        binding.textRate.setOnClickListener {
            showRatingDialog()
        }
        binding.imageCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", order.transactionModel.orderModel.shopModel?.mobile, null))
            startActivity(intent)
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
        viewModel.cancelOrderStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Cancelling order...")
                        errorSnackBar.dismiss()
                        progressDialog.show()
                    }
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        errorSnackBar.dismiss()
                        order.transactionModel.orderModel.orderStatus = AppConstants.ORDER_STATUS_CANCELLED_BY_USER
                        updateUI()
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
    private fun showRatingDialog() {
        val dialogBinding: BottomSheetRateFoodBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.bottom_sheet_rate_food, null, false)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()
        Handler().postDelayed({
            dialogBinding.smileyRating.setRating(SmileyRating.Type.GOOD, true)
        }, 200)
        dialogBinding.buttonRate.setOnClickListener {
            var smiley = dialogBinding.smileyRating.selectedSmiley
            rating = 0.0
            when (smiley) {
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

    private fun showCancelOrderDialog() {
        MaterialAlertDialogBuilder(this@OrderDetailActivity)
                .setTitle("Cancel order")
                .setMessage("Are you sure want to cancel this order?")
                .setPositiveButton("Yes") { dialog, _ ->
                    val orderId = order?.transactionModel?.orderModel?.id
                    viewModel.cancelOrder(
                            OrderStatusRequest(
                                    orderId.toInt(),
                                    AppConstants.ORDER_STATUS_CANCELLED_BY_USER
                            )
                    )
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    private fun reOrder() {
        var cartString = ""
        var cartShop = ""
        val shopList = preferencesHelper.getShopList()
        if (shopList != null) {
            for (i in shopList) {
                if (i.shopModel.id == order.transactionModel.orderModel.shopModel?.id) {
                    cartShop = Gson().toJson(i)
                }
            }
        }

        val cartList: ArrayList<MenuItemModel> = ArrayList()
        order.orderItemsList.forEach {
            cartList.add(
                    MenuItemModel(
                            category = it.itemModel.category,
                            id = it.itemModel.id,
                            isAvailable = it.itemModel.isAvailable,
                            isVeg = it.itemModel.isVeg,
                            name = it.itemModel.name,
                            photoUrl = it.itemModel.photoUrl,
                            price = it.itemModel.price.toInt(),
                            shopModel = it.itemModel.shopModel,
                            quantity = it.quantity,
                            shopId = order.transactionModel.orderModel.shopModel?.id,
                            shopName = order.transactionModel.orderModel.shopModel?.name
                    )
            )
        }
        cartString = Gson().toJson(cartList)
        preferencesHelper.cart = cartString
        preferencesHelper.cartShop = cartShop
        if (order.transactionModel.orderModel.deliveryPrice != null) {
            if (order.transactionModel.orderModel.deliveryPrice!! > 0.0) {
                preferencesHelper.cartDeliveryPref = "delivery"
                if (!order.transactionModel.orderModel.deliveryLocation.isNullOrEmpty()) {
                    preferencesHelper.cartDeliveryLocation = order.transactionModel.orderModel.deliveryLocation
                }
            } else {
                preferencesHelper.cartDeliveryPref = ""
            }
        } else {
            preferencesHelper.cartDeliveryPref = ""
        }

        if (!order.transactionModel.orderModel.cookingInfo.isNullOrEmpty()) {
            preferencesHelper.cartShopInfo = order.transactionModel.orderModel.cookingInfo
        } else {
            preferencesHelper.cartShopInfo = ""
        }
        val i = Intent(applicationContext, HomeActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
        val j = Intent(applicationContext, CartActivity::class.java)
        j.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(j)
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

    var orderStatusList: ArrayList<OrderStatus> = ArrayList()
    private fun setupOrderStatusRecyclerView() {
        orderTimelineAdapter = OrderTimelineAdapter(applicationContext, orderStatusList, object : OrderTimelineAdapter.OnItemClickListener {
            override fun onItemClick(item: OrderStatus?, position: Int) {}
        })
        binding.recyclerStatus.layoutManager = LinearLayoutManager(this@OrderDetailActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerStatus.adapter = AlphaInAnimationAdapter(orderTimelineAdapter)
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
