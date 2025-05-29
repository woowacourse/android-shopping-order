package woowacourse.shopping.presentation.binding

import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.facebook.shimmer.ShimmerFrameLayout

@BindingAdapter("isShow")
fun setShimmerBindingAdapter(
    view: ShimmerFrameLayout,
    isShow: Boolean,
) {
    view.isVisible = isShow

    if (!isShow) {
        view.stopShimmer()
        return
    }

    view.startShimmer()
}
