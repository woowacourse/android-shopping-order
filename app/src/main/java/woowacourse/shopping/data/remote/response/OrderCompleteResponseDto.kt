package woowacourse.shopping.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderCompleteResponseDto(
    val couponName: String?,
    val discountPrice: Int,
    val id: Int,
    val orderProducts: List<OrderProduct>,
    val originPrice: Int,
    val timestamp: String,
    val totalPrice: Int,
) {

    @Serializable
    data class OrderProduct(
        val productResponse: ProductResponseDto,
        val quantity: Int,
    )
}
