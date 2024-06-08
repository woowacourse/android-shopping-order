package woowacourse.shopping.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CouponResponse {
    abstract val id: Long
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: String
    abstract val discountType: String

    @Serializable
    data class Fixed5000(
        @SerialName("id")
        override val id: Long,
        @SerialName("code")
        override val code: String,
        @SerialName("description")
        override val description: String,
        @SerialName("expirationDate")
        override val expirationDate: String,
        @SerialName("discount")
        val discount: Int,
        @SerialName("minimumAmount")
        val minimumAmount: Int,
        @SerialName("discountType")
        override val discountType: String = "fixed",
    ) : CouponResponse()

    @Serializable
    data class Bogo(
        @SerialName("id")
        override val id: Long,
        @SerialName("code")
        override val code: String,
        @SerialName("description")
        override val description: String,
        @SerialName("expirationDate")
        override val expirationDate: String,
        @SerialName("buyQuantity")
        val buyQuantity: Int,
        @SerialName("getQuantity")
        val getQuantity: Int,
        @SerialName("discountType")
        override val discountType: String = "buyXgetY",
    ) : CouponResponse()

    data class Freeshipping(
        @SerialName("id")
        override val id: Long,
        @SerialName("code")
        override val code: String,
        @SerialName("description")
        override val description: String,
        @SerialName("expirationDate")
        override val expirationDate: String,
        @SerialName("minimumAmount")
        val minimumAmount: Int,
        @SerialName("discountType")
        override val discountType: String = "freeShipping",
    ) : CouponResponse()

    data class Miraclesale(
        @SerialName("id")
        override val id: Long,
        @SerialName("code")
        override val code: String,
        @SerialName("description")
        override val description: String,
        @SerialName("expirationDate")
        override val expirationDate: String,
        @SerialName("discount")
        val discount: Int,
        @SerialName("availableTime")
        val availableTime: AvailableTime,
        @SerialName("discountType")
        override val discountType: String = "percentage",
    ) : CouponResponse()
}

@Serializable
data class AvailableTime(
    val start: String,
    val end: String,
)
