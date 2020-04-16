package com.food.ordering.zinger.ui.search

import android.app.ProgressDialog
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
import com.food.ordering.zinger.data.model.MenuItem
import com.food.ordering.zinger.databinding.ActivitySearchBinding
import com.google.android.material.snackbar.Snackbar
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
    private var menuList: ArrayList<MenuItem> = ArrayList()
    private lateinit var errorSnackBar: Snackbar
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setObservers()
        binding.editSearch.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        if(s.toString().length>2){
                            viewModel.getMenu(preferencesHelper.getPlace()?.id.toString(),s.toString())
                        }else{
                            runOnUiThread {
                                menuList.clear()
                                menuAdapter.notifyDataSetChanged()
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

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.imageClose.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        val text = "<font color=#000000>Search your favourite</font> <font color=#FF4141>outlet</font> <font color=#000000>or</font> <font color=#FF4141>dish</font> <font color=#000000>in your campus</font>"
        binding.titleSearch.text = Html.fromHtml(text)
        setupShopRecyclerView()
    }

    private fun setObservers() {
        viewModel.performFetchMenuStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        errorSnackBar.dismiss()
                    }
                    Resource.Status.EMPTY -> {
                        menuList.clear()
                        menuAdapter.notifyDataSetChanged()
                        errorSnackBar.setText("No dish found")
                        errorSnackBar.show()
                    }
                    Resource.Status.SUCCESS -> {
                        errorSnackBar.dismiss()
                        menuList.clear()
                        it.data?.let { it1 -> menuList.addAll(it1) }
                        menuAdapter.notifyDataSetChanged()
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        errorSnackBar.setText("No Internet Connection")
                        errorSnackBar.show()
                    }
                    Resource.Status.ERROR -> {
                        errorSnackBar.setText("Something went wrong")
                        errorSnackBar.show()
                    }
                }
            }
        })
    }

    private fun setupShopRecyclerView() {
        menuAdapter = SearchAdapter(menuList, object : SearchAdapter.OnItemClickListener {
            override fun onItemClick(item: MenuItem?, position: Int) {
                //val intent = Intent(applicationContext, RestaurantActivity::class.java)
                //intent.putExtra("shop", item)
                //startActivity(intent)
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
