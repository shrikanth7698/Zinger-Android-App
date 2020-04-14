package com.food.ordering.zinger.ui.signup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.model.Campus
import com.food.ordering.zinger.data.model.Shop
import com.food.ordering.zinger.databinding.ItemCampusBinding
import com.food.ordering.zinger.databinding.ItemShopBinding
import com.squareup.picasso.Picasso

class CampusAdapter(private val context: Context, private val campusList: List<Campus>, private val listener: OnItemClickListener) : RecyclerView.Adapter<CampusAdapter.CampusViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CampusViewHolder {
        val binding: ItemCampusBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_campus, parent, false)
        return CampusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CampusViewHolder, position: Int) {
        holder.bind(campusList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return campusList.size
    }

    class CampusViewHolder(var binding: ItemCampusBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(campus: Campus, position: Int, listener: OnItemClickListener) {
            Picasso.get().load(campus.imageUrl).into(binding.imageCampus)
            binding.textCampusName.text = campus.name

            binding.layoutRoot.setOnClickListener { listener.onItemClick(campus, position) }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(item: Campus?, position: Int)
    }

}