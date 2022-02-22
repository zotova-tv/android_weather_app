import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.gb.weather.R
import ru.gb.weather.databinding.FragmentMainBinding
import ru.gb.weather.model.Weather
import ru.gb.weather.view.main.MainFragmentAdapter
import ru.gb.weather.viewmodel.AppState
import ru.gb.weather.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private val adapter = MainFragmentAdapter(object : OnItemViewClickListener {
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
    })
    private var isDataSetRus: Boolean = true

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
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer { renderData(it) })
        viewModel.getWeatherFromLocalSourceRus()
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
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.mainFragmentLoadingLayout.hide()
                adapter.setWeather(appState.weatherData)
            }
            is AppState.Loading -> {
                binding.mainFragmentLoadingLayout.show()
            }
            is AppState.Error -> {
                binding.mainFragmentLoadingLayout.hide()
                binding.mainFragmentRootView.showActionSnackBar(
                    R.string.error,
                    R.string.reload,
                    { viewModel.getWeatherFromLocalSourceRus() }
                )
            }
        }
    }

    private fun View.showActionSnackBar(
        resourceText: Int,
        resourceActionText: Int,
        action: (View) -> Unit,
        length: Int = Snackbar.LENGTH_INDEFINITE
    ) {
        Snackbar.make(this, getString(resourceText), length).setAction(getString(resourceActionText), action).show()
    }

    private fun View.showSnackBar(
        resourceText: Int,
        length: Int = Snackbar.LENGTH_SHORT
    ) {
        Snackbar.make(this, getString(resourceText), length).show()
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

    interface OnItemViewClickListener {
        fun onItemViewClick(weather: Weather)
    }

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onDestroy() {
        adapter.removeListener()
        super.onDestroy()
    }
}
