package woowacourse.shopping.data.dto.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.DiscountType
import java.time.LocalDate

@Serializable
data class CouponResponseItem(
    @SerialName("availableTime")
    val availableTimeResponse: AvailableTimeResponse? = null,
    @SerialName("buyQuantity")
    val buyQuantity: Int? = 0,
    @SerialName("code")
    val code: String,
    @SerialName("description")
    val description: String,
    @SerialName("discount")
    val discount: Int? = 0,
    @SerialName("discountType")
    val discountType: String,
    @SerialName("expirationDate")
    val expirationDate: String?,
    @SerialName("getQuantity")
    val getQuantity: Int? = 0,
    @SerialName("id")
    val id: Int?,
    @SerialName("minimumAmount")
    val minimumAmount: Int? = 0,
)

fun CouponResponseItem.toDomain(): Coupon =
    Coupon(
        code = this.code,
        description = this.description,
        expirationDate = LocalDate.parse(this.expirationDate),
        discount = this.discount,
        discountType = this.toDiscountType(),
        minimumAmount = this.minimumAmount,
        buyQuantity = this.buyQuantity,
        getQuantity = this.getQuantity,
        availableTime = this.availableTimeResponse?.toDomain(),
    )

private fun CouponResponseItem.toDiscountType(): DiscountType =
    when (this.discountType) {
        "fixed" -> DiscountType.FixedAmount(this.discount!!)
        "percentage" -> DiscountType.Percentage(this.discount!!)
        "freeShipping" -> DiscountType.FreeShipping
        "buyXgetY" ->
            DiscountType.BuyXGetY(
                buyQuantity = this.buyQuantity!!,
                getQuantity = this.getQuantity!!,
            )

        else -> throw IllegalArgumentException("알 수 없는 discount type: ${this.discountType}")
    }
