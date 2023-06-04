package woowacourse.shopping.data.dto

typealias ProductsDto = Products
typealias ProductDto = Product

data class Products(
    val products: List<ProductDto>,
)

data class Product(
    val id: Int = 0,
    val imageUrl: String,
    val name: String,
    val price: Int,
)
