package woowacourse.shopping.presentation.products

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import woowacourse.shopping.common.hideIf
import woowacourse.shopping.common.showIf

@BindingAdapter("loadMoreBtnVisible")
fun TextView.binLoadMoreBtnVisible(last: Boolean?) {
    last?.let { value ->
        hideIf(value)
    }
}

@BindingAdapter("emptyLoadMoreTextVisible")
fun TextView.bindEmptyLoadMoreTextVisible(last: Boolean?) {
    last?.let { value ->
        showIf(value)
    }
}

@BindingAdapter("countViewVisible")
fun View.countViewVisible(isCountMin: Boolean) {
    hideIf(isCountMin)
}

@BindingAdapter("countPlusVisible")
fun View.countPlusVisible(isCountMin: Boolean) {
    showIf(isCountMin)
}
