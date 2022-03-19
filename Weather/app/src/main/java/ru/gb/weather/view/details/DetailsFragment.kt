import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import ru.gb.kotlinapp.view.details.WeatherLoader
import ru.gb.weather.R
import ru.gb.weather.databinding.FragmentDetailsBinding
import ru.gb.weather.model.*
import ru.gb.weather.view.details.DetailsFragmentAdapter
import java.net.MalformedURLException
import java.text.SimpleDateFormat
import java.util.*

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val adapter = DetailsFragmentAdapter()

    private lateinit var loader: WeatherLoader
    private lateinit var weatherBundle: Weather

    private val onLoadListener: WeatherLoader.WeatherLoaderListener =
        object : WeatherLoader.WeatherLoaderListener {

            override fun onLoaded(weatherDTO: WeatherDTO) {
                displayWeather(weatherDTO)
            }

            override fun onFailed(throwable: Throwable) {
                Log.d(TAG, "onFailed() called with: throwable = $throwable")
                with(binding){
                    mainView.show()
                    loadingLayout.hide()
                }
                when (throwable) {
                    is MalformedURLException -> binding.detailFragmentRootView.showSnackBar(
                        R.string.sorry_error
                    )
                    else -> binding.detailFragmentRootView.showActionSnackBar(
                        R.string.connection_error,
                        R.string.reload,
                        { loader.loadWeather() }
                    )
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherBundle = arguments?.getParcelable<Weather>(BUNDLE_EXTRA) ?: Weather()
        with(binding){
            mainView.hide()
            loadingLayout.show()
        }

        loader = WeatherLoader(onLoadListener, weatherBundle.city.lat, weatherBundle.city.lon).also{
            it.loadWeather()
        }
    }

    private fun displayWeather(weatherDTO: WeatherDTO) {
        with (binding) {
            mainView.show()
            loadingLayout.hide()

            with(weatherBundle.city) {
                cityName.text = this.city
                cityCoordinates.text = String.format(
                    getString(R.string.city_coordinates),
                    this.lat.toString(),
                    this.lon.toString()
                )
            }

            weatherDTO.fact?.let { factDto ->
                weatherCondition.text = factDto.condition
                temperatureValue.text = WeatherUtils.getTemperatureString(factDto.temp ?: 0)
                feelsLikeValue.text = WeatherUtils.getTemperatureString(factDto.feels_like ?: 0)
            }

            weatherDTO.forecast?.parts?.let {
                val forecastParts: MutableList<ForecastPart> = mutableListOf()
                it.map { forecastPartDTO ->
                    val forecastName: ForecastName? = when (forecastPartDTO.part_name) {
                        ForecastName.Morning.timeOfDayCode -> ForecastName.Morning
                        ForecastName.Evening.timeOfDayCode -> ForecastName.Evening
                        ForecastName.Day.timeOfDayCode -> ForecastName.Day
                        ForecastName.Night.timeOfDayCode -> ForecastName.Night
                        else -> null
                    }

                    forecastName?.let {forecastName ->
                        forecastParts.add(
                            ForecastPart(forecastName, temperature = forecastPartDTO.temp_avg ?: 0)
                        )
                    }

                }
                with(adapter) {
                    setForecastParts(forecastParts)
                    binding.detailsFragmentRecyclerView.adapter = this
                }
            }
        }
    }

    fun Date.getFormattedDate(format: String = "dd MMMM YYYY"): String = SimpleDateFormat(format, Locale.getDefault()).format(this)

    companion object {

        const val BUNDLE_EXTRA = "weather"
        const val TAG = "lalala DetailsFragment"

        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment().apply {
                arguments = bundle
            }
            return fragment
        }
    }

    private fun View.show(): View {
        if(visibility != View.VISIBLE){
            visibility = View.VISIBLE
        }
        return this
    }

    private fun View.hide(): View {
        if(visibility != View.GONE){
            visibility = View.GONE
        }
        return this
    }

    private fun View.showSnackBar(
        resourceText: Int,
        length: Int = Snackbar.LENGTH_SHORT
    ) {
        Snackbar.make(this, getString(resourceText), length).show()
    }

    private fun View.showActionSnackBar(
        resourceText: Int,
        resourceActionText: Int,
        action: (View) -> Unit,
        length: Int = Snackbar.LENGTH_INDEFINITE
    ) {
        Snackbar.make(this, getString(resourceText), length).setAction(getString(resourceActionText), action).show()
    }
}
