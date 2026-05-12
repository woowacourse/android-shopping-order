package woowacourse.shopping.retrofit.dto

data class Product(
    val category: String,
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int
)