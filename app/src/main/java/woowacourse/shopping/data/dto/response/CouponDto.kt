package woowacourse.shopping.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Discount
import woowacourse.shopping.domain.model.DiscountType
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class CouponDto(
    @SerialName("id")
    val id: Int,
    @SerialName("code")
    val code: String,
    @SerialName("description")
    val description: String,
    @SerialName("expirationDate")
    val expirationDate: String,
    @SerialName("discountType")
    val discountType: String,
    @SerialName("discount")
    val discount: Int? = null,
    @SerialName("minimumAmount")
    val minimumAmount: Int? = null,
    @SerialName("buyQuantity")
    val buyQuantity: Int? = null,
    @SerialName("getQuantity")
    val getQuantity: Int? = null,
    @SerialName("availableTime")
    val availableTime: AvailableTimeDto? = null,
) {
    fun toCoupon(): Coupon {
        val discount =
            when (DiscountType.from(this.discountType)) {
                DiscountType.FIXED -> {
                    requireNotNull(discount)
                    requireNotNull(minimumAmount)
                    Discount.FixedAmount(discount, minimumAmount)
                }

                DiscountType.BUY_X_GET_Y -> {
                    requireNotNull(buyQuantity)
                    requireNotNull(getQuantity)
                    Discount.BuyXGetYFree(buyQuantity, getQuantity)
                }

                DiscountType.FREE_SHIPPING -> {
                    requireNotNull(minimumAmount)
                    Discount.FreeShipping(minimumAmount)
                }

                DiscountType.PERCENTAGE -> {
                    requireNotNull(discount)
                    val time = requireNotNull(availableTime)
                    Discount.Percentage(
                        discount,
                        LocalTime.parse(time.start),
                        LocalTime.parse(time.end),
                    )
                }
            }

        return Coupon(
            id = id,
            code = code,
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            discount = discount,
        )
    }
}
