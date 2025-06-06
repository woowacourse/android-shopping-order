package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BogoCouponResponse(
    @SerialName("buyQuantity")
    val buyQuantity: Int,
    @SerialName("code")
    override val code: String,
    @SerialName("description")
    override val description: String,
    @SerialName("discountType")
    override val discountType: String,
    @SerialName("expirationDate")
    override val expirationDate: String,
    @SerialName("getQuantity")
    val getQuantity: Int,
    @SerialName("id")
    override val id: Long,
) : CouponResponse()
