package woowacourse.shopping.domain.model

import java.time.LocalDate

sealed interface Coupon {
    val detail: CouponDetail

    val isSelected: Boolean

    fun apply(products: Products): Price

    fun getIsAvailable(
        products: Products,
        nowDate: LocalDate = LocalDate.now(),
    ): Boolean

    fun copy(
        detail: CouponDetail = this.detail,
        isSelected: Boolean = this.isSelected,
    ): Coupon
}
