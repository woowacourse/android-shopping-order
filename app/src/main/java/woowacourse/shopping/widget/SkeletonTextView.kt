package woowacourse.shopping.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import woowacourse.shopping.R

class SkeletonTextView : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initStyledAttributes(attrs)
    }

    private fun initStyledAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SkeletonTextView)
        val defaultBackgroundColor = R.color.skeleton_background
        val skeletonBackgroundResId = typedArray.getResourceId(
            R.styleable.SkeletonTextView_skeletonTextBackground,
            defaultBackgroundColor
        )

        setBackgroundResource(skeletonBackgroundResId)
        typedArray.recycle()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        background = null
    }
}
