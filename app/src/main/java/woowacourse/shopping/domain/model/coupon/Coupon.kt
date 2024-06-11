package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.selector.ItemSelector

data class Coupon(
    val id: Int,
    val expirationDate: String,
    val couponType: CouponType,
    val description: String,
    val discountType: String,
    val discount: Int,
    val minimumAmount: Int,
    val buyQuantity: Int,
    val getQuantity: Int,
    val availableTime: AvailableTime?,
    val itemSelector: ItemSelector = ItemSelector(),
)
