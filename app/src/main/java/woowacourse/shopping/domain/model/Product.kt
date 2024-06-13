package woowacourse.shopping.domain.model

data class Product(
    val id: Long,
    val imgUrl: String,
    val name: String,
    val price: Int,
    val quantity: Int,
)
