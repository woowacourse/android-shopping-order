package woowacourse.shopping.domain.model

data class Product(
    val id: Long,
    val name: String,
    val imgUrl: String,
    val price: Long,
    val category: String,
)
