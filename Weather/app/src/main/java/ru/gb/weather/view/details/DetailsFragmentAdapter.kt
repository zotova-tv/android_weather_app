package ru.gb.weather.view.details

import ICON_EXT
import ICON_PATH
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import ru.gb.weather.R
import ru.gb.weather.model.ForecastPart

class DetailsFragmentAdapter :
    RecyclerView.Adapter<DetailsFragmentAdapter.DetailsViewHolder>() {

    private var forecastParts: List<ForecastPart> = listOf()

    fun setForecastParts(data: List<ForecastPart>) {
        forecastParts = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailsViewHolder {
        return DetailsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_details_forecast_recycler_item, parent, false) as View
        )
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        holder.bind(forecastParts[position])
    }

    override fun getItemCount(): Int {
        return forecastParts.size
    }

    inner class DetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(forecastPart: ForecastPart) {
            itemView.findViewById<TextView>(R.id.forecast_name).text = forecastPart.name.timeOfDay
            itemView.findViewById<TextView>(R.id.forecast_temp).text = forecastPart.getTemperatureString()
            val iconView: AppCompatImageView = itemView.findViewById<AppCompatImageView>(R.id.forecast_icon)
            forecastPart.icon?.let {
                val svgImageLoader = ImageLoader.Builder(iconView.context)
                    .components {
                        add(SvgDecoder.Factory())
                    }
                    .build()
                iconView.load(ICON_PATH + it + ICON_EXT, svgImageLoader)
            }
        }
    }

    companion object {
        const val TAG = "DetailsFragmentAdapter TAG"
    }
}