package com.food.ordering.zinger.ui.profile

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.PlaceModel
import com.food.ordering.zinger.data.model.UpdateUserRequest
import com.food.ordering.zinger.data.model.UserModel
import com.food.ordering.zinger.databinding.ActivityProfileBinding
import com.food.ordering.zinger.databinding.BottomSheetCampusListBinding
import com.food.ordering.zinger.ui.order.OrdersActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileActivity : AppCompatActivity() ,PlacePickerDialog.PlaceClickListener{

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var progressDialog: ProgressDialog
    private var places: ArrayList<PlaceModel> = ArrayList()
    private var selectedPlace: PlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO add mobile edit
        initView()
        setListener()
        setObservers()
        viewModel.getPlaces()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        progressDialog = ProgressDialog(this)
        binding.editEmail.setText(preferencesHelper.email)
        binding.editName.setText(preferencesHelper.name)
        binding.textCampusName.text = preferencesHelper.getPlace()?.name
        selectedPlace = preferencesHelper.getPlace()
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener { onBackPressed() }
        binding.buttonUpdate.setOnClickListener {
            if(binding.editName.text.toString().isNotEmpty()){
                //TODO email validation
                if(binding.editEmail.text.toString().isNotEmpty()){
                    if(selectedPlace!=null){
                        val updateUserRequest = UpdateUserRequest(
                                placeModel = selectedPlace!!,
                                userModel = UserModel(
                                        preferencesHelper.userId,
                                        binding.editEmail.text.toString(),
                                        preferencesHelper.mobile,
                                        binding.editName.text.toString()
                                )
                        )
                        viewModel.signUp(updateUserRequest)
                    }else{
                        Toast.makeText(applicationContext,"Select a place", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(applicationContext,"Email is blank", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(applicationContext,"Name is blank", Toast.LENGTH_SHORT).show()
            }
        }
        binding.layoutChooseCampus.setOnClickListener {
            showCampusListBottomDialog()
        }
        binding.textYourOrders.setOnClickListener {
            startActivity(Intent(applicationContext, OrdersActivity::class.java))
        }
    }

    private fun setObservers() {
        viewModel.performFetchPlacesStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Getting places")
                        progressDialog.show()
                    }
                    Resource.Status.EMPTY -> {
                        progressDialog.dismiss()
                        //val snackbar = Snackbar.make(binding.root, "No Outlets in this college", Snackbar.LENGTH_LONG)
                        //snackbar.show()
                    }
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        places.clear()
                        it.data?.let { it1 -> it1.data?.let { it2 -> places.addAll(it2) } }
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
        viewModel.performUpdateStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        binding.buttonUpdate.isEnabled = true
                        preferencesHelper.name = binding.editName.text.toString()
                        preferencesHelper.email = binding.editEmail.text.toString()
                        preferencesHelper.place = Gson().toJson(selectedPlace)
                        progressDialog.dismiss()
                        if (resource.data != null) {
                            Toast.makeText(applicationContext, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        binding.buttonUpdate.isEnabled = true
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.ERROR -> {
                        binding.buttonUpdate.isEnabled = true
                        progressDialog.dismiss()
                        resource.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.LOADING -> {
                        binding.buttonUpdate.isEnabled = false
                        progressDialog.setMessage("Updating profile...")
                        progressDialog.show()
                    }
                }
            }
        })
    }

    private fun showCampusListBottomDialog() {
        viewModel.searchPlace("")
        val dialog = PlacePickerDialog()
        dialog.setListener(this)
        dialog.placesList = places
        dialog.show(supportFragmentManager,null)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onPlaceClick(place: PlaceModel) {
        selectedPlace = place
        binding.textCampusName.text = place.name
    }


}
