package woowacourse.shopping.domain.model

import java.time.LocalDateTime

sealed interface Coupon {
    val detail: CouponDetail

    val isSelected: Boolean

    fun apply(products: Products): Price

    fun getIsAvailable(
        products: Products,
        nowDateTime: LocalDateTime = LocalDateTime.now(),
    ): Boolean

    fun copy(
        detail: CouponDetail = this.detail,
        isSelected: Boolean = this.isSelected,
    ): Coupon
}
