package woowacourse.shopping.presentation.custom

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int,
    spacingDp: Float,
) : RecyclerView.ItemDecoration() {
    private val spacingPx = dpToPx(spacingDp)
    private val lastColumnIndex get() = spanCount - 1

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == INVALID_POSITION) {
            outRect.set(ZERO, ZERO, ZERO, ZERO)
            return
        }

        val column = position % spanCount

        val left = spacingPx * (spanCount - column) / spanCount
        val right = spacingPx * (column + 1) / spanCount

        outRect.set(left, 0, right, spacingPx)
    }

    private fun getLeftPadding(column: Int): Int =
        when (column) {
            FIRST_COLUMN -> ZERO
            lastColumnIndex -> spacingPx
            else -> spacingPx / DIVIDE_VALUE
        }

    private fun getRightPadding(column: Int): Int =
        when (column) {
            FIRST_COLUMN -> spacingPx
            lastColumnIndex -> ZERO
            else -> spacingPx / DIVIDE_VALUE
        }

    private fun dpToPx(dp: Float): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()

    companion object {
        private const val ZERO = 0
        private const val FIRST_COLUMN = 0
        private const val INVALID_POSITION = -1
        private const val DIVIDE_VALUE = 2
    }
}
