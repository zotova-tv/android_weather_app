import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import ru.gb.weather.R
import ru.gb.weather.databinding.FragmentDetailsBinding
import ru.gb.weather.model.*
import ru.gb.weather.view.details.*
import java.text.SimpleDateFormat
import java.util.*

const val DETAILS_INTENT_FILTER = "DETAILS INTENT FILTER"
const val DETAILS_LOAD_RESULT_EXTRA = "LOAD RESULT"
const val DETAILS_INTENT_EMPTY_EXTRA = "INTENT IS EMPTY"
const val DETAILS_DATA_EMPTY_EXTRA = "DATA IS EMPTY"
const val DETAILS_RESPONSE_EMPTY_EXTRA = "RESPONSE IS EMPTY"
const val DETAILS_REQUEST_ERROR_EXTRA = "REQUEST ERROR"
const val DETAILS_REQUEST_ERROR_MESSAGE_EXTRA = "REQUEST ERROR MESSAGE"
const val DETAILS_URL_MALFORMED_EXTRA = "URL MALFORMED"
const val DETAILS_RESPONSE_SUCCESS_EXTRA = "RESPONSE SUCCESS"
const val DETAILS_CONDITION_EXTRA = "CONDITION"
private const val TEMP_INVALID = -100
private const val FEELS_LIKE_INVALID = -100
private const val PROCESS_ERROR = "Обработка ошибки"

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val adapter = DetailsFragmentAdapter()

    private lateinit var weatherBundle: Weather

    private val loadResultsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra(DETAILS_LOAD_RESULT_EXTRA)) {
                DETAILS_INTENT_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_DATA_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_RESPONSE_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_REQUEST_ERROR_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_REQUEST_ERROR_MESSAGE_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_URL_MALFORMED_EXTRA -> TODO(PROCESS_ERROR)

                DETAILS_RESPONSE_SUCCESS_EXTRA -> {
                    val weatherDTO = intent.getParcelableExtra(
                        WEATHER_EXTRA
                    ) ?: WeatherDTO(
                        FactDTO(
                            TEMP_INVALID,
                            FEELS_LIKE_INVALID,
                            DETAILS_CONDITION_EXTRA
                        ),
                        null
                    )
                    displayWeather(weatherDTO)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context?.let {
            LocalBroadcastManager.getInstance(it).apply {
                registerReceiver(loadResultsReceiver, IntentFilter(DETAILS_INTENT_FILTER))
            }

        }
    }

    override fun onDestroy() {
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(loadResultsReceiver)
        }
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherBundle = arguments?.getParcelable<Weather>(BUNDLE_EXTRA) ?: Weather()
        getWeather()
    }

    private fun getWeather() {
        with(binding){
            mainView.hide()
            loadingLayout.show()
        }

        context?.let {
            it.startService(Intent(it, DetailsService::class.java).apply {
                putExtra(LATITUDE_EXTRA,  weatherBundle.city.lat)
                putExtra(LONGITUDE_EXTRA, weatherBundle.city.lon)
            })
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
                    lat.toString(),
                    lon.toString()
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
