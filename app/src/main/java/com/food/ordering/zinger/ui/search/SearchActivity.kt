package com.food.ordering.zinger.ui.search

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
import com.food.ordering.zinger.data.model.MenuItemModel
import com.food.ordering.zinger.databinding.ActivitySearchBinding
import com.food.ordering.zinger.ui.restaurant.RestaurantActivity
import com.food.ordering.zinger.utils.AppConstants
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var menuAdapter: SearchAdapter
    private lateinit var progressDialog: ProgressDialog
    private var menuList: ArrayList<MenuItemModel> = ArrayList()
    private lateinit var errorSnackBar: Snackbar
    private var timer: Timer? = null
    private var isGlobalSearch = true
    private var shopId: String? = null
    private var shopName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
        setObservers()
        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        if (s.toString().length > 2) {
                            viewModel.getMenu(preferencesHelper.getPlace()?.id.toString(), s.toString(), shopId, isGlobalSearch)
                        } else {
                            runOnUiThread {
                                menuList.clear()
                                menuAdapter.notifyDataSetChanged()
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

    private fun getArgs() {
        isGlobalSearch = intent.getBooleanExtra(AppConstants.GLOBAL_SEARCH, true)
        shopId = intent.getStringExtra(AppConstants.SHOP_ID)
        shopName = intent.getStringExtra(AppConstants.SHOP_NAME)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.imageClose.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        if (isGlobalSearch) {
            val text = "<font color=#000000>Search your favourite</font> <font color=#FF4141>outlet</font> <font color=#000000>or</font> <font color=#FF4141>dish</font> <font color=#000000>in your campus</font>"
            binding.titleSearch.text = Html.fromHtml(text)
            binding.editSearch.hint = "Search Outlets or Dish"
        } else {
            val text = "<font color=#000000>Search your favourite</font> <font color=#FF4141>dish</font> <font color=#000000>in </font> <font color=#FF4141>" + shopName + "</font>"
            binding.titleSearch.text = Html.fromHtml(text)
            binding.editSearch.hint = "Search dish"
        }
        //binding.layoutSearch.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        setupShopRecyclerView()
    }

    private fun setObservers() {
        viewModel.performFetchMenuStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        errorSnackBar.dismiss()
                    }
                    Resource.Status.EMPTY -> {
                        binding.progressBar.visibility = View.GONE
                        menuList.clear()
                        menuAdapter.notifyDataSetChanged()
                        if (isGlobalSearch) {
                            errorSnackBar.setText("No dish or restaurant found")
                        } else {
                            errorSnackBar.setText("No dish found")
                        }
                        errorSnackBar.show()
                        binding.appBarLayout.setExpanded(true, true)
                    }
                    Resource.Status.SUCCESS -> {
                        errorSnackBar.dismiss()
                        binding.progressBar.visibility = View.GONE
                        menuList.clear()
                        it.data?.let { it1 -> menuList.addAll(it1) }
                        menuAdapter.notifyDataSetChanged()
                        binding.appBarLayout.setExpanded(false, true)
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        errorSnackBar.setText("No Internet Connection")
                        errorSnackBar.show()
                        binding.appBarLayout.setExpanded(true, true)

                    }
                    Resource.Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        errorSnackBar.setText("Something went wrong")
                        errorSnackBar.show()
                        binding.appBarLayout.setExpanded(true, true)
                    }
                }
            }
        })
    }

    private fun setupShopRecyclerView() {
        menuAdapter = SearchAdapter(menuList, object : SearchAdapter.OnItemClickListener {
            override fun onItemClick(item: MenuItemModel, position: Int) {
                val shopList = preferencesHelper.getShopList()
                val shop = shopList?.firstOrNull {
                    it.shopModel.id == item.shopModel?.id
                }
                val intent = Intent(applicationContext, RestaurantActivity::class.java)
                if (item.isDish) {
                    intent.putExtra(AppConstants.ITEM_ID, item.id)
                }
                intent.putExtra(AppConstants.SHOP, Gson().toJson(shop))
                startActivity(intent)
                if (!isGlobalSearch) finish()
            }
        })
        binding.recyclerShops.layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerShops.adapter = menuAdapter
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
