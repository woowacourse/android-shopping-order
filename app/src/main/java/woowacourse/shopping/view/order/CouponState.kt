package woowacourse.shopping.view.order

import woowacourse.shopping.domain.coupon.Coupon
import java.time.LocalDate

data class CouponState(
    val id: Long,
    val isSelected: Boolean,
    val title: String,
    val expirationDate: LocalDate,
    val minimumOrderPrice: Int?,
) {
    companion object {
        fun fromDomain(domain: Coupon): CouponState {
            return CouponState(
                id = domain.id,
                isSelected = false,
                title = domain.description,
                expirationDate = domain.explanationDate,
                minimumOrderPrice = domain.minimumAmount,
            )
        }
    }
}
