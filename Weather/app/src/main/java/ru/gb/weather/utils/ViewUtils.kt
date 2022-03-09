package ru.gb.weather.utils

import android.view.View

fun View.show(): View {
    if(visibility != View.VISIBLE){
        visibility = View.VISIBLE
    }
    return this
}

fun View.hide(): View {
    if(visibility != View.GONE){
        visibility = View.GONE
    }
    return this
}