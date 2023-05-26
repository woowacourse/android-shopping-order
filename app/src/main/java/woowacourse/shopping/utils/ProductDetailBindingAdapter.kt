package woowacourse.shopping.utils

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

object ProductDetailBindingAdapter {
    @BindingAdapter("app:isVisible")
    @JvmStatic
    fun src(view: View, isVisible: Boolean?) {
        isVisible ?: return
        view.isVisible = isVisible
    }
}
