package woowacourse.shopping.data.product.model.dto.response

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
)
