package com.food.ordering.zinger.ui.order

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
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
import com.food.ordering.zinger.data.model.OrderItemListModel
import com.food.ordering.zinger.data.model.RatingRequest
import com.food.ordering.zinger.data.model.RatingShopModel
import com.food.ordering.zinger.databinding.ActivityOrdersBinding
import com.food.ordering.zinger.databinding.BottomSheetRateFoodBinding
import com.food.ordering.zinger.utils.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.hsalf.smileyrating.SmileyRating
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList


class OrdersActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityOrdersBinding
    private val viewModel: OrderViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var orderAdapter: OrdersAdapter
    private lateinit var progressDialog: ProgressDialog
    private var orderList: ArrayList<OrderItemListModel> = ArrayList()
    private lateinit var errorSnackBar: Snackbar
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setListeners()
        setObservers()
        errorSnackBar.setAction("Try again") {
            getOrders()
        }
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_orders)
        binding.imageClose.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        val text = "<font color=#000000>Manage and track<br>your </font> <font color=#FF4141>orders</font>"
        binding.titleOrders.text = Html.fromHtml(text)
        //binding.layoutSearch.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        setupShopRecyclerView()
    }

    private fun setListeners() {
        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        if (s.toString().length > 2) {
                            //viewModel.getMenu(preferencesHelper.getPlace()?.id.toString(),s.toString())
                        } else {
                            runOnUiThread {
                                orderList.clear()
                                orderAdapter.notifyDataSetChanged()
                                binding.appBarLayout.setExpanded(true, true)
                            }
                        }
                    }
                }, 600)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                timer?.cancel()
            }
        })
    }

    private fun setObservers() {
        viewModel.performFetchOrdersStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        binding.layoutStates.visibility = View.VISIBLE
                        binding.animationView.visibility = View.GONE
                        errorSnackBar.dismiss()
                    }
                    Resource.Status.EMPTY -> {
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("empty_animation.json")
                        binding.animationView.playAnimation()
                        orderList.clear()
                        orderAdapter.notifyDataSetChanged()
                        errorSnackBar.setText("No orders found")
                        Handler().postDelayed({errorSnackBar.show()},500)
                        //binding.appBarLayout.setExpanded(true, true)
                    }
                    Resource.Status.SUCCESS -> {
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.GONE
                        binding.animationView.cancelAnimation()
                        errorSnackBar.dismiss()
                        orderList.clear()
                        it.data?.let { it1 -> orderList.addAll(it1) }
                        orderAdapter.notifyDataSetChanged()
                        //binding.appBarLayout.setExpanded(false, true)
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("no_internet_connection_animation.json")
                        binding.animationView.playAnimation()
                        errorSnackBar.setText("No Internet Connection")
                        Handler().postDelayed({errorSnackBar.show()},500)
                        //binding.appBarLayout.setExpanded(true, true)

                    }
                    Resource.Status.ERROR -> {
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("order_failed_animation.json")
                        binding.animationView.playAnimation()
                        errorSnackBar.setText("Something went wrong")
                        Handler().postDelayed({errorSnackBar.show()},500)
                        //binding.appBarLayout.setExpanded(true, true)
                    }
                }
            }
        })
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
                        getOrders()
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

    private fun setupShopRecyclerView() {
        orderAdapter = OrdersAdapter(orderList, object : OrdersAdapter.OnItemClickListener {
            override fun onItemClick(item: OrderItemListModel?, position: Int) {
                val intent = Intent(applicationContext, OrderDetailActivity::class.java)
                intent.putExtra(AppConstants.ORDER_DETAIL, Gson().toJson(item))
                startActivity(intent)
            }

            override fun onRatingClick(item: OrderItemListModel?, position: Int) {
                showRatingDialog(item)
            }
        })
        binding.recyclerShops.layoutManager = LinearLayoutManager(this@OrdersActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerShops.adapter = AlphaInAnimationAdapter(orderAdapter)
    }

    private fun showRatingDialog(orderData: OrderItemListModel?) {
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
            var rating = 0.0
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
            val orderId = orderData?.transactionModel?.orderModel?.id
            val shopId = orderData?.transactionModel?.orderModel?.shopModel?.id
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

    private fun getOrders() {
        orderList.clear()
        orderAdapter.notifyDataSetChanged()
        //TODO pagination
        preferencesHelper.userId?.let {
            viewModel.getOrders(
                    it.toString(),
                    1,
                    10
            )
        }
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
        getOrders()
    }

}
