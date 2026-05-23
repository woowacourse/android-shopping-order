package woowacourse.shopping.ui.payment.uistate

import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.model.product.Money
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object CouponUiModelMapper {
    fun toUiModel(coupon: Coupon): CouponUiModel =
        CouponUiModel(
            id = coupon.id,
            code = coupon.code,
            description = coupon.description,
            expirationDate = formatExpirationDate(coupon.expirationDate),
            minimumAmount = formatMinimumAmount(coupon),
        )

    private fun formatExpirationDate(date: String): String =
        try {
            val localDate = LocalDate.parse(date)
            localDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
        } catch (e: Exception) {
            date
        }

    private fun formatMinimumAmount(coupon: Coupon): String? =
        when (coupon) {
            is Coupon.FixedDiscount -> formatMoney(coupon.minimumAmount)
            is Coupon.FreeShipping -> formatMoney(coupon.minimumAmount)
            else -> null
        }

    fun formatMoney(money: Money): String = NumberFormat.getInstance(Locale.KOREA).format(money.value) + "원"
}
