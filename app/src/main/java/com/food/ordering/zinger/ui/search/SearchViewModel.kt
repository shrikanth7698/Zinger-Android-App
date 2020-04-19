package com.food.ordering.zinger.ui.search

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.MenuItem
import com.food.ordering.zinger.data.model.Shop
import com.food.ordering.zinger.data.model.ShopsResponseData
import com.food.ordering.zinger.data.retrofit.ItemRepository
import com.food.ordering.zinger.data.retrofit.UserRepository
import kotlinx.coroutines.launch

import java.net.UnknownHostException
import java.util.ArrayList


class SearchViewModel(private val itemRepository: ItemRepository,private val preferencesHelper: PreferencesHelper) : ViewModel() {

    //search menu items
    private val performFetchMenu = MutableLiveData<Resource<List<MenuItem>>>()
    val performFetchMenuStatus: LiveData<Resource<List<MenuItem>>>
        get() = performFetchMenu

    fun getMenu(placeId: String, query: String) {
        viewModelScope.launch {
            try {
                performFetchMenu.value = Resource.loading()
                val response = itemRepository.searchItems(placeId, query)
                val shopList = preferencesHelper.getShopList()
                var shopQueryList = shopList?.filter {
                    it.shopModel.name.toLowerCase().contains(query.toLowerCase())
                }
                var shopMenuList:ArrayList<MenuItem> = ArrayList()
                shopQueryList?.forEach {
                    shopMenuList.add(
                            MenuItem(
                                "",
                                    -1,
                                    0,
                                    0,
                                    "",
                                    it.shopModel.photoUrl,
                                    -1,
                                    it.shopModel,
                                    0,
                                    -1,
                                    it.shopModel.name,
                                    false
                            )
                    )
                }
                if (response != null) {
                    if (!response.data.isNullOrEmpty()) {
                        var menuQuery:ArrayList<MenuItem> = ArrayList()
                        menuQuery.addAll(response.data)
                        for(i in menuQuery.indices){
                            menuQuery[i].isDish = true
                        }
                        if(shopMenuList.isNullOrEmpty()){
                            performFetchMenu.value = Resource.success(menuQuery)
                        }else{
                            menuQuery.addAll(shopMenuList)
                            performFetchMenu.value = Resource.success(menuQuery)
                        }
                    } else {
                        if(shopMenuList.isNullOrEmpty()){
                            performFetchMenu.value = Resource.empty()
                        }else{
                            performFetchMenu.value = Resource.success(shopMenuList)
                        }
                    }
                }
            } catch (e: Exception) {
                println("fetch stats failed ${e.message}")
                if (e is UnknownHostException) {
                    performFetchMenu.value = Resource.offlineError()
                } else {
                    performFetchMenu.value = Resource.error(e)
                }
            }
        }
    }

}