package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import woowacourse.shopping.domain.model.coupon.TimeLimitedCoupon
import java.time.LocalDate

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@SerialName("percentage")
@JsonClassDiscriminator("discountType")
data class TimeLimitedCouponResponse(
    @SerialName("availableTime")
    val availableTime: AvailableTimeResponse?,
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
) : CouponResponse()

fun TimeLimitedCouponResponse.toDomain() =
    TimeLimitedCoupon(
        availableTime!!.toDomain(),
        code,
        description,
        discount,
        LocalDate.parse(expirationDate),
        id,
    )
