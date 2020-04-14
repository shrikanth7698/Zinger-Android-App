package com.food.ordering.zinger.ui.profile

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.Campus
import com.food.ordering.zinger.data.model.Shop
import com.food.ordering.zinger.databinding.ActivityLoginBinding
import com.food.ordering.zinger.databinding.ActivityProfileBinding
import com.food.ordering.zinger.databinding.ActivitySignUpBinding
import com.food.ordering.zinger.databinding.BottomSheetCampusListBinding
import com.food.ordering.zinger.ui.home.HomeActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModel()
    private lateinit var progressDialog: ProgressDialog
    private var campusList: ArrayList<Campus> = ArrayList()
    private var selectedCampus: Campus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setListener()
        setObservers()
        viewModel.getCampusList()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        progressDialog = ProgressDialog(this)
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener { onBackPressed() }
        binding.buttonUpdate.setOnClickListener {
            //TODO finish this
            /*startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()*/
        }
        binding.layoutChooseCampus.setOnClickListener {
            showCampusListBottomDialog()
        }
    }

    private fun setObservers() {
        viewModel.performFetchCampusListStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Getting campus list")
                        progressDialog.show()
                    }
                    Resource.Status.EMPTY -> {
                        progressDialog.dismiss()
                        //val snackbar = Snackbar.make(binding.root, "No Outlets in this college", Snackbar.LENGTH_LONG)
                        //snackbar.show()
                    }
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        campusList.clear()
                        it.data?.let { it1 -> campusList.addAll(it1) }
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

    private fun showCampusListBottomDialog() {
        val dialogBinding: BottomSheetCampusListBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.bottom_sheet_campus_list, null, false)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()
        val productAdapter = CampusAdapter(applicationContext, campusList, object : CampusAdapter.OnItemClickListener {
            override fun onItemClick(item: Campus?, position: Int) {
                selectedCampus = item
                binding.textCampusName.text = item?.name
                Handler().postDelayed({
                    dialog.dismiss()
                },250)
            }
        })
        dialogBinding.recyclerCampus.layoutManager = GridLayoutManager(this, 2)
        dialogBinding.recyclerCampus.adapter = productAdapter
        dialogBinding.imageClose.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
