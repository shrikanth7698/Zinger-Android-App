package com.food.ordering.zinger.ui.search

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.Shop
import com.food.ordering.zinger.databinding.ActivitySearchBinding
import com.food.ordering.zinger.ui.restaurant.RestaurantActivity
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var shopAdapter: SearchAdapter
    private lateinit var progressDialog: ProgressDialog
    private var shopList: ArrayList<Shop> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setObservers()
        viewModel.getShops()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.imageClose.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        val text = "<font color=#000000>Search your favourite</font> <font color=#FF4141>outlet</font> <font color=#000000>or</font> <font color=#FF4141>dish</font> <font color=#000000>in your campus</font>"
        binding.titleSearch.text = Html.fromHtml(text)
        setupShopRecyclerView()
    }

    private fun setObservers() {
        viewModel.performFetchShopsStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Getting Outlets")
                        progressDialog.show()
                    }
                    Resource.Status.EMPTY -> {
                        progressDialog.dismiss()
                        val snackbar = Snackbar.make(binding.root, "No Outlets in this college", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        shopList.clear()
                        it.data?.let { it1 -> shopList.addAll(it1) }
                        shopAdapter.notifyDataSetChanged()
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        val snackbar = Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        val snackbar = Snackbar.make(binding.root, "Something went wrong", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                }
            }
        })
    }

    private fun setupShopRecyclerView() {
        shopAdapter = SearchAdapter(applicationContext, shopList, object : SearchAdapter.OnItemClickListener {
            override fun onItemClick(item: Shop?, position: Int) {
                val intent = Intent(applicationContext, RestaurantActivity::class.java)
                intent.putExtra("shop", item)
                startActivity(intent)
            }
        })
        binding.recyclerShops.layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerShops.adapter = shopAdapter
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
