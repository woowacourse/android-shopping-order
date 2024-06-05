package woowacourse.shopping.data.dto.response
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
sealed class Coupon {
    abstract val id: Long
    abstract val code: String
    abstract val description: String

    @Serializable(with = LocalDateSerializer::class)
    abstract val expirationDate: LocalDate
    abstract val discountType: String
}

@Serializable
@SerialName("fixed")
data class FixedDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    @Serializable(with = LocalDateSerializer::class)
    override val expirationDate: LocalDate,
    val discount: Int,
    val minimumAmount: Int,
    override val discountType: String = "fixed",
) : Coupon()

@Serializable
@SerialName("buyXgetY")
data class BuyXGetYCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    @Serializable(with = LocalDateSerializer::class)
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
    override val discountType: String = "buyXgetY",
) : Coupon()

@Serializable
@SerialName("freeShipping")
data class FreeShippingCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    @Serializable(with = LocalDateSerializer::class)
    override val expirationDate: LocalDate,
    val minimumAmount: Int,
    override val discountType: String = "freeShipping",
) : Coupon()

@Serializable
@SerialName("percentage")
data class PercentageDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    @Serializable(with = LocalDateSerializer::class)
    override val expirationDate: LocalDate,
    val discount: Int,
    val availableTime: AvailableTime,
    override val discountType: String = "percentage",
) : Coupon()

@Serializable
data class AvailableTime(
    val start: String,
    val end: String,
)

@Serializer(forClass = LocalDate::class)
object LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun serialize(
        output: Encoder,
        obj: LocalDate,
    ) {
        val string = obj.format(formatter)
        output.encodeString(string)
    }

    override fun deserialize(input: Decoder): LocalDate {
        val string = input.decodeString()
        return LocalDate.parse(string, formatter)
    }
}
