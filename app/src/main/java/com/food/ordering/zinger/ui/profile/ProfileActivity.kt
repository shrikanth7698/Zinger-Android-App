package com.food.ordering.zinger.ui.profile

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.PlaceModel
import com.food.ordering.zinger.data.model.UpdateUserRequest
import com.food.ordering.zinger.data.model.UserModel
import com.food.ordering.zinger.databinding.ActivityProfileBinding
import com.food.ordering.zinger.databinding.BottomSheetVerifyOtpBinding
import com.food.ordering.zinger.ui.order.OrdersActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class ProfileActivity : AppCompatActivity(), PlacePickerDialog.PlaceClickListener {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var progressDialog: ProgressDialog
    private var places: ArrayList<PlaceModel> = ArrayList()
    private var selectedPlace: PlaceModel? = null
    private var storedVerificationId = ""
    private lateinit var dialogBinding: BottomSheetVerifyOtpBinding
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var verificationCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var dialog: BottomSheetDialog
    private var otpVerified = false

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
        binding.textMobile.text = preferencesHelper.mobile
        selectedPlace = preferencesHelper.getPlace()
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener { onBackPressed() }
        binding.buttonUpdate.setOnClickListener {
            if (binding.editName.text.toString().isNotEmpty()) {
                //TODO email validation
                if (binding.editEmail.text.toString().isNotEmpty()) {
                    if (selectedPlace != null) {
                        var token = ""
                        token = preferencesHelper.fcmToken.toString()
                        val updateUserRequest = UpdateUserRequest(
                                placeModel = selectedPlace!!,
                                userModel = UserModel(
                                        preferencesHelper.userId,
                                        binding.editEmail.text.toString(),
                                        preferencesHelper.mobile,
                                        binding.editName.text.toString(),
                                        preferencesHelper.oauthId
                                )
                        )
                        viewModel.updateUserDetails(updateUserRequest)
                    } else {
                        Toast.makeText(applicationContext, "Select a place", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Email is blank", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Name is blank", Toast.LENGTH_SHORT).show()
            }
        }
        binding.layoutChooseCampus.setOnClickListener {
            showCampusListBottomDialog()
        }
        binding.textYourOrders.setOnClickListener {
            startActivity(Intent(applicationContext, OrdersActivity::class.java))
        }

        countDownTimer = object : CountDownTimer(10000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                dialogBinding.textResendOtp.setText("Resend OTP (" + millisUntilFinished / 1000 + ")")
            }

            override fun onFinish() {
                dialogBinding.textResendOtp.setText("Resend OTP")
                dialogBinding.textResendOtp.isEnabled = true
            }
        }

        verificationCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Verification Successful!", Toast.LENGTH_LONG)
                        .show()
                dialogBinding.editOtp.setText(p0.smsCode)
                viewModel.signInWithPhoneAuthCredential(p0, this@ProfileActivity)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                progressDialog.dismiss()
                p0.printStackTrace()
                Toast.makeText(applicationContext, "Verification failed!", Toast.LENGTH_LONG).show()
                dialogBinding.editOtp.setText("")
            }

            override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
            ) {
                progressDialog.dismiss()
                storedVerificationId = verificationId
                resendToken = token
                countDownTimer.start()
                dialogBinding.textResendOtp.isEnabled = false
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Verification failed!", Toast.LENGTH_LONG).show()
            }
        }

        binding.layoutMobile.setOnClickListener {
            showOtpVerificationBottomSheet(preferencesHelper.mobile!!)
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

                        if(otpVerified){
                            preferencesHelper.mobile = preferencesHelper.tempMobile
                            preferencesHelper.oauthId = preferencesHelper.tempOauthId
                            otpVerified = false
                        }

                        progressDialog.dismiss()
                        if (resource.data != null) {
                            Toast.makeText(applicationContext, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            preferencesHelper.clearCartPreferences()
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
        viewModel.verifyOtpStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Verifying OTP...")
                        progressDialog.show()
                    }
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        // dialog.dismiss()
                        otpVerified = true
                        binding.textMobile.text = preferencesHelper.tempMobile
                        countDownTimer.cancel()

                        var token = ""
                        token = preferencesHelper.fcmToken.toString()
                        val updateUserRequest = UpdateUserRequest(
                                placeModel = selectedPlace!!,
                                userModel = UserModel(
                                        preferencesHelper.userId,
                                        binding.editEmail.text.toString(),
                                        preferencesHelper.tempMobile,
                                        binding.editName.text.toString(),
                                        preferencesHelper.tempOauthId
                                )
                        )
                        viewModel.updateUserDetails(updateUserRequest)

                        dialog.let {
                            dialog.dismiss()
                        }
                    }

                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        dialog.dismiss()
                        Toast.makeText(this, "OTP verification failed ", Toast.LENGTH_LONG).show()
                        countDownTimer.cancel()
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
        dialog.show(supportFragmentManager, null)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onPlaceClick(place: PlaceModel) {
        selectedPlace = place
        binding.textCampusName.text = place.name
    }

    private fun showOtpVerificationBottomSheet(number: String) {

        dialogBinding =
                DataBindingUtil.inflate(
                        layoutInflater,
                        R.layout.bottom_sheet_verify_otp,
                        null,
                        false
                )

        dialogBinding.editMobile.setText(number)
        dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        dialogBinding.textResendOtp.setOnClickListener(View.OnClickListener {
            Toast.makeText(this, "OTP resent", Toast.LENGTH_LONG).show()
            resendVerificationCode(dialogBinding.editMobile.text.toString(), resendToken)
        })

        dialogBinding.editOtp.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    verifyOtpRequest(dialogBinding)
                    true
                }
                else -> false
            }
        }

        dialogBinding.buttonVerify.setOnClickListener {
            if (dialogBinding.layoutOtp.visibility == View.GONE) {
                dialogBinding.layoutOtp.visibility = View.VISIBLE
                dialogBinding.buttonVerify.text = "Verify OTP"
                dialogBinding.editMobile.isEnabled = false
                if(dialogBinding.editMobile.text.toString()!=preferencesHelper.mobile) {
                    sendOtp(dialogBinding.editMobile.text.toString())
                }else{
                    Toast.makeText(applicationContext,"Mobile number is same!",Toast.LENGTH_SHORT).show()
                }
            } else {
                verifyOtpRequest(dialogBinding)
            }

        }
    }

    private fun verifyOtpRequest(dialogBinding: BottomSheetVerifyOtpBinding) {
        if (dialogBinding.editOtp.text?.length?.compareTo(6) == 0) {
            val credential = PhoneAuthProvider.getCredential(
                    storedVerificationId,
                    dialogBinding.editOtp.text.toString()
            )
            viewModel.signInWithPhoneAuthCredential(credential, context = this)
        } else {
            Toast.makeText(this, "Wrong OTP length", Toast.LENGTH_LONG).show()
        }

    }

    private fun sendOtp(number: String) {
        progressDialog.setMessage("Sending OTP")
        progressDialog.show()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + number, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                verificationCallBack
        )
    }

    fun resendVerificationCode(number: String, token: PhoneAuthProvider.ForceResendingToken) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallBack,         // OnVerificationStateChangedCallbacks
                token // ForceResendingToken from callbacks
        );

    }

}
