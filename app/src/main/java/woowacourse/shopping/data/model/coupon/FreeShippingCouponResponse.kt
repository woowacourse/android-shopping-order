package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import java.time.LocalDate

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@SerialName("freeShipping")
@JsonClassDiscriminator("discountType")
data class FreeShippingCouponResponse(
    @SerialName("code")
    override val code: String,
    @SerialName("description")
    override val description: String,
    @SerialName("expirationDate")
    override val expirationDate: String,
    @SerialName("id")
    override val id: Long,
    @SerialName("minimumAmount")
    val minimumAmount: Int,
) : CouponResponse()

fun FreeShippingCouponResponse.toDomain() =
    FreeShippingCoupon(
        code,
        description,
        LocalDate.parse(expirationDate),
        id,
        minimumAmount,
    )
