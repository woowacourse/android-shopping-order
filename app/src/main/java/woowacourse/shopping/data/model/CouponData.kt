package woowacourse.shopping.data.model

import woowacourse.shopping.domain.model.BoGoCoupon
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.FixedAmountCoupon
import woowacourse.shopping.domain.model.FreeShippingCoupon
import woowacourse.shopping.domain.model.PercentageCoupon
import java.time.LocalDate

data class CouponData(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discountType: String,
    val discount: Int? = null,
    val minimumAmount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTimeData? = null,
)

// TODO: 널 단언 제거
fun CouponData.toDomain(): Coupon =
    when (code) {
        "FIXED5000" ->
            FixedAmountCoupon(
                id = this.id,
                description = this.description,
                expirationDate = LocalDate.parse(this.expirationDate),
                discount = this.discount!!,
                minimumAmount = this.minimumAmount!!,
            )

        "BOGO" ->
            BoGoCoupon(
                id = this.id,
                description = this.description,
                expirationDate = LocalDate.parse(this.expirationDate),
                buyQuantity = this.buyQuantity!!,
                getQuantity = this.getQuantity!!,
            )

        "FREESHIPPING" ->
            FreeShippingCoupon(
                id = this.id,
                description = this.description,
                expirationDate = LocalDate.parse(this.expirationDate),
                minimumAmount = this.minimumAmount!!,
            )

        "MIRACLESALE" ->
            PercentageCoupon(
                id = this.id,
                description = this.description,
                expirationDate = LocalDate.parse(this.expirationDate),
                discount = this.discount!!,
                availableTime = this.availableTime!!.toDomain(),
            )

        else -> throw IllegalArgumentException("Invalid coupon code")
    }
