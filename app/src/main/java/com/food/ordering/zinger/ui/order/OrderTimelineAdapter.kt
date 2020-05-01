package com.food.ordering.zinger.ui.order

import android.content.Context
import android.util.TypedValue
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
import java.lang.Exception
import java.text.SimpleDateFormat

class OrderTimelineAdapter(private val context: Context, private val orderStatusList: List<OrderStatus>, private val listener: OnItemClickListener) : RecyclerView.Adapter<OrderTimelineAdapter.OrderTimelineViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): OrderTimelineViewHolder {
        val binding: ItemOrderStatusBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_order_status, parent, false)
        return OrderTimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderTimelineViewHolder, position: Int) {
        holder.bind(orderStatusList[position], orderStatusList, holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return orderStatusList.size
    }

    class OrderTimelineViewHolder(var binding: ItemOrderStatusBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(orderStatus: OrderStatus, orderStatusList: List<OrderStatus>, position: Int, listener: OnItemClickListener) {
            if (position == 0) {
                binding.viewLineTop.visibility = View.INVISIBLE
                binding.viewLineBottom.visibility = View.VISIBLE
            } else if (position == orderStatusList.size - 1) {
                binding.viewLineTop.visibility = View.VISIBLE
                binding.viewLineBottom.visibility = View.INVISIBLE
            } else {
                binding.viewLineTop.visibility = View.VISIBLE
                binding.viewLineBottom.visibility = View.VISIBLE
            }
            val orderStatusModel = orderStatus.orderStatusList.firstOrNull {
                StatusHelper.getStatusMessage(it.orderStatus)==orderStatus.name
            }
            if(orderStatusModel!=null){
                try {
                    val apiDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                    val appDateFormat = SimpleDateFormat("hh:mm aaa")
                    val date = apiDateFormat.parse(orderStatusModel.updatedTime)
                    val dateString = appDateFormat.format(date)
                    binding.textTime.text = dateString
                    //binding.textTimeNew.text = dateString
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
            if (orderStatus.isCurrent) {
                when (orderStatus.name) {
                    StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED_BY_SELLER),
                    StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED_BY_USER) -> {
                        binding.textTime.visibility = View.VISIBLE
                        //binding.textTimeNew.visibility = View.VISIBLE
                        binding.textStatusDesc.visibility = View.VISIBLE
                        binding.viewCircle.setBackgroundResource(R.drawable.ic_cancel_status)
                        val width = binding.viewCircle.resources.getDimension(R.dimen.status_dot_normal_size)
                        val params = binding.viewCircle.layoutParams
                        params.width = width.toInt()
                        params.height = width.toInt()
                        binding.viewCircle.layoutParams = params
                        binding.textStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX, binding.textStatus.context.resources.getDimension(R.dimen.status_normal_size))
                        binding.textStatus.setTextColor(ContextCompat.getColor(binding.textStatus.context, android.R.color.black))
                        binding.textTime.setTextColor(ContextCompat.getColor(binding.textStatus.context, android.R.color.tab_indicator_text))
                    }
                    StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_COMPLETED),
                    StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_DELIVERED),
                    StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_REFUND_COMPLETED) -> {
                        binding.textTime.visibility = View.VISIBLE
                        //binding.textTimeNew.visibility = View.VISIBLE
                        binding.textStatusDesc.visibility = View.VISIBLE
                        binding.viewCircle.setBackgroundResource(R.drawable.ic_checked)
                        val width = binding.viewCircle.resources.getDimension(R.dimen.status_dot_normal_size)
                        val params = binding.viewCircle.layoutParams
                        params.width = width.toInt()
                        params.height = width.toInt()
                        binding.viewCircle.layoutParams = params
                        binding.textStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX, binding.textStatus.context.resources.getDimension(R.dimen.status_normal_size))
                        binding.textStatus.setTextColor(ContextCompat.getColor(binding.textStatus.context,android.R.color.black))
                        binding.textTime.setTextColor(ContextCompat.getColor(binding.textStatus.context,android.R.color.tab_indicator_text))
                    }
                    else -> {
                        binding.textTime.visibility = View.VISIBLE
                        //binding.textTimeNew.visibility = View.VISIBLE
                        binding.textStatusDesc.visibility = View.VISIBLE
                        binding.viewCircle.setBackgroundResource(R.drawable.ic_checked)
                        val width = binding.viewCircle.resources.getDimension(R.dimen.status_dot_normal_size)
                        val params = binding.viewCircle.layoutParams
                        params.width = width.toInt()
                        params.height = width.toInt()
                        binding.viewCircle.layoutParams = params
                        binding.textStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX, binding.textStatus.context.resources.getDimension(R.dimen.status_normal_size))
                        binding.textStatus.setTextColor(ContextCompat.getColor(binding.textStatus.context, android.R.color.black))
                        binding.textTime.setTextColor(ContextCompat.getColor(binding.textStatus.context, android.R.color.tab_indicator_text))
                    }
                }
            } else {
                if (orderStatus.isDone) {
                    when (orderStatus.name) {
                        StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED_BY_SELLER),
                        StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED_BY_USER) -> {
                            binding.textTime.visibility = View.GONE
                            //binding.textTimeNew.visibility = View.GONE
                            binding.textStatusDesc.visibility = View.GONE
                            binding.viewCircle.setBackgroundResource(R.drawable.ic_cancel_status)
                            val width = binding.viewCircle.resources.getDimension(R.dimen.status_dot_small_size)
                            val params = binding.viewCircle.layoutParams
                            params.width = width.toInt()
                            params.height = width.toInt()
                            binding.viewCircle.layoutParams = params
                            binding.textStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX, binding.textStatus.context.resources.getDimension(R.dimen.status_small_size))
                            binding.textStatus.setTextColor(ContextCompat.getColor(binding.textStatus.context, R.color.shimmerColor))
                            binding.textTime.setTextColor(ContextCompat.getColor(binding.textStatus.context, R.color.shimmerColor))
                        }
                        else -> {
                            binding.textTime.visibility = View.GONE
                            //binding.textTimeNew.visibility = View.GONE
                            binding.textStatusDesc.visibility = View.GONE
                            binding.viewCircle.setBackgroundResource(R.drawable.ic_checked)
                            val width = binding.viewCircle.resources.getDimension(R.dimen.status_dot_small_size)
                            val params = binding.viewCircle.layoutParams
                            params.width = width.toInt()
                            params.height = width.toInt()
                            binding.viewCircle.layoutParams = params
                            binding.textStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX, binding.textStatus.context.resources.getDimension(R.dimen.status_small_size))
                            binding.textStatus.setTextColor(ContextCompat.getColor(binding.textStatus.context, R.color.shimmerColor))
                            binding.textTime.setTextColor(ContextCompat.getColor(binding.textStatus.context, R.color.shimmerColor))
                        }
                    }
                } else {
                    binding.textTime.visibility = View.GONE
                    //binding.textTimeNew.visibility = View.GONE
                    binding.textStatusDesc.visibility = View.GONE
                    binding.viewCircle.setBackgroundResource(R.drawable.bg_rounded_grey_stroke)
                    val width = binding.viewCircle.resources.getDimension(R.dimen.status_dot_small_size)
                    val params = binding.viewCircle.layoutParams
                    params.width = width.toInt()
                    params.height = width.toInt()
                    binding.viewCircle.layoutParams = params
                    binding.textStatus.setTextSize(TypedValue.COMPLEX_UNIT_PX, binding.textStatus.context.resources.getDimension(R.dimen.status_small_size))
                    binding.textStatus.setTextColor(ContextCompat.getColor(binding.textStatus.context, R.color.shimmerColor))
                }
            }
            binding.textStatus.text = orderStatus.name
            binding.textStatusDesc.text = StatusHelper.getMessageDetail(orderStatus.name)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: OrderStatus?, position: Int)
    }

}