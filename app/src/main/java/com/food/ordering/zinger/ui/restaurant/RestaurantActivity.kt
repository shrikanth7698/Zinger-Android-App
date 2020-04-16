package com.food.ordering.zinger.ui.restaurant

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.MenuItem
import com.food.ordering.zinger.data.model.ShopsResponseData
import com.food.ordering.zinger.databinding.ActivityRestaurantBinding
import com.food.ordering.zinger.ui.cart.CartActivity
import com.food.ordering.zinger.utils.SharedPreferenceHelper
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.collections.ArrayList

class RestaurantActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantBinding
    private val viewModel: RestaurantViewModel by viewModel()
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var progressDialog: ProgressDialog
    var foodItemList: ArrayList<MenuItem> = ArrayList()
    var cartList: ArrayList<MenuItem> = ArrayList()
    var shop: ShopsResponseData? = null
    private lateinit var cartSnackbar: Snackbar
    private lateinit var errorSnackbar: Snackbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
        setObservers()
        cartSnackbar.setAction("View Cart") { startActivity(Intent(applicationContext, CartActivity::class.java)) }
        errorSnackbar.setAction("Try again") {
            viewModel.getMenu(shop?.shopModel?.id.toString())
        }
        shop?.let {
            viewModel.getMenu(shop?.shopModel?.id.toString())
        }
    }


    private fun getArgs() {
        val temp = intent.getStringExtra("shop")
        shop = Gson().fromJson(temp, ShopsResponseData::class.java)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant)
        setSupportActionBar(binding.toolbar)
        progressDialog = ProgressDialog(this)
        cartSnackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        cartSnackbar.setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.green))
        errorSnackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackbar.view.findViewById(R.id.snackbar_action)
        snackButton.setCompoundDrawables(null,null,null,null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext,R.color.accent))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        binding.toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(applicationContext, android.R.color.white))
        binding.toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(applicationContext, android.R.color.black))
        binding.appBar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            binding.appBar.post {
                if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) { //Collapsed
                    binding.textShopRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0)
                    binding.textShopRating.setTextColor(ContextCompat.getColor(applicationContext, android.R.color.black))
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
                } else { //Expanded
                    binding.textShopRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_white, 0, 0, 0)
                    binding.textShopRating.setTextColor(ContextCompat.getColor(applicationContext, android.R.color.white))
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
                }
            }
        })
        setupMenuRecyclerView()
        updateShopUI()
    }

    private fun updateShopUI() {
        binding.toolbarLayout.title = shop?.shopModel?.name
        binding.textShopRating.text = shop?.ratingModel?.rating.toString()
        Picasso.get().load(shop?.shopModel?.coverUrls?.get(0)).placeholder(R.drawable.shop_placeholder).into(binding.imageExpanded)
    }

    private fun setObservers() {
        viewModel.performFetchMenuStatus.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.LOADING -> {
                    progressDialog.setMessage("Getting menu")
                    progressDialog.show()
                    errorSnackbar.dismiss()
                }
                Resource.Status.SUCCESS -> {
                    cartList.clear()
                    cartList.addAll(cart)
                    updateCartUI()
                    foodItemList.clear()
                    resource.data?.let { it1 ->
                        it1.forEach { item ->
                            item.shopId = shop?.shopModel?.id
                            item.shopName = shop?.shopModel?.name
                            foodItemList.add(item)
                        }
                    }
                    if (cartList.size > 0) {
                        if (cartList[0].shopModel?.id == shop?.shopModel?.id) {
                            var i = 0
                            while (i < foodItemList.size) {
                                var j = 0
                                while (j < cartList.size) {
                                    if (cartList[j].id == foodItemList[i].id) {
                                        foodItemList[i].quantity = cartList[j].quantity
                                    }
                                    j++
                                }
                                i++
                            }
                        }
                    }
                    foodAdapter.notifyDataSetChanged()
                    progressDialog.dismiss()
                    errorSnackbar.dismiss()
                }
                Resource.Status.EMPTY -> {
                    progressDialog.dismiss()
                    foodItemList.clear()
                    foodAdapter.notifyDataSetChanged()
                    errorSnackbar.setText("No food items available in this shop")
                    errorSnackbar.show()
                }
                Resource.Status.OFFLINE_ERROR -> {
                    progressDialog.dismiss()
                    errorSnackbar.setText("No Internet Connection")
                    errorSnackbar.show()
                }
                Resource.Status.ERROR -> {
                    progressDialog.dismiss()
                    errorSnackbar.setText("Something went wrong")
                    errorSnackbar.show()
                }
            }
        })
    }

    private fun setupMenuRecyclerView() {
        foodAdapter = FoodAdapter(applicationContext, foodItemList, object : FoodAdapter.OnItemClickListener {
            override fun onItemClick(item: MenuItem?, position: Int) {}
            override fun onQuantityAdd(position: Int) {
                println("quantity add clicked $position")
                if (cartList.size > 0) {
                    if (cartList[0].shopId == shop?.shopModel?.id) {
                        foodItemList[position].quantity = foodItemList[position].quantity + 1
                        var k = 0
                        for (i in cartList.indices) {
                            if (cartList[i].id == foodItemList[position].id) {
                                cartList[i] = foodItemList[position]
                                k = 1
                                break
                            }
                        }
                        if (k == 0) cartList.add(foodItemList[position])
                        foodAdapter.notifyItemChanged(position)
                        updateCartUI()
                        saveCart(cartList)
                    } else { //Show replace cart confirmation dialog
                        var message = "Your cart contains food from " + cartList[0].shopName + ". "
                        message += "Do you want to discard the cart and add food from " + shop?.shopModel?.name + "?"
                        MaterialAlertDialogBuilder(this@RestaurantActivity)
                                .setTitle("Replace cart?")
                                .setMessage(message)
                                .setPositiveButton("Yes") { dialog, which ->
                                    foodItemList[position].quantity = foodItemList[position].quantity + 1
                                    foodAdapter!!.notifyItemChanged(position)
                                    cartList.clear()
                                    cartList.add(foodItemList[position])
                                    saveCart(cartList)
                                    updateCartUI()
                                    dialog.dismiss()
                                }
                                .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                                .show()
                    }
                } else {
                    foodItemList[position].quantity = foodItemList[position].quantity + 1
                    cartList.add(foodItemList[position])
                    foodAdapter!!.notifyItemChanged(position)
                    updateCartUI()
                    saveCart(cartList)
                }
            }

            override fun onQuantitySub(position: Int) {
                println("quantity sub clicked $position")
                if (foodItemList[position].quantity - 1 >= 0) {
                    foodItemList[position].quantity = foodItemList[position].quantity - 1
                    for (i in cartList.indices) {
                        if (cartList[i].id == foodItemList[position].id) {
                            if (foodItemList[position].quantity == 0) {
                                cartList.removeAt(i)
                            } else {
                                cartList[i] = foodItemList[position]
                            }
                            break
                        }
                    }
                    foodAdapter!!.notifyItemChanged(position)
                    updateCartUI()
                    saveCart(cartList)
                }
            }
        })
        binding!!.recyclerFoodItems.layoutManager = LinearLayoutManager(this@RestaurantActivity, LinearLayoutManager.VERTICAL, false)
        binding!!.recyclerFoodItems.adapter = foodAdapter
    }

    private fun updateCartUI() {
        var total = 0
        var totalItems = 0
        if (cartList.size > 0) {
            for (i in cartList.indices) {
                total += cartList[i].price * cartList[i].quantity
                totalItems += cartList[i].quantity
            }
            if (totalItems == 1) {
                cartSnackbar.setText("₹$total | $totalItems item")
            } else {
                cartSnackbar.setText("₹$total | $totalItems items")
            }
            cartSnackbar.show()
        } else {
            cartSnackbar.dismiss()
        }
    }

    fun saveCart(foodItems: List<MenuItem>) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val cartString = gson.toJson(foodItems)
        if (foodItems.size > 0) {
            SharedPreferenceHelper.setSharedPreferenceString(this, "cart", cartString)
            SharedPreferenceHelper.setSharedPreferenceString(this, "cart_shop", gson.toJson(shop))
        } else {
            SharedPreferenceHelper.setSharedPreferenceString(this, "cart", "")
            SharedPreferenceHelper.setSharedPreferenceString(this, "cart_shop", "")
        }
    }

    private val cart: List<MenuItem>
        get() {
            val items: MutableList<MenuItem> = ArrayList()
            val gson = GsonBuilder().setPrettyPrinting().create()
            val listType = object : TypeToken<List<MenuItem?>?>() {}.type
            val json = SharedPreferenceHelper.getSharedPreferenceString(this, "cart", "")
            val temp = gson.fromJson<List<MenuItem>>(json, listType)
            if (temp != null) {
                items.addAll(temp)
            }
            return items
        }

    override fun onResume() {
        super.onResume()
        cartList.clear()
        cartList.addAll(cart)
        updateCartUI()
        //viewModel.getMenu(shop);
    }
}