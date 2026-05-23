package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartItemCountResponseDto(
    val quantity: Int,
)

