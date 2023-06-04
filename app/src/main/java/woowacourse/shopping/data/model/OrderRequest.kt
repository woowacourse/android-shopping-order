package woowacourse.shopping.data.model

data class OrderRequest(
    val cartItemIds: List<Int>,
    val usedPoints: Int,
)
