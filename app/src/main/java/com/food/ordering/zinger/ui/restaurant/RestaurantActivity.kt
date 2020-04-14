package com.food.ordering.zinger.ui.restaurant

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.FoodItem
import com.food.ordering.zinger.data.model.Shop
import com.food.ordering.zinger.databinding.ActivityRestaurantBinding
import com.food.ordering.zinger.ui.cart.CartActivity
import com.food.ordering.zinger.ui.home.HomeViewModel
import com.food.ordering.zinger.utils.SharedPreferenceHelper
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.collections.ArrayList

class RestaurantActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantBinding
    private val viewModel: RestaurantViewModel by viewModel()
    lateinit var foodAdapter: FoodAdapter
    lateinit var progressDialog: ProgressDialog
    var foodItemList: ArrayList<FoodItem> = ArrayList()
    var cartList: ArrayList<FoodItem> = ArrayList()
    var shop: Shop? = null
    lateinit var snackbar: Snackbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
        setObservers()
        shop?.let { viewModel.getMenu(it) }
        snackbar!!.setAction("View Cart") { startActivity(Intent(applicationContext, CartActivity::class.java)) }
    }


    private fun getArgs() {
        shop = intent.getParcelableExtra("shop")
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant)
        snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        snackbar!!.setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.green))
        progressDialog = ProgressDialog(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        binding.toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(applicationContext, android.R.color.white))
        binding.toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(applicationContext, android.R.color.black))
        binding.appBar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) { //Collapsed
                binding.textShopRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0)
                binding.textShopRating.setTextColor(ContextCompat.getColor(applicationContext, android.R.color.black))
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
            } else { //Expanded
                binding.textShopRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_white, 0, 0, 0)
                binding.textShopRating.setTextColor(ContextCompat.getColor(applicationContext, android.R.color.white))
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
            }
        })
        setupMenuRecyclerView()
        updateShopUI()
    }

    private fun updateShopUI() {
        binding!!.toolbarLayout.title = shop!!.name
        binding!!.textShopRating.text = shop!!.rating
        Picasso.get().load(shop!!.imageUrl).into(binding!!.imageExpanded)
    }

    private fun setObservers() {
        viewModel.performFetchShopsStatus.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.LOADING -> {
                    progressDialog!!.setMessage("Getting menu")
                    progressDialog!!.show()
                }
                Resource.Status.SUCCESS -> {
                    cartList.clear()
                    cartList.addAll(cart)
                    updateCartUI()
                    progressDialog!!.dismiss()
                    foodItemList.clear()
                    resource.data?.let { it1 -> foodItemList.addAll(it1) }
                    if (cartList.size > 0) {
                        if (cartList[0].shopId == shop!!.id) {
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
                    foodAdapter!!.notifyDataSetChanged()
                }
                Resource.Status.EMPTY -> {
                    progressDialog!!.dismiss()
                    val snackbar = Snackbar.make(binding!!.root, "No food items available in this shop", Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
                Resource.Status.OFFLINE_ERROR -> {
                    progressDialog!!.dismiss()
                    val snackbar = Snackbar.make(binding!!.root, "No Internet Connection", Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
                Resource.Status.ERROR -> {
                    progressDialog!!.dismiss()
                    val snackbar = Snackbar.make(binding!!.root, "Something went wrong", Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
                else -> {
                }
            }
        })
    }

    private fun setupMenuRecyclerView() {
        foodAdapter = FoodAdapter(applicationContext, foodItemList, object : FoodAdapter.OnItemClickListener {
            override fun onItemClick(item: FoodItem?, position: Int) {}
            override fun onQuantityAdd(position: Int) {
                println("quantity add clicked $position")
                if (cartList.size > 0) {
                    if (cartList[0].shopId == shop!!.id) {
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
                        foodAdapter!!.notifyItemChanged(position)
                        updateCartUI()
                        saveCart(cartList)
                    } else { //Show replace cart confirmation dialog
                        var message = "Your cart contains food from " + cartList[0].shopName + ". "
                        message += "Do you want to discard the cart and add food from " + shop!!.name + "?"
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
                snackbar!!.setText("₹$total | $totalItems item")
            } else {
                snackbar!!.setText("₹$total | $totalItems items")
            }
            snackbar!!.show()
        } else {
            snackbar!!.dismiss()
        }
    }

    fun saveCart(foodItems: List<FoodItem>) {
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

    val cart: List<FoodItem>
        get() {
            val items: MutableList<FoodItem> = ArrayList()
            val gson = GsonBuilder().setPrettyPrinting().create()
            val listType = object : TypeToken<List<FoodItem?>?>() {}.type
            val json = SharedPreferenceHelper.getSharedPreferenceString(this, "cart", "")
            val temp = gson.fromJson<List<FoodItem>>(json, listType)
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