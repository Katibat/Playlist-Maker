package com.example.playlistmaker.playlist.ui

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class CustomTextInputEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.textInputStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)

        if (text?.isEmpty() == true) {
            mergeDrawableStates(drawableState, intArrayOf(android.R.attr.state_empty))
        }
        return drawableState
    }
}