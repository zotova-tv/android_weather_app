package ru.gb.weather.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import ru.gb.weather.R
import ru.gb.weather.model.Weather
import ru.gb.weather.view.googlemaps.GoogleMapsFragment

class MainFragmentAdapter(
    private var onItemViewClickListener: MainFragment.OnItemViewClickListener?,
    private var onItemViewLongClickListener: MainFragment.OnItemViewLongClickListener?
) :
    RecyclerView.Adapter<MainFragmentAdapter.MainViewHolder>() {

    private var weatherData: List<Weather> = listOf()

    fun setWeather(data: List<Weather>) {
        weatherData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainViewHolder {
        return MainViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_main_recycler_item, parent, false) as View
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(weatherData[position])
    }

    override fun getItemCount(): Int {
        return weatherData.size
    }

    inner class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(weather: Weather) {
            itemView.apply {
                findViewById<TextView>(R.id.mainFragmentRecyclerItemTextView).text = weather.city.city
                setOnClickListener {onItemViewClickListener?.onItemViewClick(weather)}
                setOnLongClickListener{onItemViewLongClickListener?.onItemViewLongClick(this, weather) ?: true}
            }
        }
    }

    fun removeClickListener() {
        onItemViewClickListener = null
    }

    fun removeLongClickListener() {
        onItemViewClickListener = null
    }
}
