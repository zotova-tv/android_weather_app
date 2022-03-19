package ru.gb.weather.view.details

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
        Log.d(TAG, "getItemCount() called size: " + forecastParts.size)
        return forecastParts.size
    }

    inner class DetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(forecastPart: ForecastPart) {
            Log.d(TAG, "bind() called with: forecastPart = $forecastPart")
            itemView.findViewById<TextView>(R.id.forecast_name).text = forecastPart.name.timeOfDay
            itemView.findViewById<TextView>(R.id.forecast_temp).text = forecastPart.getTemperatureString()
        }
    }

    companion object {
        const val TAG = "lalala DetailsFragmentAdapter"
    }
}