package com.food.ordering.zinger.ui.restaurant

import android.animation.LayoutTransition
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.model.MenuItemModel
import com.food.ordering.zinger.databinding.ItemFoodBinding
import com.squareup.picasso.Picasso

class FoodAdapter(private val context: Context, private val foodItemList: List<MenuItemModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): FoodViewHolder {
        val binding: ItemFoodBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_food, parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodItemList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    class FoodViewHolder(var binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: MenuItemModel, position: Int, listener: OnItemClickListener) {
            Picasso.get().load(food.photoUrl).into(binding.imageFood)
            binding.textFoodName.text = food.name
            binding.textFoodDesc.text = food.category
            binding.textFoodPrice.text = "₹" + food.price
            binding.layoutRoot.setOnClickListener { listener.onItemClick(food, position) }
            if (food.isVeg==1) {
                binding.imageVeg.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_veg))
            } else {
                binding.imageVeg.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_non_veg))
            }
            if (food.quantity == 0) {
                binding.layoutQuantityControl.imageSub.visibility = View.GONE
                binding.layoutQuantityControl.textQuantity.text = "Add"
            } else {
                binding.layoutQuantityControl.imageSub.visibility = View.VISIBLE
                binding.layoutQuantityControl.textQuantity.text = food.quantity.toString()
            }
            binding.layoutQuantityControl.imageAdd.setOnClickListener { listener.onQuantityAdd(position) }
            binding.layoutQuantityControl.imageSub.setOnClickListener { listener.onQuantitySub(position) }
        }

        init {
            binding.layoutQuantityControl.root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: MenuItemModel?, position: Int)
        fun onQuantityAdd(position: Int)
        fun onQuantitySub(position: Int)
    }

}