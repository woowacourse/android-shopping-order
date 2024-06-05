package woowacourse.shopping.domain

data class RecentProduct(
    val productId: Long,
    val name: String,
    val imgUrl: String,
    val quantity: Int,
    val price: Long,
    val createdAt: Long,
    val category: String,
    val cartId: Long,
)
