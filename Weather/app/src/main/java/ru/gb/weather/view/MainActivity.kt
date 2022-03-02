package ru.gb.weather.view

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.gb.weather.databinding.MainActivityBinding
import ru.gb.weather.R

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        savedInstanceState?.let {
        } ?: run{
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        registerReceiver(MainBroadcastReceiver(), IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION))
    }
}