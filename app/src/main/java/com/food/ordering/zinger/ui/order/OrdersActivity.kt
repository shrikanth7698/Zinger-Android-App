package com.food.ordering.zinger.ui.order

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
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
import com.food.ordering.zinger.data.model.OrderData
import com.food.ordering.zinger.databinding.ActivityOrdersBinding
import com.food.ordering.zinger.utils.AppConstants
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
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
    private var orderList: ArrayList<OrderData> = ArrayList()
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
        getOrders()
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
                        binding.progressBar.visibility = View.VISIBLE
                        errorSnackBar.dismiss()
                    }
                    Resource.Status.EMPTY -> {
                        binding.progressBar.visibility = View.GONE
                        orderList.clear()
                        orderAdapter.notifyDataSetChanged()
                        errorSnackBar.setText("No orders found")
                        errorSnackBar.show()
                        //binding.appBarLayout.setExpanded(true, true)
                    }
                    Resource.Status.SUCCESS -> {
                        errorSnackBar.dismiss()
                        binding.progressBar.visibility = View.GONE
                        orderList.clear()
                        it.data?.let { it1 -> orderList.addAll(it1) }
                        orderAdapter.notifyDataSetChanged()
                        //binding.appBarLayout.setExpanded(false, true)
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        errorSnackBar.setText("No Internet Connection")
                        errorSnackBar.show()
                        //binding.appBarLayout.setExpanded(true, true)

                    }
                    Resource.Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        errorSnackBar.setText("Something went wrong")
                        errorSnackBar.show()
                        //binding.appBarLayout.setExpanded(true, true)
                    }
                }
            }
        })
    }

    private fun setupShopRecyclerView() {
        orderAdapter = OrdersAdapter(orderList, object : OrdersAdapter.OnItemClickListener {
            override fun onItemClick(item: OrderData?, position: Int) {
                val intent = Intent(applicationContext, OrderDetailActivity::class.java)
                intent.putExtra(AppConstants.ORDER_DETAIL, Gson().toJson(item))
                startActivity(intent)
            }
        })
        binding.recyclerShops.layoutManager = LinearLayoutManager(this@OrdersActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerShops.adapter = orderAdapter
    }

    private fun getOrders(){
        //TODO pagination
        preferencesHelper.mobile?.let {
            viewModel.getMenu(
                    it,
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
    }

}
