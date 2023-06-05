package woowacourse.shopping.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderInfoDto(
    val cartItems: List<CartProductDto>,
    val totalPrice: Int,
    val currentPoints: Int,
    val earnedPoints: Int,
    val availablePoints: Int,
)
