package com.food.ordering.swaggy.ui.cart

import android.animation.LayoutTransition
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.swaggy.R
import com.food.ordering.swaggy.data.model.FoodItem
import com.food.ordering.swaggy.databinding.ItemCartProductBinding

class CartAdapter(private val context: Context, private val foodItemList: List<FoodItem>, private val listener: OnItemClickListener) : RecyclerView.Adapter<CartAdapter.FoodViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): FoodViewHolder {
        val binding: ItemCartProductBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_cart_product, parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodItemList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    class FoodViewHolder(var binding: ItemCartProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: FoodItem, position: Int, listener: OnItemClickListener) {
            binding.textFoodName.text = food.name
            binding.textFoodPrice.text = "₹" + food.price * food.quantity
            binding.layoutRoot.setOnClickListener { listener.onItemClick(food, position) }
            if (food.isVeg) {
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
        fun onItemClick(item: FoodItem?, position: Int)
        fun onQuantityAdd(position: Int)
        fun onQuantitySub(position: Int)
    }

}