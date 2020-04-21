package com.food.ordering.zinger.ui.otp

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.LoginRequest
import com.food.ordering.zinger.data.model.Response
import com.food.ordering.zinger.data.model.UserPlaceModel
import com.food.ordering.zinger.data.retrofit.UserRepository
import kotlinx.coroutines.launch

import java.net.UnknownHostException

class OtpViewModel(private val userRepository: UserRepository) : ViewModel() {

    //LOGIN
    private val performLogin = MutableLiveData<Resource<Response<UserPlaceModel>>>()
    val performLoginStatus: LiveData<Resource<Response<UserPlaceModel>>>
        get() = performLogin

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            try {
                performLogin.value = Resource.loading()
                val response = userRepository.login(loginRequest)
                if (response.code == 1 || response.code == 1163) {
                    performLogin.value = Resource.success(response)
                } else {
                    performLogin.value = Resource.error(null, message = response.message)
                }
            } catch (e: Exception) {
                println("login failed ${e.message}")
                if (e is UnknownHostException) {
                    performLogin.value = Resource.offlineError()
                } else {
                    //different type of error
                    performLogin.value = Resource.error(e, message = "Something went wrong!")
                }
            }
        }
    }


}