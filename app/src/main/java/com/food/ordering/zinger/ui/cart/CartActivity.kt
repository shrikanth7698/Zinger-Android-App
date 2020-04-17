package com.food.ordering.zinger.ui.cart

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.data.model.FoodItem
import com.food.ordering.zinger.data.model.MenuItem
import com.food.ordering.zinger.data.model.Shop
import com.food.ordering.zinger.data.model.ShopsResponseData
import com.food.ordering.zinger.databinding.ActivityCartBinding
import com.food.ordering.zinger.utils.SharedPreferenceHelper
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import java.util.*

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var cartAdapter: CartAdapter
    private lateinit var progressDialog: ProgressDialog
    private var cartList: MutableList<MenuItem> = ArrayList()
    private var shop: ShopsResponseData? = null
    private var snackbar: Snackbar? = null
    private var isPickup = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        getArgs()
        initView()
        snackbar!!.setAction("Place Order") {
            //TODO open place order activity
        }
        binding.radioPickup.setOnClickListener {
            binding.radioPickup.isChecked = true
            binding.radioDelivery.isChecked = false
            binding.textDeliveryPrice.text = "₹0"
            isPickup = true
            updateCartUI()
        }
        binding.radioDelivery.setOnClickListener {
            binding.radioDelivery.isChecked = true
            binding.radioPickup.isChecked = false
            binding.textDeliveryPrice.text = "₹"+deliveryPrice.toInt().toString()
            isPickup = false
            updateCartUI()
        }
    }

    private fun getArgs() {
        shop = preferencesHelper.getCartShop()
        if(shop?.configurationModel?.deliveryPrice!==null){
            deliveryPrice = shop?.configurationModel?.deliveryPrice!!
        }

    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        snackbar!!.setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.green))
        progressDialog = ProgressDialog(this)
        progressDialog!!.setCancelable(false)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        binding.toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(applicationContext, android.R.color.white))
        binding.toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(applicationContext, android.R.color.black))
        binding.appBar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) { //Collapsed
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
            } else { //Expanded
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
            }
        })
        setupMenuRecyclerView()
        updateShopUI()
    }

    private fun updateShopUI() {
        Picasso.get().load(shop?.shopModel?.photoUrl).placeholder(R.drawable.ic_shop).into(binding.layoutShop.imageShop)
        Picasso.get().load(shop?.shopModel?.coverUrls?.get(0)).placeholder(R.drawable.shop_placeholder).into(binding.imageExpanded)
        binding.layoutShop.textShopName.text = shop?.shopModel?.name
        if(shop?.configurationModel?.isOrderTaken==1){
            if(shop?.configurationModel?.isDeliveryAvailable==1){
                binding.layoutShop.textShopDesc.text = "Closes at "+shop?.shopModel?.closingTime?.substring(0,5)
            }else{
                binding.layoutShop.textShopDesc.text = "Closes at "+shop?.shopModel?.closingTime?.substring(0,5)+" (Delivery not available)"
            }
        }else{
            binding.layoutShop.textShopDesc.text = "Closed Now"
        }
        binding.layoutShop.textShopRating.text = shop?.ratingModel?.rating.toString()
        binding.textInfo.text = "Any information to convey to " + shop?.shopModel?.name + "?"
    }

    private fun setupMenuRecyclerView() {
        cartList.clear()
        cartList.addAll(cart)
        updateCartUI()
        cartAdapter = CartAdapter(applicationContext, cartList, object : CartAdapter.OnItemClickListener {

            override fun onItemClick(item: MenuItem?, position: Int) {

            }

            override fun onQuantityAdd(position: Int) {
                println("quantity add clicked $position")
                cartList[position].quantity = cartList[position].quantity + 1
                cartAdapter!!.notifyItemChanged(position)
                updateCartUI()
                saveCart(cartList)
            }

            override fun onQuantitySub(position: Int) {
                println("quantity sub clicked $position")
                if (cartList[position].quantity - 1 >= 0) {
                    cartList[position].quantity = cartList[position].quantity - 1
                    if (cartList[position].quantity == 0) {
                        cartList.removeAt(position)
                        cartAdapter!!.notifyDataSetChanged()
                    } else {
                        cartAdapter!!.notifyDataSetChanged()
                    }
                    updateCartUI()
                    saveCart(cartList)
                }
            }
        })
        binding.recyclerFoodItems.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerFoodItems.adapter = cartAdapter
    }

    var deliveryPrice = 0.0
    private fun updateCartUI() {
        var total = 0
        var totalItems = 0
        if (cartList.size > 0) {
            binding.layoutContent.visibility = View.VISIBLE
            binding.layoutEmpty.visibility = View.GONE
            for (i in cartList.indices) {
                total += cartList[i].price * cartList[i].quantity
                totalItems += 1
            }
            if(!isPickup) {
                total += deliveryPrice.toInt()
            }
            binding.textTotal.text = "₹$total"
            if (totalItems == 1) {
                snackbar!!.setText("₹$total | $totalItems item")
            } else {
                snackbar!!.setText("₹$total | $totalItems items")
            }
            snackbar!!.show()
        } else {
            snackbar!!.dismiss()
            binding.layoutContent.visibility = View.GONE
            binding.layoutEmpty.visibility = View.VISIBLE
        }
    }

    fun saveCart(foodItems: List<MenuItem>?) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val cartString = gson.toJson(foodItems)
        preferencesHelper.cart = cartString
    }

    val cart: List<MenuItem>
        get() {
            val items: MutableList<MenuItem> = ArrayList()
            val temp = preferencesHelper.getCart()
            if (temp != null) {
                items.addAll(temp)
            }
            return items
        }

    override fun onResume() {
        super.onResume()
    }
}