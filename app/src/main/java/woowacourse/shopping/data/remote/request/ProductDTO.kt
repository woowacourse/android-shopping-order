package woowacourse.shopping.data.remote.request

data class ProductDTO(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
)
