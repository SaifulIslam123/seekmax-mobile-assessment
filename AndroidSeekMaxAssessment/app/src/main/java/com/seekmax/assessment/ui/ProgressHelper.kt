package com.seekmax.assessment.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView


object ProgressHelper {
    private var dialog: AlertDialog? = null
    fun showDialog(context: Context?, message: String = "Please wait...") {
        if (dialog == null) {
            val llPadding = 30
            val ll = LinearLayout(context)
            ll.orientation = LinearLayout.HORIZONTAL
            ll.setPadding(llPadding, llPadding, llPadding, llPadding)
            ll.gravity = Gravity.CENTER
            var llParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            llParam.gravity = Gravity.CENTER
            ll.layoutParams = llParam
            val progressBar = ProgressBar(context)
            progressBar.isIndeterminate = true
            progressBar.setPadding(0, 0, llPadding, 0)
            progressBar.layoutParams = llParam
            llParam = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            llParam.gravity = Gravity.CENTER
            val tvText = TextView(context)
            tvText.text = message
            tvText.setTextColor(Color.parseColor("#000000"))
            tvText.textSize = 20f
            tvText.layoutParams = llParam
            ll.addView(progressBar)
            ll.addView(tvText)
            val builder = AlertDialog.Builder(context)
            builder.setCancelable(true)
            builder.setView(ll)
            dialog = builder.create()
            dialog?.show()
            val window = dialog?.window
            if (window != null) {
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(dialog?.window!!.attributes)
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                dialog?.window!!.attributes = layoutParams
            }
        }
    }

    val isDialogVisible: Boolean
        get() = if (dialog != null) {
            dialog!!.isShowing
        } else {
            false
        }

    fun dismissDialog() {
        if (dialog != null && isDialogVisible) {
            dialog!!.dismiss()
            dialog = null
        }
    }
}
