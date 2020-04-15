package com.food.ordering.zinger.ui.profile

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.Campus
import com.food.ordering.zinger.data.retrofit.UserRepository
import kotlinx.coroutines.launch

import java.net.UnknownHostException
import java.util.ArrayList


class ProfileViewModel(private val repository: UserRepository) : ViewModel() {

    //Fetch campus list
    private val performFetchCampusList = MutableLiveData<Resource<List<Campus>>>()
    val performFetchCampusListStatus: LiveData<Resource<List<Campus>>>
        get() = performFetchCampusList

    fun getCampusList() {
        viewModelScope.launch {
            try {
                performFetchCampusList.value = Resource.loading()
                //val response = productRepository.getStats()
                val response = loadCampusList()
                performFetchCampusList.value = Resource.success(response)
            } catch (e: Exception) {
                println("fetch campus list failed ${e.message}")
                if (e is UnknownHostException) {
                    performFetchCampusList.value = Resource.offlineError()
                } else {
                    performFetchCampusList.value = Resource.error(e)
                }
            }
        }
    }

    private fun loadCampusList(): List<Campus> {
        val campusList: MutableList<Campus> = ArrayList()
        var campus = Campus("1", "SSN College", "Closes at 9pm", "4.2", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg/1200px-Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg.png")
        campusList.add(campus)
        campus = Campus("2", "VIT University", "Closes at 9pm", "4.0", "https://findlogovector.com/wp-content/uploads/2019/03/vellore-institute-of-technology-vit-logo-vector.png")
        campusList.add(campus)
        campus = Campus("3", "SRM University", "Closes at 10pm", "4.8", "https://seeklogo.com/images/S/srm-university-logo-81BF9B8323-seeklogo.com.png")
        campusList.add(campus)
        campus = Campus("4", "Smartworks", "Closes at 8pm", "4.9", "https://www.et-gbs.com/wp-content/uploads/2019/02/Smartworks.png")
        campusList.add(campus)
        campus = Campus("5", "VIT University", "Closes at 9pm", "4.0", "https://findlogovector.com/wp-content/uploads/2019/03/vellore-institute-of-technology-vit-logo-vector.png")
        campusList.add(campus)
        campus = Campus("6", "SRM University", "Closes at 10pm", "4.8", "https://seeklogo.com/images/S/srm-university-logo-81BF9B8323-seeklogo.com.png")
        campusList.add(campus)
        campus = Campus("7", "Smartworks", "Closes at 8pm", "4.9", "https://www.et-gbs.com/wp-content/uploads/2019/02/Smartworks.png")
        campusList.add(campus)
        campus = Campus("8", "SSN College", "Closes at 9pm", "4.2", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg/1200px-Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg.png")
        campusList.add(campus)
        campus = Campus("9", "SRM University", "Closes at 10pm", "4.8", "https://seeklogo.com/images/S/srm-university-logo-81BF9B8323-seeklogo.com.png")
        campusList.add(campus)
        campus = Campus("10", "Smartworks", "Closes at 8pm", "4.9", "https://www.et-gbs.com/wp-content/uploads/2019/02/Smartworks.png")
        campusList.add(campus)
        campus = Campus("11", "SSN College", "Closes at 9pm", "4.2", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg/1200px-Sri_Sivasubramaniya_Nadar_College_of_Engineering.svg.png")
        campusList.add(campus)
        campus = Campus("12", "VIT University", "Closes at 9pm", "4.0", "https://findlogovector.com/wp-content/uploads/2019/03/vellore-institute-of-technology-vit-logo-vector.png")
        campusList.add(campus)
        campus = Campus("13", "SRM University", "Closes at 10pm", "4.8", "https://seeklogo.com/images/S/srm-university-logo-81BF9B8323-seeklogo.com.png")
        campusList.add(campus)
        campus = Campus("14", "Smartworks", "Closes at 8pm", "4.9", "https://www.et-gbs.com/wp-content/uploads/2019/02/Smartworks.png")
        return campusList
    }

}