package woowacourse.shopping.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class PhDividerItemDecoration(private val mHeight: Float, color: Int) : ItemDecoration() {
    private val mPaint = Paint()

    init {
        mPaint.color = color
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top: Int = child.bottom + params.bottomMargin
            val bottom = top + mHeight
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom, mPaint)
        }
    }
}
