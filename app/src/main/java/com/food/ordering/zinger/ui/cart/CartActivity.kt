package com.food.ordering.zinger.ui.cart

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.model.FoodItem
import com.food.ordering.zinger.data.model.Shop
import com.food.ordering.zinger.databinding.ActivityCartBinding
import com.food.ordering.zinger.utils.SharedPreferenceHelper
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import java.util.*

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private var cartAdapter: CartAdapter? = null
    private var progressDialog: ProgressDialog? = null
    private var cartList: MutableList<FoodItem> = ArrayList()
    private var shop: Shop? = null
    private var snackbar: Snackbar? = null

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
            deliveryPrice = 0
            updateCartUI()
        }
        binding.radioDelivery.setOnClickListener {
            binding.radioDelivery.isChecked = true
            binding.radioPickup.isChecked = false
            binding.textDeliveryPrice.text = "₹30"
            deliveryPrice = 30
            updateCartUI()
        }
    }

    private fun getArgs() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        shop = gson.fromJson(SharedPreferenceHelper.getSharedPreferenceString(this, "cart_shop", ""), Shop::class.java)
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
        binding.layoutShop.textShopName.text = shop!!.name
        binding.layoutShop.textShopDesc.text = shop!!.desc
        binding.layoutShop.textShopRating.text = shop!!.rating
        binding.textInfo.text = "Any information to convey to " + shop!!.name + "?"
        Picasso.get().load(shop!!.imageUrl).into(binding.imageExpanded)
        Picasso.get().load(shop!!.imageUrl).into(binding.layoutShop.imageShop)
    }

    private fun setupMenuRecyclerView() {
        cartList.clear()
        cartList.addAll(cart)
        updateCartUI()
        cartAdapter = CartAdapter(applicationContext, cartList, object : CartAdapter.OnItemClickListener {

            override fun onItemClick(item: FoodItem?, position: Int) {

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

    var deliveryPrice = 0
    private fun updateCartUI() {
        var total = 0
        var totalItems = 0
        if (cartList.size > 0) {
            for (i in cartList.indices) {
                total += cartList[i].price * cartList[i].quantity
                totalItems += cartList[i].quantity
            }
            total += deliveryPrice
            binding.textTotal.text = "₹$total"
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

    fun saveCart(foodItems: List<FoodItem>?) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val cartString = gson.toJson(foodItems)
        SharedPreferenceHelper.setSharedPreferenceString(this, "cart", cartString)
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
    }
}