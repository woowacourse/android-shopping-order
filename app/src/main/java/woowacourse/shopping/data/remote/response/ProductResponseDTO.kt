package woowacourse.shopping.data.remote.response

data class ProductResponseDTO(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
)
