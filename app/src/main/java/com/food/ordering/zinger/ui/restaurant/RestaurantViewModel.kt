package com.food.ordering.zinger.ui.restaurant

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.MenuItemModel
import com.food.ordering.zinger.data.retrofit.ItemRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RestaurantViewModel(private val itemRepository: ItemRepository) : ViewModel() {

    //Fetch menu items
    private val performFetchMenu = MutableLiveData<Resource<List<MenuItemModel>>>()
    val performFetchMenuStatus: LiveData<Resource<List<MenuItemModel>>>
        get() = performFetchMenu

    var menuList:ArrayList<MenuItemModel> = ArrayList()
    var menuVegList:ArrayList<MenuItemModel> = ArrayList()
    fun getMenu(shopId: String) {
        viewModelScope.launch {
            try {
                performFetchMenu.value = Resource.loading()
                val response = itemRepository.getMenu(shopId)
                if(response!=null){
                    if(!response.data.isNullOrEmpty()){
                        menuList.clear()
                        menuVegList.clear()
                        menuList.addAll(response.data)
                        menuList.sortByDescending {
                            it.category
                        }
                        menuList.forEach{
                            if(it.isVeg==1){
                                menuVegList.add(it)
                            }
                        }
                        performFetchMenu.value = Resource.success(menuList)
                    }else{
                        performFetchMenu.value = Resource.empty()
                    }
                }
            } catch (e: Exception) {
                println("fetch menu failed ${e.message}")
                if (e is UnknownHostException) {
                    performFetchMenu.value = Resource.offlineError()
                } else {
                    performFetchMenu.value = Resource.error(e)
                }
            }
        }
    }


    fun switchMenu(isVeg: Boolean){
        println("switch menu testing veg "+menuVegList.size)
        println("switch menu testing non veg "+menuList.size)
        if(isVeg){
            performFetchMenu.value = Resource.success(menuVegList)
        }else{
            performFetchMenu.value = Resource.success(menuList)
        }

    }

    fun getTime(time: String?): Date {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeCalendar = Calendar.getInstance()
        val closingTime = sdf.parse(time)
        val cal1 = Calendar.getInstance()
        cal1.time = closingTime
        timeCalendar[Calendar.HOUR_OF_DAY] = cal1.get(Calendar.HOUR_OF_DAY)
        timeCalendar[Calendar.MINUTE] = cal1.get(Calendar.MINUTE)
        timeCalendar[Calendar.SECOND] = cal1.get(Calendar.SECOND)
        return timeCalendar.time
    }
}