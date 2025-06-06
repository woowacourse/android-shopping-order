package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import woowacourse.shopping.domain.model.coupon.BogoCoupon

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("discountType")
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

fun BogoCouponResponse.toDomain() = BogoCoupon(buyQuantity, code, description, discountType, expirationDate, getQuantity, id)
