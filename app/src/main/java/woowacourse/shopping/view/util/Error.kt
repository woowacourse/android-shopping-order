package woowacourse.shopping.view.util

import woowacourse.shopping.R

sealed class Error(
    val messageId: Int,
) {
    object FailToCart : Error(R.string.fail_to_cart)

    object FailToLoadProduct : Error(R.string.fail_to_load_product)

    object FailToIncrease : Error(R.string.fail_to_increase)

    object FailToDecrease : Error(R.string.fail_to_decrease)

    object FailToOrder : Error(R.string.fail_to_order)

    object FailToLoadCoupon : Error(R.string.fail_to_load_coupon)

    object FailToDelete : Error(R.string.fail_to_delete)
}
