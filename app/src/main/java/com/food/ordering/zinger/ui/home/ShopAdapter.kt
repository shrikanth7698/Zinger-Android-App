package com.food.ordering.zinger.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.model.Shop
import com.food.ordering.zinger.data.model.ShopsResponseData
import com.food.ordering.zinger.databinding.ItemShopBinding
import com.food.ordering.zinger.ui.home.ShopAdapter.ShopViewHolder
import com.squareup.picasso.Picasso
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ShopAdapter(private val context: Context, private val shopList: List<ShopsResponseData>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ShopViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ShopViewHolder {
        val binding: ItemShopBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_shop, parent, false)
        return ShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind(shopList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return shopList.size
    }

    class ShopViewHolder(var binding: ItemShopBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(shop: ShopsResponseData, position: Int, listener: OnItemClickListener) {
            Picasso.get().load(shop.shopModel.photoUrl).into(binding.imageShop)
            binding.textShopName.text = shop.shopModel.name
            if(shop.configurationModel.isOrderTaken==1){
                if(shop.configurationModel.isDeliveryAvailable==1){
                    binding.textShopDesc.text = "Closes at "+shop.shopModel.closingTime.substring(0,5)
                }else{
                    binding.textShopDesc.text = "Closes at "+shop.shopModel.closingTime.substring(0,5)+" (Delivery not available)"
                }
            }else{
                binding.textShopDesc.text = "Closed Now"
            }
            if(shop.ratingModel.rating==0.0){
                binding.textShopRating.text = "No ratings yet"
            }else {
                binding.textShopRating.text = shop.ratingModel.rating.toString()+" ("+shop.ratingModel.userCount+")"
            }
            binding.layoutRoot.setOnClickListener { listener.onItemClick(shop, position) }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(item: ShopsResponseData, position: Int)
    }

}