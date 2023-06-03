package woowacourse.shopping.model

data class PostOrderRequest(
    val usedPoints: Int,
    val cartItemIds: List<Int>
)
