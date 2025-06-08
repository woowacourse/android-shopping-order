package woowacourse.shopping.view.order

import woowacourse.shopping.domain.order.DiscountType

data class CouponItem(
    val id: Int,
    val description: String,
    val code: String,
    val condition: String? = null,
    val expirationDate: String,
    val type: DiscountType,
    val isSelected: Boolean = false,
)
