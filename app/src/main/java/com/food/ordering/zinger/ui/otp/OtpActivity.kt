package com.food.ordering.zinger.ui.otp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.LoginRequest
import com.food.ordering.zinger.databinding.ActivityOtpBinding
import com.food.ordering.zinger.di.networkModule
import com.food.ordering.zinger.ui.home.HomeActivity
import com.food.ordering.zinger.ui.signup.SignUpActivity
import com.food.ordering.zinger.utils.AppConstants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    private val viewModel: OtpViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var number: String? = null
    private var otp = ""
    private var storedVerificationId = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var verificationCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
        setListener()
        setObservers()
        number?.let { sendOtp(it) }
    }

    private fun getArgs() {
        number = intent.getStringExtra(AppConstants.CUSTOMER_MOBILE)
        println("Number testing" + number)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        auth = FirebaseAuth.getInstance()
        binding.textOtpSent.text = "Enter the six digit OTP which has been sent to your mobile number: " + number
    }

    private fun setListener() {
        binding.buttonLogin.setOnClickListener {
            if (binding.editOtp.text.toString().isNotEmpty() && binding.editOtp.text.toString().length == 6) {
                if (storedVerificationId.isNotEmpty()) {
                    val credential = PhoneAuthProvider.getCredential(storedVerificationId, binding.editOtp.text.toString())
                    signInWithPhoneAuthCredential(credential)
                }
            }
        }
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }

        countDownTimer = object : CountDownTimer(10000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                binding.textResendOtp.setText("Resend OTP (" + millisUntilFinished / 1000 + ")")
            }

            override fun onFinish() {
                binding.textResendOtp.setText("Resend OTP")
                binding.textResendOtp.isEnabled = true
            }
        }

        verificationCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Verification Successful!", Toast.LENGTH_LONG)
                        .show()
                binding.editOtp.setText(p0.smsCode)
                signInWithPhoneAuthCredential(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                progressDialog.dismiss()
                p0.printStackTrace()
                Toast.makeText(applicationContext, "Verification failed!", Toast.LENGTH_LONG).show()
                binding.editOtp.setText("")
                binding.textResendOtp.isEnabled = true
            }

            override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
            ) {
                progressDialog.dismiss()
                storedVerificationId = verificationId
                resendToken = token
                binding.textResendOtp.isEnabled = false
                countDownTimer.start()
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                binding.textResendOtp.isEnabled = true
                Toast.makeText(applicationContext, "Verification failed!", Toast.LENGTH_LONG).show()
            }
        }

        binding.textResendOtp.setOnClickListener(View.OnClickListener { v ->
            number?.let {
                number?.let { resendVerificationCode(number!!, resendToken) }
            }
        })
    }

    private fun setObservers() {
        viewModel.performLoginStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data != null) {
                            progressDialog.dismiss()
                            if (resource.data.code == 1163) {
                                val userModel = resource.data.data?.userModel
                                preferencesHelper.userId = userModel?.userId
                                unloadKoinModules(networkModule)
                                loadKoinModules(networkModule)
                                val intent = Intent(applicationContext, SignUpActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val userModel = resource.data.data?.userModel
                                val placeModel = resource.data.data?.placeModel
                                if (userModel != null) {
                                    preferencesHelper.saveUser(
                                            userId = userModel.userId,
                                            name = userModel.name,
                                            email = userModel.email,
                                            mobile = preferencesHelper.mobile,
                                            role = userModel.role,
                                            oauthId = preferencesHelper.oauthId,
                                            place = Gson().toJson(placeModel)
                                    )
                                }
                                unloadKoinModules(networkModule)
                                loadKoinModules(networkModule)
                                startActivity(Intent(applicationContext, HomeActivity::class.java))
                                finish()
                            }
                        } else {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        resource.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Logging in...")
                        progressDialog.show()
                    }
                }
            }
        })
    }

    private fun sendOtp(number: String) {
        progressDialog.setMessage("Sending OTP")
        progressDialog.show()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                verificationCallBack) // OnVerificationStateChangedCallbacks
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        progressDialog.setMessage("Logging in...")
        progressDialog.show()
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        //Log.d(TAG, "signInWithCredential:success")
                        progressDialog.dismiss()
                        val user = task.result?.user
                        preferencesHelper.oauthId = user?.uid
                        preferencesHelper.mobile = user?.phoneNumber?.substring(3)
                        preferencesHelper.role = "CUSTOMER"
                        unloadKoinModules(networkModule)
                        loadKoinModules(networkModule)
                        FirebaseInstanceId.getInstance().instanceId
                                .addOnCompleteListener(OnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        Log.w("FCM", "getInstanceId failed", task.exception)
                                        return@OnCompleteListener
                                    }
                                    // Get new Instance ID token
                                    val token = task.result?.token
                                    preferencesHelper.fcmToken = "token"
                                    val msg = "FCM TOKEN " + token
                                    if (!token.isNullOrEmpty()) {
                                        val loginRequest = user?.uid?.let {
                                            user.phoneNumber?.let { it1 ->
                                                LoginRequest(oauthId = it, mobile = it1.substring(3))
                                            }
                                        }
                                        loginRequest?.let { viewModel.login(it) } ?: run {
                                            Toast.makeText(applicationContext, "Login failed!", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(applicationContext, "Login failed!", Toast.LENGTH_SHORT).show()
                                    }
                                    Log.d("FCM", msg)
                                })

                        // ...
                    } else {
                        progressDialog.dismiss()
                        // Sign in failed, display a message and update the UI
                        //Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            YoYo.with(Techniques.Shake)
                                    .duration(1000)
                                    .playOn(binding.editOtp)
                            (task.exception as FirebaseAuthInvalidCredentialsException).printStackTrace()
                            Toast.makeText(applicationContext, "Verification failed!", Toast.LENGTH_LONG).show()
                            binding.editOtp.setText("")
                        }
                    }
                    countDownTimer.cancel()
                    binding.textResendOtp.isEnabled = true
                }
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this@OtpActivity)
                .setTitle("Cancel process?")
                .setMessage("Are you sure want to cancel the OTP process?")
                .setPositiveButton("Yes") { dialog, which ->
                    super.onBackPressed()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                .show()
    }

    fun resendVerificationCode(number: String, token: PhoneAuthProvider.ForceResendingToken) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallBack,         // OnVerificationStateChangedCallbacks
                token // ForceResendingToken from callbacks
        );

    }


}
