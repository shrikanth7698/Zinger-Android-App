package com.food.ordering.zinger.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.amulyakhare.textdrawable.TextDrawable;
import com.food.ordering.zinger.R;
import com.food.ordering.zinger.data.local.Resource;
import com.food.ordering.zinger.data.model.FoodItem;
import com.food.ordering.zinger.data.model.Shop;
import com.food.ordering.zinger.databinding.ActivityMainBinding;
import com.food.ordering.zinger.databinding.HeaderLayoutBinding;
import com.food.ordering.zinger.ui.cart.CartActivity;
import com.food.ordering.zinger.ui.restaurant.RestaurantActivity;
import com.food.ordering.zinger.utils.SharedPreferenceHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding binding;
    HeaderLayoutBinding headerLayout;
    Drawer drawer;
    ShopAdapter shopAdapter;
    ProgressDialog progressDialog;
    List<Shop> shopList = new ArrayList<>();
    List<FoodItem> cartList = new ArrayList<>();
    Snackbar cartSnackBar;
    MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setupMaterialDrawer();
        setObservers();
        viewModel.getShops();
        cartSnackBar.setAction("View Cart", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
            }
        });
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        headerLayout = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.header_layout, null, false);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        cartSnackBar = Snackbar.make(binding.getRoot(), "", Snackbar.LENGTH_INDEFINITE);
        cartSnackBar.setBackgroundTint(ContextCompat.getColor(getApplicationContext(),R.color.green));
        binding.imageMenu.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        setStatusBarHeight();
        updateHeaderLayoutUI();
        setupShopRecyclerView();
    }

    private void setStatusBarHeight() {
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rectangle = new Rect();
                Window window = getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                int statusBarHeight = rectangle.top;
                ViewGroup.LayoutParams layoutParams = headerLayout.statusbarSpaceView.getLayoutParams();
                layoutParams.height = statusBarHeight;
                headerLayout.statusbarSpaceView.setLayoutParams(layoutParams);
                Log.d("Home", "status bar height $statusBarHeight");
                binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void updateHeaderLayoutUI() {
        headerLayout.textCustomerName.setText("Shrikanth Ravi");
        headerLayout.textEmail.setText("shrikanthravi.me@gmail.com");
        TextDrawable textDrawable = TextDrawable.builder()
                .buildRound("S", ContextCompat.getColor(this, R.color.accent));
        headerLayout.imageProfilePic.setImageDrawable(textDrawable);
        //binding.imageMenu.setImageDrawable(textDrawable);
    }

    private void setupMaterialDrawer() {
        headerLayout.layoutHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO open profile activity
            }
        });
        int identifier = 0;
        final PrimaryDrawerItem profileItem = new PrimaryDrawerItem().withIdentifier(++identifier).withName("My Profile")
                .withIcon(R.drawable.ic_drawer_user);
        final PrimaryDrawerItem ordersItem = new PrimaryDrawerItem().withIdentifier(++identifier).withName("Your Orders")
                .withIcon(R.drawable.ic_drawer_past_rides);
        final PrimaryDrawerItem contactUsItem = new PrimaryDrawerItem().withIdentifier(++identifier).withName("Contact Us")
                .withIcon(R.drawable.ic_drawer_mail);
        final PrimaryDrawerItem signOutItem = new PrimaryDrawerItem().withIdentifier(++identifier).withName("Sign out")
                .withIcon(R.drawable.ic_drawer_log_out);
        final PrimaryDrawerItem helpcenter = new PrimaryDrawerItem().withIdentifier(++identifier).withName("Help Center")
                .withIcon(R.drawable.ic_drawer_info);

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withDisplayBelowStatusBar(false)
                .withHeader(headerLayout.getRoot())
                .withTranslucentStatusBar(true)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        profileItem,
                        ordersItem,
                        helpcenter,
                        contactUsItem,
                        new DividerDrawerItem(),
                        signOutItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (profileItem.getIdentifier() == drawerItem.getIdentifier()) {
                            //TODO open profile activity
                        }
                        if (ordersItem.getIdentifier() == drawerItem.getIdentifier()) {
                            //TODO open orders activity
                        }
                        if (helpcenter.getIdentifier() == drawerItem.getIdentifier()) {
                            //TODO open help activity
                        }
                        if (contactUsItem.getIdentifier() == drawerItem.getIdentifier()) {
                            //TODO open contact us activity
                        }
                        if (signOutItem.getIdentifier() == drawerItem.getIdentifier()) {
                            //TODO show sign out dialog
                        }
                        return true;
                    }
                })
                .build();
    }

    private void setObservers(){
        viewModel.getShops().observe(this, new Observer<Resource<List<Shop>>>() {
            @Override
            public void onChanged(Resource<List<Shop>> resource) {
                switch (resource.status){
                    case Resource.LOADING: {
                        progressDialog.setMessage("Getting Outlets");
                        progressDialog.show();
                        break;
                    }
                    case Resource.SUCCESS: {
                        progressDialog.dismiss();
                        shopList.clear();
                        shopList.addAll(resource.data);
                        shopAdapter.notifyDataSetChanged();
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

    private void setupShopRecyclerView() {
        shopAdapter = new ShopAdapter(getApplicationContext(), shopList, new ShopAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Shop item, int position) {
                Intent intent = new Intent(getApplicationContext(),RestaurantActivity.class);
                intent.putExtra("shop",item);
                startActivity(intent);
            }
        });
        binding.recyclerShops.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerShops.setAdapter(shopAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_menu: {
                drawer.openDrawer();
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartList.clear();
        cartList.addAll(getCart());
        updateCartUI();
    }

    private void updateCartUI(){
        int total = 0;
        int totalItems = 0;
        if(cartList.size()>0) {
            for (int i = 0; i < cartList.size(); i++) {
                total+= cartList.get(i).getPrice() * cartList.get(i).getQuantity();
                totalItems+= cartList.get(i).getQuantity();
            }
            if(totalItems==1){
                cartSnackBar.setText("₹"+total+" | "+totalItems+" item");
            }else{
                cartSnackBar.setText("₹"+total+" | "+totalItems+" items");
            }
            cartSnackBar.show();
        }else{
            cartSnackBar.dismiss();
        }
    }

    public List<FoodItem> getCart(){
        List<FoodItem> items = new ArrayList<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<FoodItem>>() {}.getType();
        String json = SharedPreferenceHelper.getSharedPreferenceString(this,"cart","");
        List<FoodItem> temp = gson.fromJson(json,listType);
        if(temp!=null){
            items.addAll(temp);
        }
        return items;
    }
}
