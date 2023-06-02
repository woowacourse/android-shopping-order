package woowacourse.shopping.model.data.dto

data class CartProductDTO(
    val id: Long,
    val quantity: Int,
    val product: ProductDTO
)
