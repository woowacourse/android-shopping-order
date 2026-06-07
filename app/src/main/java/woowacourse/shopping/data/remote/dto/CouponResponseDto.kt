package woowacourse.shopping.data.remote.dto
import kotlinx.serialization.Serializable

@Serializable
data class CouponResponseDto(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discountType: String,
    val discount: Int? = null,
    val minimumAmount: Int? = null,
    val buyQuantity: Int? = null,
    val getQuantity: Int? = null,
    val availableTime: AvailableTimeResponseDto? = null,
)

@Serializable
data class AvailableTimeResponseDto(
    val start: String,
    val end: String,
)
