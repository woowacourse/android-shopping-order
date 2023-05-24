package woowacourse.shopping.utils.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import woowacourse.shopping.databinding.LayoutCountBadgeBinding

class CountBadge(
    context: Context,
    attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {
    private val binding =
        LayoutCountBadgeBinding.inflate(LayoutInflater.from(context), this, true)

    fun updateCount(count: Int) {
        binding.tvCartCount.text = count.toString()
        binding.root.isVisible = count > 0
    }
}
