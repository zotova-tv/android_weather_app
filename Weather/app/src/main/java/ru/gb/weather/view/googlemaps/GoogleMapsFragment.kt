package ru.gb.weather.view.googlemaps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_google_maps_main.*
import ru.gb.weather.R
import ru.gb.weather.databinding.FragmentGoogleMapsMainBinding
import java.io.IOException

const val INITIAL_LATITUDE: Double = 55.75370903771494
const val INITIAL_LONGITUDE: Double = 37.61981338262558
const val LATITUDE_KEY = "LATITUDE"
const val LONGITUDE_KEY = "LONGITUDE"
const val ADDRESS_KEY = "ADDRESS"
const val EMPTY_STRING = ""
const val START_TXT = "Start"

class GoogleMapsFragment : Fragment() {

    private var _binding: FragmentGoogleMapsMainBinding? = null
    private val binding get() = _binding!!
    private var initialLocation: LatLng? = null
    private var initialAddress: String? = null

    private lateinit var map : GoogleMap
    private val markers : ArrayList<Marker> = arrayListOf()

    companion object {
        fun newInstance(lat: Double = 0.0, lon: Double = 0.0, address: String = ""): GoogleMapsFragment{
            val newInstance = GoogleMapsFragment().apply {
                arguments = Bundle().apply {
                    putDouble(LATITUDE_KEY, lat)
                    putDouble(LONGITUDE_KEY, lon)
                    putString(ADDRESS_KEY, address)
                }
            }
            return newInstance
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        map = googleMap
        val initialPlace = initialLocation ?: LatLng(INITIAL_LATITUDE, INITIAL_LONGITUDE)
        binding.searchAddress.setText(initialAddress ?: getString(R.string.default_address))

        googleMap.addMarker(MarkerOptions().position(initialPlace).title(START_TXT))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(initialPlace))

        googleMap.setOnMapClickListener { latLng ->
            getAddressAsync(latLng)
            addMarkerToArray(latLng)
            drawLine()
        }

        activateMyLocation(googleMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let { args ->
            val lat = args.getDouble(LATITUDE_KEY)
            val lon = args.getDouble(LONGITUDE_KEY)
            val address = args.getString(ADDRESS_KEY)
            if(lat != 0.0 && lon != 0.0 && address != EMPTY_STRING) {
                initialLocation = LatLng(lat, lon)
                initialAddress = address
            }
        }

        _binding = FragmentGoogleMapsMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        initSearchByAddress()
    }

    private fun initSearchByAddress() {
        binding.buttonSearch.setOnClickListener {
            val geoCoder = Geocoder(it.context)
            val searchText = searchAddress.text.toString()

            Thread {
                try {
                    val addresses = geoCoder.getFromLocationName(searchText, 1)
                    if (addresses.size > 0) {
                        val location = LatLng(
                            addresses[0].latitude,
                            addresses[0].longitude
                        )

                        it.post {
                            goToAddress(location, searchText)
                        }

                        //goToAddress(addresses, it, searchText)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun goToAddress(location : LatLng,
                            searchText: String) {
        setMarker(location, searchText, R.drawable.ic_map_marker)
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                location,
                15f
            )
        )
    }

    private fun goToAddress(addresses: MutableList<Address>,
                            view: View,
                            searchText: String) {
        val location = LatLng(
            addresses[0].latitude,
            addresses[0].longitude
        )

        view.post {
            setMarker(location, searchText, R.drawable.ic_map_marker)
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    15f
                )
            )
        }
    }

    private fun getAddressAsync(location: LatLng) {
        context?.let {
            val geoCoder = Geocoder(it)

            Thread {
                try {
                    val addresses =
                        geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    textAddress.post { textAddress.text = addresses[0].getAddressLine(0)}

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()

        }
    }

    private fun activateMyLocation(googleMap: GoogleMap) {
        context?.let {
            val isPermissionGranted =
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED

            googleMap.isMyLocationEnabled = isPermissionGranted
            googleMap.uiSettings.isMyLocationButtonEnabled = isPermissionGranted
        }
    }

    private fun addMarkerToArray(location: LatLng) {
        val marker = setMarker(location, markers.size.toString(), R.drawable.ic_map_pin)
        markers.add(marker)
    }

    private fun setMarker(location: LatLng, searchText: String, resourceId: Int) : Marker {
        return map.addMarker(MarkerOptions()
            .position(location)
            .title(searchText)
            .icon(BitmapDescriptorFactory.fromResource(resourceId)))
    }

    private fun drawLine() {
        val last: Int = markers.size - 1

        if (last >= 1) {
            var previous : LatLng = markers[last - 1].position
            var current : LatLng = markers[last].position

            map.addPolyline(PolylineOptions()
                .add(previous, current)
                .color(Color.RED)
                .width(5f))
        }
    }
}