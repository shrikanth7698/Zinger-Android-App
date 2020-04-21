package com.food.ordering.zinger.ui.search

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.MenuItemModel
import com.food.ordering.zinger.data.retrofit.ItemRepository
import kotlinx.coroutines.launch

import java.net.UnknownHostException
import java.util.ArrayList


class SearchViewModel(private val itemRepository: ItemRepository,private val preferencesHelper: PreferencesHelper) : ViewModel() {

    //search menu items
    private val performFetchMenu = MutableLiveData<Resource<List<MenuItemModel>>>()
    val performFetchMenuStatus: LiveData<Resource<List<MenuItemModel>>>
        get() = performFetchMenu

    fun getMenu(placeId: String, query: String, shopId: String?, isGlobalSearch: Boolean) {
        viewModelScope.launch {
            try {
                performFetchMenu.value = Resource.loading()
                val response = itemRepository.searchItems(placeId, query)
                val shopList = preferencesHelper.getShopList()
                var shopQueryList = shopList?.filter {
                    it.shopModel.name.toLowerCase().contains(query.toLowerCase())
                }
                var shopMenuList:ArrayList<MenuItemModel> = ArrayList()
                shopQueryList?.forEach {
                    shopMenuList.add(
                            MenuItemModel(
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
                        var menuQuery:ArrayList<MenuItemModel> = ArrayList()
                        menuQuery.addAll(response.data)
                        for(i in menuQuery.indices){
                            menuQuery[i].isDish = true
                        }
                        if(isGlobalSearch){
                            if(shopMenuList.isNullOrEmpty()){
                                performFetchMenu.value = Resource.success(menuQuery)
                            }else{
                                menuQuery.addAll(shopMenuList)
                                performFetchMenu.value = Resource.success(menuQuery)
                            }
                        }else{
                            var menuShopQuery:ArrayList<MenuItemModel> = ArrayList()
                            for(i in menuQuery.indices){
                                if(menuQuery[i].shopModel?.id==shopId?.toInt()){
                                    menuQuery[i].shopModel?.name = menuQuery[i].category
                                    menuShopQuery.add(menuQuery[i])
                                }
                            }
                            if(menuShopQuery.isNullOrEmpty()){
                                performFetchMenu.value = Resource.empty()
                            }else {
                                performFetchMenu.value = Resource.success(menuShopQuery)
                            }
                        }
                    } else {
                        if(isGlobalSearch){
                            if (shopMenuList.isNullOrEmpty()) {
                                performFetchMenu.value = Resource.empty()
                            } else {
                                performFetchMenu.value = Resource.success(shopMenuList)
                            }
                        }else {
                            performFetchMenu.value = Resource.empty()
                        }
                    }
                }
            } catch (e: Exception) {
                println("search menu failed ${e.message}")
                if (e is UnknownHostException) {
                    performFetchMenu.value = Resource.offlineError()
                } else {
                    performFetchMenu.value = Resource.error(e)
                }
            }
        }
    }

}