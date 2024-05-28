package woowacourse.shopping.data.remote.dto.request


data class ProductRequest(
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String
)