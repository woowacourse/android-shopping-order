package woowacourse.shopping.data.dto.request

data class SaveCartRequestDto(
    val productId: Long,
    val quantity: Int,
)

data class UpdateCartRequestDto(
    val quantity: Int,
)
