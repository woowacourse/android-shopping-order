package woowacourse.shopping.data.model

data class RecentProductEntity(
    val productId: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val dateTimeMills: Long,
)
