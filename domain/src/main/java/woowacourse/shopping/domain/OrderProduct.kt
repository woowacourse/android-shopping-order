package woowacourse.shopping.domain

data class OrderProduct(
    val id: Int,
    val name: String,
    val count: Count,
    val price: Price,
    val imageUrl: String,
)
