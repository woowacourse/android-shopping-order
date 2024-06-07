package woowacourse.shopping.remote.model.response

import com.squareup.moshi.Json

data class CouponResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "code") val code: String,
    @Json(name = "description") val description: String,
    @Json(name = "expirationDate") val expirationDate: String,
    @Json(name = "discount") val discount: Int? = null,
    @Json(name = "minimumAmount") val minimumAmount: Int? = null,
    @Json(name = "buyQuantity") val buyQuantity: Int? = null,
    @Json(name = "getQuantity") val getQuantity: Int? = null,
    @Json(name = "availableTime") val availableTime: AvailableTime? = null,
    @Json(name = "discountType") val discountType: String,
) {
    data class AvailableTime(
        @Json(name = "start") val start: String,
        @Json(name = "end") val end: String,
    )
}
