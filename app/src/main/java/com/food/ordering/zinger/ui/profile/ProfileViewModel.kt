package com.food.ordering.zinger.ui.profile

import android.content.Context
import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.NotificationTokenUpdate
import com.food.ordering.zinger.data.model.PlaceModel
import com.food.ordering.zinger.data.model.Response
import com.food.ordering.zinger.data.model.UpdateUserRequest
import com.food.ordering.zinger.data.retrofit.PlaceRepository
import com.food.ordering.zinger.data.retrofit.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.launch

import java.net.UnknownHostException


class ProfileViewModel(private val userRepository: UserRepository, private val placeRepository: PlaceRepository,
                       private val preferencesHelper: PreferencesHelper) : ViewModel() {

    //Fetch places list
    private val performFetchPlacesList = MutableLiveData<Resource<Response<List<PlaceModel>>>>()
    val performFetchPlacesStatus: LiveData<Resource<Response<List<PlaceModel>>>>
        get() = performFetchPlacesList

    private var placesList: ArrayList<PlaceModel> = ArrayList()
    fun getPlaces() {
        viewModelScope.launch {
            try {
                performFetchPlacesList.value = Resource.loading()
                val response = placeRepository.getPlaces()
                if(response.code==1) {
                    if (!response.data.isNullOrEmpty()) {
                        placesList.clear()
                        placesList.addAll(response.data)
                        performFetchPlacesList.value = Resource.success(response)
                    } else {
                        if (response.data != null) {
                            if (response.data.isEmpty()) {
                                performFetchPlacesList.value = Resource.empty()
                            }
                        } else {
                            performFetchPlacesList.value = Resource.error(null, message = "Something went wrong!")
                        }
                    }
                }else{
                    performFetchPlacesList.value = Resource.error(null, message = response.message)
                }
            } catch (e: Exception) {
                println("fetch places list failed ${e.message}")
                if (e is UnknownHostException) {
                    performFetchPlacesList.value = Resource.offlineError()
                } else {
                    performFetchPlacesList.value = Resource.error(e)
                }
            }
        }
    }

    fun searchPlace(query: String?) {
        if(!query.isNullOrEmpty()) {
            val queryPlaceList = placesList.filter {
                it.name.toLowerCase().contains(query?.toLowerCase().toString())
            }
            performFetchPlacesList.value = Resource.success(Response(1, queryPlaceList, ""))
        }else{
            performFetchPlacesList.value = Resource.success(Response(1, placesList, ""))
        }
    }


    //Update User Details
    private val performUpdate = MutableLiveData<Resource<Response<String>>>()
    val performUpdateStatus: LiveData<Resource<Response<String>>>
        get() = performUpdate

    fun updateUserDetails(updateUserRequest: UpdateUserRequest) {
        viewModelScope.launch {
            try {
                performUpdate.value = Resource.loading()
                val response = userRepository.updateUser(updateUserRequest)
                if(response.code==1) {
                    if (response.data!=null) {
                        performUpdate.value = Resource.success(response)
                    } else {
                        performUpdate.value = Resource.error(null, message = "Something went wrong")
                    }
                }else{
                    performUpdate.value = Resource.error(null, message = response.message)
                }
            } catch (e: Exception) {
                println("update user details failed ${e.message}")
                if (e is UnknownHostException) {
                    performUpdate.value = Resource.offlineError()
                } else {
                    performUpdate.value = Resource.error(e)
                }
            }
        }
    }

    //Update User FCM token
    private val performNotificationTokenUpdate = MutableLiveData<Resource<Response<String>>>()
    val performNotificationTokenUpdateStatus: LiveData<Resource<Response<String>>>
        get() = performNotificationTokenUpdate

    fun updateFcmToken(notificationTokenUpdate: NotificationTokenUpdate) {
        viewModelScope.launch {
            try {
                performNotificationTokenUpdate.value = Resource.loading()
                val response = userRepository.updateFcmToken(notificationTokenUpdate)
                if(response.code==1) {
                    if (response.data!=null) {
                        performNotificationTokenUpdate.value = Resource.success(response)
                    } else {
                        performNotificationTokenUpdate.value = Resource.error(null, message = "Something went wrong")
                    }
                }else{
                    performNotificationTokenUpdate.value = Resource.error(null, message = response.message)
                }
            } catch (e: Exception) {
                println("update fcm token failed ${e.message}")
                if (e is UnknownHostException) {
                    performNotificationTokenUpdate.value = Resource.offlineError()
                } else {
                    performNotificationTokenUpdate.value = Resource.error(e)
                }
            }
        }
    }


    private val verifyOtp = MutableLiveData<Resource<String>>()
    val verifyOtpStatus: LiveData<Resource<String>>
        get() = verifyOtp

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, context: Context) {

        var auth = FirebaseAuth.getInstance()
        verifyOtp.value = Resource.loading()

        auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->

                    viewModelScope.launch {
                        if(task.isSuccessful){
                            val user = task.result?.user
                            preferencesHelper.tempOauthId = user?.uid
                            preferencesHelper.tempMobile = user?.phoneNumber?.substring(3)
                            verifyOtp.value = Resource.success("")
                        }else{
                            verifyOtp.value = Resource.error(message = "")
                        }
                    }

                }

    }


}