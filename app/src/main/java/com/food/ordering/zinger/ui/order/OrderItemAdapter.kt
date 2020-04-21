package com.food.ordering.zinger.ui.order

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.model.OrderItems
import com.food.ordering.zinger.databinding.ItemOrderProductBinding

class OrderItemAdapter(private val context: Context, private val foodItemList: List<OrderItems>, private val listener: OnItemClickListener) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): OrderItemViewHolder {
        val binding: ItemOrderProductBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_order_product, parent, false)
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(foodItemList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    class OrderItemViewHolder(var binding: ItemOrderProductBinding) : RecyclerView.ViewHolder(binding.root) {
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