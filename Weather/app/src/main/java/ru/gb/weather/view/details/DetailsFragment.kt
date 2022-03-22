import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import com.squareup.picasso.Picasso
import ru.gb.weather.R
import ru.gb.weather.databinding.FragmentDetailsBinding
import ru.gb.weather.model.*
import ru.gb.weather.utils.*
import ru.gb.weather.view.history.AddNoteFragment
import ru.gb.weather.view.details.*
import ru.gb.weather.viewmodel.AppState
import ru.gb.weather.viewmodel.DetailsViewModel

const val ICON_PATH = "https://yastatic.net/weather/i/icons/funky/dark/"
const val ICON_EXT = ".svg"
const val ADD_NOTE_FRAGMENT_KEY = "ADD_NOTE_FRAGMENT"

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val adapter = DetailsFragmentAdapter()

    private lateinit var weatherBundle: Weather

    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this)[DetailsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true);
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherBundle = arguments?.getParcelable<Weather>(BUNDLE_EXTRA) ?: Weather()
        viewModel.detailsLiveData.observe(viewLifecycleOwner, Observer { renderData(it) })
        requestWeather()
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.mainView.show()
                binding.includedLoadingLayout.loadingLayout.hide()
                setWeather(appState.weatherData[0])
            }
            is AppState.Loading -> {
                binding.mainView.hide()
                binding.includedLoadingLayout.loadingLayout.show()
            }
            is AppState.Error -> {
                binding.mainView.show()
                binding.includedLoadingLayout.loadingLayout.hide()
                binding.mainView.showActionSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    {
                        requestWeather()
                    })
            }
        }
    }

    private fun requestWeather() {
        viewModel.getWeatherFromRemoteSource(weatherBundle.city.lat, weatherBundle.city.lon)
    }

    private fun setWeather(weather: Weather) {
        val city = weatherBundle.city

        saveCity(city, weather)

        binding.cityName.text = city.city
        binding.cityCoordinates.text = String.format(
            getString(R.string.city_coordinates),
            city.lat.toString(),
            city.lon.toString()
        )
        binding.temperatureValue.text = weather.getTemperatureString()
        binding.feelsLikeValue.text = weather.getFeelsLikeString()
        binding.weatherCondition.text = weather.condition

        weather.icon?.let {
            val svgImageLoader = ImageLoader.Builder(requireContext())
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()
            binding.conditionIcon.load(ICON_PATH + it + ICON_EXT, svgImageLoader)
        }

        with(adapter) {
            setForecastParts(weather.forecastParts)
            binding.detailsFragmentRecyclerView.adapter = this
        }

        Picasso
            .get()
            .load("https://freepngimg.com/thumb/city/36275-3-city-hd.png")
            .into(binding.headerIcon)
    }

    private fun saveCity(
        city: City,
        weather: Weather
    ) {
        viewModel.saveCityToDB(Weather(
            city,
            weather.temperature,
            weather.feelsLike,
            weather.condition)
        )
    }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.menu_note -> {
                this.parentFragmentManager.apply {
                    beginTransaction()
                        .add(R.id.container, AddNoteFragment.newInstance(weatherBundle))
                        .addToBackStack(ADD_NOTE_FRAGMENT_KEY)
                        .commitAllowingStateLoss()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
