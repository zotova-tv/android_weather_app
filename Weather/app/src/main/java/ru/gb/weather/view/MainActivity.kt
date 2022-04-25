package ru.gb.weather.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import ru.gb.weather.BuildConfig
import ru.gb.weather.databinding.MainActivityBinding
import ru.gb.weather.view.history.HistoryFragment
import ru.gb.weather.R
import ru.gb.weather.utils.addDays
import ru.gb.weather.utils.getMillis
import ru.gb.weather.view.contentprovider.ContactListFragment
import ru.gb.weather.view.googlemaps.GoogleMapsFragment
import java.util.*


private const val HISTORY_FRAGMENT = "HISTORY_FRAGMENT"
private const val CONTACT_LIST_FRAGMENT = "CONTACT_LIST_FRAGMENT"
private const val GOOGLE_MAPS_FRAGMENT = "GOOGLE_MAPS_FRAGMENT"
private const val EMPTY_STRING = ""
private const val DEBUG_VERSION = BuildConfig.TYPE == "DEBUG"
private const val DEBUG_LOG_TAG = "WeatherAppDebug"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (DEBUG_VERSION){
            Log.d(DEBUG_LOG_TAG, "onCreate() called with: savedInstanceState = $savedInstanceState")
        }

        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        savedInstanceState?.let {
        } ?: run{
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_screen_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.menu_history -> {
                addFragment(HistoryFragment.newInstance(), HISTORY_FRAGMENT)
                true
            }
            R.id.menu_contact_list -> {
                addFragment(ContactListFragment.newInstance(), CONTACT_LIST_FRAGMENT)
                true
            }
            R.id.menu_history_for_week -> {
                val today = Date(System.currentTimeMillis())
                val daysAgo7 = Date(System.currentTimeMillis()).addDays(-7)
                addFragment(HistoryFragment.newInstance(daysAgo7.getMillis(), today.getMillis()), HISTORY_FRAGMENT)
                true
            }
            R.id.menu_google_maps -> {
                addFragment(GoogleMapsFragment.newInstance(), GOOGLE_MAPS_FRAGMENT)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addFragment(fragment: Fragment, backStackName: String = EMPTY_STRING){
        supportFragmentManager.apply {
            beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(backStackName)
                .commitAllowingStateLoss()
        }
    }
}