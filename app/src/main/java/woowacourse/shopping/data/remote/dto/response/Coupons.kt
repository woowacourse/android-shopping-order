package woowacourse.shopping.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class Coupons(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int = 0,
    val minimumAmount: Int = 0,
    val buyQuantity: Int = 0,
    val getQuantity: Int = 0,
    val discountType: String,
    val availableTime: AvailableTime? = null,
)
