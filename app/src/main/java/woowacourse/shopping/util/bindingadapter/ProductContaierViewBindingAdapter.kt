package woowacourse.shopping.util.bindingadapter

import androidx.databinding.BindingAdapter
import woowacourse.shopping.widget.SkeletonCounterView

@BindingAdapter("bind:count")
fun SkeletonCounterView.setCount(count: Int) {
    this.count = count
}

@BindingAdapter("bind:onPlusClick")
fun SkeletonCounterView.setOnPlusClick(onClick: Runnable) {
    setOnPlusClickListener { _, _ -> onClick.run() }
}

@BindingAdapter("bind:onMinusClick")
fun SkeletonCounterView.setOnMinusClick(onClick: Runnable) {
    setOnMinusClickListener { _, _ -> onClick.run() }
}
