package woowacourse.shopping.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderCompleteResponseDto(
    val couponName: String?,
    val discountPrice: Int,
    val id: Int,
    val orderProductDto: List<OrderProductDto>,
    val originPrice: Int,
    val timestamp: String,
    val totalPrice: Int,
) {

    @Serializable
    data class OrderProductDto(
        val productResponse: ProductResponseDto,
        val quantity: Int,
    )
}
