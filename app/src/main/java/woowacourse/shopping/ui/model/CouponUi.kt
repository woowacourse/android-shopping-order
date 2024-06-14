package woowacourse.shopping.ui.model

import woowacourse.shopping.domain.model.BoGoCoupon
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.FixedAmountCoupon
import woowacourse.shopping.domain.model.FreeShippingCoupon
import woowacourse.shopping.domain.model.PercentageCoupon
import java.time.LocalDate

data class CouponUi(
    val id: Long,
    val description: String, // 5,000 원 할인 쿠폰
    val expirationDate: LocalDate, // 2021-07-31
    val isChecked: Boolean = false,
    val minimumAmount: Int? = null,
    val isSelected: Boolean = false,
)

fun Coupon.toUi(): CouponUi =
    when (this) {
        is FixedAmountCoupon ->
            CouponUi(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumAmount = minimumAmount,
            )

        is FreeShippingCoupon ->
            CouponUi(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumAmount = minimumAmount,
            )

        is BoGoCoupon ->
            CouponUi(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumAmount = null,
            )

        is PercentageCoupon ->
            CouponUi(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumAmount = null,
            )
    }

fun List<Coupon>.toUi(): List<CouponUi> = this.map(Coupon::toUi)
