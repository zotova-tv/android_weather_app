import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import ru.gb.weather.R
import ru.gb.weather.databinding.FragmentMainBinding
import ru.gb.weather.model.City
import ru.gb.weather.model.Weather
import ru.gb.weather.utils.hide
import ru.gb.weather.utils.show
import ru.gb.weather.utils.showActionSnackBar
import ru.gb.weather.view.googlemaps.EMPTY_STRING
import ru.gb.weather.view.googlemaps.GoogleMapsFragment
import ru.gb.weather.view.main.MainFragmentAdapter
import ru.gb.weather.viewmodel.AppState
import ru.gb.weather.viewmodel.MainViewModel
import java.io.IOException

private const val IS_WORLD_KEY = "IS_WORLD_KEY"
private const val REQUEST_CODE = 12345
private const val REFRESH_PERIOD = 60000L
private const val MINIMAL_DISTANCE = 100f
private const val GOOGLE_MAPS_FRAGMENT = "GOOGLE_MAPS_FRAGMENT"

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private val adapter = MainFragmentAdapter(
        object : OnItemViewClickListener {
            override fun onItemViewClick(weather: Weather) {
                activity?.supportFragmentManager?.apply{
                    beginTransaction()
                        .add(R.id.container, DetailsFragment.newInstance(
                            Bundle().apply {putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)}
                        ))
                        .addToBackStack("")
                        .commitAllowingStateLoss()
                }
            }
        },
        object : OnItemViewLongClickListener {
            override fun onItemViewLongClick(view: View, weather: Weather): Boolean {
                context?.let {
                    PopupMenu(it, view.findViewById(R.id.mainFragmentRecyclerItemTextView)).apply {
                        inflate(R.menu.city_item_context_menu)
                        setOnMenuItemClickListener {menuItem ->
                            when(menuItem.itemId){
                                R.id.menu_show_on_gmaps -> {
                                    addFragment(
                                        GoogleMapsFragment.newInstance(weather.city.lat, weather.city.lon, weather.city.city),
                                        GOOGLE_MAPS_FRAGMENT
                                    )
                                    true
                                }
                                else -> false
                            }

                        }
                        show()
                    }
                }
                return true
            }
        }
    )
    private var isDataSetRus: Boolean = true

    private val onLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            context?.let {
                getAddressAsync(it, location)
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            super.onStatusChanged(provider, status, extras)
        }

        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
        }

        override fun onProviderDisabled(provider: String) {
            super.onProviderDisabled(provider)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainFragmentRecyclerView.adapter = adapter
        binding.mainFragmentFAB.setOnClickListener { changeWeatherDataSet() }

        binding.mainFragmentFABLocation.setOnClickListener { checkPermission() }

        viewModel.getLiveData().observe(viewLifecycleOwner, Observer { renderData(it) })
        showListOfTowns()
    }

    private fun checkPermission() {
        activity?.let {
            when {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    getLocation()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    showRationaleDialog()
                }
                else -> {
                    requestPermission()
                }
            }
        }
    }

    private fun checkPermissionsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                var grantedPermissions = 0

                if ((grantResults.isNotEmpty())) {
                    for (i in grantResults) {
                        if (i == PackageManager.PERMISSION_GRANTED) {
                            grantedPermissions++
                        }

                        if (grantResults.size == grantedPermissions) {
                            getLocation()
                        } else {
                            showDialog(
                                getString(R.string.dialog_title_no_gps),
                                getString(R.string.dialog_message_no_gps)
                            )
                        }
                    }
                } else {
                    showDialog(
                        getString(R.string.dialog_title_no_gps),
                        getString(R.string.dialog_message_no_gps)
                    )
                }

                return
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        checkPermissionsResult(requestCode, grantResults)
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    private fun showListOfTowns() {
        activity?.let {
            if(it.getPreferences(Context.MODE_PRIVATE).getBoolean(IS_WORLD_KEY, false))
                changeWeatherDataSet() else viewModel.getWeatherFromLocalSourceRus()
        }
    }

    private fun saveListOfTowns() {
        activity?.let {
            with(it.getPreferences(Context.MODE_PRIVATE).edit()) {
                putBoolean(IS_WORLD_KEY, !isDataSetRus)
                apply()
            }
        }
    }

    private fun changeWeatherDataSet() {
        if (isDataSetRus) {
            viewModel.getWeatherFromLocalSourceWorld()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        } else {
            viewModel.getWeatherFromLocalSourceRus()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        }.also {
            isDataSetRus = !isDataSetRus
        }
        saveListOfTowns()
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.includedLoadingLayout.loadingLayout.hide()
                adapter.setWeather(appState.weatherData)
            }
            is AppState.Loading -> {
                binding.includedLoadingLayout.loadingLayout.show()
            }
            is AppState.Error -> {
                binding.includedLoadingLayout.loadingLayout.hide()
                binding.mainFragmentRootView.showActionSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    { viewModel.getWeatherFromLocalSourceRus() }
                )
            }
        }
    }

    private fun showDialog(title: String, message: String) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun showRationaleDialog() {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_rationale_title))
                .setMessage(getString(R.string.dialog_rationale_meaasge))
                .setPositiveButton(getString(R.string.dialog_rationale_give_access)) { _, _ ->
                    requestPermission()
                }
                .setNegativeButton(getString(R.string.dialog_rationale_decline)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun getLocation() {
        activity?.let { context ->
            binding.includedLoadingLayout.loadingLayout.show()
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val provider = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    provider?.let {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            REFRESH_PERIOD,
                            MINIMAL_DISTANCE,
                            onLocationListener
                        )
                        locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            REFRESH_PERIOD,
                            MINIMAL_DISTANCE,
                            onLocationListener
                        )
                    }
                } else {
                    var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (location == null) {
                        showDialog(
                            getString(R.string.dialog_title_gps_turned_off),
                            getString(R.string.dialog_message_last_location_unknown)
                        )
                        binding.includedLoadingLayout.loadingLayout.hide()
                    } else {
                        getAddressAsync(context, location)
                        showDialog(
                            getString(R.string.dialog_title_gps_turned_off),
                            getString(R.string.dialog_message_last_known_location)
                        )
                    }
                }
            } else {
                showRationaleDialog()
                binding.includedLoadingLayout.loadingLayout.hide()
            }
        }
    }

    private fun getAddressAsync( context: Context, location: Location) {
        val geoCoder = Geocoder(context)

        Thread {
            try {
                val addresses = geoCoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )

                binding.mainFragmentFAB.post {
                    showAddressDialog(addresses[0].getAddressLine(0), location)
                    binding.includedLoadingLayout.loadingLayout.hide()
                }

            } catch (e: IOException) {
                e.printStackTrace()
                binding.includedLoadingLayout.loadingLayout.hide()
            }
        }.start()
    }

    private fun showAddressDialog(address: String, location: Location) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_address_title))
                .setMessage(address)
                .setPositiveButton(getString(R.string.dialog_address_get_weather)) { _, _ ->
                    openDetailsFragment(
                        Weather(
                            City(
                                address,
                                location.latitude,
                                location.longitude
                            )
                        )
                    )
                }
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun openDetailsFragment(weather: Weather) {
        activity?.supportFragmentManager?.apply {
            beginTransaction()
                .add(
                    R.id.container,
                    DetailsFragment.newInstance(Bundle().apply {
                        putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                    })
                )
                .addToBackStack("")
                .commitAllowingStateLoss()
        }
    }

    private fun addFragment(fragment: Fragment, backStackName: String = "") {
        activity?.supportFragmentManager?.apply {
            beginTransaction()
                .add(
                    R.id.container,
                    fragment
                )
                .addToBackStack(backStackName)
                .commitAllowingStateLoss()
        }
    }

    interface OnItemViewClickListener {
        fun onItemViewClick(weather: Weather)
    }

    interface OnItemViewLongClickListener {
        fun onItemViewLongClick(view: View, weather: Weather): Boolean
    }

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onDestroy() {
        adapter.removeClickListener()
        adapter.removeLongClickListener()
        super.onDestroy()
    }
}
