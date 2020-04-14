package com.food.ordering.zinger.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.model.Shop
import com.food.ordering.zinger.databinding.ItemShopBinding
import com.squareup.picasso.Picasso

class SearchAdapter(private val context: Context, private val shopList: List<Shop>, private val listener: OnItemClickListener) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): SearchViewHolder {
        val binding: ItemShopBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_shop, parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(shopList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return shopList.size
    }

    class SearchViewHolder(var binding: ItemShopBinding) : RecyclerView.ViewHolder(binding.root) {
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