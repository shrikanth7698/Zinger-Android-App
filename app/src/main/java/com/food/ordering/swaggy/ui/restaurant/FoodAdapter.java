package com.food.ordering.swaggy.ui.restaurant;

import android.animation.LayoutTransition;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.food.ordering.swaggy.R;
import com.food.ordering.swaggy.data.model.FoodItem;
import com.food.ordering.swaggy.data.model.Shop;
import com.food.ordering.swaggy.databinding.ItemFoodBinding;
import com.food.ordering.swaggy.databinding.ItemShopBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<FoodItem> foodItemList;
    private Context context;
    private final OnItemClickListener listener;

    public FoodAdapter(Context context, List<FoodItem> shops, OnItemClickListener listener) {
        foodItemList = shops;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public FoodAdapter.FoodViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        ItemFoodBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_food, parent, false);
        return new FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        holder.bind(foodItemList.get(position), position, listener);
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ItemFoodBinding binding;

        public FoodViewHolder(ItemFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.layoutQuantityControl.root.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }

        public void bind(final FoodItem food, final int position, final OnItemClickListener listener) {
            Picasso.get().load(food.getImageUrl()).into(binding.imageFood);
            binding.textFoodName.setText(food.getName());
            binding.textFoodDesc.setText(food.getDesc());
            binding.textFoodPrice.setText(food.getPrice());
            binding.layoutRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(food, position);
                }
            });
            if(food.isVeg()){
                binding.imageVeg.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_veg));
            }else{
                binding.imageVeg.setImageDrawable(binding.getRoot().getContext().getDrawable(R.drawable.ic_non_veg));
            }
            if(food.getQuantity()==0){
                binding.layoutQuantityControl.imageSub.setVisibility(View.GONE);
                binding.layoutQuantityControl.textQuantity.setText("Add");
            }else{
                binding.layoutQuantityControl.textQuantity.setText(String.valueOf(food.getQuantity()));
                binding.layoutQuantityControl.imageSub.setVisibility(View.VISIBLE);
            }
            binding.layoutQuantityControl.imageAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onQuantityAdd(position);
                }
            });
            binding.layoutQuantityControl.imageSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onQuantitySub(position);
                }
            });
        }
    }


    public interface OnItemClickListener {
        void onItemClick(FoodItem item, int position);
        void onQuantityAdd(int position);
        void onQuantitySub(int position);
    }
}