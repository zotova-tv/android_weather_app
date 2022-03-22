package ru.gb.weather.view.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.gb.weather.view.history.HistoryAdapter
import ru.gb.weather.R
import ru.gb.weather.databinding.FragmentHistoryBinding
import ru.gb.weather.utils.hide
import ru.gb.weather.utils.show
import ru.gb.weather.utils.showActionSnackBar
import ru.gb.weather.viewmodel.AppState
import ru.gb.weather.viewmodel.HistoryViewModel

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by lazy {
        ViewModelProvider(this)[HistoryViewModel::class.java]
    }

    private val adapter : HistoryAdapter by lazy {
        HistoryAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.historyFragmentRecyclerview.adapter = adapter
        viewModel.historyLiveData.observe(viewLifecycleOwner, Observer { renderData(it) })
        viewModel.getAllHistory()
    }

    private fun renderData(appState: AppState) {
        when(appState) {
            is AppState.Success -> {
                with(binding) {
                    historyFragmentRecyclerview.show()
                    includedLoadingLayout.loadingLayout.hide()
                }
                adapter.setData(appState.weatherData)
            }

            is AppState.Loading -> {
                with(binding) {
                    historyFragmentRecyclerview.hide()
                    includedLoadingLayout.loadingLayout.show()
                }
            }
            is AppState.Error -> {
                with(binding) {
                    historyFragmentRecyclerview.show()
                    includedLoadingLayout.loadingLayout.hide()

                    historyFragmentRecyclerview.showActionSnackBar(getString(R.string.error),
                        getString(R.string.reload),
                        {
                            viewModel.getAllHistory()
                        })
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HistoryFragment()
    }
}