package woowacourse.shopping.data.remote.dto.order

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalTime

sealed class CouponDto {
    data class FixedDiscountCouponDto(
        @SerializedName("id") val id: Long,
        @SerializedName("code") val code: String,
        @SerializedName("description") val description: String,
        @SerializedName("expirationDate") val expirationDate: LocalDate,
        @SerializedName("discount") val discount: Int,
        @SerializedName("minimumAmount") val minimumAmount: Int,
        @SerializedName("discountType") val discountType: String,
    ) : CouponDto()

    data class BogoCouponDto(
        @SerializedName("id") val id: Long,
        @SerializedName("code") val code: String,
        @SerializedName("description") val description: String,
        @SerializedName("expirationDate") val expirationDate: LocalDate,
        @SerializedName("buyQuantity") val buyQuantity: Int,
        @SerializedName("getQuantity") val getQuantity: Int,
        @SerializedName("discountType") val discountType: String,
    ) : CouponDto()

    data class FreeShippingCouponDto(
        @SerializedName("id") val id: Long,
        @SerializedName("code") val code: String,
        @SerializedName("description") val description: String,
        @SerializedName("expirationDate") val expirationDate: LocalDate,
        @SerializedName("minimumAmount") val minimumAmount: Int,
        @SerializedName("discountType") val discountType: String,
    ) : CouponDto()

    data class TimeBasedDiscountCouponDto(
        @SerializedName("id") val id: Long,
        @SerializedName("code") val code: String,
        @SerializedName("description") val description: String,
        @SerializedName("expirationDate") val expirationDate: LocalDate,
        @SerializedName("discount") val discount: Int,
        @SerializedName("availableTime") val availableTime: AvailableTimeDto,
        @SerializedName("discountType") val discountType: String,
    ) : CouponDto()

    data class AvailableTimeDto(
        @SerializedName("start") val start: LocalTime,
        @SerializedName("end") val end: LocalTime,
    )
}
