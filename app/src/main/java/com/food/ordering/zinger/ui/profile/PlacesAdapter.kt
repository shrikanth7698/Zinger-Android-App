package com.food.ordering.zinger.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.model.PlaceModel
import com.food.ordering.zinger.databinding.ItemCampusBinding
import com.squareup.picasso.Picasso

class PlacesAdapter(private val context: Context, private val places: List<PlaceModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PlaceViewHolder {
        val binding: ItemCampusBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_campus, parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(places[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    class PlaceViewHolder(var binding: ItemCampusBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place: PlaceModel, position: Int, listener: OnItemClickListener) {
            Picasso.get().load(place.iconUrl).into(binding.imageCampus)
            binding.textCampusName.text = place.name
            binding.textCampusAddress.text = place.address
            binding.layoutRoot.setOnClickListener { listener.onItemClick(place, position) }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(item: PlaceModel, position: Int)
    }

}