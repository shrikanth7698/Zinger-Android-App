package com.food.ordering.zinger.ui.signup

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.PlaceModel
import com.food.ordering.zinger.data.model.Response
import com.food.ordering.zinger.data.model.UpdateUserRequest
import com.food.ordering.zinger.data.retrofit.PlaceRepository
import com.food.ordering.zinger.data.retrofit.UserRepository
import kotlinx.coroutines.launch

import java.net.UnknownHostException


class SignUpViewModel(private val userRepository: UserRepository, private val placeRepository: PlaceRepository) : ViewModel() {

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
                if (response.code == 1) {
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
                } else {
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

    private val performSignUp = MutableLiveData<Resource<Response<String>>>()
    val performSignUpStatus: LiveData<Resource<Response<String>>>
        get() = performSignUp

    fun signUp(updateUserRequest: UpdateUserRequest) {
        viewModelScope.launch {
            try {
                performSignUp.value = Resource.loading()
                val response = userRepository.updateUser(updateUserRequest)
                if (response.code == 1) {
                    if (response.data != null) {
                        performSignUp.value = Resource.success(response)
                    } else {
                        performSignUp.value = Resource.error(null, message = "Something went wrong")
                    }
                } else {
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