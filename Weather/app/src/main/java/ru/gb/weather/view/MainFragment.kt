package ru.gb.weather.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import ru.gb.weather.R
import ru.gb.weather.databinding.MainFragmentBinding
import ru.gb.weather.model.Weather
import ru.gb.weather.viewmodel.AppState
import ru.gb.weather.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val observer = Observer<AppState> {
            renderData(it)
        }

        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        viewModel.getWeatherFromLocalSource()

        binding.refresh.setOnClickListener { viewModel.getWeatherFromLocalSource() }
    }

    private fun renderData(appState: AppState) {
        when(appState) {
            is AppState.Success -> {
                val weatherData = appState.weatherData
                binding.loadingLayout.visibility = View.GONE
                Snackbar.make(binding.mainView, "Success", Snackbar.LENGTH_LONG).show()

                setData(weatherData)
            }
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE

                Snackbar
                    .make(binding.mainView, getString(R.string.error), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.reload)) {
                        viewModel.getWeatherFromLocalSource()
                    }
                    .show()
            }

        }
    }

    private fun setData(weatherData: Weather) {
         binding.cityName.text = weatherData.city.city
         binding.cityCoordinates.text = String.format(
             getString(R.string.city_coordinates),
             weatherData.city.lat.toString(),
             weatherData.city.lon.toString()
         )
         binding.temperatureValue.text = weatherData.temperature.toString()
         binding.feelsLikeValue.text = weatherData.feelsLike.toString()
    }
}