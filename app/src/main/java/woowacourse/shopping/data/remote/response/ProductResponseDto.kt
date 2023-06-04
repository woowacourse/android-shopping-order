package woowacourse.shopping.data.remote.response

data class ProductResponseDto(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
)
