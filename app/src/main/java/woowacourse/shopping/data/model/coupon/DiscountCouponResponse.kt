package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import woowacourse.shopping.domain.model.coupon.DiscountCoupon
import java.time.LocalDate

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@SerialName("fixed")
@JsonClassDiscriminator("discountType")
data class DiscountCouponResponse(
    @SerialName("code")
    override val code: String,
    @SerialName("description")
    override val description: String,
    @SerialName("discount")
    val discount: Int,
    @SerialName("expirationDate")
    override val expirationDate: String,
    @SerialName("id")
    override val id: Long,
    @SerialName("minimumAmount")
    val minimumAmount: Int,
) : CouponResponse()

fun DiscountCouponResponse.toDomain() =
    DiscountCoupon(
        code,
        description,
        discount,
        LocalDate.parse(expirationDate),
        id,
        minimumAmount,
    )
