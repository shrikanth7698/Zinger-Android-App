package com.food.ordering.swaggy.ui.home;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.food.ordering.swaggy.data.local.Resource;
import com.food.ordering.swaggy.data.model.Shop;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Resource<List<Shop>>> _shopsStatus;

    public LiveData<Resource<List<Shop>>> getShops() {
        if (_shopsStatus == null) {
            _shopsStatus = new MutableLiveData<>();
            _shopsStatus.setValue(new Resource<List<Shop>>().loading());
            loadShops();
        }
        return _shopsStatus;
    }

    private void loadShops() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Shop> shops = new ArrayList<>();
                Shop shop = new Shop("Sathyas Main Canteen","Closes at 9pm","4.2","https://i.udemycdn.com/course/750x422/2729284_f9fa_3.jpg");
                shops.add(shop);
                shop = new Shop("Sathyas Mini Canteen","Closes at 9pm","4.0","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSfmlzlZEE15qncrWbPXRRgF8zXX4fllts4zQBeIXwONt3nmFXV");
                shops.add(shop);
                shop = new Shop("Snow Cube","Closes at 10pm","4.8","https://www.seriouseats.com/2018/06/20180625-no-churn-vanilla-ice-cream-vicky-wasik-13-1500x1125.jpg");
                shops.add(shop);
                shop = new Shop("Thanjavur Thattu Kadai","Closes at 8pm","4.9","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcR1tlxh62bkra0OXoVQPaogeF0Lc8tNqBGmW8dTO-Ekf13ExjsQ");
                shops.add(shop);
                shop = new Shop("Sathyas Main Canteen","Closes at 9pm","4.2","https://i.udemycdn.com/course/750x422/2729284_f9fa_3.jpg");
                shops.add(shop);
                shop = new Shop("Sathyas Mini Canteen","Closes at 9pm","4.0","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSfmlzlZEE15qncrWbPXRRgF8zXX4fllts4zQBeIXwONt3nmFXV");
                shops.add(shop);
                shop = new Shop("Snow Cube","Closes at 10pm","4.8","https://www.seriouseats.com/2018/06/20180625-no-churn-vanilla-ice-cream-vicky-wasik-13-1500x1125.jpg");
                shops.add(shop);
                shop = new Shop("Thanjavur Thattu Kadai","Closes at 8pm","4.9","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcR1tlxh62bkra0OXoVQPaogeF0Lc8tNqBGmW8dTO-Ekf13ExjsQ");
                shops.add(shop);
                shop = new Shop("Sathyas Main Canteen","Closes at 9pm","4.2","https://i.udemycdn.com/course/750x422/2729284_f9fa_3.jpg");
                shops.add(shop);
                shop = new Shop("Sathyas Mini Canteen","Closes at 9pm","4.0","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSfmlzlZEE15qncrWbPXRRgF8zXX4fllts4zQBeIXwONt3nmFXV");
                shops.add(shop);
                shop = new Shop("Snow Cube","Closes at 10pm","4.8","https://www.seriouseats.com/2018/06/20180625-no-churn-vanilla-ice-cream-vicky-wasik-13-1500x1125.jpg");
                shops.add(shop);
                shop = new Shop("Thanjavur Thattu Kadai","Closes at 8pm","4.9","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcR1tlxh62bkra0OXoVQPaogeF0Lc8tNqBGmW8dTO-Ekf13ExjsQ");
                shops.add(shop);
                _shopsStatus.setValue(new Resource<List<Shop>>().success(shops));
            }
        },1000);
    }
}
