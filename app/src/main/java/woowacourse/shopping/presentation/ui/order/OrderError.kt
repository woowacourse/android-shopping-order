package woowacourse.shopping.presentation.ui.order

import androidx.annotation.StringRes
import woowacourse.shopping.R

enum class OrderError(
    @StringRes val messageResId: Int,
) {
    CouponsNotFound(R.string.error_coupons_loaded),
    FailToOrder(R.string.error_order_failed),
}
