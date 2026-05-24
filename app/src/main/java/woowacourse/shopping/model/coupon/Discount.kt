package woowacourse.shopping.model.coupon

import woowacourse.shopping.model.Money

sealed interface Discount {
    data class OnTotal(
        val amount: Money,
    ) : Discount

    data class OnShipping(
        val amount: Money,
    ) : Discount
}
