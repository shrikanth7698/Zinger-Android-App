package com.food.ordering.zinger.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.model.MenuItemModel
import com.food.ordering.zinger.databinding.ItemSearchBinding
import com.squareup.picasso.Picasso

class SearchAdapter(private val menuList: List<MenuItemModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
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
        fun bind(menuItem: MenuItemModel, position: Int, listener: OnItemClickListener) {
            if(menuItem.isDish){
                binding.textShopName.text = menuItem.name
                binding.textShopDesc.text = menuItem.shopModel?.name
                Picasso.get().load(menuItem.photoUrl).placeholder(R.drawable.ic_food).into(binding.imageShop)
            }else{
                binding.textShopName.text = menuItem.shopModel?.name
                binding.textShopDesc.text = "Restaurant"
                Picasso.get().load(menuItem.photoUrl).placeholder(R.drawable.ic_shop).into(binding.imageShop)
            }
            binding.layoutRoot.setOnClickListener { listener.onItemClick(menuItem, position) }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(item: MenuItemModel, position: Int)
    }

}