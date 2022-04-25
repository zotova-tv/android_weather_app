package ru.gb.weather.view.history

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.gb.weather.R
import ru.gb.weather.databinding.FragmentHistoryBinding
import ru.gb.weather.utils.hide
import ru.gb.weather.utils.show
import ru.gb.weather.utils.showActionSnackBar
import ru.gb.weather.viewmodel.AppState
import ru.gb.weather.viewmodel.HistoryViewModel

const val DATE_FROM_KEY = "DATE_FROM"
const val DATE_TO_KEY = "DATE_TO"


class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private var dateFrom: Long = 0
    private var dateTo: Long = 0

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
        setHasOptionsMenu(true);
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.historyFragmentRecyclerview.adapter = adapter
        viewModel.historyLiveData.observe(viewLifecycleOwner, Observer { renderData(it) })
        arguments?.let {args ->
            dateFrom = args.getLong(DATE_FROM_KEY)
            dateTo = args.getLong(DATE_TO_KEY)
        }
        if(dateFrom != 0.toLong() && dateTo != 0.toLong()){
            viewModel.getHistoryListByDatesInterval(dateFrom, dateTo)
        }else{
            viewModel.getAllHistory()
        }
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
        fun newInstance(dateFrom: Long? = null, dateTo: Long? = null): HistoryFragment {
            val historyFragment = HistoryFragment()
            val bundle = Bundle()
            dateFrom?.let {
                bundle.putLong(DATE_FROM_KEY, it)
            }
            dateTo?.let {
                bundle.putLong(DATE_TO_KEY, it)
            }

            historyFragment.arguments = bundle
            return historyFragment
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.menu_note)?.setVisible(false)
        menu.findItem(R.id.menu_history)?.setVisible(false)
        super.onCreateOptionsMenu(menu, inflater)
    }
}