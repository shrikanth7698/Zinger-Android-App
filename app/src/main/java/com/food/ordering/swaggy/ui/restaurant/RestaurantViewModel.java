package com.food.ordering.swaggy.ui.restaurant;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.food.ordering.swaggy.data.local.Resource;
import com.food.ordering.swaggy.data.model.FoodItem;
import com.food.ordering.swaggy.data.model.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class RestaurantViewModel extends ViewModel {

    private MutableLiveData<Resource<List<FoodItem>>> _menuStatus;

    public LiveData<Resource<List<FoodItem>>> getMenu() {
        if (_menuStatus == null) {
            _menuStatus = new MutableLiveData<>();
            _menuStatus.setValue(new Resource<List<FoodItem>>().loading());
            loadMenu();
        }
        return _menuStatus;
    }

    private void loadMenu() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<FoodItem> foodItems = new ArrayList<>();
                FoodItem foodItem = new FoodItem("Plain Dosa","South Indian","₹30","https://i2.wp.com/www.vegrecipesofindia.com/wp-content/uploads/2018/11/plain-dosa-recipe-1a.jpg",true);
                foodItems.add(foodItem);
                foodItem = new FoodItem("Pongal","South Indian","₹25","https://recipes.timesofindia.com/thumb/67488780.cms?width=1200&height=1200",true);
                foodItems.add(foodItem);
                foodItem = new FoodItem("Veg Noodles","Chinese","₹55","https://i.ndtvimg.com/i/2016-06/noodles-625_625x350_41466064269.jpg",true);
                foodItems.add(foodItem);
                foodItem = new FoodItem("Chicken biriyani","South Indian","₹80","https://food.fnr.sndimg.com/content/dam/images/food/fullset/2019/9/9/0/FNK_the-best-biryani_H.JPG.rend.hgtvcom.616.462.suffix/1568143107638.jpeg",false);
                foodItems.add(foodItem);
                foodItem = new FoodItem("Chappathi (2 pcs)","South Indian","₹30","https://1.bp.blogspot.com/-zmtVTuvvEIM/VtPUnCv42ZI/AAAAAAAAFYE/uvwEYpxz7ns/s1600/Home%2Bmade%2Bchapathi.JPG",true);
                foodItems.add(foodItem);
                foodItem = new FoodItem("Plain Dosa","South Indian","₹30","https://i2.wp.com/www.vegrecipesofindia.com/wp-content/uploads/2018/11/plain-dosa-recipe-1a.jpg",true);
                foodItems.add(foodItem);
                foodItem = new FoodItem("Pongal","South Indian","₹25","https://recipes.timesofindia.com/thumb/67488780.cms?width=1200&height=1200",true);
                foodItems.add(foodItem);
                foodItem = new FoodItem("Veg Noodles","Chinese","₹55","https://i.ndtvimg.com/i/2016-06/noodles-625_625x350_41466064269.jpg",true);
                foodItems.add(foodItem);
                foodItem = new FoodItem("Chicken biriyani","South Indian","₹80","https://food.fnr.sndimg.com/content/dam/images/food/fullset/2019/9/9/0/FNK_the-best-biryani_H.JPG.rend.hgtvcom.616.462.suffix/1568143107638.jpeg",false);
                foodItems.add(foodItem);
                foodItem = new FoodItem("Chappathi (2 pcs)","South Indian","₹30","https://1.bp.blogspot.com/-zmtVTuvvEIM/VtPUnCv42ZI/AAAAAAAAFYE/uvwEYpxz7ns/s1600/Home%2Bmade%2Bchapathi.JPG",true);
                _menuStatus.setValue(new Resource<List<FoodItem>>().success(foodItems));
            }
        },1000);
    }
}
