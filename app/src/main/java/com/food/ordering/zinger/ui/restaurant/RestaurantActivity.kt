package com.food.ordering.zinger.ui.restaurant

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.MenuItemModel
import com.food.ordering.zinger.data.model.ShopConfigurationModel
import com.food.ordering.zinger.databinding.ActivityRestaurantBinding
import com.food.ordering.zinger.ui.cart.CartActivity
import com.food.ordering.zinger.ui.search.SearchActivity
import com.food.ordering.zinger.utils.AppConstants
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class RestaurantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantBinding
    private val viewModel: RestaurantViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var progressDialog: ProgressDialog
    var foodItemList: ArrayList<MenuItemModel> = ArrayList()
    var cartList: ArrayList<MenuItemModel> = ArrayList()
    var shop: ShopConfigurationModel? = null
    var itemId = -1
    private lateinit var cartSnackBar: Snackbar
    private lateinit var errorSnackBar: Snackbar
    private lateinit var closedSnackBar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
        setObservers()
        cartSnackBar.setAction("View Cart") { startActivity(Intent(applicationContext, CartActivity::class.java)) }
        errorSnackBar.setAction("Try again") {
            viewModel.getMenu(shop?.shopModel?.id.toString())
        }
        binding.textSearchMenu.setOnClickListener {
            val intent = Intent(applicationContext, SearchActivity::class.java)
            intent.putExtra(AppConstants.GLOBAL_SEARCH, false)
            intent.putExtra(AppConstants.SHOP_ID, shop?.shopModel?.id.toString())
            intent.putExtra(AppConstants.SHOP_NAME, shop?.shopModel?.name)
            startActivity(intent)
        }
        binding.switchVeg.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.switchMenu(isChecked)
        }
        shop?.let {
            viewModel.getMenu(shop?.shopModel?.id.toString())
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            shop?.let {
                viewModel.getMenu(shop?.shopModel?.id.toString())
            }
        }
        binding.imageCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", shop?.shopModel?.mobile, null))
            startActivity(intent)
        }
    }


    private fun getArgs() {
        val temp = intent.getStringExtra(AppConstants.SHOP)
        shop = Gson().fromJson(temp, ShopConfigurationModel::class.java)
        itemId = intent.getIntExtra(AppConstants.ITEM_ID, -1)
    }

    var isShopOpen = true
    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant)
        setSupportActionBar(binding.toolbar)
        progressDialog = ProgressDialog(this)
        cartSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        cartSnackBar.setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.green))
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        closedSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val closedSnackButton: Button = closedSnackBar.view.findViewById(R.id.snackbar_action)
        closedSnackButton.setCompoundDrawables(null, null, null, null)
        closedSnackButton.background = null
        closedSnackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        binding.toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(applicationContext, android.R.color.white))
        binding.toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(applicationContext, android.R.color.black))
        binding.appBar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            binding.appBar.post {
                if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) { //Collapsed
                    binding.textShopRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0)
                    binding.imageCall.setImageDrawable(getDrawable(R.drawable.ic_call_primary))
                    binding.textShopRating.setTextColor(ContextCompat.getColor(applicationContext, android.R.color.black))
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
                } else { //Expanded
                    binding.textShopRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_white, 0, 0, 0)
                    binding.imageCall.setImageDrawable(getDrawable(R.drawable.ic_call_white))
                    binding.textShopRating.setTextColor(ContextCompat.getColor(applicationContext, android.R.color.white))
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
                }
            }
        })
        val openingTime = viewModel.getTime(shop?.shopModel?.openingTime)
        val closingTime = viewModel.getTime(shop?.shopModel?.closingTime)
        val currentTime = Date()
        isShopOpen = currentTime.before(closingTime) && currentTime.after(openingTime)
        if(isShopOpen){
            isShopOpen = shop?.configurationModel?.isOrderTaken == 1
        }
        setupMenuRecyclerView()
        updateShopUI()
    }

    private fun updateShopUI() {
        binding.toolbarLayout.title = shop?.shopModel?.name
        if (shop?.ratingModel?.rating == 0.0) {
            binding.textShopRating.text = "N/R"
        } else {
            binding.textShopRating.text = shop?.ratingModel?.rating.toString() + " (" + shop?.ratingModel?.userCount + ")"
        }
        shop?.shopModel?.coverUrls?.let { binding.coverSlider.setAdapter(CoverSliderAdapter(it)) }
    }

    private fun setObservers() {
        viewModel.performFetchMenuStatus.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.LOADING -> {
                    if (!binding.swipeRefreshLayout.isRefreshing) {
                        binding.layoutStates.visibility = View.VISIBLE
                        binding.animationView.visibility = View.GONE
                    }
                    errorSnackBar.dismiss()
                }
                Resource.Status.SUCCESS -> {
                    binding.swipeRefreshLayout.isRefreshing = false
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
                        if (cartList[0].shopId == shop?.shopModel?.id) {
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
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.GONE
                    binding.animationView.cancelAnimation()
                    //progressDialog.dismiss()
                    errorSnackBar.dismiss()
                    highlightRedirectedItem()
                }
                Resource.Status.EMPTY -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.VISIBLE
                    binding.animationView.loop(true)
                    binding.animationView.setAnimation("empty_animation.json")
                    binding.animationView.playAnimation()
                    //progressDialog.dismiss()
                    foodItemList.clear()
                    foodAdapter.notifyDataSetChanged()
                    errorSnackBar.setText("No food items available in this shop")
                    Handler().postDelayed({ errorSnackBar.show() }, 500)
                }
                Resource.Status.OFFLINE_ERROR -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.VISIBLE
                    binding.animationView.loop(true)
                    binding.animationView.setAnimation("no_internet_connection_animation.json")
                    binding.animationView.playAnimation()
                    //progressDialog.dismiss()
                    errorSnackBar.setText("No Internet Connection")
                    Handler().postDelayed({ errorSnackBar.show() }, 500)
                }
                Resource.Status.ERROR -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.VISIBLE
                    binding.animationView.loop(true)
                    binding.animationView.setAnimation("order_failed_animation.json")
                    binding.animationView.playAnimation()
                    //progressDialog.dismiss()
                    errorSnackBar.setText("Something went wrong")
                    Handler().postDelayed({ errorSnackBar.show() }, 500)
                }
            }
        })
    }

    lateinit var layoutManager: LinearLayoutManager
    private fun setupMenuRecyclerView() {
        foodAdapter = FoodAdapter(applicationContext, foodItemList, object : FoodAdapter.OnItemClickListener {
            override fun onItemClick(item: MenuItemModel?, position: Int) {}
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
                        if (k == 0) {
                            cartList.add(foodItemList[position])
                        }
                        foodAdapter.notifyItemChanged(position)
                        updateCartUI()
                        saveCart(cartList)
                    } else { //Show replace cart confirmation dialog
                        var message = "Your cart contains food from " + cartList[0].shopName + ". "
                        message += "Do you want to discard the cart and add food from " + shop?.shopModel?.name + "?"
                        MaterialAlertDialogBuilder(this@RestaurantActivity)
                                .setTitle("Replace cart?")
                                .setMessage(message)
                                .setPositiveButton("Yes") { dialog, _ ->
                                    preferencesHelper.clearCartPreferences()
                                    foodItemList[position].quantity = foodItemList[position].quantity + 1
                                    foodAdapter.notifyItemChanged(position)
                                    cartList.clear()
                                    cartList.add(foodItemList[position])
                                    saveCart(cartList)
                                    updateCartUI()
                                    dialog.dismiss()
                                }
                                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                                .show()
                    }
                } else {
                    foodItemList[position].quantity = foodItemList[position].quantity + 1
                    cartList.add(foodItemList[position])
                    foodAdapter.notifyItemChanged(position)
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
                    foodAdapter.notifyItemChanged(position)
                    updateCartUI()
                    saveCart(cartList)
                }
            }
        }, isShopOpen)
        layoutManager = LinearLayoutManager(this@RestaurantActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerFoodItems.layoutManager = layoutManager
        binding.recyclerFoodItems.adapter = AlphaInAnimationAdapter(foodAdapter)
    }

    private fun updateCartUI() {
        var total = 0
        var totalItems = 0
        if (cartList.size > 0) {
            for (i in cartList.indices) {
                total += cartList[i].price * cartList[i].quantity
                totalItems += 1
            }
            if (totalItems == 1) {
                cartSnackBar.setText("₹$total | $totalItems item")
            } else {
                cartSnackBar.setText("₹$total | $totalItems items")
            }
            if (shop?.configurationModel?.isOrderTaken == 1)
                cartSnackBar.show()
        } else {
            preferencesHelper.clearCartPreferences()
            cartSnackBar.dismiss()
        }
        if (shop?.configurationModel?.isOrderTaken == 1) {
            if (shop?.configurationModel?.isDeliveryAvailable == 1) {
                //supportActionBar?.subtitle = "Open now"
                //binding.textPickupOnly.visibility = View.GONE
                closedSnackBar.dismiss()
            } else {
                //binding.textPickupOnly.text = "Pick up only"
                //binding.textPickupOnly.visibility = View.VISIBLE
            }
        } else {
            cartSnackBar.dismiss()
            closedSnackBar.setText("Not taking orders")
            closedSnackBar.duration = Snackbar.LENGTH_LONG
            closedSnackBar.show()
            //binding.textPickupOnly.text = "Not taking orders"
            //binding.textPickupOnly.visibility = View.VISIBLE
        }
    }

    fun saveCart(foodItems: List<MenuItemModel>) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val cartString = gson.toJson(foodItems)
        if (foodItems.isNotEmpty()) {
            preferencesHelper.cart = cartString
            preferencesHelper.cartShop = gson.toJson(shop)
        } else {
            preferencesHelper.cart = ""
            preferencesHelper.cartShop = ""
        }
    }

    private val cart: List<MenuItemModel>
        get() {
            val items: MutableList<MenuItemModel> = ArrayList()
            val temp = preferencesHelper.getCart()
            if (!temp.isNullOrEmpty()) {
                items.addAll(temp)
            }
            return items
        }

    private fun highlightRedirectedItem() {
        var position = -1
        if (itemId != -1) {
            for (i in foodItemList.indices) {
                if (foodItemList[i].id == itemId) {
                    position = i
                    break
                }
            }
            if (position != -1) {
                binding.recyclerFoodItems.scrollToPosition(position)
                Handler().postDelayed({
                    binding.appBar.setExpanded(false, true)
                    val view = layoutManager.findViewByPosition(position)
                    if (view != null) {
                        YoYo.with(Techniques.Pulse)
                                .duration(1000)
                                .playOn(view)
                        itemId = -1
                        position = -1
                    }
                }, 500)

            }
        }
    }

    override fun onResume() {
        super.onResume()
        cartList.clear()
        cartList.addAll(cart)
        updateCartUI()
        foodItemList.clear()
        foodAdapter.notifyDataSetChanged()
        viewModel.getMenu(shop?.shopModel?.id.toString())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}