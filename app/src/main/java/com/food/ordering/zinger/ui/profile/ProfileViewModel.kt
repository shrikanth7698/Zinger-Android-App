package com.food.ordering.zinger.ui.profile

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.PlacesResponse
import com.food.ordering.zinger.data.model.UpdateUserRequest
import com.food.ordering.zinger.data.model.UpdateUserResponse
import com.food.ordering.zinger.data.retrofit.PlaceRepository
import com.food.ordering.zinger.data.retrofit.UserRepository
import kotlinx.coroutines.launch

import java.net.UnknownHostException


class ProfileViewModel(private val userRepository: UserRepository, private val placeRepository: PlaceRepository) : ViewModel() {

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

    private val performUpdate = MutableLiveData<Resource<UpdateUserResponse>>()
    val performUpdateStatus: LiveData<Resource<UpdateUserResponse>>
        get() = performUpdate

    fun signUp(updateUserRequest: UpdateUserRequest) {
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
                println("Sign Up failed ${e.message}")
                if (e is UnknownHostException) {
                    performUpdate.value = Resource.offlineError()
                } else {
                    performUpdate.value = Resource.error(e)
                }
            }
        }
    }

}