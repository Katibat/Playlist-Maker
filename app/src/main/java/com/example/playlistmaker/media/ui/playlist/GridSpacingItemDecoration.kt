package com.example.playlistmaker.media.ui.playlist

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val count: Int,
    private val horizontalSpacing: Int,
    private val verticalSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % count

        if (column == 0) {
            outRect.left = horizontalSpacing
            outRect.right = horizontalSpacing / 2
        } else {
            outRect.left = horizontalSpacing / 2
            outRect.right = horizontalSpacing
        }

        if (position >= count) {
            outRect.top = verticalSpacing
        }
    }
}