package com.food.ordering.zinger.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.model.Shop
import com.food.ordering.zinger.databinding.ItemShopBinding
import com.food.ordering.zinger.ui.home.ShopAdapter.ShopViewHolder
import com.squareup.picasso.Picasso

class ShopAdapter(private val context: Context, private val shopList: List<Shop>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ShopViewHolder>() {
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
        fun bind(shop: Shop, position: Int, listener: OnItemClickListener) {
            Picasso.get().load(shop.imageUrl).into(binding.imageShop)
            binding.textShopName.text = shop.name
            binding.textShopDesc.text = shop.desc
            binding.textShopRating.text = shop.rating
            binding.layoutRoot.setOnClickListener { listener.onItemClick(shop, position) }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(item: Shop?, position: Int)
    }

}