package ru.gb.weather.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.show(): View {
    visibility = View.VISIBLE
    return this
}

fun View.hide(): View {
    visibility = View.GONE
    return this
}

fun View.showActionSnackBar(
    text: String,
    actionText: String,
    action: (View) -> Unit,
    length: Int = Snackbar.LENGTH_INDEFINITE
) {
    Snackbar.make(this, text, length).setAction(actionText, action).show()
}

fun View.showSnackBar(
    text: String,
    length: Int = Snackbar.LENGTH_SHORT
) {
    Snackbar.make(this, text, length).show()
}