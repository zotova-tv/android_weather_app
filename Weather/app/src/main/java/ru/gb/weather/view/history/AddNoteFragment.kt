package ru.gb.weather.view.history

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.gb.weather.R
import ru.gb.weather.databinding.FragmentAddNoteBinding
import ru.gb.weather.databinding.FragmentDetailsBinding
import ru.gb.weather.model.Note
import ru.gb.weather.model.Weather
import ru.gb.weather.utils.hide
import ru.gb.weather.utils.show
import ru.gb.weather.utils.showActionSnackBar
import ru.gb.weather.utils.showSnackBar
import ru.gb.weather.viewmodel.AddNoteViewModel
import ru.gb.weather.viewmodel.AppState
import ru.gb.weather.viewmodel.DetailsViewModel

const val WEATHER_KEY = "WEATHER"
const val EMPTY_STRING = ""
const val COMMENT_TEXT_IS_REQUIRED_MSG = "Comment text is required"
const val SAVED_SUCCESSFULLY_MSG = "Saved successfully"
const val ERROR_MSG = "Error"


class AddNoteFragment : Fragment() {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!
    private var weatherBundle: Weather? = null
    private val viewModel: AddNoteViewModel by lazy {
        ViewModelProvider(this)[AddNoteViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
        arguments?.let {
            weatherBundle = it.getParcelable(WEATHER_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.noteLiveData.observe(viewLifecycleOwner, Observer { renderData(it) })
        binding.saveNote.setOnClickListener {
            activity?.let {notNullActivity ->
                val inputMethodManager = notNullActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }

            val text = binding.noteText.text.toString()
            if(text != EMPTY_STRING){
                viewModel.saveNote(Note(text, weatherBundle))
            }else{
                binding.root.showSnackBar(COMMENT_TEXT_IS_REQUIRED_MSG)
            }
        }
    }

    private fun renderData(appState: AppState) {
        when(appState) {
            is AppState.Success -> {
                binding.root.showSnackBar(SAVED_SUCCESSFULLY_MSG)
                activity?.onBackPressed()
            }
            is AppState.Error -> {
                binding.root.showSnackBar(appState.error.message ?: ERROR_MSG)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(weather: Weather) =
            AddNoteFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(WEATHER_KEY, weather)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.menu_note).setVisible(false)
        menu.findItem(R.id.menu_history).setVisible(false)
        super.onCreateOptionsMenu(menu, inflater)
    }
}