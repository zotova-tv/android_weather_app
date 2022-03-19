import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.gb.weather.R
import ru.gb.weather.databinding.FragmentDetailsBinding
import ru.gb.weather.model.Weather
import ru.gb.weather.view.details.DetailsFragmentAdapter
import java.text.SimpleDateFormat
import java.util.*

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val adapter = DetailsFragmentAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.detailsFragmentRecyclerView.adapter = adapter
        arguments?.getParcelable<Weather>(BUNDLE_EXTRA)?.let{weather ->
            weather.city.let { city ->
                binding.cityName.text = city.city
                binding.cityCoordinates.text = String.format(
                    getString(R.string.city_coordinates),
                    city.lat.toString(),
                    city.lon.toString()
                )
                binding.temperatureValue.text = weather.getTemperatureString()
                binding.feelsLikeValue.text = weather.getFeelsLikeString()
                binding.date.text = weather.date.getFormattedDate()
            }
            adapter.setForecastParts(weather.forecastParts)
        }
    }

    fun Date.getFormattedDate(format: String = "dd MMMM YYYY"): String = SimpleDateFormat(format, Locale.getDefault()).format(this)

    companion object {

        const val BUNDLE_EXTRA = "weather"

        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
