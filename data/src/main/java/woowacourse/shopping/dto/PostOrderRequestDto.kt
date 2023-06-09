package woowacourse.shopping.dto

data class PostOrderRequestDto(
    val usedPoints: Int,
    val cartItemIds: List<Int>
)
