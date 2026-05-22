package woowacourse.shopping.repository.http.dto.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("BUY_X_GET_Y")
data class BuyXGetYCouponResponseDto(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val buyQuantity: Int,
    val getQuantity: Int,
    override val discountType: String = "BUY_X_GET_Y",
) : CouponResponseDto()
