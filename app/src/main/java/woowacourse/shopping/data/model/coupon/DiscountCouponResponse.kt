package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import woowacourse.shopping.domain.model.coupon.DiscountCoupon

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("discountType")
data class DiscountCouponResponse(
    @SerialName("code")
    override val code: String,
    @SerialName("description")
    override val description: String,
    @SerialName("discount")
    val discount: Int,
    @SerialName("discountType")
    override val discountType: String,
    @SerialName("expirationDate")
    override val expirationDate: String,
    @SerialName("id")
    override val id: Long,
    @SerialName("minimumAmount")
    val minimumAmount: Int,
) : CouponResponse()

fun DiscountCouponResponse.toDomain() = DiscountCoupon(code, description, discount, discountType, expirationDate, id, minimumAmount)
