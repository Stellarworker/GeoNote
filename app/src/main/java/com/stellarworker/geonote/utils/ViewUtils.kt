package com.stellarworker.geonote.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar
import com.stellarworker.geonote.R

fun makeSnackbar(
    view: View,
    text: String = String.EMPTY,
    actionText: String = String.EMPTY,
    action: (View) -> Unit = {},
    length: Int = Snackbar.LENGTH_LONG,
    anchor: View? = null
) {
    Snackbar
        .make(view, text, length)
        .setAction(actionText, action)
        .setAnchorView(anchor)
        .show()
}

fun showDialog(
    context: Context,
    title: String,
    message: String,
    positiveButtonText: String = context.getString(R.string.dialogs_positive_button)
) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButtonText)
        { dialog, _ -> dialog.dismiss() }
        .create()
        .show()
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.hideKeyboard() =
    ViewCompat.getWindowInsetsController(this)?.hide(WindowInsetsCompat.Type.ime())