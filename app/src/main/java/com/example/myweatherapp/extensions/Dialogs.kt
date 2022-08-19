package com.example.myweatherapp.extensions

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myweatherapp.R
import com.example.myweatherapp.databinding.DialogErrorBinding
import com.example.myweatherapp.databinding.DialogLoadingBinding
import com.example.myweatherapp.utils.Constants

fun AppCompatActivity.getAlertDialog(activity: Activity = this, dialogType: String): AlertDialog {
    val builder = AlertDialog.Builder(activity)
    val layoutInflater = LayoutInflater.from(activity)
    val dialogBinding = if (dialogType == Constants.DIALOGS.DIALOG_LOADING) {
        builder.setCancelable(false)
        DialogLoadingBinding.inflate(layoutInflater)
    } else {
        DialogErrorBinding.inflate(layoutInflater)
    }
    builder.setView(dialogBinding.root)
    val alertDialog = builder.create()
    alertDialog.window?.let {
        it.setBackgroundDrawable(ColorDrawable(0))
        it.attributes?.windowAnimations = R.style.AlertDialogSweet
    }
    return alertDialog
}