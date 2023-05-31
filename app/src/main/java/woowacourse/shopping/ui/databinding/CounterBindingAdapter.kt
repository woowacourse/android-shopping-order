package woowacourse.shopping.ui.databinding

import androidx.databinding.BindingAdapter
import woowacourse.shopping.ui.customview.Counter

object CounterBindingAdapter {
    @BindingAdapter("app:id")
    @JvmStatic
    fun id(counter: Counter, id: Long?) {
        id ?: return
        counter.binding.id = id
    }

    @BindingAdapter("app:count")
    @JvmStatic
    fun count(counter: Counter, count: Int?) {
        count ?: return
        counter.binding.count = count
    }
}
