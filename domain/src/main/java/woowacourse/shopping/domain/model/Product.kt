package woowacourse.shopping.domain.model

data class Product(
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
)
