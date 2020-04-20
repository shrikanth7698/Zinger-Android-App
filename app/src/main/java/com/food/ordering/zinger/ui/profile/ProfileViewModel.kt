package com.food.ordering.zinger.ui.profile

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.PlaceModel
import com.food.ordering.zinger.data.model.PlacesResponse
import com.food.ordering.zinger.data.model.Response
import com.food.ordering.zinger.data.model.UpdateUserRequest
import com.food.ordering.zinger.data.retrofit.PlaceRepository
import com.food.ordering.zinger.data.retrofit.UserRepository
import kotlinx.coroutines.launch

import java.net.UnknownHostException


class ProfileViewModel(private val userRepository: UserRepository, private val placeRepository: PlaceRepository) : ViewModel() {

    //Fetch places list
    private val performFetchPlacesList = MutableLiveData<Resource<PlacesResponse>>()
    val performFetchPlacesStatus: LiveData<Resource<PlacesResponse>>
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
            performFetchPlacesList.value = Resource.success(PlacesResponse(1, queryPlaceList, ""))
        }else{
            performFetchPlacesList.value = Resource.success(PlacesResponse(1, placesList, ""))
        }
    }

    private val performUpdate = MutableLiveData<Resource<Response<String>>>()
    val performUpdateStatus: LiveData<Resource<Response<String>>>
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