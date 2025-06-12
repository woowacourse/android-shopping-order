package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import woowacourse.shopping.domain.model.coupon.BogoCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.DiscountCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.TimeLimitedCoupon
import java.time.LocalDate

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("discountType")
sealed class CouponResponse {
    @SerialName("code")
    abstract val code: String

    @SerialName("description")
    abstract val description: String

    @SerialName("expirationDate")
    abstract val expirationDate: String

    @SerialName("id")
    abstract val id: Long
}

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

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@SerialName("buyXgetY")
@JsonClassDiscriminator("discountType")
data class BogoCouponResponse(
    @SerialName("buyQuantity")
    val buyQuantity: Int,
    @SerialName("code")
    override val code: String,
    @SerialName("description")
    override val description: String,
    @SerialName("expirationDate")
    override val expirationDate: String,
    @SerialName("getQuantity")
    val getQuantity: Int,
    @SerialName("id")
    override val id: Long,
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

fun TimeLimitedCouponResponse.toDomain() =
    TimeLimitedCoupon(
        availableTime!!.toDomain(),
        code,
        description,
        discount,
        LocalDate.parse(expirationDate),
        id,
    )

fun BogoCouponResponse.toDomain() =
    BogoCoupon(
        buyQuantity,
        code,
        description,
        LocalDate.parse(expirationDate),
        getQuantity,
        id,
    )

fun FreeShippingCouponResponse.toDomain() =
    FreeShippingCoupon(
        code,
        description,
        LocalDate.parse(expirationDate),
        id,
        minimumAmount,
    )

fun CouponResponse.toTypedDomain(): Coupon =
    when (this) {
        is BogoCouponResponse -> this.toDomain()
        is FreeShippingCouponResponse -> this.toDomain()
        is TimeLimitedCouponResponse -> this.toDomain()
        is DiscountCouponResponse -> this.toDomain()
    }
