package woowacourse.shopping.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import woowacourse.shopping.R

class PointEditText : AppCompatEditText {
    var maxPoint: Int = DEFAULT_MAX_POINT
        set(value) {
            field = value
            initMaxPointHint()
        }
    val point: Int
        get() = text.toString().toIntOrNull() ?: DEFAULT_USE_POINT

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(
        context, attrs, R.attr.PointEditTextStyle
    ) {
        initMaxPoint(attrs)
        initEditTextConfig()
    }

    private fun initMaxPoint(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.PointEditText).use {
            maxPoint = it.getInt(R.styleable.PointEditText_maxPoint, DEFAULT_MAX_POINT)
        }
    }

    private fun initEditTextConfig() {
        initMaxPointHint()
        initPointInputChangedListener()
    }

    private fun initMaxPointHint() {
        hint = context.getString(R.string.point_format, maxPoint)
    }

    private fun initPointInputChangedListener() {
        addTextChangedListener { pointEditable ->
            if (pointEditable.isNullOrEmpty()) return@addTextChangedListener
            if (pointEditable.startsWith('0')) {
                pointEditable.clear()
                return@addTextChangedListener
            }

            val point = pointEditable.toString().toInt()
            if (point > maxPoint) {
                setText(maxPoint.toString())
                setSelection(text?.length ?: 0)
            }
        }
    }

    companion object {
        private const val DEFAULT_MAX_POINT = 0
        private const val DEFAULT_USE_POINT = 0
    }
}
