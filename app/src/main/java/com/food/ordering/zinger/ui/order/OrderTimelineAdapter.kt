package com.food.ordering.zinger.ui.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.model.OrderItems
import com.food.ordering.zinger.databinding.ItemOrderStatusBinding
import com.food.ordering.zinger.utils.AppConstants
import com.food.ordering.zinger.utils.StatusHelper

class OrderTimelineAdapter(private val context: Context, private val orderStatusList: List<OrderStatus>, private val listener: OnItemClickListener) : RecyclerView.Adapter<OrderTimelineAdapter.OrderTimelineViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): OrderTimelineViewHolder {
        val binding: ItemOrderStatusBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_order_status, parent, false)
        return OrderTimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderTimelineViewHolder, position: Int) {
        holder.bind(orderStatusList[position],orderStatusList, holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return orderStatusList.size
    }

    class OrderTimelineViewHolder(var binding: ItemOrderStatusBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(orderStatus: OrderStatus, orderStatusList: List<OrderStatus>, position: Int, listener: OnItemClickListener) {
            if(position==0){
                binding.viewLineTop.visibility = View.INVISIBLE
                binding.viewLineBottom.visibility = View.VISIBLE
            }else if(position==orderStatusList.size-1){
                binding.viewLineTop.visibility = View.VISIBLE
                binding.viewLineBottom.visibility = View.INVISIBLE
            }else{
                binding.viewLineTop.visibility = View.VISIBLE
                binding.viewLineBottom.visibility = View.VISIBLE
            }
            if(orderStatus.isCurrent){
                binding.viewCircle.setBackgroundResource(R.drawable.bg_rounded_red)
                binding.textStatus.setTextColor(ContextCompat.getColor(binding.textStatus.context,android.R.color.black))
            }else{
                if(orderStatus.isDone){
                    when (orderStatus.name) {
                        StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED_BY_SELLER),
                        StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED_BY_USER) -> {
                            binding.viewCircle.setBackgroundResource(R.drawable.ic_cancel_status)
                            binding.textStatus.setTextColor(ContextCompat.getColor(binding.textStatus.context,android.R.color.black))
                        }
                        else -> {
                            when (orderStatus.name) {
                                StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_COMPLETED),
                                StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_DELIVERED),
                                StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_COMPLETED) -> {
                                    binding.viewCircle.setBackgroundResource(R.drawable.bg_rounded_green)
                                    binding.textStatus.setTextColor(ContextCompat.getColor(binding.textStatus.context,android.R.color.black))
                                }
                                else -> {
                                    binding.viewCircle.setBackgroundResource(R.drawable.bg_rounded_grey_solid)
                                    binding.textStatus.setTextColor(ContextCompat.getColor(binding.textStatus.context,R.color.shimmerColor))
                                }
                            }
                        }
                    }
                }else{
                    binding.viewCircle.setBackgroundResource(R.drawable.bg_rounded_grey_stroke)
                    binding.textStatus.setTextColor(ContextCompat.getColor(binding.textStatus.context,R.color.shimmerColor))
                }
            }
            binding.textStatus.text = orderStatus.name
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: OrderStatus?, position: Int)
    }

}