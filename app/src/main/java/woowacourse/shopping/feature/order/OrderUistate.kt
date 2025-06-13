package woowacourse.shopping.feature.order

import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.domain.model.Payment

data class OrderUistate(
    val order: Carts,
    val coupons: List<CouponState>,
    val payment: Payment,
)
