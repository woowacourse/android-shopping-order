package woowacourse.shopping.util.bindingadapter

import androidx.databinding.BindingAdapter
import woowacourse.shopping.widget.SkeletonCounterView

@BindingAdapter("bind:onPlusClick")
fun SkeletonCounterView.setOnPlusClick(listener: CountChangedListener) {
    setOnPlusClickListener { _, count -> listener.onCountChanged(count) }
}

@BindingAdapter("bind:onMinusClick")
fun SkeletonCounterView.setOnMinusClick(listener: CountChangedListener) {
    setOnMinusClickListener { _, count -> listener.onCountChanged(count) }
}

interface CountChangedListener {
    fun onCountChanged(changedCount: Int)
}
