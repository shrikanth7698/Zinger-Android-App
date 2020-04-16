package com.food.ordering.zinger.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.model.MenuItem
import com.food.ordering.zinger.databinding.ItemSearchBinding
import com.food.ordering.zinger.databinding.ItemShopBinding
import com.squareup.picasso.Picasso

class SearchAdapter(private val menuList: List<MenuItem>, private val listener: OnItemClickListener) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): SearchViewHolder {
        val binding: ItemSearchBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_search, parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(menuList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    class SearchViewHolder(var binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(menuItem: MenuItem, position: Int, listener: OnItemClickListener) {
            Picasso.get().load(menuItem.photoUrl).into(binding.imageShop)
            binding.textShopName.text = menuItem.name
            binding.textShopDesc.text = "Dish"
            binding.layoutRoot.setOnClickListener { listener.onItemClick(menuItem, position) }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(item: MenuItem?, position: Int)
    }

}