package com.food.ordering.zinger.ui.cart

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.*
import com.food.ordering.zinger.databinding.ActivityCartBinding
import com.food.ordering.zinger.databinding.BottomSheetDeliveryLocationBinding
import com.food.ordering.zinger.databinding.BottomSheetShopInfoBinding
import com.food.ordering.zinger.ui.payment.PaymentActivity
import com.food.ordering.zinger.utils.AppConstants
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.collections.ArrayList
import kotlin.math.abs

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    private val viewModel: CartViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()

    private lateinit var cartAdapter: CartAdapter
    private lateinit var progressDialog: ProgressDialog
    private var cartList: MutableList<MenuItemModel> = ArrayList()
    private var shop: ShopConfigurationModel? = null
    private lateinit var snackBar: Snackbar
    private lateinit var errorSnackBar: Snackbar
    private var isPickup = true
    private lateinit var placeOrderRequest: PlaceOrderRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        getArgs()
        initView()
        setListeners()
        setObservers()
    }

    private fun getArgs() {
        shop = preferencesHelper.getCartShop()
        if(shop?.configurationModel?.deliveryPrice!==null){
            deliveryPrice = shop?.configurationModel?.deliveryPrice!!
        }
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        snackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        snackBar.setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.green))
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        binding.toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(applicationContext, android.R.color.white))
        binding.toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(applicationContext, android.R.color.black))
        binding.appBar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) { //Collapsed
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
            } else { //Expanded
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
            }
        })
        setupMenuRecyclerView()
        updateShopUI()
    }

    @SuppressLint("SetTextI18n")
    private fun setListeners(){
        snackBar.setAction("Place Order") {
            if(!isPickup){
                if(preferencesHelper.cartDeliveryLocation.isNullOrEmpty()){
                    Handler().postDelayed({
                        snackBar.show()
                    },500)
                    Toast.makeText(applicationContext,"Please choose a delivery location",Toast.LENGTH_SHORT).show()
                }else{
                    if(cart.isEmpty()){
                        Toast.makeText(applicationContext,"Cart is empty",Toast.LENGTH_SHORT).show()
                    }else{
                        showOrderConfirmation()
                    }
                }
            }else{
                if(cart.isEmpty()){
                    Toast.makeText(applicationContext,"Cart is empty",Toast.LENGTH_SHORT).show()
                }else{
                    showOrderConfirmation()
                }
            }
        }
        errorSnackBar.setAction("Try again") {
            viewModel.verifyOrder(placeOrderRequest)
        }
        binding.radioPickup.setOnClickListener {
            binding.radioPickup.isChecked = true
            binding.radioDelivery.isChecked = false
            binding.textDeliveryPrice.text = "₹0"
            isPickup = true
            binding.textDeliveryLocation.visibility = View.GONE
            preferencesHelper.cartDeliveryPref = ""
            updateCartUI()
        }
        binding.radioDelivery.setOnClickListener {
            binding.radioDelivery.isChecked = true
            binding.radioPickup.isChecked = false
            binding.textDeliveryPrice.text = "₹"+deliveryPrice.toInt().toString()
            isPickup = false
            binding.textDeliveryLocation.visibility = View.VISIBLE
            updateCartUI()
        }
        binding.textInfo.setOnClickListener {
            val dialogBinding: BottomSheetShopInfoBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.bottom_sheet_shop_info, null, false)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(dialogBinding.root)
            dialog.show()
            dialogBinding.editTextInfo.setText(preferencesHelper.cartShopInfo)
            dialogBinding.buttonSaveTextInfo.setOnClickListener {
                preferencesHelper.cartShopInfo = dialogBinding.editTextInfo.text.toString()
                if(!preferencesHelper.cartShopInfo.isNullOrEmpty()){
                    binding.textInfo.text = preferencesHelper.cartShopInfo
                }else{
                    binding.textInfo.text = "Any information to convey to " + shop?.shopModel?.name + "?"
                }
                dialog.dismiss()
            }
        }
        binding.textDeliveryLocation.setOnClickListener {
            val dialogBinding: BottomSheetDeliveryLocationBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.bottom_sheet_delivery_location, null, false)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(dialogBinding.root)
            dialog.show()
            dialogBinding.editLocation.setText(preferencesHelper.cartDeliveryLocation)
            dialogBinding.buttonSaveLocation.setOnClickListener {
                preferencesHelper.cartDeliveryLocation = dialogBinding.editLocation.text.toString()
                if(!preferencesHelper.cartDeliveryLocation.isNullOrEmpty()){
                    binding.textDeliveryLocation.text = preferencesHelper.cartDeliveryLocation
                }else{
                    binding.textDeliveryLocation.text = "Enter delivery location"
                }
                dialog.dismiss()
            }
        }
    }

    private fun setObservers(){
        viewModel.insertOrderStatus.observe(this, androidx.lifecycle.Observer {
            when(it.status){
                Resource.Status.LOADING -> {
                    errorSnackBar.dismiss()
                    progressDialog.setMessage("Verifying cart items...")
                    progressDialog.show()
                }
                Resource.Status.SUCCESS -> {
                    progressDialog.dismiss()
                    errorSnackBar.dismiss()
                    initiatePayment(it.data?.data?.transactionToken,it.data?.data?.orderId.toString())
                }
                Resource.Status.OFFLINE_ERROR -> {
                    progressDialog.dismiss()
                    errorSnackBar.setText("No Internet Connection")
                    errorSnackBar.show()
                }
                Resource.Status.ERROR -> {
                    progressDialog.dismiss()
                    if(!it.message.isNullOrEmpty()){
                        errorSnackBar.setText(it.message.toString())
                    }else{
                        errorSnackBar.setText("Cart verify failed")
                    }
                    errorSnackBar.show()
                }
                else -> {}
            }
        })
    }

    @SuppressLint("SetTextI18n")
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
        if(!preferencesHelper.cartShopInfo.isNullOrEmpty()){
            binding.textInfo.text = preferencesHelper.cartShopInfo
        }else{
            binding.textInfo.text = "Any information to convey to " + shop?.shopModel?.name + "?"
        }
        if(!preferencesHelper.cartDeliveryLocation.isNullOrEmpty()){
            binding.textDeliveryLocation.text = preferencesHelper.cartDeliveryLocation
        }else{
            binding.textDeliveryLocation.text = "Enter delivery location"
        }
        if(!preferencesHelper.cartDeliveryPref.isNullOrEmpty()){
            if(preferencesHelper.cartDeliveryPref=="delivery") {
                binding.radioDelivery.isChecked = true
                binding.radioPickup.isChecked = false
                binding.textDeliveryPrice.text = "₹" + deliveryPrice.toInt().toString()
                isPickup = false
                updateCartUI()
                binding.textDeliveryLocation.visibility = View.VISIBLE
            }else{
                binding.textDeliveryLocation.visibility = View.GONE
            }
        }else{
            binding.textDeliveryLocation.visibility = View.GONE
        }
    }

    private fun setupMenuRecyclerView() {
        cartList.clear()
        cartList.addAll(cart)
        updateCartUI()
        cartAdapter = CartAdapter(applicationContext, cartList, object : CartAdapter.OnItemClickListener {

            override fun onItemClick(item: MenuItemModel?, position: Int) {
                //TODO navigate to restaurant activity
            }

            override fun onQuantityAdd(position: Int) {
                println("quantity add clicked $position")
                cartList[position].quantity = cartList[position].quantity + 1
                cartAdapter.notifyItemChanged(position)
                updateCartUI()
                saveCart(cartList)
            }

            override fun onQuantitySub(position: Int) {
                println("quantity sub clicked $position")
                if (cartList[position].quantity - 1 >= 0) {
                    cartList[position].quantity = cartList[position].quantity - 1
                    if (cartList[position].quantity == 0) {
                        cartList.removeAt(position)
                        cartAdapter.notifyDataSetChanged()
                    } else {
                        cartAdapter.notifyDataSetChanged()
                    }
                    updateCartUI()
                    saveCart(cartList)
                }
            }
        })
        binding.recyclerFoodItems.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerFoodItems.adapter = cartAdapter
    }

    private var deliveryPrice = 0.0
    private var cartTotalPrice = 0
    @SuppressLint("SetTextI18n")
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
                preferencesHelper.cartDeliveryPref = "delivery"
            }
            binding.textTotal.text = "₹$total"
            if (totalItems == 1) {
                snackBar.setText("₹$total | $totalItems item")
            } else {
                snackBar.setText("₹$total | $totalItems items")
            }
            snackBar.show()
            cartTotalPrice = total
        } else {
            preferencesHelper.clearCartPreferences()
            snackBar.dismiss()
            binding.layoutContent.visibility = View.GONE
            binding.layoutEmpty.visibility = View.VISIBLE
        }
    }

    fun saveCart(foodItems: List<MenuItemModel>?) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val cartString = gson.toJson(foodItems)
        preferencesHelper.cart = cartString
    }

    val cart: List<MenuItemModel>
        get() {
            val items: MutableList<MenuItemModel> = ArrayList()
            val temp = preferencesHelper.getCart()
            if (temp != null) {
                items.addAll(temp)
            }
            return items
        }

    private fun initiatePayment(token: String?, orderId: String){
        val intent = Intent(applicationContext,PaymentActivity::class.java)
        intent.putExtra(AppConstants.TRANSACTION_TOKEN,token)
        intent.putExtra(AppConstants.ORDER_ID,orderId)
        startActivity(intent)
        finish()
    }

    private fun verifyOrder(){
        var cookingInfo:String? = null
        var deliveryLocation = ""
        if(!preferencesHelper.cartShopInfo.isNullOrEmpty()){
            cookingInfo = preferencesHelper.cartShopInfo
        }
        if(!preferencesHelper.cartDeliveryLocation.isNullOrEmpty()){
            deliveryLocation = preferencesHelper.cartDeliveryLocation!!
        }
        val cartOrderModel = CartOrderModel(
                cookingInfo,
                if(isPickup) null else deliveryLocation,
                if(isPickup) null else deliveryPrice.toInt(),
                cartTotalPrice,
                CartShopModel(shop?.shopModel?.id),
                CartUserModel(preferencesHelper.userId)
        )
        val cartTransactionModel = CartTransactionModel(cartOrderModel)
        val listCartOrderItems:ArrayList<CartOrderItems> = ArrayList()
        cart.forEach {
            listCartOrderItems.add(CartOrderItems(FoodItem(it.id), it.price, it.quantity))
        }
        placeOrderRequest = PlaceOrderRequest(listCartOrderItems,cartTransactionModel)
        viewModel.verifyOrder(placeOrderRequest)
    }

    private fun showOrderConfirmation(){
        MaterialAlertDialogBuilder(this@CartActivity)
                .setTitle("Place order")
                .setCancelable(false)
                .setMessage("Are you sure want to place this order?")
                .setPositiveButton("Yes") { _, _ ->
                    verifyOrder()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                    Handler().postDelayed({
                        snackBar.show()
                    },500)
                }
                .show()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
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