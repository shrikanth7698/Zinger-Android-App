package com.food.ordering.zinger.ui.order

import android.animation.LayoutTransition
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.model.FoodItem
import com.food.ordering.zinger.data.model.OrderItems
import com.food.ordering.zinger.databinding.ItemCartProductBinding
import com.food.ordering.zinger.databinding.ItemOrderProductBinding

class OrderItemAdapter(private val context: Context, private val foodItemList: List<OrderItems>, private val listener: OnItemClickListener) : RecyclerView.Adapter<OrderItemAdapter.FoodViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): FoodViewHolder {
        val binding: ItemOrderProductBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_order_product, parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodItemList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    class FoodViewHolder(var binding: ItemOrderProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: OrderItems, position: Int, listener: OnItemClickListener) {
            binding.textFoodName.text = food.quantity.toString()+" x "+food.itemModel.name
            binding.textFoodPrice.text = "₹" + food.itemModel.price.toInt() * food.quantity
            binding.layoutRoot.setOnClickListener { listener.onItemClick(food, position) }
            if (food.itemModel.isVeg==1) {
                binding.imageVeg.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_veg))
            } else {
                binding.imageVeg.setImageDrawable(binding.root.context.getDrawable(R.drawable.ic_non_veg))
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: OrderItems?, position: Int)
    }

}