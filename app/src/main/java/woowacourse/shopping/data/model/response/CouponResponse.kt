package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.CouponDetail
import woowacourse.shopping.domain.model.CouponDiscountType
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class CouponResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("code")
    val code: String,
    @SerialName("description")
    val description: String,
    @SerialName("expirationDate")
    val expirationDate: String,
    @SerialName("discount")
    val discount: Int?,
    @SerialName("minimumAmount")
    val minimumAmount: Int?,
    @SerialName("discountType")
    val discountType: String,
    @SerialName("buyQuantity")
    val buyQuantity: Int?,
    @SerialName("getQuantity")
    val getQuantity: Int?,
    @SerialName("availableTime")
    val availableTime: AvailableTime?,
) {
    @Serializable
    data class AvailableTime(
        @SerialName("start")
        val start: String,
        @SerialName("end")
        val end: String,
    )

    companion object {
        fun CouponResponse.toDomain(): CouponDetail? {
            return CouponDetail(
                id = id,
                code = code,
                name = description,
                expirationDate = LocalDate.parse(expirationDate),
                discount = discount,
                minimumPurchase = minimumAmount,
                discountType = CouponDiscountType.from(discountType) ?: return null,
                buyQuantity = buyQuantity,
                getQuantity = getQuantity,
                availableTime =
                    availableTime?.let {
                        CouponDetail.AvailableTime(
                            start = LocalTime.parse(it.start),
                            end = LocalTime.parse(it.end),
                        )
                    },
            )
        }
    }
}
