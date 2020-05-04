package com.food.ordering.zinger.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amulyakhare.textdrawable.TextDrawable
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.*
import com.food.ordering.zinger.databinding.ActivityHomeBinding
import com.food.ordering.zinger.databinding.HeaderLayoutBinding
import com.food.ordering.zinger.ui.cart.CartActivity
import com.food.ordering.zinger.ui.contactus.ContactUsActivity
import com.food.ordering.zinger.ui.contributors.ContributorsActivity
import com.food.ordering.zinger.ui.login.LoginActivity
import com.food.ordering.zinger.ui.order.OrdersActivity
import com.food.ordering.zinger.ui.profile.ProfileActivity
import com.food.ordering.zinger.ui.profile.ProfileViewModel
import com.food.ordering.zinger.ui.restaurant.RestaurantActivity
import com.food.ordering.zinger.ui.search.SearchActivity
import com.food.ordering.zinger.utils.AppConstants
import com.food.ordering.zinger.utils.FcmUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var headerLayout: HeaderLayoutBinding
    private lateinit var drawer: Drawer
    private lateinit var shopAdapter: ShopAdapter
    private lateinit var progressDialog: ProgressDialog
    private var shopList: ArrayList<ShopConfigurationModel> = ArrayList()
    private var cartList: ArrayList<MenuItemModel> = ArrayList()
    private lateinit var cartSnackBar: Snackbar
    private lateinit var errorSnackbar: Snackbar
    private var placeId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setupMaterialDrawer()
        setObservers()
        placeId = preferencesHelper.getPlace()?.id.toString()
        viewModel.getShops(placeId)
        cartSnackBar.setAction("View Cart") { startActivity(Intent(applicationContext, CartActivity::class.java)) }
        errorSnackbar.setAction("Try again") {
            viewModel.getShops(preferencesHelper.getPlace()?.id.toString())
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
                viewModel.getShops(placeId)
        }
        getFCMToken()
        FcmUtils.subscribeToTopic(AppConstants.NOTIFICATION_TOPIC_GLOBAL)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        headerLayout = DataBindingUtil.inflate(LayoutInflater.from(applicationContext), R.layout.header_layout, null, false)
        cartSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        cartSnackBar.setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.green))
        errorSnackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackbar.view.findViewById(R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        binding.imageMenu.setOnClickListener(this)
        binding.textSearch.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        setStatusBarHeight()
        setupShopRecyclerView()
    }

    private fun setStatusBarHeight() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rectangle = Rect()
                val window = window
                window.decorView.getWindowVisibleDisplayFrame(rectangle)
                val statusBarHeight = rectangle.top
                val layoutParams = headerLayout.statusbarSpaceView.layoutParams
                layoutParams.height = statusBarHeight
                headerLayout.statusbarSpaceView.layoutParams = layoutParams
                Log.d("Home", "status bar height $statusBarHeight")
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun updateHeaderLayoutUI() {
        headerLayout.textCustomerName.text = preferencesHelper.name
        headerLayout.textEmail.text = preferencesHelper.email
        val letter = preferencesHelper.name?.get(0).toString()
        val textDrawable = TextDrawable.builder()
                .buildRound(letter, ContextCompat.getColor(this, R.color.accent))
        headerLayout.imageProfilePic.setImageDrawable(textDrawable)
        //binding.imageMenu.setImageDrawable(textDrawable);
    }

    private fun setupMaterialDrawer() {
        headerLayout.layoutHeader.setOnClickListener {
            startActivity(Intent(applicationContext, ProfileActivity::class.java))
        }
        var identifier = 0L
        val profileItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("My Profile")
                .withIcon(R.drawable.ic_drawer_user)
        val ordersItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Your Orders")
                .withIcon(R.drawable.ic_drawer_past_rides)
        val contactUsItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Contact Us")
                .withIcon(R.drawable.ic_drawer_mail)
        val signOutItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Sign out")
                .withIcon(R.drawable.ic_drawer_log_out)
        val contributorsItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Contributors")
                .withIcon(R.drawable.ic_drawer_info)
        drawer = DrawerBuilder()
                .withActivity(this)
                .withDisplayBelowStatusBar(false)
                .withHeader(headerLayout.root)
                .withTranslucentStatusBar(true)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        profileItem,
                        ordersItem,
                        contactUsItem,
                        contributorsItem,
                        DividerDrawerItem(),
                        signOutItem
                )
                .withOnDrawerItemClickListener { view, position, drawerItem ->
                    if (profileItem.identifier == drawerItem.identifier) {
                        startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    }
                    if (ordersItem.identifier == drawerItem.identifier) {
                        startActivity(Intent(applicationContext, OrdersActivity::class.java))
                    }
                    if (contributorsItem.identifier == drawerItem.identifier) {
                        startActivity(Intent(applicationContext, ContributorsActivity::class.java))
                    }
                    if (contactUsItem.identifier == drawerItem.identifier) {
                        startActivity(Intent(applicationContext, ContactUsActivity::class.java))
                    }
                    if (signOutItem.identifier == drawerItem.identifier) {
                        MaterialAlertDialogBuilder(this@HomeActivity)
                                .setTitle("Confirm Sign Out")
                                .setMessage("Are you sure want to sign out?")
                                .setPositiveButton("Yes") { _, _ ->
                                    FcmUtils.unsubscribeFromTopic(AppConstants.NOTIFICATION_TOPIC_GLOBAL)
                                    FirebaseAuth.getInstance().signOut()
                                    preferencesHelper.clearPreferences()
                                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                                    finish()
                                }
                                .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                                .show()
                    }
                    true
                }
                .build()
    }

    var isError = false
    private fun setObservers() {
        viewModel.performFetchShopsStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        isError = false
                        if(!binding.swipeRefreshLayout.isRefreshing) {
                            binding.layoutStates.visibility = View.VISIBLE
                            binding.animationView.visibility = View.GONE
                        }
                        errorSnackbar.dismiss()
                        //progressDialog.setMessage("Getting Outlets")
                        //progressDialog.show()
                    }
                    Resource.Status.EMPTY -> {
                        isError = true
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("empty_animation.json")
                        binding.animationView.playAnimation()
                        //progressDialog.dismiss()
                        shopList.clear()
                        shopAdapter.notifyDataSetChanged()
                        errorSnackbar.setText("No Outlets in this place")
                        Handler().postDelayed({errorSnackbar.show()},500)
                    }
                    Resource.Status.SUCCESS -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        isError = false
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.GONE
                        binding.animationView.cancelAnimation()
                        //progressDialog.dismiss()
                        errorSnackbar.dismiss()
                        shopList.clear()
                        it.data?.let { it1 -> shopList.addAll(it1) }
                        shopAdapter.notifyDataSetChanged()
                        preferencesHelper.shopList = Gson().toJson(shopList)
                        updateCartUI()
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        isError = true
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("no_internet_connection_animation.json")
                        binding.animationView.playAnimation()
                        //progressDialog.dismiss()
                        errorSnackbar.setText("No Internet Connection")
                        shopList.clear()
                        shopAdapter.notifyDataSetChanged()
                        Handler().postDelayed({errorSnackbar.show()},500)
                    }
                    Resource.Status.ERROR -> {
                        isError = true
                        binding.swipeRefreshLayout.isRefreshing = false
                        //progressDialog.dismiss()
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("order_failed_animation.json")
                        binding.animationView.playAnimation()
                        errorSnackbar.setText("Something went wrong")
                        shopList.clear()
                        shopAdapter.notifyDataSetChanged()
                        Handler().postDelayed({errorSnackbar.show()},500)
                    }
                }
            }
        })
    }

    private fun setupShopRecyclerView() {
        shopAdapter = ShopAdapter(applicationContext, shopList, object : ShopAdapter.OnItemClickListener {
            override fun onItemClick(item: ShopConfigurationModel, position: Int) {
                val intent = Intent(applicationContext, RestaurantActivity::class.java)
                intent.putExtra(AppConstants.SHOP, Gson().toJson(item))
                startActivity(intent)
            }
        })
        binding.recyclerShops.layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerShops.adapter = AlphaInAnimationAdapter(shopAdapter)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.image_menu -> {
                drawer.openDrawer()
            }
            R.id.text_search -> {
                startActivity(Intent(applicationContext, SearchActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateGreetingMessage()
        //Checking whether user has changed their place and refreshing shops accordingly
        if (placeId != preferencesHelper.getPlace()?.id.toString()) {
            placeId = preferencesHelper.getPlace()?.id.toString()
            viewModel.getShops(placeId)
        }
        cartList.clear()
        cartList.addAll(getCart())
        updateCartUI()
        updateHeaderLayoutUI()
        if(isError){
            errorSnackbar.show()
        }
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
                cartSnackBar!!.setText("₹$total | $totalItems item")
            } else {
                cartSnackBar!!.setText("₹$total | $totalItems items")
            }
            cartSnackBar!!.show()
        } else {
            cartSnackBar!!.dismiss()
        }
    }

    fun getCart(): ArrayList<MenuItemModel> {
        val items: ArrayList<MenuItemModel> = ArrayList()
        val temp = preferencesHelper.getCart()
        if (!temp.isNullOrEmpty()) {
            items.addAll(temp)
        }
        return items
    }

    private fun updateGreetingMessage(){
        val timeOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        var message = ""
        when (timeOfDay) {
            in 0..11 -> message = "Good Morning,\n"
            in 12..15 -> message = "Good Afternoon,\n"
            in 16..23 -> message = "Good Evening,\n"
        }
        var temp = preferencesHelper.name
        var tempList = temp?.split(" ")
        message += if(!tempList.isNullOrEmpty()){
            tempList[0]
        }else{
            preferencesHelper.name
        }
        binding.textGreeting.text = message
    }


    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this@HomeActivity)
                .setTitle("Exit app?")
                .setMessage("Are you sure want to exit the app?")
                .setPositiveButton("Yes") { dialog, which ->
                    super.onBackPressed()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                .show()
    }

    private fun getFCMToken(){
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("FCM", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }
                    // Get new Instance ID token
                    val token = task.result?.token
                    if(preferencesHelper.fcmToken!=token){
                        preferencesHelper.fcmToken = token
                        preferencesHelper.fcmToken?.let {
                            profileViewModel.updateFcmToken(NotificationTokenUpdate(it,preferencesHelper.userId.toString()))
                        }
                    }else{
                        //FCM token is same. No need to update
                    }
                    val msg = "FCM TOKEN "+token
                    Log.d("FCM", msg)
                })
    }

}
