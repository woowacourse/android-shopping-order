package woowacourse.shopping.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import woowacourse.shopping.R

class SkeletonImageView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initStyledAttributes(attrs)
    }

    private fun initStyledAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SkeletonImageView)
        val defaultBackgroundColor = R.color.skeleton_background
        val skeletonBackgroundResId = typedArray.getResourceId(
            R.styleable.SkeletonImageView_skeletonImageBackground,
            defaultBackgroundColor
        )

        setBackgroundResource(skeletonBackgroundResId)
        typedArray.recycle()
    }
}
