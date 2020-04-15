package com.food.ordering.zinger.ui.signup

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.PlacesResponse
import com.food.ordering.zinger.data.model.UpdateUserRequest
import com.food.ordering.zinger.data.model.UpdateUserResponse
import com.food.ordering.zinger.data.retrofit.PlaceRepository
import com.food.ordering.zinger.data.retrofit.UserRepository
import kotlinx.coroutines.launch

import java.net.UnknownHostException


class SignUpViewModel(private val userRepository: UserRepository,private val placeRepository: PlaceRepository) : ViewModel() {

    //Fetch places list
    private val performFetchPlacesList = MutableLiveData<Resource<PlacesResponse>>()
    val performFetchPlacesStatus: LiveData<Resource<PlacesResponse>>
        get() = performFetchPlacesList

    fun getPlaces() {
        viewModelScope.launch {
            try {
                performFetchPlacesList.value = Resource.loading()
                val response = placeRepository.getPlaces()
                if(response.code==1) {
                    if (!response.data.isNullOrEmpty()) {
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

    private val performSignUp = MutableLiveData<Resource<UpdateUserResponse>>()
    val performSignUpStatus: LiveData<Resource<UpdateUserResponse>>
        get() = performSignUp

    fun signUp(updateUserRequest: UpdateUserRequest) {
        viewModelScope.launch {
            try {
                performSignUp.value = Resource.loading()
                val response = userRepository.updateUser(updateUserRequest)
                if(response.code==1) {
                    if (response.data!=null) {
                        performSignUp.value = Resource.success(response)
                    } else {
                        performSignUp.value = Resource.error(null, message = "Something went wrong")
                    }
                }else{
                    performSignUp.value = Resource.error(null, message = response.message)
                }
            } catch (e: Exception) {
                println("Sign Up failed ${e.message}")
                if (e is UnknownHostException) {
                    performSignUp.value = Resource.offlineError()
                } else {
                    performSignUp.value = Resource.error(e)
                }
            }
        }
    }

}