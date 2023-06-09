package woowacourse.shopping.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import woowacourse.shopping.databinding.CustomCounterBinding

class Counter(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    val binding = CustomCounterBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.count = 1
    }
}
