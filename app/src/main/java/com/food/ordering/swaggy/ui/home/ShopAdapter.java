package com.food.ordering.swaggy.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.food.ordering.swaggy.R;
import com.food.ordering.swaggy.data.model.Shop;
import com.food.ordering.swaggy.databinding.ItemShopBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {

    private List<Shop> shopList;
    private Context context;

    public ShopAdapter(Context context, List<Shop> shops) {
        shopList = shops;
        this.context = context;
    }

    @Override
    public ShopAdapter.ShopViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        ItemShopBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_shop, parent, false);
        return new ShopViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ShopViewHolder holder, int position) {
        holder.bind(shopList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        ItemShopBinding binding;

        public ShopViewHolder(ItemShopBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Shop shop, int position) {
            Picasso.get().load(shop.getImageUrl()).into(binding.imageShop);
            binding.textShopName.setText(shop.getName());
            binding.textShopDesc.setText(shop.getDesc());
            binding.textShopRating.setText(shop.getRating());
            binding.layoutRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}