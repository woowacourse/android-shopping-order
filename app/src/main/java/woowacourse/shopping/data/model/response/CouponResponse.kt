package woowacourse.shopping.data.model.response

import android.util.Log
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.CouponDetail
import woowacourse.shopping.domain.model.CouponDiscountType
import woowacourse.shopping.domain.model.CouponDiscountType.BUY_X_GET_Y
import woowacourse.shopping.domain.model.CouponDiscountType.FIXED
import woowacourse.shopping.domain.model.CouponDiscountType.FREE_SHIPPING
import woowacourse.shopping.domain.model.CouponDiscountType.PERCENTAGE
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
                discountType = discountType.toDiscountType() ?: return null,
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

        private fun String.toDiscountType(): CouponDiscountType? =
            when (this) {
                "fixed" -> FIXED
                "buyXgetY" -> BUY_X_GET_Y
                "freeShipping" -> FREE_SHIPPING
                "percentage" -> PERCENTAGE
                else -> {
                    Log.e("[CouponResponse]", "유효하지 않은 쿠폰 타입이 존재합니다!")
                    null
                }
            }
    }
}
