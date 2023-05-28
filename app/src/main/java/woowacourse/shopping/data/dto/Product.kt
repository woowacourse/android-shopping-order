package woowacourse.shopping.data.dto

typealias ProductDto = Product

data class Product(
    val id: Int = 0,
    val imageUrl: String,
    val name: String,
    val price: Int,
)
