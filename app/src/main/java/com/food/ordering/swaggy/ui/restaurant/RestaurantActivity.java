package com.food.ordering.swaggy.ui.restaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.food.ordering.swaggy.R;
import com.food.ordering.swaggy.data.local.Resource;
import com.food.ordering.swaggy.data.model.FoodItem;
import com.food.ordering.swaggy.data.model.Shop;
import com.food.ordering.swaggy.databinding.ActivityMainBinding;
import com.food.ordering.swaggy.databinding.ActivityRestaurantBinding;
import com.food.ordering.swaggy.ui.home.MainActivity;
import com.food.ordering.swaggy.ui.home.MainViewModel;
import com.food.ordering.swaggy.ui.home.ShopAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantActivity extends AppCompatActivity {

    ActivityRestaurantBinding binding;
    FoodAdapter foodAdapter;
    ProgressDialog progressDialog;
    List<FoodItem> foodItemList = new ArrayList<>();
    Shop shop;
    RestaurantViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArgs();
        initView();
        setObservers();
        viewModel.getMenu();
    }

    private void getArgs(){
        shop = getIntent().getParcelableExtra("shop");
    }

    private void initView(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant);
        viewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        progressDialog = new ProgressDialog(this);
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(),android.R.color.white));
        binding.toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(),android.R.color.black));
        binding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {//Collapsed
                    binding.textShopRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star,0,0,0);
                    binding.textShopRating.setTextColor(ContextCompat.getColor(getApplicationContext(),android.R.color.black));
                }
                else
                {//Expanded
                    binding.textShopRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_black,0,0,0);
                    binding.textShopRating.setTextColor(ContextCompat.getColor(getApplicationContext(),android.R.color.white));
                }
            }
        });
        setupMenuRecyclerView();
        updateShopUI();
    }

    private void updateShopUI(){
        binding.toolbarLayout.setTitle(shop.getName());
        binding.textShopRating.setText(shop.getRating());
        Picasso.get().load(shop.getImageUrl()).into(binding.imageExpanded);
    }

    private void setObservers(){
        viewModel.getMenu().observe(this, new Observer<Resource<List<FoodItem>>>() {
            @Override
            public void onChanged(Resource<List<FoodItem>> resource) {
                switch (resource.status){
                    case Resource.LOADING: {
                        progressDialog.setMessage("Getting menu");
                        progressDialog.show();
                        break;
                    }
                    case Resource.SUCCESS: {
                        progressDialog.dismiss();
                        foodItemList.clear();
                        foodItemList.addAll(resource.data);
                        foodAdapter.notifyDataSetChanged();
                        break;
                    }
                    case Resource.EMPTY: {
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar.make(binding.getRoot(), "No Outlets in this college", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                    case Resource.NO_INTERNET: {
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar.make(binding.getRoot(), "No Internet Connection", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                    case Resource.ERROR: {
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar.make(binding.getRoot(), "Something went wrong", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        break;
                    }
                    default:{
                        break;
                    }
                }
            }
        });
    }

    private void setupMenuRecyclerView() {
        foodAdapter = new FoodAdapter(getApplicationContext(), foodItemList, new FoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FoodItem item, int position) {

            }

            @Override
            public void onQuantityAdd(int position) {
                System.out.println("quantity add clicked "+position);
                foodItemList.get(position).setQuantity(foodItemList.get(position).getQuantity()+1);
                foodAdapter.notifyItemChanged(position);
            }

            @Override
            public void onQuantitySub(int position) {
                System.out.println("quantity sub clicked "+position);
                if(foodItemList.get(position).getQuantity()-1>=0) {
                    foodItemList.get(position).setQuantity(foodItemList.get(position).getQuantity() - 1);
                    foodAdapter.notifyItemChanged(position);
                }
            }
        });
        binding.recyclerFoodItems.setLayoutManager(new LinearLayoutManager(RestaurantActivity.this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerFoodItems.setAdapter(foodAdapter);
    }
}
