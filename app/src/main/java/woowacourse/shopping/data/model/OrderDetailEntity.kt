package woowacourse.shopping.data.model

data class OrderDetailEntity(
    val id: Long,
    val usedPoint: Int,
    val savedPoint: Int,
    val orderedAt: String,
    val products: List<CartRemoteEntity>,
)
