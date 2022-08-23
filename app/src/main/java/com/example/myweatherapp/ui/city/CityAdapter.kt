package com.example.myweatherapp.ui.city

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapp.R
import com.example.myweatherapp.databinding.CityItemBinding
import com.example.myweatherapp.model.domain.Weather

class CityAdapter : RecyclerView.Adapter<CityAdapter.Holder>() {

    private val list = arrayListOf<Weather>()

    var onItemClick: ((Weather) -> Unit)? = null

    internal fun setCitiesList(list: List<Weather>) {
        if (this.list.isNotEmpty()) {
            this.list.clear()
        }
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<CityItemBinding>(inflater, R.layout.city_item, parent, false)

        return Holder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(list[position])
        }
    }

    inner class Holder(var binding: CityItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Weather) = binding.apply {
            weather = item
            weather?.let {
                textViewCityName.text = it.cityName
            }

        }
    }
}
